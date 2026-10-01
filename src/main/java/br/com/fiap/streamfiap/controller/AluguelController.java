package br.com.fiap.streamfiap.controller;

import br.com.fiap.streamfiap.model.Usuario;
import br.com.fiap.streamfiap.service.AluguelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alugueis")
public class AluguelController {

    private final AluguelService aluguelService;

    public AluguelController(AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }

    @PostMapping
    public ResponseEntity<Usuario> alugar(@RequestParam Long usuarioId, @RequestParam Long conteudoId) {
        Usuario usuarioAtualizado = aluguelService.processarAluguel(usuarioId, conteudoId);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}