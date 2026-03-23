package br.org.fadesp.desafio.pagamentos.dto;

import br.org.fadesp.desafio.pagamentos.domain.entity.Pagamento;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagamentoMapper {

    public PagamentoResponseDTO toResponseDTO(Pagamento pagamento) {
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

    public List<PagamentoResponseDTO> toResponseDTOList(List<Pagamento> pagamentos) {
        return pagamentos.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Pagamento toEntity(PagamentoRequestDTO dto) {
        Pagamento pagamento = new Pagamento();
        pagamento.setCodigoDebito(dto.codigoDebito());
        pagamento.setCpfCnpjPagador(dto.cpfCnpjPagador());
        pagamento.setMetodoPagamento(dto.metodoPagamento());
        pagamento.setNumeroCartao(dto.numeroCartao());
        pagamento.setValor(dto.valor());
        return pagamento;
    }
}