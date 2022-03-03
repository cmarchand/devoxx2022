package com.oxiane.formation.devoxx22.refacto.helpers;

import java.text.SimpleDateFormat;

public class VerboseDateFormat extends SimpleDateFormat {
    private final String format;

    public VerboseDateFormat(String format) {
        super(format);
        this.format = format;
    }

    public String getFormat() { return format; };


}
