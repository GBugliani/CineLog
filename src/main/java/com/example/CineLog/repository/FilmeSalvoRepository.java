// FilmeSalvoRepository.java
package com.example.CineLog.repository;

import com.example.CineLog.model.FilmeSalvo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FilmeSalvoRepository extends JpaRepository<FilmeSalvo, Long> {
    List<FilmeSalvo> findByUsuarioId(Long usuarioId);
    List<FilmeSalvo> findByUsuarioIdOrderByIdDesc(Long usuarioId);
    boolean existsByImdbIdAndUsuarioId(String imdbId, Long usuarioId);
    Optional<FilmeSalvo> findByIdAndUsuarioId(Long id, Long usuarioId);
}