package com.carsharing.service.impl.payment.calculators;

import com.carsharing.model.Rental;
import com.carsharing.model.enums.PaymentType;
import java.math.BigDecimal;

public interface PaymentCalculator {
    PaymentType getType();

    BigDecimal calculate(Rental rental);
}
