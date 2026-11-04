package com.almacen.movimiento.dto;

/** Stock calculado: total de entradas - total de salidas. */
public record StockResponse(Long productoId, Long totalEntradas, Long totalSalidas, Long stockActual) {
}
