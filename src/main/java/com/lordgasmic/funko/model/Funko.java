package com.lordgasmic.funko.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Funko {
    private int id;
    private String title;
    private String fandom;
    private int seriesId;
    private String name;
    private List<FunkoExtra> extras = new ArrayList<>();
}
