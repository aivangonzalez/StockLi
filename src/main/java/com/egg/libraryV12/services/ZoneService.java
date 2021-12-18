package com.egg.libraryV12.services;

import com.egg.libraryV12.entities.Zone;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.repositories.ZoneRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zr;

    @Transactional
    public void save(String name, String description) throws ExceptionService {
        validateCreate(name.toUpperCase());
        Zone z = new Zone();
        z.setNombre(name.toUpperCase());
        z.setDescripcion(validateDescription(description));
        zr.save(z);
    }

    @Transactional
    public void modifyZone(String id, String name, String description) throws ExceptionService {
        validateModify(id, name, description);
        Zone z = zr.findById(id).get();
        z.setNombre(name.toUpperCase());
        z.setDescripcion(description);
        
        zr.save(z);
    }

    
    public List<Zone> searchAll() throws ExceptionService {
        return zr.findAll();
    }
    
    public Zone searchById(String id) throws ExceptionService {
        validateId(id);
        return zr.findById(id).get();
    }
    
    
    //------------------Validaciones
    
    public void validateCreate(String name) throws ExceptionService {
        if (name == null || name.trim().isEmpty()) {
            throw new ExceptionService("Error al ingresar el nombre.");
        }
        Zone z = zr.searchByName(name.toUpperCase());
        if (z != null) {
            throw new ExceptionService("Ya se encuentra un/a autor/a con el mismo nombre.");
        }
        
    }

    public String validateDescription(String description) throws ExceptionService {
        if(description == null){
            return "Sin descripción";
        } else {
            return description;
        }
    }    
    
    public void validateModify(String id, String name, String description)throws ExceptionService {
        validateId(id);
        validateDescription(description);
        if(name == null || name.trim().isEmpty()){
            throw new ExceptionService("Error al ingresar el nombre.");
        }
    }
    
    public void validateId(String id) throws ExceptionService {
        Optional<Zone> answer = zr.findById(id);
        if (!answer.isPresent()) {
            throw new ExceptionService("No se encontró ningun autor.");
        }
    }
}
