# v1__init.sql
---

## Customers

| id | fullName   | email                    | phone            |
|----|------------|--------------------------|------------------|
| 1  | John Doe   | john.doe@example.com     | +34 600 111 222  |
| 2  | Jane Smith | jane.smith@example.com   | +34 600 333 444  |

---

## Addresses

| id | customer_id | line1             | line2    | city   | postalCode | country | is_default |
|----|-------------|-------------------|----------|--------|------------|---------|------------|
| 1  | 1 (John)    | Calle Mayor 1     | —        | Madrid | 28013      | España  | **TRUE**   |
| 2  | 1 (John)    | Avenida Prado 10  | Piso 2B  | Madrid | 28014      | España  | false      |
| 3  | 2 (Jane)    | Gran Vía 123      | —        | Madrid | 28010      | España  | **TRUE**   |

> Address 3 pertenece a Jane (customer 2). Usarla en un pedido de John (customer 1) debe devolver 400.

---

## Products

| id | sku   | name               | price | stock | active |
|----|-------|--------------------|-------|-------|--------|
| 1  | P-100 | Café Premium 250g  | 7.50  | 100   | true   |
| 2  | P-200 | Taza Cerámica      | 12.00 | 50    | true   |
| 3  | P-300 | Pack Galletas 6u   | 3.20  | 300   | true   |

> DELETE /api/products/{id} hace soft-delete (active = false). El producto sigue en BD pero no aparece en GET ni se puede pedir.

---

## Orders

| id | customer_id | shipping_address_id | status  | total |
|----|-------------|---------------------|---------|-------|
| 1  | 1 (John)    | 1                   | CREATED | 27.00 |
| 2  | 2 (Jane)    | 3                   | CREATED | 16.00 |

### Order Items

| order_id | product_id | quantity | unit_price |
|----------|------------|----------|------------|
| 1        | 1 (Café)   | 2        | 7.50       |
| 1        | 2 (Taza)   | 1        | 12.00      |
| 2        | 3 (Galletas)| 5       | 3.20       |
