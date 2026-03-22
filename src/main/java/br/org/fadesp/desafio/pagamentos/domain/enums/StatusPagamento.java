package br.org.fadesp.desafio.pagamentos.domain.enums;

public enum StatusPagamento {
    PENDENTE_DE_PROCESSAMENTO,
    PROCESSADO_COM_SUCESSO,
    PROCESSADO_COM_FALHA,
    INATIVO;

    public boolean podeAlterarPara(StatusPagamento novoStatus) {
        return switch (this) {
            case PENDENTE_DE_PROCESSAMENTO -> novoStatus == PROCESSADO_COM_SUCESSO || novoStatus == PROCESSADO_COM_FALHA;
            case PROCESSADO_COM_FALHA -> novoStatus == PENDENTE_DE_PROCESSAMENTO;
            default -> false;
        };
    }
}