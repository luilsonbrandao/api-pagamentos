package br.org.fadesp.desafio.pagamentos.controller;

import br.org.fadesp.desafio.pagamentos.dto.AtualizaStatusDTO;
import br.org.fadesp.desafio.pagamentos.dto.PagamentoRequestDTO;
import br.org.fadesp.desafio.pagamentos.dto.PagamentoResponseDTO;
import br.org.fadesp.desafio.pagamentos.domain.enums.StatusPagamento;
import br.org.fadesp.desafio.pagamentos.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> receberPagamento(@RequestBody @Valid PagamentoRequestDTO dto) {
        PagamentoResponseDTO response = service.receberPagamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PagamentoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizaStatusDTO dto) {
        PagamentoResponseDTO response = service.atualizarStatus(id, dto.status());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PagamentoResponseDTO>> listarPagamentos(
            @RequestParam(required = false) Integer codigoDebito,
            @RequestParam(required = false) String cpfCnpj,
            @RequestParam(required = false) StatusPagamento status) {
        List<PagamentoResponseDTO> response = service.listarComFiltros(codigoDebito, cpfCnpj, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarPagamento(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}