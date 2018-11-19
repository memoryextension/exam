package org.memoryextension.java.samples.examMaker.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = UserRoleValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserRoleCheck {
    String message() default "{UserRole}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String clientRole() default "client";
}
