package com.delogica.tienda_api.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.delogica.tienda_api.domain.Order;
import com.delogica.tienda_api.domain.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>
{
        // Endpoint:    GET /api/orders
        //                  ?customerId={customerId}
        //                  &status={status}
        @Query(
        "SELECT o FROM Order o " +
        "WHERE (:customerId IS NULL OR o.customer.id = :customerId) " +
        "AND (:status IS NULL OR o.status = :status)"
        )
        Page<Order> findByFilters(
                @Param("customerId") Long customerId,
                @Param("status") OrderStatus status,
                Pageable pageable
        );
}
