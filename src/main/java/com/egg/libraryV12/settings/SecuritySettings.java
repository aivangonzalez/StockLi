package com.egg.libraryV12.settings;

import com.egg.libraryV12.services.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration //esto determina que esta clase va a hacer configuraciones en el proyecto
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class SecuritySettings extends WebSecurityConfigurerAdapter {  //extends para poder utilizar metodos

    @Autowired
    private UserLogService ulService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // -------------------Configuracion para dar autorizaciones
                .authorizeRequests()
                .antMatchers("/admin/*").hasRole("ADMIN")//aquellos que entren a /admin deben tener el rol de ADMIN
                .antMatchers("/css/*", "/js/*", "/img/*", "/**").permitAll()
                .and()
                // -------------------Configuracion del formulario del login para LOGUEARNOS
                .formLogin()
                .loginPage("/login") // Que formulario esta mi login
                .loginProcessingUrl("/logincheck")
                .usernameParameter("username") // Como viajan los datos del logueo
                .passwordParameter("password")// Como viajan los datos del logueo
                .defaultSuccessUrl("/inicio") // A que URL viaja 
                .permitAll()
                .and()
                // -------------------Configuracion para DESLOGUEARNOS
                .logout() // Aca configuro la salida
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll().and().csrf().disable();
    }
}
