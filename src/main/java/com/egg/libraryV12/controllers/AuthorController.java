package com.egg.libraryV12.controllers;

import com.egg.libraryV12.entities.Author;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.AuthorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author LENOVO
 */
@Controller
@RequestMapping("/autor")
public class AuthorController {

    @Autowired
    private AuthorService aService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registro")
    public String form() {
        return "form-author";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registro")
    public String save(ModelMap model, @RequestParam(required = true) String nombre) {
        try {
            aService.createAuthor(nombre);
            model.put("exito", "Autor creado exitosamente!!");
            return "form-author";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "form-author";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/lista")
    public String list(ModelMap model) {
        try {
            model.addAttribute("autores", aService.searchAll());
            return "list-author";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "list-author";
        }
    }

    //en ninguno de los dos metodos de los botones para dar de baja en el controlador, le coloque en los parametros el ModelMap
    //sin embargo funciono bien ya que no lo estoy implementando. al mismo tiempo en los metodos drop y hight del servicio de
    //autor tampoco retorno nada a diferencia de los metodos que el profesor fiordelisi hizo, y de igual manera funciona bien
    //ya que el controlador no requiere que el servicio devuelva nada
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/baja/{id}")
    public String drop(@PathVariable String id) {
        try {
            aService.dropAuthor(id);
            return "redirect:/autor/lista";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/alta/{id}")
    public String high(@PathVariable String id) {
        try {
            aService.highAuthor(id);
            return "redirect:/autor/lista";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model) {
        try {
            model.put("autor", aService.searchById(id));
            return "modify-author";
        } catch (Exception e) {
            return "modify-author";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model, @RequestParam(required = false) String nombre) {
        try {
            aService.modifyAuthor(id, nombre);
            model.put("exito", "Autor/a modificado/a exitosamente!!");
            model.put("autor", aService.searchById(id));
            return "modify-author";
        } catch (ExceptionService e) {
            try {
                model.put("autor", aService.searchById(id));
                model.put("error", e.getMessage());
                return "modify-author";
            } catch (ExceptionService ex) {
                return "redirect:/autor/lista";
            }
        }
    }
}
