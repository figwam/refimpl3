/**
 * 
 */
package com.swisscom.refimpl.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author <a href="alexander.schamne@swisscom.com">Alexander Schamne</a>
 *
 */
public class ObjectUtil {
	
	public static <T> void copyValues(T object1, T object2) {
		Method[] methods = object1.getClass().getMethods();
		// put field names into collection
		String name = "";
		for (Method m : methods) {
			if (m.getName().startsWith("set")) {
				name =  m.getName().replaceAll("set", "");
				try {
					Method setMethodObject2 = object2.getClass().getDeclaredMethod("set"+name, m.getParameterTypes());
					Method getMethodObject1 = object1.getClass().getDeclaredMethod("get"+name);
					Object val;
					val = getMethodObject1.invoke(object1, new Object[]{});
					setMethodObject2.invoke(object2, val);
				} catch (NoSuchMethodException e) {
					// not every getter has a setter and vice versa, just ignore
				} catch (IllegalArgumentException e) {
					// normally should not happen, as the object are the same (type safe)
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// normally should not happen, as the object are the same (type safe)
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// normally should not happen, as the object are the same (type safe)
					e.printStackTrace();
				}
			}
		}
	}

}
