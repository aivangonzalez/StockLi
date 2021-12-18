
package com.egg.libraryV12.repositories;

import com.egg.libraryV12.entities.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends JpaRepository<UserLog, String>{
    
    @Query("SELECT u FROM UserLog u WHERE u.mail= :mail")
    public UserLog searchByMail(@Param("mail") String mail);
    
}
