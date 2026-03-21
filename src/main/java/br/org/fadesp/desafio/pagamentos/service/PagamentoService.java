package br.org.fadesp.desafio.pagamentos.service;

import br.org.fadesp.desafio.pagamentos.dto.PagamentoRequestDTO;
import br.org.fadesp.desafio.pagamentos.dto.PagamentoResponseDTO;
import br.org.fadesp.desafio.pagamentos.domain.enums.MetodoPagamento;
import br.org.fadesp.desafio.pagamentos.domain.entity.Pagamento;
import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;
import br.org.fadesp.desafio.pagamentos.repository.PagamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;

    public PagamentoService(PagamentoRepository repository) {
        this.repository = repository;
    }

    public PagamentoResponseDTO receberPagamento(PagamentoRequestDTO dto) {
        if (isPagamentoComCartao(dto.metodoPagamento()) &&
                (dto.numeroCartao() == null || dto.numeroCartao().isBlank())) {
            throw new IllegalArgumentException("O número do cartão é obrigatório para pagamentos em crédito ou débito.");
        }
        if (!isPagamentoComCartao(dto.metodoPagamento()) &&
                (dto.numeroCartao() != null && !dto.numeroCartao().isBlank())) {
            throw new IllegalArgumentException("O número do cartão não deve ser informado para pagamentos via PIX ou Boleto.");
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setCodigoDebito(dto.codigoDebito());
        pagamento.setCpfCnpjPagador(dto.cpfCnpjPagador());
        pagamento.setMetodoPagamento(dto.metodoPagamento());
        pagamento.setNumeroCartao(dto.numeroCartao());
        pagamento.setValor(dto.valor());

        // Todo pagamento nasce como Pendente
        pagamento.setStatus(StatusPagamento.PENDENTE_DE_PROCESSAMENTO);

        Pagamento pagamentoSalvo = repository.save(pagamento);
        return converterParaResponseDTO(pagamentoSalvo);
    }

    public PagamentoResponseDTO atualizarStatus(Long id, StatusPagamento novoStatus) {
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));

        StatusPagamento statusAtual = pagamento.getStatus();

        // Máquina de Estados
        if (statusAtual == StatusPagamento.PROCESSADO_COM_SUCESSO) {
            throw new IllegalArgumentException("Pagamentos processados com sucesso não podem ter o status alterado.");
        }

        if (statusAtual == StatusPagamento.PROCESSADO_COM_FALHA && novoStatus != StatusPagamento.PENDENTE_DE_PROCESSAMENTO) {
            throw new IllegalArgumentException("Pagamentos com falha só podem retornar para Pendente de Processamento.");
        }

        if (statusAtual == StatusPagamento.PENDENTE_DE_PROCESSAMENTO &&
                (novoStatus != StatusPagamento.PROCESSADO_COM_SUCESSO && novoStatus != StatusPagamento.PROCESSADO_COM_FALHA)) {
            throw new IllegalArgumentException("Um pagamento pendente só pode ser alterado para Sucesso ou Falha.");
        }

        pagamento.setStatus(novoStatus);
        pagamento = repository.save(pagamento);

        return converterParaResponseDTO(pagamento);
    }

    public void inativar(Long id) {
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));

        if (pagamento.getStatus() != StatusPagamento.PENDENTE_DE_PROCESSAMENTO) {
            throw new IllegalArgumentException("Apenas pagamentos Pendentes de Processamento podem ser inativados.");
        }

        // Exclusão Lógica
        pagamento.setStatus(StatusPagamento.INATIVO);
        repository.save(pagamento);
    }

    public List<PagamentoResponseDTO> listarComFiltros(Integer codigoDebito, String cpfCnpj, StatusPagamento status) {
        return repository.buscarComFiltros(codigoDebito, cpfCnpj, status)
                .stream()
                .map(this::converterParaResponseDTO)
                .toList();
    }

    private boolean isPagamentoComCartao(MetodoPagamento metodo) {
        return metodo == MetodoPagamento.CARTAO_CREDITO || metodo == MetodoPagamento.CARTAO_DEBITO;
    }

    private PagamentoResponseDTO converterParaResponseDTO(Pagamento pagamento) {
        return new PagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getCodigoDebito(),
                pagamento.getCpfCnpjPagador(),
                pagamento.getMetodoPagamento(),
                pagamento.getNumeroCartao(),
                pagamento.getValor(),
                pagamento.getStatus()
        );
    }
}