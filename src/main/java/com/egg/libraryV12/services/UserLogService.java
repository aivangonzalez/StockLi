package com.egg.libraryV12.services;

import com.egg.libraryV12.entities.Client;
import com.egg.libraryV12.entities.Photo;
import com.egg.libraryV12.entities.UserLog;
import com.egg.libraryV12.entities.Zone;
import com.egg.libraryV12.enums.Rol;
import com.egg.libraryV12.exceptions.ExceptionService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.egg.libraryV12.repositories.UserLogRepository;
import com.egg.libraryV12.repositories.ZoneRepository;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserLogService implements UserDetailsService { //implementa la interface de UserDetailsService

    @Autowired
    private UserLogRepository ulr;

    @Autowired
    private PhotoService ps;

    @Autowired
    private ClientService cs;

    @Autowired
    private ZoneRepository zr;

    @Transactional
    public void register(String name, String surname, String mail, MultipartFile photo,
            String zoneId, String password, String password2) throws ExceptionService, Exception {
        MultipartFile photo2 = validatePhotoUser(photo);
        validate(name, surname, mail, photo2, password, password2, zoneId);

        validateCreate(mail);

        UserLog u = new UserLog();
        u.setNombre(name);
        u.setApellido(surname);
        u.setMail(mail);

        String encripted = new BCryptPasswordEncoder().encode(password); //Para encriptar la clave y guardarla encriptada
        u.setClave(encripted);
        Zone zone = zr.findById(zoneId).get();
        u.setZona(zone);

        Photo p = ps.savePhoto(photo2);
        u.setFoto(p);

        u.setAlta(new Date());
        u.setRol(Rol.USUARIO);

        ulr.save(u);
    }

    //Este metodo lo que hace es setearle el atributo cliente cuando el usuario decida pedir prestado un libro, ya que para
    //pedir un libro es necesario ser cliente pero, segun lo pensado, un usuario no necesariamente debe ser un cliente
    @Transactional
    public void setAtributeClient(String id, Client client) throws ExceptionService {
        validateId(id);

        UserLog userlog = ulr.findById(id).get();
        userlog.setCliente(client);
        ulr.save(userlog);
    }

    @Transactional
    public void modify(String id, String name, String surname, String mail, String zoneId, MultipartFile photo,
            String password, String password2) throws ExceptionService, Exception {
        validateId(id);
        MultipartFile photo2 = validatePhotoUser(photo);
        validate(name, surname, mail, photo2, password, password2, zoneId);

        UserLog u = ulr.findById(id).get();
        u.setNombre(name);
        u.setApellido(surname);
        u.setMail(mail);

        String encripted = new BCryptPasswordEncoder().encode(password); //Para encriptar la clave y guardarla encriptada
        u.setClave(encripted);
        u.setZona(zr.findById(zoneId).get());

        String idFoto = null;
        if (u.getFoto() != null) {
            idFoto = u.getFoto().getId();
            Photo p1 = ps.modifyPhoto(idFoto, photo2);
            u.setFoto(p1);
        } else {
            Photo p2 = ps.savePhoto(photo2);
            u.setFoto(p2);
        }

        ulr.save(u);

    }

    @Transactional
    public void dropUserLog(String id) throws ExceptionService {
        validateId(id);

        UserLog ul = ulr.findById(id).get();
        ul.setBaja(new Date());

        ulr.save(ul);
    }

    @Transactional
    public void highUserLog(String id) throws ExceptionService {
        validateId(id);

        UserLog ul = ulr.findById(id).get();
        ul.setBaja(null);

        ulr.save(ul);
    }

    @Transactional
    public void changeRol(String id) throws ExceptionService {
        validateId(id);

        UserLog ul = ulr.findById(id).get();

        if (ul.getRol().equals(Rol.ADMIN)) {
            ul.setRol(Rol.USUARIO);
        } else if (ul.getRol().equals(Rol.USUARIO)) {
            ul.setRol(Rol.ADMIN);
        }
    }

    public UserLog searchById(String id) throws ExceptionService {
        validateId(id);

        UserLog ul = ulr.findById(id).get();
        return ul;
    }

    public List<UserLog> searchAll() throws ExceptionService {
        return ulr.findAll();
    }

    //Para determinar permisos a determinados roles
    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        UserLog userlog = ulr.searchByMail(mail);

        if (userlog != null) {

            //Esto me valida que si el usuario esta dado de baja no puede ingresar
            if (!validateQualification(userlog)) {
                return null;
            }

            List<GrantedAuthority> permisos = new ArrayList<>();

            //Creo una lista de permisos! 
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + userlog.getRol());
            permisos.add(p1);

            //Esto me permite guardar el OBJETO USUARIO LOG, para luego ser utilizado
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true); //Se utiliza para crear una secion entre un cliente y servidor, mantiene la sesion

            session.setAttribute("usuariosession", userlog); // llave + valor

            User user = new User(userlog.getMail(), userlog.getClave(), permisos);

            return user;

        } else {
            return null;
        }

    }

    //Me valida si el usuario esta dado de baja para que no pueda loguerse
    //Este metodo es llamado en loadUserByUsername
    public Boolean validateQualification(UserLog userlog) {
        if (userlog.getBaja() != null) {
            return false;
        }
        return true;
    }

    //--------------------------------validaciones
    public void validate(String name, String surname, String mail, MultipartFile photo, String password, String password2,
            String zoneId) throws ExceptionService {

        //-------Valido el nombre ingresado
        if (name.trim().isEmpty() || name == null) {
            throw new ExceptionService("Error al ingresar en nombre.");
        }
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isAlphabetic((name.charAt(i)))) {
                throw new ExceptionService("El nombre no debe contener numeros.");
            }
        }

        //-------Valido el apellido ingresado
        if (surname.trim().isEmpty() || surname == null) {
            throw new ExceptionService("Error al ingresar el apellido.");
        }
        for (int i = 0; i < surname.length(); i++) {
            if (!Character.isAlphabetic((surname.charAt(i)))) {
                throw new ExceptionService("El apellido no debe contener numeros.");
            }
        }

        //-------Valido el mail ingresado
        if (mail.trim().isEmpty() || mail == null || !mail.contains("@")) {
            throw new ExceptionService("Error al ingresar el mail");
        } else {
            //Expresion regular  
            String regx = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
            //Esto se encarga de compilar la expresion regular para obtener el patrón
            Pattern pattern = Pattern.compile(regx);
            //Se crea una instacia del matcher
            Matcher matcher = pattern.matcher(mail);
            //Si el resultado del matches es falso quiere decir que e usuario ingreso un correo incorrecto
            if (!matcher.matches()) {
                throw new ExceptionService("El formato de mail ingresado es invalido.");
            }
        }

        //https://www.delftstack.com/es/howto/java/email-validation-method-in-java/
        
        
        //------------Validacion del formato del archivo
        if (photo != null) {
            if (!photo.getContentType().equals("image/jpeg") && !photo.getContentType().equals("image/png")) {
                throw new ExceptionService("El formato del archivo debe ser .jpge o .png");
            }
        }

        if (password.trim().isEmpty() || password == null) {
            throw new ExceptionService("Error al ingresar la contraseña.");
        }
        if (password.trim().length() < 6) {
            throw new ExceptionService("La contraseña debe contener 6 o mas caracteres.");
        }
        if (!password.equals(password2)) {
            throw new ExceptionService("Error al validar las contraseñas.");
        }
        if (zoneId.trim().isEmpty() || zoneId == null) {
            throw new ExceptionService("Debe ingresar su zona de residencia.");
        }
        Optional<Zone> optional = zr.findById(zoneId);
        if (!optional.isPresent()) {
            throw new ExceptionService("No se encontro ninguna zona.");
        }
        //no valido el atributo cliente por que en un principio este atributo sera si o si nulo, posteriormente el usuario 
        //si esta interesado en sacar un libro en prestamo va a tener ingresar otros datos para persistir el cliente y luego
        //se setea el atributo cliente de la entidad userlog
    }

    //Este metodo me arregla el problema cuando el usuario no ingresa foto, ya que en este caso la foto tendria que
    //ser null pero al colocarle la propiedad para agrandar el tamaño de archivo que se pueden persistir en el 
    //properties, se coloca por defecto un archivo
    public MultipartFile validatePhotoUser(MultipartFile photo) throws ExceptionService {
        if (photo.getContentType().equals("application/octet-stream")) {
            return null;
        } else {
            return photo;
        }
    }

    public void validateCreate(String mail) throws ExceptionService {
        UserLog u = ulr.searchByMail(mail);
        if (u != null) {
            throw new ExceptionService("Ya se encuentra un usuario con el mismo mail.");
        }
    }

    public void validateId(String id) throws ExceptionService {
        Optional<UserLog> userlog = ulr.findById(id);
        if (!userlog.isPresent()) {
            throw new ExceptionService("No se encontro ningun usuario con el id ingresado.");
        }
    }
}
