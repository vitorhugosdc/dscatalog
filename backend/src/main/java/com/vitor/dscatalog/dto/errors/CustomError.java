package com.vitor.dscatalog.dto.errors;

import java.time.Instant;

public class CustomError {

    private Instant moment;
    private Integer status;
    private String error;
    private String path;

    public CustomError(Instant moment, Integer status, String error, String path) {
        this.moment = moment;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public Instant getMoment() {
        return moment;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}
