package com.cesar.User.helper;

import java.lang.reflect.Field;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Service
public class ReflectionHelper {
	
	public Field[] getFieldss(Class<?> targetType){
		return targetType.getDeclaredFields();
	}
	
	public Field findField(Class<?> targetType, String name){
		return ReflectionUtils.findField(targetType, name);
	}
}