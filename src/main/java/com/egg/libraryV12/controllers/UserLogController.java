package com.egg.libraryV12.controllers;

import com.egg.libraryV12.entities.UserLog;
import com.egg.libraryV12.entities.Zone;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.ClientService;
import com.egg.libraryV12.services.UserLogService;
import com.egg.libraryV12.services.ZoneService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/usuario")
public class UserLogController {

    @Autowired
    private UserLogService ulService;

    @Autowired
    private ZoneService zService;
    
    @Autowired
    private ClientService cService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/editar-perfil/{id}")
    public String modifyProfile(@PathVariable String id, HttpSession session, ModelMap model) {
        UserLog login = (UserLog) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        try {
            model.put("zonas", zService.searchAll());
            model.put("usuario", ulService.searchById(id));
            model.put("cliente", ulService.searchById(id).getCliente());
            
            
        } catch (ExceptionService e) {
            model.put("error", e.getMessage());
        }
        return "profile";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @PostMapping("/actualizar-perfil/{id}")
    public String modifyProfilePost(@PathVariable String id, HttpSession session,
            @RequestParam String nombre, @RequestParam String apellido, @RequestParam String username,
            @RequestParam(required = false) String zonaId, MultipartFile foto, @RequestParam String password, @RequestParam String password2, ModelMap model) {
        UserLog user = null;
        try {
            UserLog login = (UserLog) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equalsIgnoreCase(id)) {
            return "redirect:/inicio";
        }

            ulService.modify(id, nombre, apellido, username, zonaId, foto, password, password2);

            user = ulService.searchById(id);
            session.setAttribute("usuariosession", user);
            
            model.put("exito", "Se actualizó de forma correcta tu perfil!!");
            model.put("usuario", ulService.searchById(id));
            model.put("zonas", zService.searchAll());
            model.put("cliente", ulService.searchById(id).getCliente());

            return "profile";
        } catch (Exception e) {
            try {
                
                model.put("usuario", ulService.searchById(id));
                model.put("zonas", zService.searchAll());
                model.put("nombre", nombre);
                model.put("apellido", apellido);
                model.put("username", username);
                model.put("password", password);
                model.put("password2", password2);
                model.put("error", e.getMessage());
                model.put("cliente", ulService.searchById(id).getCliente());

                return "profile";
            } catch (ExceptionService ex) {
                System.out.println(ex.getMessage());
                return "redirect:/inicio";
            }
//        } catch (ExceptionService ex) {
//            model.put("error", ex.getMessage());
//            ex.getMessage();
//            System.out.println(ex.getStackTrace());
//            return "index";
        
//            try {
//                model.put("zonas", zService.searchAll());
//            } catch (ExceptionService ex1) {
//                model.put("error", ex.getMessage());
//            }
//            model.put("error", ex.getMessage());
//            model.put("perfil", user);
//
//            return "profile.html";
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/baja/{id}")
    public String drop(@PathVariable String id, ModelMap model) {
        try {
            ulService.dropUserLog(id);
            return "redirect:/admin/dashboard";
        } catch (ExceptionService e) {
            System.out.println(e.getMessage());
            return "redirect;/admin/dashboard";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta/{id}")
    public String high(@PathVariable String id, ModelMap model) {
        try {
            ulService.highUserLog(id);
            return "redirect:/admin/dashboard";
        } catch (ExceptionService e) {
            System.out.println(e.getMessage());
            return "redirect;/admin/dashboard";
        }
    }
}
