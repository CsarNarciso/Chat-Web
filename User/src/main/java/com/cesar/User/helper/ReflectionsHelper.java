package com.cesar.User.helper;

import java.lang.reflect.Field;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class ReflectionsHelper {
	
	public Field[] getFieldss(Class<?> targetType){
		return targetType.getDeclaredFields();
	}
	
	public Field findField(Class<?> targetType, String name){
		return ReflectionUtils.findField(targetType, name);
	}
}