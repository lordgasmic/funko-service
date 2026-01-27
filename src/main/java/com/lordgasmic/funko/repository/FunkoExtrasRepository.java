package com.lordgasmic.funko.repository;

import com.lordgasmic.funko.entities.FunkoExtrasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunkoExtrasRepository extends JpaRepository<FunkoExtrasEntity, Long> {
}
