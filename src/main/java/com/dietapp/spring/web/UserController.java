package com.dietapp.spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dietapp.spring.dto.AuthRequest;
import com.dietapp.spring.dto.UserResponseDto;
import com.dietapp.spring.service.UserService;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto findUser(@PathVariable("id") String id){
        return new UserResponseDto(userService.findUserById(id));
    }

    @PostMapping("/users/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody AuthRequest request) {
        return new UserResponseDto(userService.createUser(request.getName(), request.getEmail(), request.getPassword(), request.getAge(), request.getWeight(), request.isVegetarian(), request.isGlutenFree()));
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUser(@PathVariable("id") String id, @RequestBody UserResponseDto dto){
        return new UserResponseDto(userService.updateUser(id, dto));
    }
}
