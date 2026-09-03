package SystemITR.JosueGuinea.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Filtro de seguridad que se ejecuta una vez por cada petición HTTP (OncePerRequestFilter).
 * Se encarga de extraer el token JWT desde la cookie HTTP, validarlo y registrar
 * el contexto de autenticación del usuario en Spring Security.
 */
@Component
@RequiredArgsConstructor
public class JwtCookieAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtCookieAuthFilter.class);

    // Nombre de la Cookie que almacena el token JWT
    private static final String AUTH_COOKIE_NAME = "authToken";

    // Servicio de utilidad para manipulación, parseo y extracción de claims del JWT
    private final JwtUtils jwtUtils;
    private final JwtRevocationService jwtRevocationService;

    /**
     * Intercepta la petición HTTP para realizar la lógica de autenticación JWT.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Omitir validación si la ruta es un endpoint público
        if (isPublicEndPoint(request)){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extraer el token de las cookies de la petición
            String token = extractTokenFromCookies(request);

            // 3. Validar presencia del token en la cookie
            if (token == null || token.isBlank()){
                // Si la ruta requiere autenticación y no hay token, detiene la cadena enviando error 401
                if (!isPublicEndPoint(request)){
                    sendError(response, "Token no encontrado", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                filterChain.doFilter(request, response);
                return;
            }

            // 4. Decodificar el token y extraer sus atributos (Claims) y rol
            Claims claims = jwtUtils.parseTokenAndClaims(token);
            if (jwtRevocationService.isRevoked(claims.getId())) {
                sendError(response, "Token revocado", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String rol = claims.get("rol", String.class);

            // 5. Crear la autoridad (rol) asignándole el prefijo estándar 'ROLE_'
            Collection<? extends GrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol));

            // 6. Generar el objeto de autenticación para Spring Security
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(), // Usuario/Subject extraído del token
                            null,                // Credenciales sensibles (no requeridas aquí)
                            authorities          // Lista de roles asignados
                    );

            // 7. Establecer la autenticación activa en el SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 8. Permitir el paso del request hacia el controlador correspondiente
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
            sendError(response, "Token expirado", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            log.warn("Token malformado: {}", e.getMessage());
            sendError(response, "Token inválido", HttpServletResponse.SC_FORBIDDEN);
        } catch (Exception e) {
            log.error("Error de autenticación", e);
            sendError(response, "Error de autenticación", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Inspecciona las cookies de la petición buscando la cookie de autenticación ("authToken").
     *
     * @param request Petición HTTP entrante
     * @return El valor del token JWT si existe la cookie, o null si no existe.
     */
    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(c -> AUTH_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    /**
     * Escribe una respuesta de error con estructura JSON y un código de estado HTTP específico.
     *
     * @param response Objeto de respuesta HTTP
     * @param message  Mensaje descriptivo del error
     * @param status   Código de estado HTTP (ej. 401, 403, 500)
     */
    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write(String.format(
                "{\"error\": \"%s\", \"status\": %d}", message, status));
    }

    /**
     * Verifica si la petición corresponde a una ruta pública que no requiere token de autenticación.
     *
     * @param request Petición HTTP a evaluar
     * @return true si coincide con un endpoint público y su verbo HTTP; false de lo contrario.
     */
    private boolean isPublicEndPoint(HttpServletRequest request) {
        //Extracción del endpoint
        String path = request.getRequestURI();
        //Extracción del método HTTP utilizado en la request
        String method = request.getMethod();

        return "OPTIONS".equals(method) ||
                (path.equals("/api/auth/login") && "POST".equals(method)) ||
                (path.equals("/api/auth/logout") && "POST".equals(method)) ||
                (path.equals("/api/auth/register") && "POST".equals(method)) ||
                (path.equals("/api/forgotPassword") && "POST".equals(method));
    }
}
