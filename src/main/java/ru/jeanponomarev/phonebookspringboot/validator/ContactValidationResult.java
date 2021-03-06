package ru.jeanponomarev.phonebookspringboot.validator;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ContactValidationResult {

    private boolean isValid;

    private String message;

    public ContactValidationResult() {

    }

    public ContactValidationResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    @JsonIgnore
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
