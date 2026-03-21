package br.org.fadesp.desafio.pagamentos.dto;

import br.org.fadesp.desafio.pagamentos.domain.enums.MetodoPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PagamentoRequestDTO(
        @NotNull(message = "O código do débito é obrigatório")
        Integer codigoDebito,

        @NotBlank(message = "O CPF/CNPJ é obrigatório")
        @Pattern(regexp = "(^\\d{11}$)|(^\\d{14}$)", message = "O CPF deve conter 11 dígitos ou o CNPJ 14 dígitos (Apenas números)")
        String cpfCnpjPagador,

        @NotNull(message = "O método de pagamento é obrigatório")
        MetodoPagamento metodoPagamento,

        @Pattern(regexp = "^\\d{16}$", message = "O número do cartão deve conter exatamente 16 dígitos numéricos")
        String numeroCartao,

        @NotNull(message = "O valor do pagamento é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal valor
) {}