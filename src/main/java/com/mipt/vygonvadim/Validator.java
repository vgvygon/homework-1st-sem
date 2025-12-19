package com.mipt.vygonvadim;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotNull {
    String message();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Size {
    int min();
    int max();
    String message();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Range {
    long min();
    long max();
    String message();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Email {
    String message();
}

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

public class Validator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static ValidationResult validate(Object object) {
        ValidationResult result = new ValidationResult();

        if (object == null) {
            result.addError("Validated object is null");
            return result;
        }

        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            Object value;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                continue;
            }

            if (field.isAnnotationPresent(NotNull.class)) {
                NotNull annotation = field.getAnnotation(NotNull.class);
                if (value == null) {
                    result.addError(annotation.message());
                }
            }

            if (field.isAnnotationPresent(Size.class) && value != null) {
                Size annotation = field.getAnnotation(Size.class);
                if (value instanceof String) {
                    String strValue = (String) value;
                    int length = strValue.length();
                    if (length < annotation.min() || length > annotation.max()) {
                        result.addError(annotation.message());
                    }
                }
            }

            if (field.isAnnotationPresent(Range.class) && value != null) {
                Range annotation = field.getAnnotation(Range.class);
                if (value instanceof Number) {
                    long longValue = ((Number) value).longValue();
                    if (longValue < annotation.min() || longValue > annotation.max()) {
                        result.addError(annotation.message());
                    }
                }
            }

            if (field.isAnnotationPresent(Email.class) && value != null) {
                Email annotation = field.getAnnotation(Email.class);
                if (value instanceof String) {
                    String email = (String) value;
                    if (!EMAIL_PATTERN.matcher(email).matches()) {
                        result.addError(annotation.message());
                    }
                }
            }
        }

        return result;
    }
}
