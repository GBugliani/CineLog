package com.example.CineLog.controller;

import com.example.CineLog.model.Usuario;
import com.example.CineLog.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioController(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping("/registro")
    public String registroForm() { return "registro"; }

    @PostMapping("/registro")
    public String registrar(@RequestParam String username,
                            @RequestParam String password,
                            RedirectAttributes redirectAttributes) {
        String nome = username == null ? "" : username.trim();

        if (nome.isBlank()) {
            redirectAttributes.addFlashAttribute("erro", "Informe um nome de usuário.");
            return "redirect:/registro";
        }

        if (password == null || password.length() < 6) {
            redirectAttributes.addFlashAttribute("erro", "A senha precisa ter pelo menos 6 caracteres.");
            return "redirect:/registro";
        }

        if (repo.findByUsername(nome).isPresent()) {
            redirectAttributes.addFlashAttribute("erro", "Este nome de usuário já está em uso.");
            return "redirect:/registro";
        }

        Usuario u = new Usuario();
        u.setUsername(nome);
        u.setPassword(encoder.encode(password));
        repo.save(u);

        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String loginForm() { return "login"; }
}