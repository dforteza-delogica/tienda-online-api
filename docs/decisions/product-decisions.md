# Product — puntos clave

- **Soft delete**: `active = false` en vez de borrado físico. Preserva referencias desde Order.
- **`findByIdAndActiveTrue`**: todos los reads y el update filtran por activo. Un producto inactivo devuelve 404.
- **SKU inmutable**: no se puede cambiar en update. `ProductUpdateDto` no incluye el campo `sku`.
- **`update en service`** fundamental al no comparar la existencia del sku. Si compruebas la existencia en el update siempre será positiva al no poder cambiar y devuelve 409.
- **`ProductUpdateDto` separado**: el update no acepta `sku` ni `active`, solo `name`, `description`, `price` y `stock`.
- **Paginación con filtro por nombre**: `findAll` acepta `?name=` opcional. Default: página 0, 10 elementos, orden por `name`.

