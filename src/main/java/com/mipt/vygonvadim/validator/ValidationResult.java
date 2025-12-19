package com.mipt.vygonvadim.validator;

import java.util.ArrayList;
import java.util.List;

class ValidationResult {
    private boolean isValid;
    private List<String> errors;

    public ValidationResult() {
        this.errors = new ArrayList<>();
        this.isValid = true;
    }

    public boolean isValid() {
        return isValid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);
        this.isValid = false;
    }
}
