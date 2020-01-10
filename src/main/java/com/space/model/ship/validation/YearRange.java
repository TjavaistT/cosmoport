package com.space.model.ship.validation;

import com.space.model.ship.Ship;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = YearRangeConstraintValidator.class)
public @interface YearRange {

    String message() default "{validation.Date.YearRange.NotInRange}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 0;

    int max() default 9999;
}
