package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.controllers;

public class ApiResponse {
    private int code;
    private Object data;
    private String message;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private ApiResponse(Builder builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.data = builder.data;
    }

    //Builder Class
    public static class Builder{
        private int code;
        private Object data;
        private String message;

        public Builder(){
        }

        public Builder(int code, String message, Object data){
            this.code=code;
            this.message=message;
            this.data=data;
        }

        public Builder setCode(int code) {
            this.code = code;
            return this;
        }

        public Builder setData(Object data) {
            this.data = data;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ApiResponse build(){
            return new ApiResponse(this);
        }

    }
}
