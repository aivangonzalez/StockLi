
package com.egg.libraryV12.controllers;

import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.ZoneService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//DUDAS SOBRE SI VOY A DEJAR ESTE CONTROLADOR YA QUE NO SERIA MALA IDEA PODER INGRESAR LA ZONA 
//DIRECTAMENTE DESDE LA BASE DE DATOS
@Controller
@RequestMapping("/zona")
public class ZoneController {
    
    @Autowired
    private ZoneService zService;
    
    @GetMapping("/registro")
    public String form(ModelMap model){
        return "form-zone";
    }
    
    @PostMapping("/registro")
    public String save(ModelMap model, @RequestParam String nombre, @RequestParam(required = false) String descripcion){
        try {
            zService.save(nombre, descripcion);
            model.put("exito", "La zona se registro correctamente!!");
            return "form-zone";
        } catch (ExceptionService e) {
            model.put("error", e.getMessage());
            return "form-zone";
        }
    }
    
    //zona no tiene alta o baja
    
    @GetMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model){
        try {
            model.put("zona", zService.searchById(id));
            return "modify-zone";
        } catch (ExceptionService e) {
            return "redirect:/zona/lista"; 
        }
    }
    
    @PostMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model, @RequestParam String nombre, @RequestParam(required = false) String descripcion){
        try {
            zService.modifyZone(id, nombre, descripcion);
            model.put("exito", "La zona fue modificada correctamente!!");
            model.put("zona", zService.searchById(id));
            return "modify-zone";
        } catch (ExceptionService e) {
            try {
                model.put("zona", zService.searchById(id));
                model.put("error", e.getMessage());
                return "modify-zone";
            } catch (Exception ex) {
                ex.getMessage();
                return "redirect:/zona/registro";
            }
        }
    }    
}
