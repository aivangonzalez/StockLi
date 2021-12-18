
package com.egg.libraryV12.services;


import com.egg.libraryV12.entities.PublishingHouse;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.repositories.PublishingHouseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishingHouseService {
    
    @Autowired
    private PublishingHouseRepository phr;
    
    @Transactional
    public void createPHouse(String name) throws ExceptionService {
        validateCreate(name);
        
        PublishingHouse p = new PublishingHouse();
        p.setNombre(name.toUpperCase());
        phr.save(p);
    }
    
    @Transactional
    public void modifyPHouse(String id, String name) throws ExceptionService {
        validateModify(id, name);
        
        PublishingHouse p = phr.findById(id).get();
        p.setNombre(name.toUpperCase());
        phr.save(p);
    }
    
    @Transactional
    public void dropPHouse(String id) throws ExceptionService {
        validateId(id);
        
        PublishingHouse p = phr.findById(id).get();
        p.setAlta(false);
        phr.save(p);
    }
    
    @Transactional
    public void highPHouse(String id) throws ExceptionService {
        validateId(id);
        
        PublishingHouse p = phr.findById(id).get();
        p.setAlta(true);
        phr.save(p);
    }
    
    public List<PublishingHouse> searchAll() throws ExceptionService {
        return phr.findAll();
    }
    
    public PublishingHouse searchById(String id) throws ExceptionService {
        validateId(id);
        return phr.findById(id).get();
    }
    
    //---------------------------Validaciones-----------------------------------
    
    public void validateCreate(String name) throws ExceptionService {
        if (name == null || name.trim().isEmpty()) {
            throw new ExceptionService("Error al ingresar el nombre.");
        }
        PublishingHouse p = phr.searchByName(name.toUpperCase());
        if (p != null) {
            throw new ExceptionService("Ya se encuentra una editorial con el mismo nombre.");
        }
    }

    public void validateModify(String id, String name) throws ExceptionService {
        validateId(id);
        if (name.trim().isEmpty()) {
            throw new ExceptionService("Error al ingresar el nombre.");
        }
    }

    //Valido que haya una editorial con el id pasado por parametro
    public void validateId(String id) throws ExceptionService {
        Optional<PublishingHouse> answer = phr.findById(id);
        if (!answer.isPresent()) {
            throw new ExceptionService("No se encontró ninguna editorial.");
        }
    }
}
