package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.controller;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.DiscountService;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.in.DiscountCalculationRequest;
import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.application.dto.out.DiscountCalculationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    public ResponseEntity<DiscountCalculationResponse> calculateDiscounts(
            @Valid @RequestBody DiscountCalculationRequest request) {
        return ResponseEntity.ok(discountService.calculateDiscounts(request));
    }
}
