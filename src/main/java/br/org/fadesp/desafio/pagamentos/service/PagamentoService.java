package br.org.fadesp.desafio.pagamentos.service;

import br.org.fadesp.desafio.pagamentos.dto.PagamentoRequestDTO;
import br.org.fadesp.desafio.pagamentos.dto.PagamentoResponseDTO;
import br.org.fadesp.desafio.pagamentos.domain.enums.MetodoPagamento;
import br.org.fadesp.desafio.pagamentos.domain.entity.Pagamento;
import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;
import br.org.fadesp.desafio.pagamentos.repository.PagamentoRepository;
import br.org.fadesp.desafio.pagamentos.repository.PagamentoSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;

    public PagamentoService(PagamentoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PagamentoResponseDTO receberPagamento(PagamentoRequestDTO dto) {
        if (dto.metodoPagamento().requerCartao() &&
                (dto.numeroCartao() == null || dto.numeroCartao().isBlank())) {
            throw new IllegalArgumentException("O número do cartão é obrigatório para pagamentos em crédito ou débito.");
        }
        if (!dto.metodoPagamento().requerCartao() &&
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

    @Transactional
    public PagamentoResponseDTO atualizarStatus(Long id, StatusPagamento novoStatus) {
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));


        // Máquina de Estados
        if (!pagamento.getStatus().podeAlterarPara(novoStatus)) {
            throw new IllegalArgumentException("Transição de status de " + pagamento.getStatus() + " para " + novoStatus + " não é permitida.");
        }

        pagamento.setStatus(novoStatus);
        pagamento = repository.save(pagamento);

        return converterParaResponseDTO(pagamento);
    }

    @Transactional
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

        Specification<Pagamento> spec = Specification.allOf(
                PagamentoSpecification.comCodigoDebito(codigoDebito),
                PagamentoSpecification.comCpfCnpj(cpfCnpj),
                PagamentoSpecification.comStatus(status)
        );

        return repository.findAll(spec)
                .stream()
                .map(this::converterParaResponseDTO)
                .toList();
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