package com.egg.libraryV12.services;

import com.egg.libraryV12.entities.Author;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.repositories.AuthorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository ar;

    //Las anotaciones transactional sirven para separar los metodos que crean modificaciones en la base de datos
    //si por alguna razon no se realiza esa modificacion o salta algun error, transactional se encarga de regresar 
    //todo a la funcionalidad correcta
    @Transactional
    public void createAuthor(String name) throws ExceptionService {
        validateCreate(name);

        Author a = new Author();
        a.setNombre(name.toUpperCase());//Lo guardo con mayusculas para que luego lo pueda comparar facilmente
        System.out.println(a.toString());
        ar.save(a);
    }

    @Transactional
    public void modifyAuthor(String id, String name) throws ExceptionService {
        validateModify(id, name);

        Author a = ar.findById(id).get();
        a.setNombre(name.toUpperCase());
        ar.save(a);
    }

    @Transactional
    public void dropAuthor(String id) throws ExceptionService {
        validateId(id);
        Author a = ar.findById(id).get();
        a.setAlta(false);
        ar.save(a);
    }

    @Transactional
    public void highAuthor(String id) throws ExceptionService {
        validateId(id);
        Author a = ar.findById(id).get();
        a.setAlta(true);
        ar.save(a);
    }
    
    public List<Author> searchAll() throws ExceptionService {
        return ar.findAll();
    }
    
    public Author searchById(String id) throws ExceptionService {
        validateId(id);
        return ar.findById(id).get();
    }
    
    //---------------------------Validaciones-----------------------------------
    
    public void validateCreate(String name) throws ExceptionService {
        if (name == null || name.trim().isEmpty()) {
            throw new ExceptionService("Error al ingresar el nombre.");
        }
        Author a = ar.searchByName(name.toUpperCase());
        if (a != null) {
            throw new ExceptionService("Ya se encuentra un/a autor/a con el mismo nombre.");
        }
    }

    public void validateModify(String id, String name) throws ExceptionService {
        validateId(id);
        if (name.trim().isEmpty()) {
            throw new ExceptionService("Error al ingresar el nombre.");
        }
    }

    //Valido que haya un author con el id pasado por parametro
    public void validateId(String id) throws ExceptionService {
        Optional<Author> answer = ar.findById(id);
        if (!answer.isPresent()) {
            throw new ExceptionService("No se encontró ningun autor.");
        }
    }
}
