package br.com.everrise.service;

import br.com.everrise.dto.response.PlanoResponse;

import java.util.List;

public interface PlanoService {

    List<PlanoResponse> findAll();
}

