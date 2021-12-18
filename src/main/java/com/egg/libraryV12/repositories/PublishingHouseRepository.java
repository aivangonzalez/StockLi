
package com.egg.libraryV12.repositories;

import com.egg.libraryV12.entities.PublishingHouse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishingHouseRepository extends JpaRepository<PublishingHouse, String>{
    
    @Query("SELECT p FROM PublishingHouse p WHERE p.nombre= :nombre")
    public PublishingHouse searchByName(@Param("nombre") String nombre);
    
    @Query("SELECT p FROM PublishingHouse p")
    public List<PublishingHouse> searchPHouses();
}
