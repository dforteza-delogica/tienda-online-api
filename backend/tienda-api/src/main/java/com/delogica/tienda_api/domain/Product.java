package com.delogica.tienda_api.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter @Setter 
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String sku;

    @Column()
    private String name;

    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column()
    private Integer stock;

    @Column()
    private Boolean active;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column()
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() 
    {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}