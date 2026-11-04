# Pruebas en Postman – paso a paso

Requisito: los tres servicios levantados (8081, 8082, 8083) y MySQL encendido.
En cada petición con body: pestaña **Body → raw → JSON**. Ejecutar **en orden** (los ids se encadenan).
Si las bases ya tenían datos, reemplaza `1` por el id que te devuelva cada creación.

---

## A. Proveedores (ms-proveedor · 8082)

### P01 – Crear proveedor ✅
- **POST** `http://localhost:8082/api/proveedores`
```json
{
  "ruc": "20123456789",
  "razonSocial": "Distribuidora Andina SAC",
  "telefono": "014567890",
  "email": "ventas@andina.pe",
  "direccion": "Av. Argentina 1234, Lima"
}
```
- **Esperado:** `201 Created` con el proveedor y su `id` (ej. 1).

### P02 – Listar proveedores ✅
- **GET** `http://localhost:8082/api/proveedores` → `200 OK` (arreglo).

### P03 – Buscar proveedor por id ✅
- **GET** `http://localhost:8082/api/proveedores/1` → `200 OK`.

### P04 – Actualizar proveedor ✅
- **PUT** `http://localhost:8082/api/proveedores/1` — mismo body de P01 con `"telefono": "987654321"`.
- **Esperado:** `200 OK` con el teléfono actualizado.

### P05 – Error de validación ❌
- **POST** `http://localhost:8082/api/proveedores`
```json
{ "ruc": "123", "razonSocial": "", "email": "correo-malo" }
```
- **Esperado:** `400 Bad Request` con `errors.ruc`, `errors.razonSocial` y `errors.email`.

### P06 – RUC duplicado ❌
- **POST** con el mismo body de P01 → `409 Conflict` (“Ya existe un proveedor con RUC…”).

### P07 – Proveedor inexistente ❌
- **GET** `http://localhost:8082/api/proveedores/9999` → `404 Not Found`.

---

## B. Productos (ms-producto · 8081)

### R01 – Crear producto ✅
- **POST** `http://localhost:8081/api/productos`
```json
{
  "codigo": "PRD-001",
  "nombre": "Tornillo hexagonal 1/2",
  "descripcion": "Tornillo de acero galvanizado",
  "precio": 0.85,
  "unidadMedida": "UNIDAD",
  "proveedorId": 1
}
```
- **Esperado:** `201 Created` con `id` (ej. 1).

### R02 – Listar productos ✅ · **GET** `http://localhost:8081/api/productos` → `200`
### R03 – Buscar por id ✅ · **GET** `http://localhost:8081/api/productos/1` → `200`
### R04 – Productos de un proveedor ✅ · **GET** `http://localhost:8081/api/productos/proveedor/1` → `200`

### R05 – Actualizar producto ✅
- **PUT** `http://localhost:8081/api/productos/1` — body de R01 con `"precio": 0.95` → `200 OK`.

### R06 – Error de validación ❌
- **POST** `http://localhost:8081/api/productos`
```json
{ "codigo": "PRD-002", "nombre": "", "precio": -5, "unidadMedida": "UNIDAD", "proveedorId": 1 }
```
- **Esperado:** `400` con `errors.nombre` y `errors.precio` (“El precio debe ser mayor a cero”).

### R07 – Producto inexistente ❌
- **PUT** `http://localhost:8081/api/productos/9999` con el body de R01 → `404 Not Found`.

---

## C. Movimientos (ms-movimiento · 8083) — **Prueba de aceptación**

### M01 – Registrar ENTRADA ✅
- **POST** `http://localhost:8083/api/movimientos`
```json
{
  "productoId": 1,
  "cantidad": 100,
  "fecha": "2026-11-03",
  "tipo": "ENTRADA",
  "observacion": "Compra inicial a proveedor"
}
```
- **Esperado:** `201 Created`.

### M02 – Registrar SALIDA ✅
- **POST** `http://localhost:8083/api/movimientos`
```json
{
  "productoId": 1,
  "cantidad": 30,
  "fecha": "2026-11-03",
  "tipo": "SALIDA",
  "observacion": "Despacho a taller"
}
```
- **Esperado:** `201 Created`.

### M03 – Stock actual ✅
- **GET** `http://localhost:8083/api/movimientos/producto/1/stock`
- **Esperado:** `200 OK`
```json
{ "productoId": 1, "totalEntradas": 100, "totalSalidas": 30, "stockActual": 70 }
```

### M04 – Movimientos por producto ✅
- **GET** `http://localhost:8083/api/movimientos/producto/1` → `200 OK` (la entrada y la salida).

### M05 – Validación de cantidad = 0 ❌ (RF 63)
- **POST** `http://localhost:8083/api/movimientos`
```json
{ "productoId": 1, "cantidad": 0, "tipo": "ENTRADA", "observacion": "Cantidad invalida" }
```
- **Esperado:** `400` → `"errors": { "cantidad": "La cantidad debe ser mayor a cero" }`

### M06 – Validación de cantidad negativa ❌ (RF 63)
- Igual que M05 con `"cantidad": -10, "tipo": "SALIDA"` → `400`.

### M07 – Salida mayor al stock ❌
- **POST** con `{ "productoId": 1, "cantidad": 500, "tipo": "SALIDA", "observacion": "Excede stock" }`
- **Esperado:** `409 Conflict` → “Stock insuficiente… Disponible: 70, solicitado: 500”.

### M08 – Tipo no permitido ❌
- **POST** con `{ "productoId": 1, "cantidad": 5, "tipo": "DEVOLUCION" }` → `400` (cuerpo mal formado).

### M09 – Movimiento inexistente ❌
- **GET** `http://localhost:8083/api/movimientos/9999` → `404 Not Found`.

---

## D. Limpieza (opcional, al final)
- **DELETE** `http://localhost:8081/api/productos/1` → `204 No Content`
- **DELETE** `http://localhost:8082/api/proveedores/1` → `204 No Content`

---

## Capturas mínimas recomendadas para la entrega
1. M01 (entrada 201) y M02 (salida 201) → **prueba de aceptación**.
2. M03 (stock = 70).
3. M05 (cantidad 0 → 400) → **validación de cantidad**.
4. P01 o R01 (caso exitoso) y P07 o R07 (404).
5. SQLyog mostrando las tres bases con su tabla.
6. Resultado de **Run collection** con todos los tests en verde.
