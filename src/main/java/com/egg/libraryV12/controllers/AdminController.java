
package com.egg.libraryV12.controllers;

import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.BookService;
import com.egg.libraryV12.services.ClientService;
import com.egg.libraryV12.services.LoanService;
import com.egg.libraryV12.services.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserLogService ulService;
    
    @Autowired
    private ClientService cService;
    
    @Autowired
    private LoanService lService;
    
    @Autowired
    private BookService bService;
    
    @GetMapping("/dashboard")
    public String dashboard(ModelMap model){
        try {
            model.put("usuarios", ulService.searchAll());
            model.put("clientes", cService.searchAll());
            model.put("prestamos", lService.searchAll());
            return "homepage-admin";
        } catch (ExceptionService e) {
            model.put("error", e.getMessage());
            return "homepage-admin";
        }
        
    }
    
    
    @GetMapping("/cambiar_rol/{id}")
    public String changeRol(@PathVariable String id, ModelMap model){
        try {
            ulService.changeRol(id);
            return "redirect:/admin/dashboard";
        } catch (ExceptionService e) {
            System.out.println(e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }
    
    
    @GetMapping("/baja_cliente/{id}")
    public String dropClient(@PathVariable String id) {
        try {
            cService.dropClient(id);
            return "redirect:/admin/dashboard";
        } catch (ExceptionService e) {
            return "redirect:/";
        }
    }
    
    @GetMapping("/alta_cliente/{id}")
    public String highClient(@PathVariable String id) {
        try {
            cService.highClient(id);
            return "redirect:/admin/dashboard";
        } catch (ExceptionService e) {
            return "redirect:/";
        }
    }
    
    @GetMapping("/baja_prestamo/{id}")
    public String dropLoan(@PathVariable String id) {
        try {
            lService.dropLoan(id);
            
            //Este metodo me actualiza la cantidad disponible de libros
            bService.modifyQuantityDropLoan(lService.searchById(id).getBook());
            return "redirect:/admin/dashboard";
        } catch (ExceptionService e) {
            return "redirect:/";
        }
    }
    
    @GetMapping("/alta_prestamo/{id}")
    public String highLoan(@PathVariable String id) {
        try {
            lService.highLoan(id);
            
            //Este metodo me actualiza la cantidad disponible de libros
            bService.modifyQuantityHighLoan(lService.searchById(id).getBook());
            return "redirect:/admin/dashboard";
        } catch (ExceptionService e) {
            return "redirect:/";
        }
    }
    
}
