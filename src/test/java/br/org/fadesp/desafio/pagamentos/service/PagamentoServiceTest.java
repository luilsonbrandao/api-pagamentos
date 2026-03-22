package br.org.fadesp.desafio.pagamentos.service;

import br.org.fadesp.desafio.pagamentos.domain.entity.Pagamento;
import br.org.fadesp.desafio.pagamentos.domain.enums.MetodoPagamento;
import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;
import br.org.fadesp.desafio.pagamentos.dto.PagamentoMapper;
import br.org.fadesp.desafio.pagamentos.dto.PagamentoRequestDTO;
import br.org.fadesp.desafio.pagamentos.dto.PagamentoResponseDTO;
import br.org.fadesp.desafio.pagamentos.repository.PagamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository repository;

    @Spy
    private PagamentoMapper mapper = new PagamentoMapper();

    @InjectMocks
    private PagamentoService service;


    // TESTES DE RECEBER PAGAMENTO (POST)

    @Test
    @DisplayName("Deve salvar pagamento PIX com sucesso e nascer com status PENDENTE")
    void receberPagamento_PixComSucesso() {
        PagamentoRequestDTO request = new PagamentoRequestDTO(
                123, "12345678909", MetodoPagamento.PIX, null, new BigDecimal("100.00")
        );

        Pagamento pagamentoSalvo = new Pagamento();
        try {
            var campoId = Pagamento.class.getDeclaredField("id");
            campoId.setAccessible(true);
            campoId.set(pagamentoSalvo, 1L);
        } catch (Exception e) {
            fail("Falha ao injetar ID no mock");
        }
        pagamentoSalvo.setCodigoDebito(123);
        pagamentoSalvo.setStatus(StatusPagamento.PENDENTE_DE_PROCESSAMENTO);

        when(repository.save(any(Pagamento.class))).thenReturn(pagamentoSalvo);

        PagamentoResponseDTO response = service.receberPagamento(request);

        assertNotNull(response);
        assertEquals(StatusPagamento.PENDENTE_DE_PROCESSAMENTO, response.status());
        verify(repository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pagar PIX informando número do cartão")
    void receberPagamento_PixComCartao_DeveLancarExcecao() {
        PagamentoRequestDTO request = new PagamentoRequestDTO(
                123, "12345678909", MetodoPagamento.PIX, "1234123412341234", new BigDecimal("100.00")
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.receberPagamento(request);
        });

        assertEquals("O número do cartão não deve ser informado para pagamentos via PIX ou Boleto.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pagar Cartão de Crédito sem informar número do cartão")
    void receberPagamento_CartaoCreditoSemCartao_DeveLancarExcecao() {
        PagamentoRequestDTO request = new PagamentoRequestDTO(
                123, "12345678909", MetodoPagamento.CARTAO_CREDITO, null, new BigDecimal("100.00")
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.receberPagamento(request);
        });

        assertEquals("O número do cartão é obrigatório para pagamentos em crédito ou débito.", exception.getMessage());
        verify(repository, never()).save(any());
    }


    // TESTES DA MÁQUINA DE ESTADOS (PATCH)

    @Test
    @DisplayName("Deve atualizar status de PENDENTE para PROCESSADO_COM_SUCESSO")
    void atualizarStatus_TransicaoValida_DeveAtualizar() {
        Pagamento pagamentoNoBanco = new Pagamento();
        pagamentoNoBanco.setStatus(StatusPagamento.PENDENTE_DE_PROCESSAMENTO);

        when(repository.findById(1L)).thenReturn(Optional.of(pagamentoNoBanco));
        when(repository.save(any(Pagamento.class))).thenReturn(pagamentoNoBanco);

        PagamentoResponseDTO response = service.atualizarStatus(1L, StatusPagamento.PROCESSADO_COM_SUCESSO);

        assertEquals(StatusPagamento.PROCESSADO_COM_SUCESSO, response.status());
        verify(repository, times(1)).save(pagamentoNoBanco);
    }

    @Test
    @DisplayName("Deve barrar transição de status de SUCESSO para PENDENTE")
    void atualizarStatus_TransicaoInvalida_DeveLancarExcecao() {
        Pagamento pagamentoNoBanco = new Pagamento();
        pagamentoNoBanco.setStatus(StatusPagamento.PROCESSADO_COM_SUCESSO);

        when(repository.findById(1L)).thenReturn(Optional.of(pagamentoNoBanco));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.atualizarStatus(1L, StatusPagamento.PENDENTE_DE_PROCESSAMENTO);
        });

        assertTrue(exception.getMessage().contains("Transição de status de PROCESSADO_COM_SUCESSO para PENDENTE_DE_PROCESSAMENTO não é permitida."));
        verify(repository, never()).save(any());
    }

    // TESTES DE INATIVAÇÃO (DELETE)

    @Test
    @DisplayName("Deve inativar pagamento que está PENDENTE")
    void inativar_PagamentoPendente_DeveInativar() {
        Pagamento pagamentoNoBanco = new Pagamento();
        pagamentoNoBanco.setStatus(StatusPagamento.PENDENTE_DE_PROCESSAMENTO);

        when(repository.findById(1L)).thenReturn(Optional.of(pagamentoNoBanco));

        service.inativar(1L);

        assertEquals(StatusPagamento.INATIVO, pagamentoNoBanco.getStatus());
        verify(repository, times(1)).save(pagamentoNoBanco);
    }
}