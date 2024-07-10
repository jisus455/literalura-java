package com.jisus.literalura.repository;

import com.jisus.literalura.models.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloContainsIgnoreCase(String titulo);

    List<Libro> findByIdiomasContainsIgnoreCase(String idioma);

    @Query("SELECT l FROM Libro l ORDER BY l.descargas DESC LIMIT 3")
    List<Libro> top3Libros();
}
