# Order — puntos clave

- **Estados con transiciones controladas**: `CREATED -> PAID -> SHIPPED` / `CREATED o PAID -> CANCELLED`. `SHIPPED` y `CANCELLED` son estados finales.
- **Restauración de stock al cancelar**: al pasar a `CANCELLED` se devuelve el stock de cada item al producto correspondiente.
- **Descuento de stock al crear**: al crear el pedido se descuenta el stock de cada producto inmediatamente.
- **Validación de dirección pertenece al cliente**: al crear un pedido se valida que la `shippingAddress` pertenece al customer del pedido.
- **El service recibe el DTO directamente**: la creación es demasiado compleja para delegarla al mapper. El service construye el `Order` y sus `OrderItem` manualmente.
- **Cascada en items**: `CascadeType.ALL` en `Order.items` — al persistir el `Order` se persisten todos sus `OrderItem` automáticamente.
- **Total calculado en el service**: se calcula sumando `unitPrice * quantity` de cada item antes de persistir. No se confía en el cliente para enviarlo.
- **Paginación con filtro por customerId y status**: `findAll` acepta `?customerId=` y `?status=` opcionales. Default: página 0, 10 elementos, orden por `orderDate` descendente.
- **Response devuelve solo IDs de customer y address**: el mapper extrae `customer.id` y `shippingAddress.id` en vez de los objetos completos.
