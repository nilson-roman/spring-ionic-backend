package com.udemy.nelio.cursomc.services;

import com.udemy.nelio.cursomc.domain.Estado;
import com.udemy.nelio.cursomc.repositories.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoService {

    @Autowired
    private EstadoRepository repo;

    public List<Estado> findAll() {
        return repo.findAllByOrderByName();
    }
}
