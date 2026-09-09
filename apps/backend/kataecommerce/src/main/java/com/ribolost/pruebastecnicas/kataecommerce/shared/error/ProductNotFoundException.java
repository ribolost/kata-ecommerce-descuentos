package com.ribolost.pruebastecnicas.kataecommerce.shared.error;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
