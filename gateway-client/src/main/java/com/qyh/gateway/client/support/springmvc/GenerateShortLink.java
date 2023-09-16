package com.qyh.gateway.client.support.springmvc;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented

//自动生成短链
public @interface GenerateShortLink {
}
