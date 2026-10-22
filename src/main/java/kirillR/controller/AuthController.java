package kirillR.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kirillR.dto.AuthRequest;
import kirillR.dto.AuthResponse;
import kirillR.dto.ErrorResponse;
import kirillR.entity.User;
import kirillR.repository.UserRepository;
import kirillR.security.JwtService;
import kirillR.util.XssSanitizer;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Username already taken"));
        }

        User user = new User(request.username(), passwordEncoder.encode(request.password()));
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, XssSanitizer.sanitize(user.getUsername())));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPasswordHash()))
                .<ResponseEntity<?>>map(user -> {
                    String token = jwtService.generateToken(user.getUsername());
                    return ResponseEntity.ok(
                            new AuthResponse(token, XssSanitizer.sanitize(user.getUsername()))
                    );
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid username or password")));
    }
}
