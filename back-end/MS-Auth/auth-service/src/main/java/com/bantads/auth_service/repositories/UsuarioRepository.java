package com.bantads.auth_service.repositories;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.bantads.auth_service.models.Usuario;
import java.util.List;
import java.util.Optional;


public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByLogin(String login);
    Optional<Usuario> findByCpfUsuario(String cpfUsuario);
    List<Usuario> findByTipoUsuario(String tipoUsuario);
}
