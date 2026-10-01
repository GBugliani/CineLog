package com.example.CineLog.controller;

import com.example.CineLog.model.FilmeSalvo;
import com.example.CineLog.model.Usuario;
import com.example.CineLog.repository.*;
import com.example.CineLog.service.OmdbService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CatalogoController {

    private final OmdbService omdb;
    private final FilmeSalvoRepository filmeRepo;
    private final UsuarioRepository usuarioRepo;

    public CatalogoController(OmdbService omdb, FilmeSalvoRepository filmeRepo, UsuarioRepository usuarioRepo) {
        this.omdb = omdb;
        this.filmeRepo = filmeRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping("/catalogo")
    public String catalogo(@AuthenticationPrincipal UserDetails user, Model model) {
        Usuario u = usuarioRepo.findByUsername(user.getUsername()).orElseThrow();

        List<FilmeSalvo> filmes = filmeRepo.findByUsuarioIdOrderByIdDesc(u.getId());
        model.addAttribute("filmes", filmes);
        model.addAttribute("totalFilmes", filmes.size());

        double media = filmes.stream()
            .filter(f -> f.getNota() != null)
            .mapToInt(FilmeSalvo::getNota)
            .average()
            .orElse(0.0);
        model.addAttribute("mediaNotas", media);

        return "catalogo";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String titulo,
                        Model model) {
        if (titulo != null && !titulo.isBlank()) {
            model.addAttribute("resultados", omdb.buscar(titulo));
        }
        return "buscar";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam String imdbId,
                         @RequestParam String titulo,
                         @RequestParam String poster,
                         @RequestParam Integer nota,
                         @RequestParam String comentario,
                         @AuthenticationPrincipal UserDetails user,
                         RedirectAttributes redirectAttributes) {
        Usuario u = usuarioRepo.findByUsername(user.getUsername()).orElseThrow();

        if (nota == null || nota < 1 || nota > 5) {
            redirectAttributes.addFlashAttribute("erro", "A nota deve estar entre 1 e 5.");
            return "redirect:/buscar";
        }

        if (!filmeRepo.existsByImdbIdAndUsuarioId(imdbId, u.getId())) {
            FilmeSalvo f = new FilmeSalvo();
            f.setImdbId(imdbId); f.setTitulo(titulo);
            f.setPoster(poster); f.setNota(nota);
            f.setComentario(comentario == null ? "" : comentario.trim()); f.setUsuario(u);
            filmeRepo.save(f);
            redirectAttributes.addFlashAttribute("sucesso", "Filme salvo no seu catálogo.");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Este filme já está no seu catálogo.");
        }
        return "redirect:/catalogo";
    }

    @PostMapping("/remover/{id}")
    public String remover(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails user,
                          RedirectAttributes redirectAttributes) {
        Usuario u = usuarioRepo.findByUsername(user.getUsername()).orElseThrow();

        filmeRepo.findByIdAndUsuarioId(id, u.getId()).ifPresentOrElse(filmeRepo::delete, () ->
            redirectAttributes.addFlashAttribute("erro", "Filme não encontrado no seu catálogo."));

        return "redirect:/catalogo";
    }
}