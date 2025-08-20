package com.patrykmarchewka.concordiapi.DTO;

import jakarta.validation.groups.Default;
import org.springframework.core.annotation.AliasFor;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated
public @interface ValidateGroup {

    @AliasFor(annotation = Validated.class, attribute = "value")
    Class<?>[] value() default {Default.class};


}
