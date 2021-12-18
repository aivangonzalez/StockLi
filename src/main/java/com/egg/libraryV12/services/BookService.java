package com.egg.libraryV12.services;

import com.egg.libraryV12.entities.Author;
import com.egg.libraryV12.entities.Book;
import com.egg.libraryV12.entities.PublishingHouse;
import com.egg.libraryV12.exceptions.ExceptionService;
import com.egg.libraryV12.repositories.AuthorRepository;
import com.egg.libraryV12.repositories.BookRepository;
import com.egg.libraryV12.repositories.PublishingHouseRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    //Para litar autores y editoriales en la vista, en el controlador del registro del libro Get COLOCAR EL MODEL map para listar autores y editoriales
    //el select le trae al controlador el objeto entero de autor o editorial no el id
    //Para colocar fotos, hay que hacer un controlador. ver proyecto de seguridad del video de mariela
    @Autowired
    private BookRepository br;

    @Autowired
    private AuthorRepository ar;

    @Autowired
    private PublishingHouseRepository phr;

    @Transactional
    public void createBook(Long isbn, String title, Integer year, Integer totalBooks,
            Author author, PublishingHouse phouse) throws ExceptionService {
        validateCreateAndModify(isbn, title, year, totalBooks, author, phouse);
        validateCreate(isbn, title);

        Book b = new Book();
        b.setIsbn(isbn);
        b.setTitulo(title.toUpperCase());
        b.setAnio(year);
        b.setEjemplares(totalBooks);
        b.setEjemplaresRestantes(totalBooks);
        b.setAuthor(author);
        b.setP_House(phouse);

        br.save(b);
    }

    @Transactional
    public void modifyBook(String id, Long isbn, String title, Integer year, Integer totalBooks,
            Author author, PublishingHouse phouse) throws ExceptionService {
        validateId(id);
        validateCreateAndModify(isbn, title, year, totalBooks, author, phouse);

        Book b = br.findById(id).get();
        b.setIsbn(isbn);
        b.setTitulo(title.toUpperCase());
        b.setAnio(year);
        b.setEjemplares(totalBooks);
        b.setEjemplaresRestantes(totalBooks);
        b.setAuthor(author);
        b.setP_House(phouse);

        br.save(b);
    }
    
    @Transactional
    public void modifyQuantityHighLoan(Book book) throws ExceptionService {
        book.setEjemplaresRestantes(book.getEjemplaresRestantes() - 1);
        book.setEjemplaresPrestados(book.getEjemplaresPrestados() + 1);
        br.save(book);
    }

    @Transactional
    public void modifyQuantityDropLoan(Book book) throws ExceptionService {
        book.setEjemplaresRestantes(book.getEjemplaresRestantes() + 1);
        book.setEjemplaresPrestados(book.getEjemplaresPrestados() - 1);
        br.save(book);
    }

    
    @Transactional
    public void dropBook(String id) throws ExceptionService {
        validateId(id);

        Book b = br.findById(id).get();
        b.setAlta(false);
        br.save(b);
    }

    @Transactional
    public void highBook(String id) throws ExceptionService {
        validateId(id);

        Book b = br.findById(id).get();
        b.setAlta(true);
        br.save(b);
    }

    public List<Book> searchAll() throws ExceptionService {
        return br.findAll();
    }

    public List<Book> searchAllAvailable() throws ExceptionService {
        List<Book> books =  br.findAll();
        List<Book> available = new ArrayList<Book>();
        //itero la lista con todos los libros y selecciono aquellos que tengan ejemplares disponibles
        //para luego usarlo en el controlador de loan
        for (Book book : books) {
            if(book.getEjemplaresRestantes() >= 1 && book.getAlta() == true){
                available.add(book);
            }
        }
        return available;
    }
    
    public Book searchById(String id) throws ExceptionService {
        validateId(id);
        Book book = br.findById(id).get();
        return book;
    }

    //---------------------------Validaciones-----------------------------------
    public void validateCreateAndModify(Long isbn, String title, Integer year, Integer totalBooks,
            Author author, PublishingHouse phouse) throws ExceptionService {
        if (isbn == null) {
            throw new ExceptionService("Error al ingresar el isbn.");
        }
        if (title.trim().isEmpty() || title == null) {
            throw new ExceptionService("Error al ingresar el titulo.");
        }
        if (year == null || year < 1 || year > 2021) {
            throw new ExceptionService("Error al ingresar el año.");
        }
        if (totalBooks == null || totalBooks < 0) {
            throw new ExceptionService("Error al ingresar la cantidad de ejemplares.");
        }
//        if(borrowedBooks == null){
//            throw new ExceptionService("Error al ingresar la cantidad de ejemplares prestados.");
//        }
//        if (borrowedBooks > totalBooks) {
//            throw new ExceptionService("La cantidad de ejemplares prestados no debe ser mayor a la cantidad total libros.");
//        }

        if (author == null) {
            throw new ExceptionService("Debe seleccionar un autor antes de enviar el formulario!!");
        }
        Optional<Author> optionalA = ar.findById(author.getId());
        if (!optionalA.isPresent()) {
            throw new ExceptionService("No se encontró el autor en la base de datos. Antes de ingresar un libro o modificarlo debe ingresar el autor.");
        }

        if (phouse == null) {
            throw new ExceptionService("Debe seleccionar una editorial antes de enviar el formulario!!");
        }
        Optional<PublishingHouse> optionalPH = phr.findById(phouse.getId());
        if (!optionalPH.isPresent()) {
            throw new ExceptionService("No se encontró la editorial en la base de datos. Antes de ingresar un libro o modificarlo debe ingresar la editorial.");
        }

    }

    public void validateCreate(Long isbn, String title) throws ExceptionService {
        Book bIsbn = br.searchByIsbn(isbn);
        if (bIsbn != null) {
            throw new ExceptionService("Ya se encuentra un libro con el mismo isbn.");
        }
        Book bTitle = br.searchByTitle(title);
        if (bTitle != null) {
            throw new ExceptionService("Ya se encuentra un libro con el mismo titulo.");
        }
    }

    //Valido que haya un libro con el id pasado por parametro
    public void validateId(String id) throws ExceptionService {
        Optional<Book> answer = br.findById(id);
        if (!answer.isPresent()) {
            throw new ExceptionService("No se encontró ningun libro.");
        }
    }
}
