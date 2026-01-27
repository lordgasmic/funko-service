package com.lordgasmic.funko.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "funko_extras_vw")
public class FunkoExtrasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "funko_id")
    private long funkoId;

    private String text;
}
