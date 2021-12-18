
package com.egg.libraryV12.controllers;

import com.egg.libraryV12.entities.UserLog;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.services.UserLogService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/foto")
public class PhotoController {

    @Autowired
    private UserLogService ulService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USUARIO')")
    @GetMapping("/usuario/{id}")
    public ResponseEntity<byte[]> photoUser(@PathVariable String id) {

        try {
            UserLog user = ulService.searchById(id);
            if (user.getFoto() == null) {
                throw new ExceptionService("El usuario no tiene una foto asignada.");
            }
            byte[] photo = user.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(photo, headers, HttpStatus.OK);
        } catch (ExceptionService ex) {
            Logger.getLogger(PhotoController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

  
}
