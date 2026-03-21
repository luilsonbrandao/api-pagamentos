package br.org.fadesp.desafio.pagamentos.repository;

import br.org.fadesp.desafio.pagamentos.domain.entity.Pagamento;
import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long>, JpaSpecificationExecutor<Pagamento> {

}