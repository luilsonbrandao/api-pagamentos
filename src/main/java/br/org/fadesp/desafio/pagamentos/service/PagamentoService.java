package br.org.fadesp.desafio.pagamentos.service;

import br.org.fadesp.desafio.pagamentos.dto.PagamentoMapper;
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
    private final PagamentoMapper mapper;

    public PagamentoService(PagamentoRepository repository, PagamentoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
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

        Pagamento pagamento = mapper.toEntity(dto);

        // Todo pagamento nasce como Pendente
        pagamento.setStatus(StatusPagamento.PENDENTE_DE_PROCESSAMENTO);

        Pagamento pagamentoSalvo = repository.save(pagamento);
        return mapper.toResponseDTO(pagamentoSalvo);
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

        return mapper.toResponseDTO(pagamento);
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

        List<Pagamento> pagamentosEncontrados = repository.findAll(spec);

        return mapper.toResponseDTOList(pagamentosEncontrados);
    }

}