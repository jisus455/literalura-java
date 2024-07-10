package com.jisus.literalura.principal;

import com.jisus.literalura.models.dto.AutorDTO;
import com.jisus.literalura.models.model.Autor;
import com.jisus.literalura.models.datos.DatosApi;
import com.jisus.literalura.models.datos.DatosLibro;
import com.jisus.literalura.models.model.Libro;
import com.jisus.literalura.models.dto.LibroDTO;
import com.jisus.literalura.repository.AutorRepository;
import com.jisus.literalura.repository.LibroRepository;
import com.jisus.literalura.service.ConsumoApi;
import com.jisus.literalura.service.ConvierteDatos;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoAPI = new ConsumoApi();
    private ConvierteDatos conversor = new ConvierteDatos();

    private final String URL_SEARCH = "http://gutendex.com/books/?search=";

    private boolean continuar = true;
    private int opcion;

    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraMenu() {
        while (continuar) {
            System.out.println("--------------------------------------------------");
            System.out.println("""
                    LITERALURA
                    
                    [0] - Buscar libro por titulo
                    [1] - Mostrar todos los libros registrados
                    [2] - Mostrar libros por idioma
                    [3] - Mostrar todos los autores registrados
                    [4] - Mostrar autores vivos en un determinado año
                    [5] - Mostrar top 3 libros mas descargados
                    [6] - Cerrar programa """);
            System.out.println("--------------------------------------------------");
            System.out.println("Seleccione una opcion");
            try {
                opcion = teclado.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("--------------------");
                System.out.println("Opcion invalida");
                System.out.println("--------------------");
                teclado.next();
                continue;
            }

            switch (opcion) {
                case 0:
                    buscarPorTitulo();
                    break;
                case 1:
                    mostrarLibrosRegistrados();
                    break;
                case 2:
                    mostrarLibrosPorIdioma();
                    break;
                case 3:
                    mostrarAutoresRegistrados();
                    break;
                case 4:
                    mostrarAutoresPorAnio();
                    break;
                case 5:
                    mostrarLibrosMasDescargados();
                    break;
                case 6:
                    this.continuar = false;
                    System.out.println("--------------------");
                    System.out.println("Cerrando programa..");
                    System.out.println("--------------------");
                    break;
                default:
                    System.out.println("--------------------");
                    System.out.println("Opcion incorrecta");
                    System.out.println("--------------------");
            }
        }
    }

    private void buscarPorTitulo() {
        System.out.println("Escriba el titulo");
        teclado.nextLine();
        String titulo = teclado.nextLine();

        try {
            var json = consumoAPI.obtenerDatos(URL_SEARCH + titulo.replace(" ", "+"));
            var datos = conversor.obtenerDatos(json, DatosApi.class);

            DatosLibro datosLibro = datos.result().get(0);
            Libro libro = new Libro(datosLibro);

            LibroDTO libroDto = new LibroDTO(libro.getTitulo(), libro.getAutores().get(0).getNombre(), libro.getIdiomas(), libro.getDescargas());
            System.out.println("------------------------------");
            System.out.println("Libro");
            System.out.println("Titulo: " + libroDto.titulo());
            System.out.println("Autores: " + libroDto.autores());
            System.out.println("Idiomas: " + libroDto.idiomas());
            System.out.println("Descargas: " + libroDto.descargas());
            System.out.println("------------------------------");

            List<Autor> autores = datosLibro.autores().stream()
                    .map(Autor::new)
                    .collect(Collectors.toList());
            libro.setAutores(autores);

            Optional<Libro> libroBuscado = libroRepository.findByTituloContainsIgnoreCase(libroDto.titulo());
            if(libroBuscado.isPresent()) {
                System.out.println("-------------------------");
                System.out.println("El libro ya esta guardado");
                System.out.println("-------------------------");
            } else {
                System.out.println("--------------------");
                System.out.println("Guardando libro..");
                System.out.println("--------------------");
                libroRepository.save(libro);
            }

        } catch (IndexOutOfBoundsException e) {
            System.out.println("--------------------");
            System.out.println("Libro no encontrado");
            System.out.println("--------------------");
        }
    }

    private void mostrarLibrosPorIdioma() {
        System.out.println("""
                [0] - Español
                [1] - Ingles
                [2] - Frances
                [3] - Portugues
                """);
        System.out.println("Seleccione una opcion");
        try {
            opcion = teclado.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("--------------------");
            System.out.println("Opcion invalida");
            System.out.println("--------------------");
            teclado.next();
        }

        List<Libro> librosPorIdioma;
        List<LibroDTO> libros;

        switch (opcion) {
            case 0:
                librosPorIdioma = libroRepository.findByIdiomasContainsIgnoreCase("es");
                libros = convierteDatosLibro(librosPorIdioma);
                for(LibroDTO libro : libros) {
                    System.out.println("------------------------------");
                    System.out.println("Libro");
                    System.out.println("Titulo: " + libro.titulo());
                    System.out.println("Autores: " + libro.autores());
                    System.out.println("Idiomas: " + libro.idiomas());
                    System.out.println("Descargas: " + libro.descargas());
                    System.out.println("------------------------------");
                }
                break;
            case 1:
                librosPorIdioma = libroRepository.findByIdiomasContainsIgnoreCase("en");
                libros = convierteDatosLibro(librosPorIdioma);
                for(LibroDTO libro : libros) {
                    System.out.println("------------------------------");
                    System.out.println("Libro");
                    System.out.println("Titulo: " + libro.titulo());
                    System.out.println("Autores: " + libro.autores());
                    System.out.println("Idiomas: " + libro.idiomas());
                    System.out.println("Descargas: " + libro.descargas());
                    System.out.println("------------------------------");
                }
                break;
            case 2:
                librosPorIdioma = libroRepository.findByIdiomasContainsIgnoreCase("fr");
                libros = convierteDatosLibro(librosPorIdioma);
                for(LibroDTO libro : libros) {
                    System.out.println("------------------------------");
                    System.out.println("Libro");
                    System.out.println("Titulo: " + libro.titulo());
                    System.out.println("Autores: " + libro.autores());
                    System.out.println("Idiomas: " + libro.idiomas());
                    System.out.println("Descargas: " + libro.descargas());
                    System.out.println("------------------------------");
                }
                break;
            case 3:
                librosPorIdioma = libroRepository.findByIdiomasContainsIgnoreCase("pt");
                libros = convierteDatosLibro(librosPorIdioma);
                for(LibroDTO libro : libros) {
                    System.out.println("------------------------------");
                    System.out.println("Libro");
                    System.out.println("Titulo: " + libro.titulo());
                    System.out.println("Autores: " + libro.autores());
                    System.out.println("Idiomas: " + libro.idiomas());
                    System.out.println("Descargas: " + libro.descargas());
                    System.out.println("------------------------------");
                }
                break;
            default:
                System.out.println("--------------------");
                System.out.println("Opcion incorrecta");
                System.out.println("--------------------");
        }
    }

    private void mostrarLibrosRegistrados() {
        System.out.println("Libros");
        List<Libro> librosRegistrados = libroRepository.findAll();
        List<LibroDTO> libros = convierteDatosLibro(librosRegistrados);
        for(LibroDTO libro : libros) {
            System.out.println("------------------------------");
            System.out.println("Libro");
            System.out.println("Titulo: " + libro.titulo());
            System.out.println("Autores: " + libro.autores());
            System.out.println("Idiomas: " + libro.idiomas());
            System.out.println("Descargas: " + libro.descargas());
            System.out.println("------------------------------");
        }
    }

    private void mostrarAutoresRegistrados() {
        System.out.println("Autores");
        List<Autor> autoresRegistrados = autorRepository.findAll();
        List<AutorDTO> autores = convierteDatosAutor(autoresRegistrados);
        for(AutorDTO autor : autores) {
            System.out.println("------------------------------");
            System.out.println("Autor");
            System.out.println("Nombre: " + autor.nombre());
            System.out.println("Nacimiento: " + autor.nacimiento());
            System.out.println("Fallecimiento: " + autor.fallecimiento());
            System.out.println("Libros: " + autor.libros());
            System.out.println("------------------------------");
        }
    }

    private void mostrarAutoresPorAnio() {
        System.out.println("Ingrese anio");
        try {
            int anio = teclado.nextInt();
            List<Autor> autoresPorAnio = autorRepository.buscarAutoresVivosPorAnio(anio);
            List<AutorDTO> autores = convierteDatosAutor(autoresPorAnio);
            for(AutorDTO autor : autores) {
                System.out.println("------------------------------");
                System.out.println("Autor");
                System.out.println("Nombre: " + autor.nombre());
                System.out.println("Nacimiento: " + autor.nacimiento());
                System.out.println("Fallecimiento: " + autor.fallecimiento());
                System.out.println("Libros: " + autor.libros());
                System.out.println("------------------------------");
            }
        } catch (InputMismatchException e) {
            System.out.println("--------------------");
            System.out.println("Año invalido");
            System.out.println("--------------------");
            teclado.next();
        }
    }

    private void mostrarLibrosMasDescargados() {
        List<Libro> top3Libros = libroRepository.top3Libros();
        List<LibroDTO> libros = convierteDatosLibro(top3Libros);
        for(LibroDTO libro : libros) {
            System.out.println("--------------------");
            System.out.println("Libro");
            System.out.println("Titulo: " + libro.titulo());
            System.out.println("Autores: " + libro.autores());
            System.out.println("Idiomas: " + libro.idiomas());
            System.out.println("Descargas: " + libro.descargas());
            System.out.println("--------------------");
        }
    }

    private List<LibroDTO> convierteDatosLibro(List<Libro> libros) {
        return libros.stream()
                .map(l -> new LibroDTO(l.getTitulo(), l.getAutores().get(0).getNombre(), l.getIdiomas(), l.getDescargas()))
                .collect(Collectors.toList());
    }

    private List<AutorDTO> convierteDatosAutor(List<Autor> autores) {
        return autores.stream()
                .map(a -> new AutorDTO(a.getNombre(), a.getNacimiento(), a.getFallecimiento(), a.getLibro().getTitulo()))
                .collect(Collectors.toList());
    }


}
