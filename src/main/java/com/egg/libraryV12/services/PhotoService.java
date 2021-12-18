package com.egg.libraryV12.services;

import com.egg.libraryV12.entities.Photo;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.repositories.PhotoRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository pr;

    @Transactional
    public Photo savePhoto(MultipartFile photo) throws ExceptionService, Exception {
        if (photo == null) {
            return null;
        }
        
        
        Photo p = new Photo();
        p.setMime(photo.getContentType());
        
        p.setNombre(photo.getName());
        p.setContenido(photo.getBytes());

        return pr.save(p);
//        if (photo != null && !photo.isEmpty()) {
//            try {
//                Photo p = new Photo();
//                p.setMime(photo.getContentType());
//                p.setNombre(photo.getName());
//                p.setContenido(photo.getBytes());
//
//                return pr.save(p);
//            } catch (Exception e) {
//                System.out.println("No se pudo persistir la fotografia.");
//                System.err.println("No se pudo persistir la fotografia.");
//            }
//        }
//        return null;
    }

    @Transactional
    public Photo modifyPhoto(String id, MultipartFile photo) throws ExceptionService, Exception {

        if (photo == null) {
            return null;
        }

        if (id != null) {
            Optional<Photo> optional = pr.findById(id);
            if (optional.isPresent()) {
                Photo p = optional.get();
                p.setNombre(photo.getName());
                p.setMime(photo.getContentType());
                p.setContenido(photo.getBytes());

                return pr.save(p);
            } 
        }
        return null;
    }

    //--------------------validaciones
    public void validateId(String id) throws ExceptionService {
        if (id.trim().isEmpty() || id == null) {
            throw new ExceptionService("Id nulo o vacio.");
        }
        Optional<Photo> p = pr.findById(id);
        if (!p.isPresent()) {
            throw new ExceptionService("No se encuetra una foto con el id ingresado.");
        }
    }
    
    public void validatePhoto(MultipartFile photo) throws ExceptionService{
        if(!photo.getContentType().equals("image/jpeg") || !photo.getContentType().equals("image/png")){
            throw new ExceptionService("El formato del archivo debe ser .jpge o .png");
        } 
    }
    
    
}
