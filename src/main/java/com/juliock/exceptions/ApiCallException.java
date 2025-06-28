package com.juliock.exceptions;

public class ApiCallException extends RuntimeException {
    private Integer statusCode;
    private String apiMessage;

    public ApiCallException(Integer statusCode, String apiMessage) {
        super("Http error: " + statusCode + " - " + apiMessage);
        this.statusCode = statusCode;
        this.apiMessage = apiMessage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getApiMessage() {
        return apiMessage;
    }
}
