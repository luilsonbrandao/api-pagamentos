package br.org.fadesp.desafio.pagamentos.dto;

import br.org.fadesp.desafio.pagamentos.domain.enums.MetodoPagamento;
import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;

import java.math.BigDecimal;

public record PagamentoResponseDTO(
        Long id,
        Integer codigoDebito,
        String cpfCnpjPagador,
        MetodoPagamento metodoPagamento,
        String numeroCartao,
        BigDecimal valor,
        StatusPagamento status
) {}