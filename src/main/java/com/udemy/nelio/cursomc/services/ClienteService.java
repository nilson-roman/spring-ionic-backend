package com.udemy.nelio.cursomc.services;

import com.udemy.nelio.cursomc.domain.Cliente;
import com.udemy.nelio.cursomc.repositories.ClienteRepository;
import com.udemy.nelio.cursomc.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public Cliente buscar(Integer id) {
        Optional<Cliente> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Cliente não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
    }
}
