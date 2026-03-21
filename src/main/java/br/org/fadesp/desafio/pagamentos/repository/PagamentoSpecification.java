package br.org.fadesp.desafio.pagamentos.repository;

import br.org.fadesp.desafio.pagamentos.domain.entity.Pagamento;
import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;
import org.springframework.data.jpa.domain.Specification;

public class PagamentoSpecification {

    public static Specification<Pagamento> comCodigoDebito(Integer codigoDebito) {
        return (root, query, cb) -> codigoDebito == null ? null : cb.equal(root.get("codigoDebito"), codigoDebito);
    }

    public static Specification<Pagamento> comCpfCnpj(String cpfCnpj) {
        return (root, query, cb) -> cpfCnpj == null ? null : cb.equal(root.get("cpfCnpjPagador"), cpfCnpj);
    }

    public static Specification<Pagamento> comStatus(StatusPagamento status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }
}