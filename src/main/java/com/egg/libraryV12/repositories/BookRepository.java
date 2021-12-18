
package com.egg.libraryV12.repositories;

import com.egg.libraryV12.entities.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String>{
    
    @Query("SELECT b FROM Book b WHERE b.titulo= :titulo")
    public Book searchByTitle(@Param("titulo") String titulo);
    
    @Query("SELECT b FROM Book b")
    public List<Book> searchBooks();
    
    @Query("SELECT b FROM Book b WHERE b.isbn= :isbn")
    public Book searchByIsbn(@Param("isbn") Long isbn);
}
