package com.almacen.proveedor.dto;

import com.almacen.proveedor.entity.Proveedor;

public record ProveedorResponse(Long id, String ruc, String razonSocial,
                                String telefono, String email, String direccion) {

    public static ProveedorResponse from(Proveedor p) {
        return new ProveedorResponse(p.getId(), p.getRuc(), p.getRazonSocial(),
                p.getTelefono(), p.getEmail(), p.getDireccion());
    }
}
