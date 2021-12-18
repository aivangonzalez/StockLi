package com.egg.libraryV12.services;

import com.egg.libraryV12.entities.Book;
import com.egg.libraryV12.entities.Client;
import com.egg.libraryV12.entities.Loan;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.repositories.LoanRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository lr;

    @Transactional
    public void createLoan(Date returnBook, Book book, Client client) throws ExceptionService { //validar la fecha de devolucion para que no sea una fecha anterior a la fecha prestamo
        validateCreate(returnBook, book, client);

        Loan l = new Loan();
        l.setFechaPrestamo(new Date());
        l.setFechaDevolucion(returnBook);
        l.setBook(book);
        l.setClient(client);
        System.out.println(l.toString());
        lr.save(l);
    }

    @Transactional
    public void modifyLoan(String id, Date returnBook) throws ExceptionService {
        validateModify(id, returnBook);

        Loan l = lr.findById(id).get();
        l.setFechaDevolucion(returnBook);
        lr.save(l);
    }

    @Transactional
    public void dropLoan(String id) throws ExceptionService {
        validateId(id);

        Loan l = lr.findById(id).get();
        l.setAlta(false);
        lr.save(l);
    }

    @Transactional
    public void highLoan(String id) throws ExceptionService {
        validateId(id);

        Loan l = lr.findById(id).get();
        l.setAlta(true);
        lr.save(l);
    }

    //metodo para dar de baja automaticamente un prestamo cuando la fecha de devolucion es hoy o ha sido un dia antes de hoy
    @Transactional
    public void automaticLow() throws ExceptionService {
        List<Loan> loans = lr.findAll(); //busco todos los prestamos
        Date today = new Date(); // instancio una variable con la fecha actual
        for (Loan loan : loans) { //itero cada prestamo y si cumple las condiciones le seteo el atributo alta llamando al metodo dropLoan
            if (loan.getFechaDevolucion().equals(today)) {
                dropLoan(loan.getId());
            }
            if (loan.getFechaDevolucion().before(today)) {
                dropLoan(loan.getId());
            }
        }
    }

    public List<Loan> searchByClient(String idClient) throws ExceptionService {
        return lr.searchByClient(idClient);
    }

    public List<Loan> searchAll() throws ExceptionService {
        return lr.findAll();
    }

    public Loan searchById(String id) throws ExceptionService {
        validateId(id);
        return lr.findById(id).get();
    }

    //---------------------------Validaciones-----------------------------------
    public void validateCreate(Date returnBook, Book book, Client client) throws ExceptionService {
        Date date = new Date();
        if (returnBook == null) {
            throw new ExceptionService("Debe ingresar una fecha de devolución.");
        }
        if (returnBook.before(date) || returnBook.equals(date)) {
            throw new ExceptionService("La fecha de devolución es invalida.");
        }
        if (book == null) {
            throw new ExceptionService("No se encontro un libro.");
        }
        if (client == null) {
            throw new ExceptionService("No se encontro el cliente.");
        }
    }

    public void validateModify(String id, Date returnBook) throws ExceptionService {
        validateId(id);

        Loan l = lr.findById(id).get();
        if (returnBook.before(l.getFechaPrestamo()) || returnBook.equals(l.getFechaPrestamo())) {
            throw new ExceptionService("La fecha de devolución es invalida.");
        }
        Date date = new Date();
        if (returnBook.before(date)) {
            throw new ExceptionService("La fecha ingresada no puede ser anterior a la fecha actual.");
        }
    }

    //Valido que haya un prestamo con el id pasado por parametro
    public void validateId(String id) throws ExceptionService {
        Optional<Loan> answer = lr.findById(id);
        if (!answer.isPresent()) {
            throw new ExceptionService("No se encontró ningun prestamo.");
        }
    }
}
