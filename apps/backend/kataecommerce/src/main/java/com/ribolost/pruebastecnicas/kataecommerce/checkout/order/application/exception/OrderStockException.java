package com.ribolost.pruebastecnicas.kataecommerce.checkout.order.application.exception;

import com.ribolost.pruebastecnicas.kataecommerce.shared.error.BusinessException;

public class OrderStockException extends BusinessException {

    public OrderStockException(String message) {
        super(message);
    }

    public OrderStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
