package com.almacen.producto.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Datos de entrada para crear/actualizar un producto. */
public record ProductoRequest(
        @NotBlank(message = "El codigo es obligatorio")
        @Size(max = 20, message = "El codigo no debe superar 20 caracteres")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no debe superar 120 caracteres")
        String nombre,

        @Size(max = 255, message = "La descripcion no debe superar 255 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
        BigDecimal precio,

        @NotBlank(message = "La unidad de medida es obligatoria")
        String unidadMedida,

        @NotNull(message = "El proveedorId es obligatorio")
        @Positive(message = "El proveedorId debe ser mayor a cero")
        Long proveedorId
) {
}
