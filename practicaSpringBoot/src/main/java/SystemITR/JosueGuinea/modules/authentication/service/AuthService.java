package SystemITR.JosueGuinea.modules.authentication.service;

import SystemITR.JosueGuinea.exceptions.DataNotFoundException;
import SystemITR.JosueGuinea.modules.authentication.model.dto.AuthRequestDTO;
import SystemITR.JosueGuinea.modules.authentication.model.dto.AuthResponseDTO;
import SystemITR.JosueGuinea.modules.authentication.model.entity.UsuarioEntity;
import SystemITR.JosueGuinea.modules.authentication.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repoUsuario;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO login(AuthRequestDTO request){
        System.out.println("Entre al service");
        //Se valida la existencia del usuario
        //Crear un objeto de tipo UsuarioEntity ya que guardará los valores del usuario en caso existan
        //De no existir, se lanza una excepción especificando que las credenciales son inválidas.
        UsuarioEntity usuario = repoUsuario.findByUsername(request.getUsername())
                .orElse(null);
        if (usuario != null){
            //Validar que el usuario este activo
            if ("ACTIVO".equals(usuario.getEstado())){
                //Validar que la contraseña sea la correcta
                if (passwordEncoder.matches(request.getPassword(), usuario.getPasswordhash())){
                    return new AuthResponseDTO(
                            usuario.getUsuarioId(),
                            usuario.getUsername(),
                            usuario.getRol().getNombreRol(),
                            "Autenticación exitosa"
                    );
                }
            }
        }
        return null;
    }

    public AuthResponseDTO getAuthenticatedUser(String username) {
        return repoUsuario.findByUsername(username)
                .filter(usuario -> "ACTIVO".equals(usuario.getEstado()))
                .map(usuario -> new AuthResponseDTO(
                        usuario.getUsuarioId(),
                        usuario.getUsername(),
                        usuario.getRol().getNombreRol(),
                        "Sesión activa"
                ))
                .orElse(null);
    }
}
