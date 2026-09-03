package SystemITR.JosueGuinea.config.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtRevocationService {

    private final Map<String, Long> revokedTokens = new ConcurrentHashMap<>();

    public void revoke(Claims claims) {
        String tokenId = claims.getId();
        if (tokenId == null || tokenId.isBlank()) return;

        Date expiration = claims.getExpiration();
        revokedTokens.put(tokenId, expiration == null ? Long.MAX_VALUE : expiration.getTime());
    }

    public boolean isRevoked(String tokenId) {
        if (tokenId == null || tokenId.isBlank()) return false;

        Long expiration = revokedTokens.get(tokenId);
        if (expiration == null) return false;

        if (expiration < System.currentTimeMillis()) {
            revokedTokens.remove(tokenId);
            return false;
        }

        return true;
    }
}
