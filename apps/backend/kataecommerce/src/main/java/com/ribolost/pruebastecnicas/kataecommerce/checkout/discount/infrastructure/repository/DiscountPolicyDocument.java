package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository;

import com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain.DiscountRuleDefinition;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;


@Document(collection = "discount_policies")
public class DiscountPolicyDocument {

    @Id
    private String id;

    @Field("rules")
    private List<DiscountRuleDefinition> rules = new ArrayList<>();

    public DiscountPolicyDocument() {
    }

    public DiscountPolicyDocument(String id, List<DiscountRuleDefinition> rules) {
        this.id = id;
        this.rules = rules;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DiscountRuleDefinition> getRules() {
        return rules;
    }

    public void setRules(List<DiscountRuleDefinition> rules) {
        this.rules = rules;
    }
}
