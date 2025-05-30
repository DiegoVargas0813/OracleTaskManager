package com.springboot.MyTodoList.controller;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.repository.UserRepository;
import com.springboot.MyTodoList.repository.ManagerRepository;
import com.springboot.MyTodoList.security.JwtUtil;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
// TODO: Puede que el cross origin no sea necesario, ya que el backend y
// frontend están en el mismo dominio.

// @CrossOrigin(origins = "http://localhost:8080") // 🛠️ it has to be the same
// what's in the backend, no the frontend. (USE IF FRONTEND RUNS FIRST)

@CrossOrigin(origins = "http://localhost:8081") // 🛠️ it has to be the same what's in the backend, no the frontend.
                                                // (USE IF BACKEND RUNS FIRST)
public class AuthController {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, ManagerRepository managerRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.managerRepository = managerRepository; // Si no se usa, puedes eliminar esta línea
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("El correo ya está registrado");
        }

        // Validar rol
        if (!user.getRole().equalsIgnoreCase("user") && !user.getRole().equalsIgnoreCase("manager")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Rol inválido. Debe ser 'user' o 'manager'");
        }

        // ⚠️ En producción deberías encriptar la contraseña
        userRepository.save(user);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        // Primero buscar si es manager
        Optional<Manager> optionalManager = managerRepository.findByEmailAndPassword(email, password);
        if (optionalManager.isPresent()) {
            Manager manager = optionalManager.get();
            String jwt = jwtUtil.generateToken(manager.getEmail());

            return ResponseEntity.ok(Map.of(
                    "jwt", jwt,
                    "email", manager.getEmail(),
                    "name", manager.getName(),
                    "role", "manager"));
        }

        // Si no es manager, buscar como usuario normal
        Optional<User> optionalUser = userRepository.findByEmailAndPassword(email, password);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String jwt = jwtUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "jwt", jwt,
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");

    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String idTokenString = payload.get("idToken");

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(
                            "620904658382-u0nhrdtispsvvmsdglrfjl3qp92h3m9s.apps.googleusercontent.com"))
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
                    user.setRole("user"); // 👈 asignar rol por defecto
                    user = userRepository.save(user);
                }

                String jwt = jwtUtil.generateToken(user.getEmail());
                return ResponseEntity.ok(Map.of(
                        "jwt", jwt,
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "role", user.getRole()));
            } else {
                return ResponseEntity.status(401).body("Token de Google inválido");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error verificando token: " + e.getMessage());
        }
    }
}
