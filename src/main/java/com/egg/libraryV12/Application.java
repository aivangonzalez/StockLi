package com.egg.libraryV12;

import com.egg.libraryV12.services.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Application {

    @Autowired
    private UserLogService userService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService) //esta interface se implementa en el UserLogService y busca el metodo loadbyusername
                .passwordEncoder(new BCryptPasswordEncoder()); //sirve para encriptar la contraseña, es necesario que en el servicio de user tenga el mismo encriptador
    }
}
