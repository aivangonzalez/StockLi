
package com.egg.libraryV12.repositories;

import com.egg.libraryV12.entities.Author;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, String>{
    
    @Query("SELECT a FROM Author a WHERE a.nombre= :nombre")
    public Author searchByName(@Param("nombre") String nombre);
    
    @Query("SELECT a FROM Author a")
    public List<Author> searchAuthors();
}
