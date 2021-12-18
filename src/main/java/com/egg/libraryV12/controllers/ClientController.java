package com.egg.libraryV12.controllers;

import com.egg.libraryV12.entities.Client;
import com.egg.libraryV12.entities.UserLog;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.ClientService;
import com.egg.libraryV12.services.UserLogService;
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

@Controller
@RequestMapping("/cliente")
public class ClientController {

    @Autowired
    private ClientService cService;

    @Autowired
    private UserLogService ulService;
    
    //El id que le pasa en la url es el id del usuario logueado
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/registro/{id}")
    public String form(@PathVariable String id, ModelMap model) {
        try {
            model.put("usuario", ulService.searchById(id));
            return "form-client";
        } catch (Exception e) {
            return "/homepage";
        }
    }

    //este id pasado por parametros no es el id del cliente sino es el id del usuario que quiere pedir prestado un libro
    //y para pedir un prestamo de libro es necesario ser cliente
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @PostMapping("/registro/{id}")
    public String save(@PathVariable String id, ModelMap model, Long dni, @RequestParam String telefono) {
        try {
            UserLog userlog = ulService.searchById(id); //aca busco el usuario logueado segun el id 
            Client client = cService.createClient(dni, userlog.getNombre(), userlog.getApellido(), telefono); //aca envio los datos para que luego se persistan
            ulService.setAtributeClient(id, client); //seteo el atributo cliente del usuario 
            model.put("exito", "El/la cliente fue registrado/a correctamente!!Ya podes pedir libros!!");
            model.put("usuario", ulService.searchById(id));
            return "redirect:/logout";
        } catch (ExceptionService e) {
            try {
                model.put("error", e.getMessage());
                model.put("usuario", ulService.searchById(id));
                return "form-client";
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return "/";
            }
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/lista")
    public String list(ModelMap model) {
        try {
            model.addAttribute("clientes", cService.searchAll());
            return "list-client";
        } catch (ExceptionService e) {
            return "list-client";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/baja/{id}")
    public String drop(@PathVariable String id) {
        try {
            cService.dropClient(id);
            return "redirect:/cliente/lista";
        } catch (ExceptionService e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/alta/{id}")
    public String high(@PathVariable String id) {
        try {
            cService.highClient(id);
            return "redirect:/cliente/lista";
        } catch (ExceptionService e) {
            return "redirect:/";
        }

    }

    //-----------------------------BAJA Y ALTA HECHA AUTONOMA POR EL CLIENTE---------
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/auto_baja/{id}")
    public String dropByClient(@PathVariable String id) {
        try {
            cService.dropClient(id);
            
            return "redirect:/logout";
        } catch (ExceptionService e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/auto_alta/{id}")
    public String highByClient(@PathVariable String id) {
        try {
            cService.highClient(id);
            return "redirect:/logout";
        } catch (ExceptionService e) {
            return "redirect:/";
        }

    }
    
    //-----------------------------BAJA Y ALTA HECHA AUTONOMA POR EL CLIENTE---------
    
    //este id es el id del cliente
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model) {
        try {
            model.addAttribute("cliente", cService.searchById(id));
            return "modify-client";
        } catch (ExceptionService e) {
            return "redirect:/";
        }
    }

    //este id es el id del cliente
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @PostMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model, Long dni, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String telefono) {
        try {
            cService.modifyClient(id, dni, nombre, apellido, telefono);
            model.put("exito", "El/la cliente fue modificado/a correctamente!!");
            model.put("cliente", cService.searchById(id));
            return "modify-client";
        } catch (ExceptionService e) {
            try {
                model.put("cliente", cService.searchById(id));
                model.put("error", e.getMessage());
                return "modify-client";
            } catch (Exception ex) {
                return "redirect:/";
            }
        }
    }

}
