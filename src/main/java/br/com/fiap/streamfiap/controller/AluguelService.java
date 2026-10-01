package br.com.fiap.streamfiap.service;

import br.com.fiap.streamfiap.exception.ClassificacaoIndicativaException;
import br.com.fiap.streamfiap.exception.ConteudoIndisponivelException;
import br.com.fiap.streamfiap.exception.ConteudoNaoEncontradoException;
import br.com.fiap.streamfiap.exception.CreditosInsuficientesException;
import br.com.fiap.streamfiap.exception.UsuarioNaoEncontradoException;
import br.com.fiap.streamfiap.model.Conteudo;
import br.com.fiap.streamfiap.model.Usuario;
import br.com.fiap.streamfiap.repository.ConteudoRepository;
import br.com.fiap.streamfiap.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AluguelService {

    private final UsuarioRepository usuarioRepository;
    private final ConteudoRepository conteudoRepository;

    public AluguelService(UsuarioRepository usuarioRepository, ConteudoRepository conteudoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.conteudoRepository = conteudoRepository;
    }

    public Usuario processarAluguel(Long usuarioId, Long conteudoId) {
        Usuario usuario = buscarUsuarioOuFalhar(usuarioId);
        Conteudo conteudo = buscarConteudoOuFalhar(conteudoId);

        validarClassificacao(usuario, conteudo);
        validarDisponibilidade(conteudo);
        validarCreditos(usuario, conteudo);

        Usuario usuarioAtualizado = usuario.alugar(conteudo);

        conteudoRepository.save(conteudo);
        return usuarioRepository.save(usuarioAtualizado);
    }

    private Usuario buscarUsuarioOuFalhar(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado: " + usuarioId));
    }

    private Conteudo buscarConteudoOuFalhar(Long conteudoId) {
        return conteudoRepository.findById(conteudoId)
                .orElseThrow(() -> new ConteudoNaoEncontradoException("Conteúdo não encontrado: " + conteudoId));
    }

    private void validarClassificacao(Usuario usuario, Conteudo conteudo) {
        if (usuario.getIdade() < conteudo.getClassificacaoEtaria()) {
            throw new ClassificacaoIndicativaException("Usuário de " + usuario.getIdade()
                    + " anos não pode assistir a " + conteudo.getTitulo()
                    + " (classificação " + conteudo.getClassificacaoEtaria() + " anos)");
        }
    }

    private void validarDisponibilidade(Conteudo conteudo) {
        if (!conteudo.isDisponivel()) {
            throw new ConteudoIndisponivelException("Conteúdo indisponível para aluguel: " + conteudo.getTitulo());
        }
    }

    private void validarCreditos(Usuario usuario, Conteudo conteudo) {
        double preco = conteudo.calcularPrecoAluguel();
        if (!usuario.temCreditosSuficientes(preco)) {
            throw new CreditosInsuficientesException("Créditos insuficientes para alugar " + conteudo.getTitulo());
        }
    }
}