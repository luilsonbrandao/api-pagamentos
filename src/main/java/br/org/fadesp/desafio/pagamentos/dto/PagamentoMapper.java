package br.org.fadesp.desafio.pagamentos.dto;

import br.org.fadesp.desafio.pagamentos.domain.entity.Pagamento;
import org.springframework.stereotype.Component;

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
}