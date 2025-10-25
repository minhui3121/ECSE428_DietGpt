package com.dietapp.spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dietapp.spring.dto.AuthRequest;
import com.dietapp.spring.dto.UserResponseDto;
import com.dietapp.spring.service.UserService;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    public UserResponseDto findUser(@PathVariable("id") String id){
        return new UserResponseDto(userService.findUserById(id));
    }

    public UserResponseDto createUser(@RequestBody AuthRequest request) {
        return new UserResponseDto(userService.createUser(request.getName(), request.getEmail(), request.getPassword(), request.getAge(), request.getWeight(), request.isVegetarian(), request.isGlutenFree()));
    }
    public UserResponseDto updateUser(@PathVariable("id") String id, @RequestBody UserResponseDto dto){
        return new UserResponseDto(userService.updateUser(id, dto));
    }
}
