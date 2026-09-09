package com.ribolost.pruebastecnicas.kataecommerce.shared.error;

public class EmptyCartException extends BusinessException {

    public EmptyCartException(String message) {
        super(message);
    }
}
