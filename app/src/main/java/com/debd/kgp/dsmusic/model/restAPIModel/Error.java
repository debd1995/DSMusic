package com.debd.kgp.dsmusic.model.restAPIModel;

import java.io.Serializable;

public class Error implements Serializable{

    public static final int USERNAME_ALREADY_EXISTS = 460;
    public static final int AUTHENTICATION_ERROR = 461;
    public static final int AUTHORIZATION_ERROR = 462;
    public static final int GENERICS_ERROR = 500;
    public static final int SIGNUP_ERROR = 470;
    public static final int UPLOAD_ERROR = 471;
    public static final int STREAMING_ERROR = 480;
    
    private String responseMessage, description, suggession;
    private int responseCode;

    public Error() {}

    public Error(String responseMessage, String description, String suggession, int errorCode) {
        this.responseMessage = responseMessage;
        this.description = description;
        this.suggession = suggession;
        this.responseCode = errorCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggession() {
        return suggession;
    }

    public void setSuggession(String suggession) {
        this.suggession = suggession;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    
    
    
    
}
