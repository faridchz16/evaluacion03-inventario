package com.almacen.producto.service.impl;

import com.almacen.producto.dto.ProductoRequest;
import com.almacen.producto.dto.ProductoResponse;
import com.almacen.producto.entity.Producto;
import com.almacen.producto.exception.BusinessException;
import com.almacen.producto.exception.ResourceNotFoundException;
import com.almacen.producto.repository.ProductoRepository;
import com.almacen.producto.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return repository.findAll().stream().map(ProductoResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        return ProductoResponse.from(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorProveedor(Long proveedorId) {
        return repository.findByProveedorId(proveedorId).stream().map(ProductoResponse::from).toList();
    }

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (repository.existsByCodigo(request.codigo())) {
            throw new BusinessException("Ya existe un producto con codigo " + request.codigo());
        }
        Producto p = Producto.builder()
                .codigo(request.codigo())
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .unidadMedida(request.unidadMedida())
                .proveedorId(request.proveedorId())
                .build();
        return ProductoResponse.from(repository.save(p));
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto p = obtener(id);
        if (repository.existsByCodigoAndIdNot(request.codigo(), id)) {
            throw new BusinessException("Ya existe otro producto con codigo " + request.codigo());
        }
        p.setCodigo(request.codigo());
        p.setNombre(request.nombre());
        p.setDescripcion(request.descripcion());
        p.setPrecio(request.precio());
        p.setUnidadMedida(request.unidadMedida());
        p.setProveedorId(request.proveedorId());
        return ProductoResponse.from(repository.save(p));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }

    private Producto obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }
}
