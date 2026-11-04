package com.almacen.proveedor.service.impl;

import com.almacen.proveedor.dto.ProveedorRequest;
import com.almacen.proveedor.dto.ProveedorResponse;
import com.almacen.proveedor.entity.Proveedor;
import com.almacen.proveedor.exception.BusinessException;
import com.almacen.proveedor.exception.ResourceNotFoundException;
import com.almacen.proveedor.repository.ProveedorRepository;
import com.almacen.proveedor.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> listar() {
        return repository.findAll().stream().map(ProveedorResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse buscarPorId(Long id) {
        return ProveedorResponse.from(obtener(id));
    }

    @Override
    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        if (repository.existsByRuc(request.ruc())) {
            throw new BusinessException("Ya existe un proveedor con RUC " + request.ruc());
        }
        Proveedor p = Proveedor.builder()
                .ruc(request.ruc())
                .razonSocial(request.razonSocial())
                .telefono(request.telefono())
                .email(request.email())
                .direccion(request.direccion())
                .build();
        return ProveedorResponse.from(repository.save(p));
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor p = obtener(id);
        if (repository.existsByRucAndIdNot(request.ruc(), id)) {
            throw new BusinessException("Ya existe otro proveedor con RUC " + request.ruc());
        }
        p.setRuc(request.ruc());
        p.setRazonSocial(request.razonSocial());
        p.setTelefono(request.telefono());
        p.setEmail(request.email());
        p.setDireccion(request.direccion());
        return ProveedorResponse.from(repository.save(p));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }

    private Proveedor obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
    }
}
