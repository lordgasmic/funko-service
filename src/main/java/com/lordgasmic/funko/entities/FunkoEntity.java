package com.lordgasmic.funko.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "funko_vw")
public class FunkoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String fandom;

    @Column(name = "series_id")
    private int seriesId;

    private String name;
}
