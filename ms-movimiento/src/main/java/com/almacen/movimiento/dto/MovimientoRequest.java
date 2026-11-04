package com.almacen.movimiento.dto;

import com.almacen.movimiento.entity.TipoMovimiento;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** Datos de entrada para registrar una entrada o salida de stock. */
public record MovimientoRequest(
        @NotNull(message = "El productoId es obligatorio")
        @Positive(message = "El productoId debe ser mayor a cero")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer cantidad,

        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate fecha,

        @NotNull(message = "El tipo es obligatorio (ENTRADA o SALIDA)")
        TipoMovimiento tipo,

        @Size(max = 255, message = "La observacion no debe superar 255 caracteres")
        String observacion
) {
}
