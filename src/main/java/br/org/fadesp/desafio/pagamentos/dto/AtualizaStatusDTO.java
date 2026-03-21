package br.org.fadesp.desafio.pagamentos.dto;

import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;
import jakarta.validation.constraints.NotNull;

public record AtualizaStatusDTO(
        @NotNull(message = "O novo status é obrigatório")
        StatusPagamento status
) {}