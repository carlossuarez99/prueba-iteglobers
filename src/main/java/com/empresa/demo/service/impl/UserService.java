package com.empresa.demo.service.impl;

import com.empresa.demo.exeptions.UserException;
import com.empresa.demo.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    //seria nuestra data similar a un registro de base de datos
    private final Map<Long, User> userDatabase = new HashMap<>();

    @PostConstruct
    public void init() {
        userDatabase.put(1L, new User(1L, "Juan Pérez", "juan.perez@ejemplo.com"));
        userDatabase.put(2L, new User(2L, "María Gómez", "maria.gomez@ejemplo.com"));
        userDatabase.put(3L, new User(3L, "Carlos Ruiz", "carlos.ruiz@ejemplo.com"));
    }

    public User findById(Long id) {
        if (id == null) {
            throw new UserException("El ID no puede ser nulo", 400);
        }

        User user = userDatabase.get(id);

        if (user == null) {
            throw new UserException("Usuario no encontrado para ID: " + id, 400);
        }

        return user;
    }
}

