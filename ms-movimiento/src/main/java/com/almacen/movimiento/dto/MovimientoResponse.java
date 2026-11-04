package com.almacen.movimiento.dto;

import com.almacen.movimiento.entity.Movimiento;
import com.almacen.movimiento.entity.TipoMovimiento;

import java.time.LocalDate;

public record MovimientoResponse(Long id, Long productoId, Integer cantidad, LocalDate fecha,
                                 TipoMovimiento tipo, String observacion) {

    public static MovimientoResponse from(Movimiento m) {
        return new MovimientoResponse(m.getId(), m.getProductoId(), m.getCantidad(),
                m.getFecha(), m.getTipo(), m.getObservacion());
    }
}
