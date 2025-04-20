package org.example.urlshortener.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {
    private String title;

    private Integer status;

    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String title,
                         Integer status,
                         String message) {
        this.title = title;
        this.status = status;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ErrorResponse) obj;
        return Objects.equals(this.title, that.title) &&
                Objects.equals(this.status, that.status) &&
                Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, status, message);
    }

    @Override
    public String toString() {
        return "ErrorResponse[" +
                "title=" + title + ", " +
                "status=" + status + ", " +
                "message=" + message + ']';
    }


}
