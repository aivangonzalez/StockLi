/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.libraryV12.repositories;

import com.egg.libraryV12.entities.Zone;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author LENOVO
 */
public interface ZoneRepository extends JpaRepository<Zone, String>{
    
    @Query("SELECT z FROM Zone z WHERE z.nombre = :nombre")
    public Zone searchByName(@Param("nombre") String nombre);
    
    @Query("SELECT z FROM Zone z")
    public List<Zone> searchAll();
    
}
