package com.empresa.demo.controller.handler;

import com.empresa.demo.exeptions.UserException;
import com.empresa.demo.model.User;
import com.empresa.demo.model.generic.ApiResponse;
import com.empresa.demo.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
            var user = userService.findById(id);
            ApiResponse<User> response = ApiResponse.success(user);
            return ResponseEntity.ok(response);
    }
}
