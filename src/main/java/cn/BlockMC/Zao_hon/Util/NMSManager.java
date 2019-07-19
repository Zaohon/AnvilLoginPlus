package cn.BlockMC.Zao_hon.Util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public class NMSManager {
	public static Class<?> getHandleClass(Class<?> clazz) {
		Method m = getMethod("getHandle", clazz, new Class[0]);
		return m.getReturnType();

	}

	public static Object getHandle(Object o) {
		try {
			Method m = getMethod("getHandle", o.getClass(), new Class[0]);
			Object handle = m.invoke(o, new Object[0]);
			return handle;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Object invokeMethod(String method, Object o, Object[] parameters) {

		try {
			Class<?>[] paraclazz = new Class<?>[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				paraclazz[i] = parameters[i].getClass();
			}

			Method m = getMethod(method, o.getClass(), paraclazz);
			return m.invoke(o, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static Object invokeMethod(String method, Object o) {
		try {
			return o.getClass().getMethod(method, new Class[0]).invoke(o, new Object[0]);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setField(String fieldname, Object o, Object value) {
		Class<?> clazz = o.getClass();
		try {
			Field field = clazz.getField(fieldname);
			field.setAccessible(true);
			field.set(o, value);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();

		}
	}

	public static Field getField(String fieldname, Class<?> clazz) {
		try {
			Field field = clazz.getField(fieldname);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getField(String fieldname, Object o) {
		try {
			Field field = o.getClass().getField(fieldname);
			field.setAccessible(true);
			return field.get(o);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(String name, Class<?> clazz, Class<?>[] parameterTypes) {
		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getNMSClass(String classname) {
		String fullname = "net.minecraft.server" + getVersion() + classname;
		try {
			Class<?> clazz = Class.forName(fullname);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getCraftClass(String classname) {
		String fullname = "org.bukkit.craftbukkit" + getVersion() + classname;

		Class<?> clazz;
		try {
			clazz = Class.forName(fullname);
			return clazz;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getVersion() {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		version = version.substring(version.lastIndexOf(".")) + ".";
		return version;
	}

}
