package com.almacen.proveedor.service;

import com.almacen.proveedor.dto.ProveedorRequest;
import com.almacen.proveedor.dto.ProveedorResponse;

import java.util.List;

public interface ProveedorService {
    List<ProveedorResponse> listar();
    ProveedorResponse buscarPorId(Long id);
    ProveedorResponse crear(ProveedorRequest request);
    ProveedorResponse actualizar(Long id, ProveedorRequest request);
    void eliminar(Long id);
}
