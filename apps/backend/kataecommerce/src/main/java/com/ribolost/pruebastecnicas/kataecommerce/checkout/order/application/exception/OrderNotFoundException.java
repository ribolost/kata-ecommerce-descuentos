package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.exception;

import com.ribolost.pruebastecnicas.kataecommerce.shared.error.BusinessException;

public class OrderNotFoundException extends BusinessException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
