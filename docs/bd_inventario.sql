-- =====================================================================
-- Laboratorio 07 - Gestion de inventario de almacen
-- Una base de datos independiente por microservicio (Database per Service)
-- Las tablas las crea Hibernate (ddl-auto: update) al iniciar en perfil dev.
-- =====================================================================
CREATE DATABASE IF NOT EXISTS bd_proveedor  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bd_producto   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bd_movimiento CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Verificacion (actividad 66): cada servicio usa su propia base
-- Ejecutar despues de levantar los 3 servicios:
-- SHOW TABLES FROM bd_proveedor;   -> proveedores
-- SHOW TABLES FROM bd_producto;    -> productos
-- SHOW TABLES FROM bd_movimiento;  -> movimientos
