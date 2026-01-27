package com.lordgasmic.funko.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Funko {
    private long id;
    private String title;
    private String fandom;
    private long seriesId;
    private String name;
    private List<FunkoExtra> extras = new ArrayList<>();
}
