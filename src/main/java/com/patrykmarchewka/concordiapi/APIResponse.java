package com.patrykmarchewka.concordiapi;

import java.time.OffsetDateTime;

public class APIResponse<T> {
    private String message;
    private T data;
    private String timestamp;

    public APIResponse(String message, T data){
        this.message = message;
        this.data = data;
        this.timestamp = OffsetDateTimeConverter.formatDate(OffsetDateTime.now());
    }

    public String getMessage(){return message;}
    public void setMessage(String message) {this.message = message;}

    public T getData() {return data;}
    public void setData(T data){this.data = data;}

    public String getTimestamp(){return timestamp;}
    public void setTimestamp(String timestamp){this.timestamp = timestamp;}
}
