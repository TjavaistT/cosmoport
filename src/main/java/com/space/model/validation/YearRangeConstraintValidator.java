package com.space.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;
import java.util.Date;

public class YearRangeConstraintValidator implements ConstraintValidator<YearRange, Date> {

   private YearRange annotYearRange;

   public void initialize(YearRange constraint) {
      this.annotYearRange = constraint;
   }

   public boolean isValid(Date prodDate, ConstraintValidatorContext context) {
      if(prodDate == null) return true;

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(prodDate);
      int year = calendar.get(Calendar.YEAR);

      return year > annotYearRange.min() && year < annotYearRange.max();
   }
}
