package com.reliaquest.api.model;

public class Response<T> {
    private T data; // holds the actual payload
    private String status; // optional status, e.g., "success" or "error"
    private String message; // optional message

    // Default constructor (needed for Jackson)
    public Response() {}

    // Constructor with data
    public Response(T data, String status) {
        this.data = data;
        this.status = status;
    }

    // Convenience method to create a successful response
    public static <T> Response<T> handledWith(T data) {
        return new Response<>(data, "success");
    }

    // Getters and setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
