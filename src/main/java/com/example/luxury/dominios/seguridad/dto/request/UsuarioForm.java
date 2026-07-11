package com.example.luxury.dominios.seguridad.dto.request;

import com.example.luxury.dominios.seguridad.enums.NombreRol;
import com.example.luxury.dominios.seguridad.enums.TipoDocumento;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioForm {

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    @NotNull
    private TipoDocumento tipoDocumento;

    @NotBlank
    private String numeroDocumento;

    @NotBlank
    private String telefono;

    @NotBlank
    @Email
    private String correo;

    private String contrasena;

    @NotNull
    private NombreRol rol;

    private boolean activo = true;

    public UsuarioRequest toRequest() {
        return new UsuarioRequest(nombres, apellidos, tipoDocumento, numeroDocumento, telefono, correo, contrasena, rol,
                activo);
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public NombreRol getRol() {
        return rol;
    }

    public void setRol(NombreRol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
