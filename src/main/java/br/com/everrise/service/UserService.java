package br.com.everrise.service;

import br.com.everrise.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> findAll();

    UserResponse findById(Long id);
}

