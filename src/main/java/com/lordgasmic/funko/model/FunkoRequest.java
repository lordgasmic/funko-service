package com.lordgasmic.funko.model;

import lombok.Data;

import java.util.List;

@Data
public class FunkoRequest {

    private String title;
    private String fandom;
    private int seriesId;
    private String name;
    private List<Extras> extras;

    @Data
    public static class Extras {
        private String text;
    }
}
