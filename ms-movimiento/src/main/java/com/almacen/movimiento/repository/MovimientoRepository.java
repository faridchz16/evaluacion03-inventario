package com.almacen.movimiento.repository;

import com.almacen.movimiento.entity.Movimiento;
import com.almacen.movimiento.entity.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByProductoIdOrderByFechaDescIdDesc(Long productoId);

    @Query("SELECT COALESCE(SUM(m.cantidad), 0L) FROM Movimiento m " +
           "WHERE m.productoId = :productoId AND m.tipo = :tipo")
    Long sumarCantidad(@Param("productoId") Long productoId, @Param("tipo") TipoMovimiento tipo);
}
