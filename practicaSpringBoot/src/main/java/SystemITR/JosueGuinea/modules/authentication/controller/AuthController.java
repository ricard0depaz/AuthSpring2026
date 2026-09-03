package SystemITR.JosueGuinea.modules.authentication.controller;

import SystemITR.JosueGuinea.config.jwt.JwtUtils;
import SystemITR.JosueGuinea.config.jwt.JwtRevocationService;
import SystemITR.JosueGuinea.modules.authentication.model.dto.AuthRequestDTO;
import SystemITR.JosueGuinea.modules.authentication.model.dto.AuthResponseDTO;
import SystemITR.JosueGuinea.modules.authentication.service.AuthService;
import SystemITR.JosueGuinea.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;
    private final JwtUtils jwtUtils;
    private final JwtRevocationService jwtRevocationService;

    @Value("${security.cookie.secure}")
    private boolean cookieSecure;

    @Value("${security.cookie.same-site}")
    private String cookieSameSite;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login (@Valid @RequestBody AuthRequestDTO request, HttpServletResponse response){
        if (request.getUsername().trim() == null || request.getUsername().trim().isBlank() || request.getUsername().trim().isEmpty() ||
                request.getPassword().trim() == null || request.getPassword().trim().isBlank() || request.getPassword().trim().isEmpty()){
            ApiResponse<AuthResponseDTO> credencialesNoEncontradas = new ApiResponse<>(false, "Credenciales incompletas");
            return ResponseEntity.status(404).body(credencialesNoEncontradas);
        }

        AuthResponseDTO responseDTO = service.login(request);
        if (responseDTO != null){
            addTokenCookie(responseDTO, response);
            ApiResponse<AuthResponseDTO> AccesoValido = new ApiResponse<>(true, "Inicio de sesión exitoso", responseDTO);
            return ResponseEntity.ok(AccesoValido);
        }

        ApiResponse<AuthResponseDTO> AccesoInvalido = new ApiResponse<>(false, "Acceso denegado");
        return ResponseEntity.status(401).body(AccesoInvalido);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> me(Authentication authentication) {
        AuthResponseDTO usuario = service.getAuthenticatedUser(authentication.getName());

        if (usuario == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "La sesión no es válida"));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Sesión activa", usuario));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractTokenFromCookies(request);
        if (token != null && !token.isBlank()) {
            try {
                jwtRevocationService.revoke(jwtUtils.parseTokenAndClaims(token));
            } catch (JwtException | IllegalArgumentException ignored) {
                // Aunque el token no sea válido, la cookie debe eliminarse.
            }
        }

        ResponseCookie cookie = buildAuthCookie("", 0);
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(true, "Sesión cerrada correctamente"));
    }

    private void addTokenCookie(AuthResponseDTO dataToken, HttpServletResponse response) {
        String token = jwtUtils.create(
                String.valueOf(dataToken.getId()),
                dataToken.getUsername(),
                dataToken.getRol(),
                dataToken.getMensaje()
        );

        ResponseCookie cookie = buildAuthCookie(token, 86400);

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private ResponseCookie buildAuthCookie(String value, long maxAge) {
        return ResponseCookie.from("authToken", value)
                .path("/")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .maxAge(maxAge)
                .build();
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> "authToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
