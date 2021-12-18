
package com.egg.libraryV12.repositories;

import com.egg.libraryV12.entities.Client;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String>{
    
    @Query("SELECT c FROM Client c WHERE c.nombre= :nombre")
    public Client searchByName(@Param("nombre") String nombre);
    
    @Query("SELECT c FROM Client c WHERE c.apellido= :apellido")
    public Client searchBySurname(@Param("apellido") String apellido);
    
    @Query("SELECT c FROM Client c")
    public List<Client> searchClients();
    
    @Query("SELECT c FROM Client c WHERE c.documento= :dni")
    public Client searchByDNI(@Param("dni") Long dni);
}
