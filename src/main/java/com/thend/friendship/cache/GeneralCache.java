package com.thend.friendship.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD) 
@Retention(RetentionPolicy.RUNTIME) 
public @interface GeneralCache {
	String keyPre() default "";
	String key() default "";
	int cacheTime() default 60 * 10;
}
