package br.com.everrise.service;

import br.com.everrise.dto.response.PlanoResponse;
import br.com.everrise.mapper.PlanoMapper;
import br.com.everrise.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanoServiceImpl implements PlanoService {

    private final PlanoRepository planoRepository;
    private final PlanoMapper planoMapper;

    @Override
    public List<PlanoResponse> findAll() {
        return planoRepository.findAll().stream().map(planoMapper::toResponse).toList();
    }
}

