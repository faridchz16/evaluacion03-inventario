# Laboratorio 07 – Gestión de inventario de almacén

Sistema de inventario basado en **microservicios Spring Boot**: cada dominio (Proveedor, Producto, Movimiento) es un proyecto Maven independiente, con su propio puerto y su propia base de datos MySQL (patrón *Database per Service*).

| Microservicio   | Puerto | Base de datos   | Responsabilidad                                   |
|-----------------|--------|-----------------|---------------------------------------------------|
| `ms-producto`   | 8081   | `bd_producto`   | CRUD de productos                                 |
| `ms-proveedor`  | 8082   | `bd_proveedor`  | CRUD de proveedores                               |
| `ms-movimiento` | 8083   | `bd_movimiento` | Entradas/salidas de stock y cálculo de stock      |

Diagrama: [`docs/diagrama-microservicios.svg`](docs/diagrama-microservicios.svg)

## 1. Tecnologías

Java 17 · Spring Boot 3.5 · Spring Web · Spring Data JPA (Hibernate) · Bean Validation · MySQL 8 (XAMPP) · Lombok · Maven · Postman

## 2. Generación con Spring Initializr

Los tres proyectos se generaron con la estructura de **Spring Initializr** (https://start.spring.io) y la siguiente configuración:

| Campo | ms-producto | ms-proveedor | ms-movimiento |
|---|---|---|---|
| Project / Language | Maven / Java | Maven / Java | Maven / Java |
| Spring Boot | 3.5.6 | 3.5.6 | 3.5.6 |
| Group | `com.almacen` | `com.almacen` | `com.almacen` |
| Artifact | `ms-producto` | `ms-proveedor` | `ms-movimiento` |
| Package name | `com.almacen.producto` | `com.almacen.proveedor` | `com.almacen.movimiento` |
| Packaging / Java | Jar / 17 | Jar / 17 | Jar / 17 |

**Dependencias:** Spring Web, Spring Data JPA, MySQL Driver, Validation, Lombok, Spring Boot DevTools (+ Spring Boot Starter Test, que Initializr agrega siempre).

Archivos de Initializr incluidos en cada servicio: `mvnw`, `mvnw.cmd`, `.mvn/wrapper`, `HELP.md`, `.gitignore`, `.gitattributes` y la clase de prueba `src/test/.../Ms*ApplicationTests.java`. El `application.properties` original se reemplazó por `application.yml` (actividad 69).

> Nota: la prueba `contextLoads()` levanta el contexto completo, por lo que **MySQL debe estar encendido** si se ejecuta `mvn test` o `mvn package`. Para empaquetar sin MySQL: `mvnw package -DskipTests`.

## 3. Estructura (igual en los tres servicios)

```
ms-xxx/
├── pom.xml · mvnw · mvnw.cmd · .mvn/ · HELP.md
├── src/test/java/...  → Ms*ApplicationTests (generada por Initializr)
└── src/main/
    ├── java/com/almacen/xxx/
    │   ├── MsXxxApplication.java
    │   ├── controller/      → endpoints REST (@RestController)
    │   ├── service/         → interfaz de la lógica de negocio
    │   │   └── impl/        → implementación (@Service, @Transactional)
    │   ├── repository/      → acceso a datos (JpaRepository)
    │   ├── entity/          → entidades JPA (@Entity)
    │   ├── dto/             → Request (con validaciones) y Response
    │   └── exception/       → excepciones propias + @RestControllerAdvice
    └── resources/application.yml   → perfiles dev / prod
```

## 4. Cómo ejecutar

1. **Iniciar MySQL** desde XAMPP (puerto 3306, usuario `root` sin contraseña).
2. **Crear las bases**: ejecutar `docs/bd_inventario.sql` en SQLyog. *(Opcional: la URL de `dev` ya incluye `createDatabaseIfNotExist=true`.)*
3. **Abrir en IntelliJ**: `File → Open` → seleccionar el `pom.xml` de la carpeta `lab07-inventario` → **Open as Project**.
   El POM raíz es solo un agregador: IntelliJ detecta los tres módulos y descarga las dependencias.
4. Si IntelliJ lo pide, habilitar **Annotation Processing** (Lombok):
   `Settings → Build → Compiler → Annotation Processors → Enable`.
5. **Ejecutar** cada clase `Ms*Application` (botón ▶). El orden no importa porque los servicios son independientes.
6. Verificar en consola: `Tomcat started on port 808X` y `The following 1 profile is active: "dev"`.
7. Verificar en SQLyog que cada base tenga su tabla: `proveedores`, `productos`, `movimientos`.

Por consola (alternativa, Git Bash): `cd ms-producto && ./mvnw spring-boot:run` · en CMD: `mvnw.cmd spring-boot:run`

## 5. Perfiles de configuración (dev / prod)

El perfil activo se define en `spring.profiles.active` (por defecto `dev`). Para usar `prod`:

- IntelliJ: `Run → Edit Configurations → Active profiles: prod`
- Consola: `mvn spring-boot:run -Dspring-boot.run.profiles=prod`
- Variable de entorno: `SPRING_PROFILES_ACTIVE=prod`

| Propiedad                     | `dev`                                         | `prod`                                      | Motivo del cambio |
|-------------------------------|-----------------------------------------------|---------------------------------------------|-------------------|
| `datasource.url`              | `localhost:3306/bd_xxx` + `createDatabaseIfNotExist` | `${DB_URL}` (variable de entorno)    | En producción el servidor de BD es otro |
| `datasource.username/password`| `root` / vacío (XAMPP)                        | `${DB_USER}` / `${DB_PASSWORD}`             | No se escriben credenciales en el código |
| `jpa.hibernate.ddl-auto`      | `update` (crea/actualiza tablas)              | `validate` (solo valida el esquema)         | Evitar cambios accidentales en datos reales |
| `jpa.show-sql`                | `true`                                        | `false`                                     | Depuración vs. rendimiento y seguridad |
| `logging.level`               | `com.almacen: DEBUG`                          | `root: WARN`, `com.almacen: INFO`           | Menos ruido en producción |
| `hikari.maximum-pool-size`    | por defecto (10)                              | `20`                                        | Mayor concurrencia |
| `server.error.include-stacktrace` | por defecto                               | `never`                                     | No exponer detalles internos |

Lo que **no** cambia entre perfiles: `server.port` y `spring.application.name`.

## 6. Endpoints

### ms-proveedor (8082)
| Método | URL | Descripción | Éxito |
|---|---|---|---|
| GET | `/api/proveedores` | Listar | 200 |
| GET | `/api/proveedores/{id}` | Buscar por id | 200 |
| POST | `/api/proveedores` | Crear | 201 |
| PUT | `/api/proveedores/{id}` | Actualizar | 200 |
| DELETE | `/api/proveedores/{id}` | Eliminar | 204 |

### ms-producto (8081)
| Método | URL | Descripción | Éxito |
|---|---|---|---|
| GET | `/api/productos` | Listar | 200 |
| GET | `/api/productos/{id}` | Buscar por id | 200 |
| GET | `/api/productos/proveedor/{proveedorId}` | Productos de un proveedor | 200 |
| POST | `/api/productos` | Crear | 201 |
| PUT | `/api/productos/{id}` | Actualizar | 200 |
| DELETE | `/api/productos/{id}` | Eliminar | 204 |

### ms-movimiento (8083)
| Método | URL | Descripción | Éxito |
|---|---|---|---|
| GET | `/api/movimientos` | Listar todos | 200 |
| GET | `/api/movimientos/{id}` | Buscar por id | 200 |
| POST | `/api/movimientos` | Registrar ENTRADA o SALIDA | 201 |
| GET | `/api/movimientos/producto/{productoId}` | Movimientos de un producto (RF 64) | 200 |
| GET | `/api/movimientos/producto/{productoId}/stock` | Stock actual (RF 64) | 200 |

**Alcance del stock:** `stockActual = Σ cantidades ENTRADA − Σ cantidades SALIDA` del producto, calculado con una consulta JPQL agregada. Una SALIDA mayor al stock disponible se rechaza con **409**.

## 7. Validaciones y códigos HTTP

| Situación | Código | Ejemplo |
|---|---|---|
| Campo obligatorio vacío o formato inválido | **400** | RUC con menos de 11 dígitos, email inválido, precio ≤ 0 |
| **Cantidad ≤ 0** (RF 63) | **400** | `"cantidad": 0` → `"La cantidad debe ser mayor a cero"` |
| JSON mal formado o tipo no permitido | **400** | `"tipo": "DEVOLUCION"` |
| Recurso inexistente | **404** | `GET /api/productos/9999` |
| Duplicado (RUC/código) o stock insuficiente | **409** | Salida de 500 con stock 70 |
| Error no controlado | **500** | — |

Formato de error uniforme:
```json
{
  "timestamp": "2026-11-03T22:15:30.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos de entrada invalidos",
  "path": "/api/movimientos",
  "errors": { "cantidad": "La cantidad debe ser mayor a cero" }
}
```

## 8. Pruebas con Postman

Importar `docs/Lab07-Inventario.postman_collection.json` (`Import → File`). La colección guarda automáticamente los ids creados en las variables `proveedorId` y `productoId`, e incluye *tests* que verifican el código esperado. Se puede ejecutar completa con **Run collection**.

Detalle paso a paso de cada prueba: [`docs/PRUEBAS_POSTMAN.md`](docs/PRUEBAS_POSTMAN.md)

## 9. Conclusiones

1. **Separar el sistema en tres microservicios** permite que cada dominio evolucione, se despliegue y escale de forma independiente: un fallo o cambio en `ms-movimiento` no detiene el CRUD de productos ni de proveedores.
2. **El patrón *Database per Service*** asegura bajo acoplamiento: cada servicio es dueño de sus datos y las relaciones entre dominios se expresan con referencias lógicas por id (`proveedorId`, `productoId`) en lugar de llaves foráneas físicas entre bases.
3. **Spring Data JPA con Hibernate** reduce el código de persistencia: los repositorios generan las consultas a partir del nombre del método o de JPQL, y `ddl-auto` crea las tablas desde las entidades.
4. **Las validaciones declarativas en los DTO** (`@NotNull`, `@Positive`, `@Pattern`, `@Email`) junto con un `@RestControllerAdvice` garantizan que los datos inválidos se rechacen antes de llegar a la base, con respuestas HTTP coherentes (400, 404, 409) y mensajes claros.
5. **Los perfiles `dev` y `prod` en `application.yml`** separan la configuración del código: el mismo artefacto se ejecuta en distintos entornos cambiando solo el perfil y variables de entorno, sin exponer credenciales.

Preparación para la sustentación: [`docs/SUSTENTACION.md`](docs/SUSTENTACION.md)
