
package com.egg.libraryV12.repositories;

import com.egg.libraryV12.entities.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, String>{
    
    @Query("SELECT l FROM Loan l WHERE l.client.id = :id")
    public List<Loan> searchByClient(@Param("id") String id);
    
}
