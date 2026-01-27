package com.lordgasmic.funko.repository;

import com.lordgasmic.funko.entities.FunkoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunkoRepository extends JpaRepository<FunkoEntity, Long> {
}
