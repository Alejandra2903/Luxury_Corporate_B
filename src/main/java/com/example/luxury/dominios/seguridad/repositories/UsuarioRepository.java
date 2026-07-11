package com.example.luxury.dominios.seguridad.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.luxury.dominios.seguridad.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select u from Usuario u where u.numeroDocumento = :numeroDocumento")
    Optional<Usuario> findByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

    @Query("select u from Usuario u where u.telefono = :telefono")
    Optional<Usuario> findByTelefono(@Param("telefono") String telefono);

    @Query("select count(u) > 0 from Usuario u where u.numeroDocumento = :numeroDocumento")
    boolean existsByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

    @Query("select count(u) > 0 from Usuario u where u.telefono = :telefono")
    boolean existsByTelefono(@Param("telefono") String telefono);

    @Query("select count(u) > 0 from Usuario u where u.correo = :correo")
    boolean existsByCorreo(@Param("correo") String correo);

    @Query("""
            select u from Usuario u
            where u.numeroDocumento = trim(:identificador)
               or u.telefono = trim(:identificador)
               or lower(u.correo) = lower(trim(:identificador))
            """)
    Optional<Usuario> buscarPorIdentificador(@Param("identificador") String identificador);

    @Query("select count(u) > 0 from Usuario u where lower(trim(u.nombres)) = lower(trim(:nombres)) and lower(trim(u.apellidos)) = lower(trim(:apellidos))")
    boolean existsByNombresAndApellidos(@Param("nombres") String nombres, @Param("apellidos") String apellidos);
}

