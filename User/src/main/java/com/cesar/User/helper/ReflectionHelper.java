package com.cesar.User.helper;

import java.lang.reflect.Field;

import org.springframework.stereotype.Service;

@Service
public class ReflectionHelper {
	public Field[] getFieldss(Class<?> targetType){
		return targetType.getDeclaredFields();
	}
}