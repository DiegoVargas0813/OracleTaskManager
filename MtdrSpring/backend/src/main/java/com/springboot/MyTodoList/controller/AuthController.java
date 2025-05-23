package com.springboot.MyTodoList.controller;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.UserRepository;
import com.springboot.MyTodoList.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
//TODO: Puede que el cross origin no sea necesario, ya que el backend y frontend están en el mismo dominio.
@CrossOrigin(origins = "http://localhost:8080") // 🛠️ it has to be the same what's in the backend, no the frontend.
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        Optional<User> optionalUser = userRepository.findByEmailAndPassword(email,password);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            String jwt = jwtUtil.generateToken(user.getEmail());

            // Retornar token y datos del usuario (sin contraseña)
            return ResponseEntity.ok(Map.of(
                "jwt", jwt,
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole()
            ));

        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String idTokenString = payload.get("idToken");

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(
                        "620904658382-u0nhrdtispsvvmsdglrfjl3qp92h3m9s.apps.googleusercontent.com"
                    ))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload tokenPayload = idToken.getPayload();
                String email = tokenPayload.getEmail();
                String name = (String) tokenPayload.get("name");

                Optional<User> optionalUser = userRepository.findByEmail(email);
                User user;

                if (optionalUser.isPresent()) {
                    user = optionalUser.get();
                } else {
                    user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    user = userRepository.save(user); 
                }

                String jwt = jwtUtil.generateToken(user.getEmail());
                return ResponseEntity.ok(Map.of("jwt", jwt));
            } else {
                return ResponseEntity.status(401).body("Token de Google inválido");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error verificando token: " + e.getMessage());
        }
    }
}
