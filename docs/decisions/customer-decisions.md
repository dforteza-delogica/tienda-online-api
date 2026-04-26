# Customer — puntos clave

- **Borrado físico**: los clientes se eliminan de BD. No hay soft delete.
- **Cascada en addresses**: `CascadeType.ALL` + `orphanRemoval = true` — al borrar un customer se borran sus addresses automáticamente.
- **Email único**: `save()` valida con `existsByEmail` antes de persistir. Lanza 409 si ya existe.
- **Validación de email en update**: solo comprueba unicidad si el email cambió. Evita que el cliente choque con su propio email.
- **Addresses separadas del customer**: se añaden tras la creación con `POST /api/customers/{id}/addresses`. El mapper ignora `addresses` en create y update.
- **Una sola dirección por defecto**: al marcar una como default, `clearDefaultByCustomerId` pone `isDefault = false` en todas las del cliente antes con un UPDATE directo en BD.
- **`@Modifying` + `@Transactional` en el repository**: necesario para ejecutar el UPDATE de `clearDefaultByCustomerId` fuera del flujo estándar de JPA.
- **Validación de pertenencia en `setDefaultAddress`**: se usa `findByIdAndCustomerId` — una sola query que valida existencia y pertenencia a la vez.
- **Paginación con filtro por email**: `findAll` acepta `?email=` opcional. Sin filtro devuelve todos los clientes paginados.
