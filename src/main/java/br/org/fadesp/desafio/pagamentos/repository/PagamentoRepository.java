package br.org.fadesp.desafio.pagamentos.repository;

import br.org.fadesp.desafio.pagamentos.domain.entity.Pagamento;
import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    @Query("SELECT p FROM Pagamento p WHERE " +
            "(:codigoDebito IS NULL OR p.codigoDebito = :codigoDebito) AND " +
            "(:cpfCnpj IS NULL OR p.cpfCnpjPagador = :cpfCnpj) AND " +
            "(:status IS NULL OR p.status = :status)")
    List<Pagamento> buscarComFiltros(@Param("codigoDebito") Integer codigoDebito,
                                     @Param("cpfCnpj") String cpfCnpj,
                                     @Param("status") StatusPagamento status);
}