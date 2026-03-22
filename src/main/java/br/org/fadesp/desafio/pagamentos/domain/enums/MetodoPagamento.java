package br.org.fadesp.desafio.pagamentos.domain.enums;

public enum MetodoPagamento {
    BOLETO,
    PIX,
    CARTAO_CREDITO,
    CARTAO_DEBITO;

    public boolean requerCartao() {
        return this == CARTAO_CREDITO || this == CARTAO_DEBITO;
    }
}