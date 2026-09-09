package com.ribolost.pruebastecnicas.kataecommerce.shared.validation;

import com.ribolost.pruebastecnicas.kataecommerce.shared.error.InvalidResponseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ResponseValidator {

    private final Validator validator;

    public ResponseValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> T validate(T response) {
        Set<ConstraintViolation<T>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            String detail = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new InvalidResponseException(
                    "La respuesta generada no cumple su propio contrato de validación: " + detail);
        }
        return response;
    }

    public <T> List<T> validateAll(List<T> responses) {
        responses.forEach(this::validate);
        return responses;
    }
}
