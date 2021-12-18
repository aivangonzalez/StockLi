package com.egg.libraryV12.controllers;

import com.egg.libraryV12.entities.Author;
import com.egg.libraryV12.entities.Book;
import com.egg.libraryV12.entities.PublishingHouse;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.AuthorService;
import com.egg.libraryV12.services.BookService;
import com.egg.libraryV12.services.PublishingHouseService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/libro")
public class BookController {

    @Autowired
    private BookService bService;

    @Autowired
    private AuthorService aService;

    @Autowired
    private PublishingHouseService phService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registro")
    public String form(ModelMap model) {
        try {
            model.addAttribute("autores", aService.searchAll());
            model.addAttribute("editoriales", phService.searchAll());
            return "form-books";
        } catch (ExceptionService ex) {
            model.put("error", ex.getMessage());
            return "index";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registro")
    public String save(ModelMap model, /*@RequestParam */ Long isbn, @RequestParam String titulo, /*@RequestParam*/ Integer anio,
            /*@RequestParam*/ Integer ejemplares, @RequestParam(required = false) Author autor, @RequestParam(required = false) PublishingHouse editorial) {
        try {
            bService.createBook(isbn, titulo, anio, ejemplares, autor, editorial);
            model.put("exito", "Libro creado exitosamente!!");
            return "form-books";
        } catch (ExceptionService e) {
            try {
                model.addAttribute("autores", aService.searchAll());
                model.addAttribute("editoriales", phService.searchAll());
                model.put("error", e.getMessage());
                return "form-books";
            } catch (Exception ex) {
                System.out.println(ex.getStackTrace());
                return "form-books";
            }
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/lista")
    public String listBooks(ModelMap model) {
        try {
            model.addAttribute("libros", bService.searchAll());
            return "list-books";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "list-books";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/baja/{id}")
    public String drop(@PathVariable String id) {
        try {
            bService.dropBook(id);
            return "redirect:/libro/lista";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta/{id}")
    public String high(@PathVariable String id) {
        try {
            bService.highBook(id);
            return "redirect:/libro/lista";
            //me redirecciona al metodo get de este controlador que se encarga de volver a cargar la lista con el atributo
            //alta modificado
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model) {
        try {
            model.addAttribute("autores", aService.searchAll());
            model.addAttribute("editoriales", phService.searchAll());
            model.put("libro", bService.searchById(id));
            return "modify-book";
        } catch (Exception e) {
            return "modify-book";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model, /*@RequestParam*/ Long isbn, @RequestParam String titulo, /*@RequestParam*/ Integer anio,
            /*@RequestParam*/ Integer ejemplares, @RequestParam(required = false) Author autor, @RequestParam(required = false) PublishingHouse editorial) {
        try {
            bService.modifyBook(id, isbn, titulo, anio, ejemplares, autor, editorial);
            model.put("exito", "El libro fue modificado exitosamente!!");
            model.put("libro", bService.searchById(id));
            return "modify-book";
        } catch (ExceptionService e) {
            try {
                model.put("libro", bService.searchById(id));
                model.put("error", e.getMessage());
                return "modify-book";
            } catch (Exception ex) {
                return "redirect:/libro/lista";
            }
        }
    }
}
