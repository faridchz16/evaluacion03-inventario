package com.almacen.movimiento.service.impl;

import com.almacen.movimiento.dto.MovimientoRequest;
import com.almacen.movimiento.dto.MovimientoResponse;
import com.almacen.movimiento.dto.StockResponse;
import com.almacen.movimiento.entity.Movimiento;
import com.almacen.movimiento.entity.TipoMovimiento;
import com.almacen.movimiento.exception.BusinessException;
import com.almacen.movimiento.exception.ResourceNotFoundException;
import com.almacen.movimiento.repository.MovimientoRepository;
import com.almacen.movimiento.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar() {
        return repository.findAll().stream().map(MovimientoResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(MovimientoResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento", id));
    }

    @Override
    @Transactional
    public MovimientoResponse registrar(MovimientoRequest request) {
        // Regla de negocio: una salida no puede dejar el stock en negativo
        if (request.tipo() == TipoMovimiento.SALIDA) {
            long stock = consultarStock(request.productoId()).stockActual();
            if (request.cantidad() > stock) {
                throw new BusinessException("Stock insuficiente para el producto " + request.productoId()
                        + ". Disponible: " + stock + ", solicitado: " + request.cantidad());
            }
        }
        Movimiento m = Movimiento.builder()
                .productoId(request.productoId())
                .cantidad(request.cantidad())
                .fecha(request.fecha() != null ? request.fecha() : LocalDate.now())
                .tipo(request.tipo())
                .observacion(request.observacion())
                .build();
        return MovimientoResponse.from(repository.save(m));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarPorProducto(Long productoId) {
        return repository.findByProductoIdOrderByFechaDescIdDesc(productoId).stream()
                .map(MovimientoResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse consultarStock(Long productoId) {
        long entradas = repository.sumarCantidad(productoId, TipoMovimiento.ENTRADA);
        long salidas = repository.sumarCantidad(productoId, TipoMovimiento.SALIDA);
        return new StockResponse(productoId, entradas, salidas, entradas - salidas);
    }
}
