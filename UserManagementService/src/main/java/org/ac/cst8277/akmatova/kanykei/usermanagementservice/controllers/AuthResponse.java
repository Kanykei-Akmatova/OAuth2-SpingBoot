package org.ac.cst8277.akmatova.kanykei.usermanagementservice.controllers;

public class AuthResponse {
    private int code;
    private Object user;
    private String status;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private AuthResponse(Builder builder) {
        this.code = builder.code;
        this.status = builder.status;
        this.user = builder.user;
    }

    //Builder Class
    public static class Builder{
        private int code;
        private Object user;
        private String status;

        public Builder(){
        }

        public Builder(int code, String status, Object data){
            this.code = code;
            this.status = status;
            this.user = data;
        }

        public Builder setCode(int code) {
            this.code = code;
            return this;
        }

        public Builder setUser(Object user) {
            this.user = user;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public AuthResponse build(){
            return new AuthResponse(this);
        }
    }
}
