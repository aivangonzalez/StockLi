
package com.egg.libraryV12.repositories;

import com.egg.libraryV12.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, String>{
    
}
