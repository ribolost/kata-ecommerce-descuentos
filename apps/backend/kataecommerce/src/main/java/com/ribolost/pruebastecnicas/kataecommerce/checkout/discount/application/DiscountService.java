package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.in.DiscountCalculationRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountCalculationResponse;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository.DiscountPolicyRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación del Motor de Descuentos.
 *
 * Expone la Command CalculateDiscounts (ver arquitectura.md, 1.9): calcula el
 * desglose de descuentos para el carrito, considerando opcionalmente un
 * cupón, sin modificar el stock ni el estado de un cupón.
 *
 * Este mismo servicio es consumido directamente por PlaceOrderService
 * (a través de DiscountCalculationPort / DiscountCalculationAdapter) durante
 * la confirmación de la compra, reutilizando la misma lógica de cálculo.
 */
@Service
public class DiscountService {

    private final DiscountPolicyRepository discountPolicyRepository;

    public DiscountService(DiscountPolicyRepository discountPolicyRepository) {
        this.discountPolicyRepository = discountPolicyRepository;
    }

    public DiscountCalculationResponse calculateDiscounts(DiscountCalculationRequest request) {
        // TODO: cargar la DiscountPolicy vigente, construir la cadena con
        // DiscountChainFactory y ejecutar el cálculo sobre un DiscountContext.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
