package com.patrykmarchewka.concordiapi.DTO;

import jakarta.validation.groups.Default;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated({Default.class, OnCreate.class})
public @interface ValidateOnCreate {}