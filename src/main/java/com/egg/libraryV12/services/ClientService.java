package com.egg.libraryV12.services;

import com.egg.libraryV12.entities.Client;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.repositories.ClientRepository;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository cr;

    @Transactional
    public Client createClient(Long identDocument, String name, String surname, String phone) throws ExceptionService {
        validateCreate(identDocument, name, surname, phone);

        Client c = new Client();
        c.setDocumento(identDocument);
        c.setNombre(name);
        c.setApellido(surname);
        c.setTelefono(phone);

        cr.save(c);
        return c;
    }

    @Transactional
    public void modifyClient(String id, Long identDocument, String name, String surname, String phone) throws ExceptionService {
        validateModify(id, identDocument, name, surname, phone);

        Client c = cr.findById(id).get();
        c.setDocumento(identDocument);
        c.setNombre(name);
        c.setApellido(surname);
        c.setTelefono(phone);

        cr.save(c);
    }

    @Transactional
    public void dropClient(String id) throws ExceptionService {
        validateId(id);
        
        Client c = cr.findById(id).get();
        c.setAlta(false);
        cr.save(c);
    }
    
    @Transactional
    public void highClient(String id) throws ExceptionService {
        validateId(id);
        
        Client c = cr.findById(id).get();
        c.setAlta(true);
        cr.save(c);
    }
    
    public List<Client> searchAll() throws ExceptionService {
        return cr.findAll();
    }
    
    public Client searchById(String id) throws ExceptionService {
        validateId(id);
        return cr.findById(id).get();
    }
    
    //---------------------------Validaciones-----------------------------------
    public void validateCreate(Long identDocument, String name, String surname, String phone) throws ExceptionService {
        Client client = cr.searchByDNI(identDocument);
        if (client != null) {
            throw new ExceptionService("Ya se encuentra un cliente con el documento ingresado.");
        }
        if (identDocument == null || identDocument < 10000000) {
            throw new ExceptionService("Error al ingresar el documento nacional. Debe contener 8 caracteres.");
        }
        
        //-------Valido el nombre ingresado
        if (name.trim().isEmpty() || name == null) {
            throw new ExceptionService("Error al ingresar en nombre.");
        }
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isAlphabetic((name.charAt(i)))) {
                throw new ExceptionService("El nombre no debe contener numeros.");
            }
        }

        //-------Valido el apellido ingresado
        if (surname.trim().isEmpty() || surname == null) {
            throw new ExceptionService("Error al ingresar el apellido.");
        }
        for (int i = 0; i < surname.length(); i++) {
            if (!Character.isAlphabetic((surname.charAt(i)))) {
                throw new ExceptionService("El apellido no debe contener numeros.");
            }
        }
        
        if (phone.trim().isEmpty() || phone == null || phone.length() < 10) {
            throw new ExceptionService("Error al ingresar el numero. Debe contener 10 caracteres.");
        }
        
    }

    public void validateModify(String id, Long identDocument, String name, String surname, String phone) throws ExceptionService {
        validateId(id);
        if (identDocument == null || identDocument < 10000000) {
            throw new ExceptionService("Error al ingresar el documento nacional. Debe contener 8 caracteres.");
        }
        if (name.trim().isEmpty() || name == null) {
            throw new ExceptionService("Error al ingresar el nombre.");
        }
        if (surname.trim().isEmpty() || surname == null) {
            throw new ExceptionService("Error al ingresar el apellido.");
        }
        if (phone.trim().isEmpty() || phone == null || phone.length() < 10) {
            throw new ExceptionService("Error al ingresar el numero. Debe contener 10 caracteres.");
        }
    }

    //Valido que haya un cliente con el id pasado por parametro
    public void validateId(String id) throws ExceptionService {
        Optional<Client> answer = cr.findById(id);
        if (!answer.isPresent()) {
            throw new ExceptionService("No se encontró ningun autor.");
        }
    }
}
