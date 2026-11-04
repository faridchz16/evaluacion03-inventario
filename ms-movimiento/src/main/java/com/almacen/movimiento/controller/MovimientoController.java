package com.almacen.movimiento.controller;

import com.almacen.movimiento.dto.MovimientoRequest;
import com.almacen.movimiento.dto.MovimientoResponse;
import com.almacen.movimiento.dto.StockResponse;
import com.almacen.movimiento.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService service;

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MovimientoResponse> registrar(@Valid @RequestBody MovimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MovimientoResponse>> porProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.listarPorProducto(productoId));
    }

    @GetMapping("/producto/{productoId}/stock")
    public ResponseEntity<StockResponse> stock(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.consultarStock(productoId));
    }
}
