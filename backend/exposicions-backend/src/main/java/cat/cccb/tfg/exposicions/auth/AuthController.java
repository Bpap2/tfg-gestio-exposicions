package cat.cccb.tfg.exposicions.auth;

import cat.cccb.tfg.exposicions.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        // IMPORTANT: d'aquí treus rol/authorities reals
        // Si tens UserDetails amb roles: auth.getAuthorities()
        String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_USER");

        String token = jwtService.generateToken(req.email(), role);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", req.email(),
                "role", role
        ));
    }
}
