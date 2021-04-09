package ru.jeanponomarev.phonebookspringboot.error;

public class ErrorInfo {

    private String errorMessage;

    public ErrorInfo(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
