package com.egg.libraryV12.controllers;

import com.egg.libraryV12.entities.Book;
import com.egg.libraryV12.entities.Client;
import com.egg.libraryV12.entities.Loan;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.BookService;
import com.egg.libraryV12.services.ClientService;
import com.egg.libraryV12.services.LoanService;
import com.egg.libraryV12.services.UserLogService;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/prestamo")
public class LoanController {

    @Autowired
    private LoanService lService;

    @Autowired
    private BookService bService;

    @Autowired
    private ClientService cService;

    @Autowired
    private UserLogService ulService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/registro/{id}")
    public String form(@PathVariable String id, ModelMap model) {
        try {
            model.addAttribute("cliente", cService.searchById(id));
            model.addAttribute("libros", bService.searchAllAvailable());

            return "form-loan";
        } catch (ExceptionService e) {
            model.put("error", e.getMessage());
            return "homepage";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @PostMapping("/registro/{id}")//si lo hago con una select me va a traer un objeto cliente por ende no hace falta colocar el pathVariable ni el id
    public String save(@PathVariable String id, ModelMap model, @RequestParam(required = false) Book libro, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion) {
        try {
            Client client = cService.searchById(id);
            lService.createLoan(fechaDevolucion, libro, client);
            bService.modifyQuantityHighLoan(libro);
            model.put("exito", "El prestamo fue creado exitosamente!! Ahora queda disfrutar del libro!!");
            return "form-loan";
        } catch (ExceptionService e) {
            try {
                model.addAttribute("cliente", cService.searchById(id));
                model.addAttribute("libros", bService.searchAllAvailable());
                model.put("error", e.getMessage());
                return "form-loan";
            } catch (Exception ex) {
                System.out.println(ex.getStackTrace());
                return "form-loan";
            }
        }
    }

    //es el id del cliente
    //este metodo va a tener que traerme los prestamos segun el cliente logueado
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/lista/{id}")
    public String listByClient(@PathVariable String id, ModelMap model) {
        try {
            lService.automaticLow(); //metodo para dar de baja automatica cuando es la fecha de devolucion o posterior a la misma
            model.put("prestamosSegunCliente", lService.searchByClient(id)); //busco primero al usuario y despues al id de su atributo cliente
            return "list-loan";
        } catch (ExceptionService e) {
            return "redirect:/";
        }
    }

//    @GetMapping("/lista")
//    public String listAll(ModelMap model) {
//        try {
//            lService.automaticLow(); //metodo para dar de baja automatica cuando es la fecha de devolucion o posterior a la misma
//            model.addAttribute("prestamos", lService.searchAll());
//            return "list-loan";
//        } catch (ExceptionService e) {
//            return "redirect:/";
//        }
//    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/baja/{id}")
    public String drop(@PathVariable String id, ModelMap model) {
        try {
            lService.dropLoan(id);
            Loan loan = lService.searchById(id);
            
            //Este metodo me actualiza la cantidad disponible de libros
            bService.modifyQuantityDropLoan(loan.getBook());
            model.addAttribute("cliente", cService.searchById(loan.getClient().getId()));
            model.addAttribute("libros", bService.searchAllAvailable());
            return "redirect:/prestamo/lista/" + loan.getClient().getId();
        } catch (ExceptionService e) {
            return "redirect:/";
        }
    }

    //este metodo no estoy seguro si dejarlo ya que el cliente una vez que da de baja no tendria la oportunidad de
    //volver a dar el alta del mismo prestamo sino que deberia pedir otro prestamo
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
//    @GetMapping("/alta/{id}")
//    public String high(@PathVariable String id) {
//        try {
//            lService.highLoan(id);
//            return "redirect:/prestamo/lista";
//        } catch (ExceptionService e) {
//            return "redirect:/";
//        }
//    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/modificar/{id}")
    public String modify(@PathVariable String id, ModelMap model) {
        try {
            model.addAttribute("prestamo", lService.searchById(id));
            return "modify-loan";
        } catch (ExceptionService e) {
            return "homepage";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @PostMapping("/modificar/{id}")
    public String modify(@PathVariable String id, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, ModelMap model) {
        try {
            lService.modifyLoan(id, fechaDevolucion);
            lService.automaticLow(); //metodo para dar de baja automatica cuando es la fecha de devolucion o ya ha pasado el dia de devolución
            model.put("exito", "La fecha de devolucion se modifico correctamente!!");
            model.put("prestamo", lService.searchById(id));
            return "modify-loan";
        } catch (ExceptionService e) {
            try {
                model.put("prestamo", lService.searchById(id));
                model.put("error", e.getMessage());
                return "modify-loan";
            } catch (ExceptionService ex) {
                return "homepage";
            }
        }
    }

}
