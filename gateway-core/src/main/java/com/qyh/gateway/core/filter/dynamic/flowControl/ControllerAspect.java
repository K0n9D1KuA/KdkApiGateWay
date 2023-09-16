package com.qyh.gateway.core.filter.dynamic.flowControl;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ControllerAspect {
    String type();
}
