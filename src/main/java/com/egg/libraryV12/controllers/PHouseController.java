package com.egg.libraryV12.controllers;

import com.egg.libraryV12.entities.PublishingHouse;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.PublishingHouseService;
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


@Controller
@RequestMapping("/editorial")
public class PHouseController {

    @Autowired
    private PublishingHouseService phService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registro")
    public String form() {
        return "form-phouse";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registro")
    public String save(ModelMap model, @RequestParam(required = true) String nombre) {
        try {
            phService.createPHouse(nombre);
            model.put("exito", "Editorial creada exitosamente!!");
            return "form-phouse";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "form-phouse";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/lista")
    public String list(ModelMap model) {
        try {
            model.put("editoriales", phService.searchAll());
            return "list-phouse";
        } catch (Exception e) {

            model.put("error", e.getMessage());
            return "list-phouse";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/baja/{id}")
    public String drop(@PathVariable String id) {
        try {
            phService.dropPHouse(id);
            return "redirect:/editorial/lista";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta/{id}")
    public String high(@PathVariable String id) {
        try {
            phService.highPHouse(id);
            return "redirect:/editorial/lista";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model) {
        try {
            model.put("editorial", phService.searchById(id));
            return "modify-phouse";
        } catch (Exception e) {
            return "modify-phouse";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model, @RequestParam String nombre) {
        try {
            phService.modifyPHouse(id, nombre);
            model.put("exito", "Editorial modificada exitosamente!!");
            model.put("editorial", phService.searchById(id));
            return "modify-phouse";
        } catch (Exception e) {
            //Aca modelo nuevamente la editorial para poder mostrar el error
            try {
                model.put("editorial", phService.searchById(id));
                model.put("error", e.getMessage());
                return "modify-phouse";
            } catch (ExceptionService ex) {
                System.out.println(ex.getMessage());
                return "redirect:/editorial/registro";
            }
        }
    }
}
