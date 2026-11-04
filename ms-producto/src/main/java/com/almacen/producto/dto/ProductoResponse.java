package com.almacen.producto.dto;

import com.almacen.producto.entity.Producto;

import java.math.BigDecimal;

public record ProductoResponse(Long id, String codigo, String nombre, String descripcion,
                               BigDecimal precio, String unidadMedida, Long proveedorId) {

    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(p.getId(), p.getCodigo(), p.getNombre(), p.getDescripcion(),
                p.getPrecio(), p.getUnidadMedida(), p.getProveedorId());
    }
}
