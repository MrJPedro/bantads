package com.bantads.auth_service.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bantads.auth_service.models.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

}
