# Preparación para la sustentación individual

## Pregunta: ¿Por qué separó el sistema en estos servicios?

- Cada servicio representa un **contexto de negocio distinto** con responsabilidades claras: Proveedor (quién abastece), Producto (qué se almacena) y Movimiento (cómo varía el stock).
- **Independencia:** cada uno es un proyecto Maven con su propio puerto (8081, 8082, 8083); se puede desplegar, reiniciar o escalar sin afectar a los demás. Por ejemplo, `ms-movimiento` es el que recibe más tráfico y podría escalarse solo.
- **Bajo acoplamiento y alta cohesión:** un cambio en la lógica de stock no obliga a recompilar el CRUD de productos.
- **Aislamiento de fallos:** si un servicio cae, los demás siguen respondiendo.

## Pregunta: ¿Cómo persiste los datos?

- Con **Spring Data JPA** sobre **Hibernate** como implementación de JPA, contra **MySQL**.
- Cada servicio tiene **su propia base**: `bd_proveedor`, `bd_producto`, `bd_movimiento` (patrón *Database per Service*).
- Las **entidades** (`@Entity`) se mapean a tablas; en `dev`, `ddl-auto: update` hace que Hibernate cree o actualice las tablas al iniciar.
- Los **repositorios** extienden `JpaRepository` y obtienen el CRUD sin escribir SQL. Consultas propias:
  - Derivadas del nombre: `findByProductoIdOrderByFechaDescIdDesc`, `existsByRuc`.
  - JPQL con `@Query` para sumar cantidades por tipo y calcular el stock.
- Entre servicios **no hay llaves foráneas físicas**: `productos.proveedor_id` y `movimientos.producto_id` son referencias lógicas por id, porque están en bases distintas.
- La capa de servicio usa `@Transactional` para que cada operación sea atómica (`readOnly = true` en consultas).

## Pregunta: ¿Qué ocurre cuando la API recibe información inválida?

1. El controlador recibe el JSON en un **DTO** marcado con `@Valid`.
2. **Bean Validation** revisa las anotaciones (`@NotNull`, `@Positive`, `@NotBlank`, `@Pattern`, `@Email`, `@Size`, `@PastOrPresent`).
3. Si algo falla, Spring lanza `MethodArgumentNotValidException` **antes de entrar al servicio**, así que nada se guarda en la base.
4. El `@RestControllerAdvice` (`GlobalExceptionHandler`) la captura y responde **400 Bad Request** con un JSON uniforme que indica qué campo falló y por qué.
5. Otros casos:
   - Id inexistente → `ResourceNotFoundException` → **404**.
   - RUC/código duplicado o salida mayor al stock → `BusinessException` → **409**.
   - JSON mal formado o tipo no válido (ej. `"DEVOLUCION"`) → **400**.
   - Cualquier error no previsto → **500** sin exponer el stacktrace.

**Ejemplo para mostrar en vivo:** `POST /api/movimientos` con `"cantidad": 0` → `400` con `"cantidad": "La cantidad debe ser mayor a cero"` (RF 63).

## Preguntas probables del profesor

**¿Por qué usar DTO y no la entidad directamente?**
Separan el contrato de la API del modelo de persistencia: validan la entrada, ocultan campos internos (el cliente no envía el `id`) y evitan que un cambio en la tabla rompa la API.

**¿Cómo calcula el stock?**
Con dos consultas JPQL `SUM(cantidad)` filtradas por producto y tipo: `stock = entradas − salidas`. No se guarda un campo stock, así siempre es consistente con el historial de movimientos.

**¿Por qué 409 y no 400 para stock insuficiente?**
Los datos tienen formato válido (400 sería formato incorrecto), pero entran en conflicto con el estado actual del recurso.

**¿Qué cambia entre `dev` y `prod`?**
URL y credenciales de BD (en prod vienen de variables de entorno), `ddl-auto` (`update` → `validate`), `show-sql` (`true` → `false`), nivel de logs y que en prod no se expone el stacktrace.

**¿Qué limitación tiene la versión actual?**
Como los servicios no se comunican entre sí, `ms-movimiento` no verifica que el `productoId` exista en `ms-producto`. La mejora natural es una llamada REST (RestClient/OpenFeign) y, a mayor escala, Eureka + API Gateway.

**¿Por qué `IDENTITY` en el id?**
Delega la generación a `AUTO_INCREMENT` de MySQL, que es simple y eficiente para este caso.
