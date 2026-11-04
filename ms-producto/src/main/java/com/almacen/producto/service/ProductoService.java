package com.almacen.producto.service;

import com.almacen.producto.dto.ProductoRequest;
import com.almacen.producto.dto.ProductoResponse;

import java.util.List;

public interface ProductoService {
    List<ProductoResponse> listar();
    ProductoResponse buscarPorId(Long id);
    List<ProductoResponse> listarPorProveedor(Long proveedorId);
    ProductoResponse crear(ProductoRequest request);
    ProductoResponse actualizar(Long id, ProductoRequest request);
    void eliminar(Long id);
}
