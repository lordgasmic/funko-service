package com.lordgasmic.funko.model;

import lombok.Data;

@Data
public class FunkoResponse {
    private int id;
    private String title;
    private String fandom;
    private int seriesId;
    private String name;
}
