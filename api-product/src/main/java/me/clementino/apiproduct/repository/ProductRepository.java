package me.clementino.apiproduct.repository;

import me.clementino.apiproduct.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {

}

