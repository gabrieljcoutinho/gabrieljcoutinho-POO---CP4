package br.com.fiap.streamfiap.controller;

import br.com.fiap.streamfiap.exception.ConteudoNaoEncontradoException;
import br.com.fiap.streamfiap.model.Conteudo;
import br.com.fiap.streamfiap.model.Documentario;
import br.com.fiap.streamfiap.model.Filme;
import br.com.fiap.streamfiap.model.Serie;
import br.com.fiap.streamfiap.repository.ConteudoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conteudos")
public class ConteudoController {

    private final ConteudoRepository conteudoRepository;

    public ConteudoController(ConteudoRepository conteudoRepository) {
        this.conteudoRepository = conteudoRepository;
    }

    @GetMapping
    public List<Conteudo> listarTodos() {
        return conteudoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conteudo> buscarPorId(@PathVariable Long id) {
        Conteudo conteudo = conteudoRepository.findById(id)
                .orElseThrow(() -> new ConteudoNaoEncontradoException("Conteúdo não encontrado: " + id));
        return ResponseEntity.ok(conteudo);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Conteudo> listarPorCategoria(@PathVariable String categoria) {
        return conteudoRepository.findByCategoria(categoria);
    }

    @GetMapping("/{id}/preco-promocional")
    public double precoPromocional(@PathVariable Long id) {
        Conteudo conteudo = conteudoRepository.findById(id)
                .orElseThrow(() -> new ConteudoNaoEncontradoException("Conteúdo não encontrado: " + id));
        return conteudo.calcularPrecoPromocional();
    }

    @PostMapping("/filme")
    public ResponseEntity<Filme> cadastrarFilme(@RequestBody Filme filme) {
        Filme novoFilme = new Filme(
                filme.getTitulo(),
                filme.getCategoria(),
                filme.getDuracaoMinutos(),
                filme.getClassificacaoEtaria(),
                filme.isDisponivel(),
                filme.isEstreia()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(conteudoRepository.save(novoFilme));
    }

    @PostMapping("/serie")
    public ResponseEntity<Serie> cadastrarSerie(@RequestBody Serie serie) {
        Serie novaSerie = new Serie(
                serie.getTitulo(),
                serie.getCategoria(),
                serie.getDuracaoMinutos(),
                serie.getClassificacaoEtaria(),
                serie.getNumeroTemporadas()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(conteudoRepository.save(novaSerie));
    }

    @PostMapping("/documentario")
    public ResponseEntity<Documentario> cadastrarDocumentario(@RequestBody Documentario documentario) {
        Documentario novoDocumentario = new Documentario(
                documentario.getTitulo(),
                documentario.getCategoria(),
                documentario.getDuracaoMinutos(),
                documentario.getClassificacaoEtaria(),
                documentario.isDisponivel(),
                documentario.getTema()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(conteudoRepository.save(novoDocumentario));
    }
}