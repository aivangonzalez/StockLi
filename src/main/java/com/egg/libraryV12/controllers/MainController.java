package com.egg.libraryV12.controllers;

import com.egg.libraryV12.entities.Zone;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.UserLogService;
import com.egg.libraryV12.services.ZoneService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private ZoneService zService;

    @Autowired
    private UserLogService ulService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USUARIO')")
    @GetMapping("/inicio")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap model) {
        if (error != null) {
            model.put("error", "Usuario o clave incorrectos.");
            model.put("error2", "En su defecto: Usuario dado de baja.");
        }
        if (logout != null) {
            model.put("logout", "Ha salido correctamente.");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registration(ModelMap model) {
        try {
            model.put("zonas", zService.searchAll());

            return "registration";
        } catch (ExceptionService e) {
            return "index";
        }
    }

    @PostMapping("/registro")
    public String registrationPost(ModelMap model, @RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String username, MultipartFile foto, @RequestParam(required = false) String zonaId,
            @RequestParam String password, @RequestParam String password2) {
        try {
            ulService.register(nombre, apellido, username, foto, zonaId, password, password2);
            model.put("exito", "Registro exitoso!! Ahora ingresa a tu perfil colocando el mail y la contraseña ingresada anteriormente!");
            return "login";
        } catch (ExceptionService e) {
            try {
                List<Zone> zonas = zService.searchAll();
                model.put("error", e.getMessage());
                model.put("zonas", zonas);
                model.put("nombre", nombre);
                model.put("apellido", apellido);
                model.put("username", username);
                model.put("password", password);
                model.put("password2", password2);
                return "registration";
            } catch (ExceptionService ex) {
                System.out.println(ex.getMessage());
                return "registration";
            }
        } catch (Exception ex) {
            model.put("error", ex.getMessage());
            ex.getMessage();
            System.out.println(ex.getStackTrace());
            return "index";
        }
    }

}
