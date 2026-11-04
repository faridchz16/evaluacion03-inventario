package com.almacen.proveedor.dto;

import jakarta.validation.constraints.*;

/** Datos de entrada para crear/actualizar un proveedor. */
public record ProveedorRequest(
        @NotBlank(message = "El RUC es obligatorio")
        @Pattern(regexp = "\\d{11}", message = "El RUC debe tener exactamente 11 digitos")
        String ruc,

        @NotBlank(message = "La razon social es obligatoria")
        @Size(max = 150, message = "La razon social no debe superar 150 caracteres")
        String razonSocial,

        @Pattern(regexp = "^$|\\d{7,15}", message = "El telefono debe tener entre 7 y 15 digitos")
        String telefono,

        @Email(message = "El email no tiene un formato valido")
        String email,

        @Size(max = 200, message = "La direccion no debe superar 200 caracteres")
        String direccion
) {
}
