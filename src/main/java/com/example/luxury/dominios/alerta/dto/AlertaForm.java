package com.example.luxury.dominios.alerta.dto;

import com.example.luxury.dominios.common.enums.NivelAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlertaForm {

    @NotBlank
    private String mensaje;

    @NotNull
    private NivelAlerta nivel;
}
