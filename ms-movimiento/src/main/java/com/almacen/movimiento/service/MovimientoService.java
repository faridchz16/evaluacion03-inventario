package com.almacen.movimiento.service;

import com.almacen.movimiento.dto.MovimientoRequest;
import com.almacen.movimiento.dto.MovimientoResponse;
import com.almacen.movimiento.dto.StockResponse;

import java.util.List;

public interface MovimientoService {
    List<MovimientoResponse> listar();
    MovimientoResponse buscarPorId(Long id);
    MovimientoResponse registrar(MovimientoRequest request);
    List<MovimientoResponse> listarPorProducto(Long productoId);
    StockResponse consultarStock(Long productoId);
}
