package SystemITR.JosueGuinea.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Componente de utilidad para la gestión de Tokens JWT.
 * Permite la creación, lectura de claims (atributos) y validación de firma y expiración.
 */
@Component
public class JwtUtils {

    /** Clave secreta codificada en Base64 para firmar los tokens. */
    @Value("${security.jwt.secret}")
    private String jwtSecreto;

    /** Emisor o aplicación que genera el token. */
    @Value("${security.jwt.issuer}")
    private String issuer;

    /** Tiempo de expiración del token expresado en milisegundos. */
    @Value("${security.jwt.expiration}")
    private long expirationMs;

    private final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * Construye y firma un token JWT compacto con los claims básicos y personalizados.
     *
     * @param id       Identificador único de la sesión o del registro de usuario.
     * @param username Nombre de usuario (Subject).
     * @param rol      Rol asignado al usuario dentro del sistema.
     * @param mensaje  Mensaje o información adicional opcional.
     * @return String que representa el token JWT codificado.
     */
    public String create(String id, String username, String rol, String mensaje){
        // Decodifica la clave secreta desde Base64 y genera la llave HMAC-SHA
        SecretKey signinKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecreto));

        // Obtiene el instante de creación y calcula el momento exacto de expiración
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        // Construcción y firma del payload del JWT
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())                   // Identificador único del token (jti)
                .setIssuedAt(now)                                     // Establece la fecha de emisión (iat)
                .setSubject(username)                                 // Define el usuario dueño del token (sub)
                .claim("id", id)                                       // Claim personalizado: id
                .claim("rol", rol)                                     // Claim personalizado: rol del usuario
                .setIssuer(issuer)                                     // Define el emisor del token (iss)
                .setExpiration(expirationMs >= 0 ? expiration : null) // Define la fecha de expiración si corresponde
                .signWith(signinKey, SignatureAlgorithm.HS256)        // Firma digital mediante algoritmo HS256
                .compact();                                           // Serializa a formato String (header.payload.signature)
    }

    /**
     * Extrae el valor del claim personalizado "rol" contenido dentro del token.
     *
     * @param token Cadena del token JWT.
     * @return El nombre del rol.
     */
    public String extractRol(String token){
        Claims claims = parseTokenAndClaims(token);
        return claims.get("rol", String.class);
    }

    /**
     * Extrae el sujeto principal (username) del token JWT.
     *
     * @param token Cadena del token JWT.
     * @return Nombre de usuario almacenado en el token.
     */
    public String getValue(String token){
        Claims claims = parseTokenAndClaims(token);
        return claims.getSubject();
    }

    /**
     * Extrae el identificador único (ID) asignado al token.
     *
     * @param token Cadena del token JWT.
     * @return ID del token.
     */
    public String getKey(String token){
        Claims claims = parseTokenAndClaims(token);
        return claims.getId();
    }

    /**
     * Verifica la validez del token comprobando su firma y que no esté expirado o malformado.
     *
     * @param token Cadena del token JWT a evaluar.
     * @return {@code true} si el token es válido; {@code false} en caso contrario.
     */
    public boolean validate(String token){
        try {
            parseTokenAndClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Decodifica y parsea el token JWT para extraer sus Claims (payload).
     *
     * @param token Cadena del token JWT.
     * @return Objeto {@link Claims} con todos los atributos presentes en el token.
     * @throws ExpiredJwtException   Si el token ya superó la fecha/hora de expiración.
     * @throws MalformedJwtException Si la estructura del token es incorrecta o la firma no coincide.
     */
    public Claims parseTokenAndClaims(String token) throws ExpiredJwtException, MalformedJwtException {
        // Valida la firma del token utilizando la clave pública/privada configurada
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecreto)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
