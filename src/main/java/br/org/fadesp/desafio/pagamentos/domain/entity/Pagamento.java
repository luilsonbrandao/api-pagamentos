package br.org.fadesp.desafio.pagamentos.domain.entity;

import br.org.fadesp.desafio.pagamentos.domain.enums.MetodoPagamento;
import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_pagamento")
@Getter
@Setter
@NoArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "codigo_debito", nullable = false)
    private Integer codigoDebito;

    @Column(name = "cpf_cnpj_pagador", nullable = false)
    private String cpfCnpjPagador;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false)
    private MetodoPagamento metodoPagamento;

    @Column(name = "numero_cartao")
    private String numeroCartao;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

}