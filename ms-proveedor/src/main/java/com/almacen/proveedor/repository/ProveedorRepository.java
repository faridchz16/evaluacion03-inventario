package com.almacen.proveedor.repository;

import com.almacen.proveedor.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    boolean existsByRuc(String ruc);
    boolean existsByRucAndIdNot(String ruc, Long id);
}
