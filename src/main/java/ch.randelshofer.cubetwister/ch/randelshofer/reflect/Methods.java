/* @(#)Methods.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.reflect;

import org.jhotdraw.annotation.Nonnull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * Methods contains convenience methods for method invocations using
 * java.lang.reflect.
 *
 * @author  Werner Randelshofer
 */
public class Methods {
    /**
     * Prevent instance creation.
     */
    private Methods() {
    }

    /**
     * Invokes the specified accessible parameterless method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     * @return NoSuchMethodException if the method does not exist or is not
     * accessible.
     */
    public static Object invoke(@Nonnull Object obj, @Nonnull String methodName)
            throws NoSuchMethodException {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[0]);
            Object result = method.invoke(obj, new Object[0]);
            return result;
        } catch (IllegalAccessException e) {
            throw new NoSuchMethodException(methodName + " is not accessible");
        } catch (InvocationTargetException e) {
            // The method is not supposed to throw exceptions
            throw new InternalError(e.getMessage());
        }
    }
    /**
     * Invokes the specified accessible method with a string parameter if it exists.
     *
     * @param obj The object on which to invoke the method.
     * @param methodName The name of the method.
     * @param stringParameter The String parameter
     * @return The return value of the method or METHOD_NOT_FOUND.
     * @return NoSuchMethodException if the method does not exist or is not accessible.
     */
    public static Object invoke(@Nonnull Object obj, @Nonnull String methodName, String stringParameter)
            throws NoSuchMethodException {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[]{String.class});
            Object result = method.invoke(obj, new Object[]{stringParameter});
            return result;
        } catch (IllegalAccessException e) {
            throw new NoSuchMethodException(methodName + " is not accessible");
        } catch (InvocationTargetException e) {
            // The method is not supposed to throw exceptions
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Invokes the specified method if it exists.
     *
     * @param obj The object on which to invoke the method.
     * @param methodName The name of the method.
     * @param types The parameter types.
     * @param values The parameter values.
     * @return The return value of the method.
     * @return NoSuchMethodException if the method does not exist or is not accessible.
     */
    public static Object invoke(@Nonnull Object obj, @Nonnull String methodName, Class<?>[] types, Object[] values)
            throws NoSuchMethodException {
        try {
            Method method = obj.getClass().getMethod(methodName, types);
            Object result = method.invoke(obj, values);
            return result;
        } catch (IllegalAccessException e) {
            throw new NoSuchMethodException(methodName + " is not accessible");
        } catch (InvocationTargetException e) {
            // The method is not supposed to throw exceptions
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Invokes the specified accessible parameterless method if it exists.
     *
     * @param clazz The class on which to invoke the method.
     * @param methodName The name of the method.
     * @return The return value of the method or METHOD_NOT_FOUND.
     * @return NoSuchMethodException if the method does not exist or is not accessible.
     */
    public static Object invokeStatic(@Nonnull Class<?> clazz, @Nonnull String methodName)
            throws NoSuchMethodException {
        try {
            Method method = clazz.getMethod(methodName, new Class<?>[0]);
            Object result = method.invoke(null, new Object[0]);
            return result;
        } catch (IllegalAccessException e) {
            throw new NoSuchMethodException(methodName + " is not accessible");
        } catch (InvocationTargetException e) {
            // The method is not supposed to throw exceptions
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Invokes the specified static parameterless method if it exists.
     *
     * @param clazz The class on which to invoke the method.
     * @param methodName The name of the method.
     * @return The return value of the method.
     * @return NoSuchMethodException if the method does not exist or is not accessible.
     */
    public static Object invokeStatic(String clazz, @Nonnull String methodName)
            throws NoSuchMethodException {
        try {
            return invokeStatic(Class.forName(clazz), methodName);
        } catch (ClassNotFoundException e) {
            throw new NoSuchMethodException("class " + clazz + " not found");
        }
    }

    /**
     * Invokes the specified static method if it exists.
     *
     * @param clazz      The class on which to invoke the method.
     * @param methodName The name of the method.
     * @param types      The parameter types.
     * @param values     The parameter values.
     * @return NoSuchMethodException if the method does not exist or is not accessible.
     */
    public static Object invokeStatic(@Nonnull Class<?> clazz, @Nonnull String methodName,
                                      Class<?>[] types, Object[] values)
            throws NoSuchMethodException {
        try {
            Method method = clazz.getMethod(methodName, types);
            Object result = method.invoke(null, values);
            return result;
        } catch (IllegalAccessException e) {
            throw new NoSuchMethodException(methodName + " is not accessible");
        } catch (InvocationTargetException e) {
            // The method is not supposed to throw exceptions
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Invokes the specified static method if it exists.
     *
     * @param clazz The class on which to invoke the method.
     * @param methodName The name of the method.
     * @param types The parameter types.
     * @param values The parameter values.
     * @return The return value of the method.
     * @return NoSuchMethodException if the method does not exist or is not accessible.
     */
    public static Object invokeStatic(String clazz, @Nonnull String methodName,
                                      Class<?>[] types, Object[] values)
            throws NoSuchMethodException {
        try {
            return invokeStatic(Class.forName(clazz), methodName, types, values);
        } catch (ClassNotFoundException e) {
            throw new NoSuchMethodException("class " + clazz + " not found");
        }
    }

    /**
     * Invokes the specified static method if it exists.
     *
     * @param clazz      The class on which to invoke the method.
     * @param methodName The name of the method.
     * @param type       The parameter types.
     * @param value      The parameter values.
     * @return NoSuchMethodException if the method does not exist or is not accessible.
     */
    public static Object invokeStatic(String clazz, @Nonnull String methodName,
                                      Class<?> type, Object value)
            throws NoSuchMethodException {
        try {
            return invokeStatic(Class.forName(clazz), methodName, new Class<?>[]{type}, new Object[]{value});
        } catch (ClassNotFoundException e) {
            throw new NoSuchMethodException("class " + clazz + " not found");
        }
    }

    /**
     * Invokes the specified static method if it exists.
     *
     * @param clazz        The class on which to invoke the method.
     * @param methodName   The name of the method.
     * @param types        The parameter types.
     * @param values       The parameter values.
     * @param defaultValue The default value.
     * @return The return value of the method or the default value if the method
     * does not exist or is not accessible.
     */
    public static Object invokeStatic(String clazz, @Nonnull String methodName,
                                      Class<?>[] types, Object[] values, Object defaultValue) {
        try {
            return invokeStatic(Class.forName(clazz), methodName, types, values);
        } catch (ClassNotFoundException e) {
            return defaultValue;
        } catch (NoSuchMethodException e) {
            return defaultValue;
        }
    }

    /**
     * Invokes the specified static method if it exists.
     *
     * @param clazz      The class on which to invoke the method.
     * @param methodName The name of the method.
     * @param type       The parameter type.
     * @param value      The parameter value.
     * @return The return value of the method or the default value if the method
     * does not exist or is not accessible.
     */
    public static Object invokeStatic(@Nonnull Class<?> clazz, @Nonnull String methodName,
                                      Class<?> type, Object value) throws NoSuchMethodException {
        return invokeStatic(clazz, methodName, new Class<?>[]{type}, new Object[]{value});
    }

    /**
     * Invokes the specified getter method if it exists.
     *
     * @param obj The object on which to invoke the method.
     * @param methodName The name of the method.
     * @param defaultValue This value is returned, if the method does not exist.
     * @return The value returned by the getter method or the default value.
     */
    public static int invokeGetter(@Nonnull Object obj, @Nonnull String methodName, int defaultValue) {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[0]);
            Object result = method.invoke(obj, new Object[0]);
            return ((Integer) result).intValue();
        } catch (NoSuchMethodException e) {
            return defaultValue;
        } catch (IllegalAccessException e) {
            return defaultValue;
        } catch (InvocationTargetException e) {
            return defaultValue;
        }
    }

    /**
     * Invokes the specified getter method if it exists.
     *
     * @param obj The object on which to invoke the method.
     * @param methodName The name of the method.
     * @param defaultValue This value is returned, if the method does not exist.
     * @return The value returned by the getter method or the default value.
     */
    public static long invokeGetter(@Nonnull Object obj, @Nonnull String methodName, long defaultValue) {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[0]);
            Object result = method.invoke(obj, new Object[0]);
            return ((Long) result).longValue();
        } catch (NoSuchMethodException e) {
            return defaultValue;
        } catch (IllegalAccessException e) {
            return defaultValue;
        } catch (InvocationTargetException e) {
            return defaultValue;
        }
    }

    /**
     * Invokes the specified getter method if it exists.
     *
     * @param obj The object on which to invoke the method.
     * @param methodName The name of the method.
     * @param defaultValue This value is returned, if the method does not exist.
     * @return The value returned by the getter method or the default value.
     */
    public static boolean invokeGetter(@Nonnull Object obj, @Nonnull String methodName, boolean defaultValue) {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[0]);
            Object result = method.invoke(obj, new Object[0]);
            return ((Boolean) result).booleanValue();
        } catch (NoSuchMethodException e) {
            return defaultValue;
        } catch (IllegalAccessException e) {
            return defaultValue;
        } catch (InvocationTargetException e) {
            return defaultValue;
        }
    }

    /**
     * Invokes the specified getter method if it exists.
     *
     * @param obj The object on which to invoke the method.
     * @param methodName The name of the method.
     * @param defaultValue This value is returned, if the method does not exist.
     * @return The value returned by the getter method or the default value.
     */
    public static Object invokeGetter(@Nonnull Object obj, @Nonnull String methodName, Object defaultValue) {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[0]);
            Object result = method.invoke(obj, new Object[0]);
            return result;
        } catch (NoSuchMethodException e) {
            return defaultValue;
        } catch (IllegalAccessException e) {
            return defaultValue;
        } catch (InvocationTargetException e) {
            return defaultValue;
        }
    }

    /**
     * Invokes the specified getter method if it exists.
     *
     * @param clazz The class on which to invoke the method.
     * @param methodName The name of the method.
     * @param defaultValue This value is returned, if the method does not exist.
     * @return The value returned by the getter method or the default value.
     */
    public static boolean invokeStaticGetter(@Nonnull Class<?> clazz, @Nonnull String methodName,
                                             boolean defaultValue) {
        try {
            Method method = clazz.getMethod(methodName, new Class<?>[0]);
            Object result = method.invoke(null, new Object[0]);
            return ((Boolean) result).booleanValue();
        } catch (NoSuchMethodException e) {
            return defaultValue;
        } catch (IllegalAccessException e) {
            return defaultValue;
        } catch (InvocationTargetException e) {
            return defaultValue;
        }
    }

    /**
     * Invokes the specified setter method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     */
    public static Object invoke(@Nonnull Object obj, @Nonnull String methodName, boolean newValue)
            throws NoSuchMethodException {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[]{Boolean.TYPE});
            return method.invoke(obj, new Object[]{new Boolean(newValue)});
        } catch (IllegalAccessException e) {
            throw new NoSuchMethodException(methodName + " is not accessible");
        } catch (InvocationTargetException e) {
            // The method is not supposed to throw exceptions
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Invokes the specified method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     */
    public static Object invoke(@Nonnull Object obj, @Nonnull String methodName, int newValue)
            throws NoSuchMethodException {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[]{Integer.TYPE});
            return method.invoke(obj, new Object[]{new Integer(newValue)});
        } catch (IllegalAccessException e) {
            throw new NoSuchMethodException(methodName + " is not accessible in " + obj.getClass());
        } catch (InvocationTargetException e) {
            Error err;
            // The method is not supposed to throw exceptions
            if (e.getMessage() == null) {
                err = new InternalError("InvocationTargetException on method "+methodName+"(int) in "+obj.getClass());
            } else {
            err = new InternalError(e.getMessage());
            }
            err.initCause(e);
            throw err;
        }
    }

    /**
     * Invokes the specified setter method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     */
    public static Object invoke(@Nonnull Object obj, @Nonnull String methodName, float newValue)
            throws NoSuchMethodException {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[]{Float.TYPE});
            return method.invoke(obj, new Object[]{new Float(newValue)});
        } catch (IllegalAccessException e) {
            throw new NoSuchMethodException(methodName + " is not accessible");
        } catch (InvocationTargetException e) {
            // The method is not supposed to throw exceptions
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Invokes the specified setter method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     */
    public static Object invoke(@Nonnull Object obj, @Nonnull String methodName, Class<?> clazz, Object newValue)
            throws NoSuchMethodException {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class<?>[]{clazz});
            return method.invoke(obj, new Object[]{newValue});
        } catch (IllegalAccessException e) {
            throw new NoSuchMethodException(methodName + " is not accessible");
        } catch (InvocationTargetException e) {
            // The method is not supposed to throw exceptions
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Invokes the specified setter method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     */
    public static void invokeIfExists(@Nonnull Object obj, @Nonnull String methodName) {
        try {
            invoke(obj, methodName);
        } catch (NoSuchMethodException e) {
            // ignore
        }
    }

    /**
     * Invokes the specified setter method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     */
    public static void invokeIfExists(@Nonnull Object obj, @Nonnull String methodName, int newValue) {
        try {
            invoke(obj, methodName, newValue);
        } catch (NoSuchMethodException e) {
            // ignore
        }
    }

    /**
     * Invokes the specified setter method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     */
    public static void invokeIfExists(@Nonnull Object obj, @Nonnull String methodName, float newValue) {
        try {
            invoke(obj, methodName, newValue);
        } catch (NoSuchMethodException e) {
            // ignore
        }
    }

    /**
     * Invokes the specified method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     */
    public static void invokeIfExists(@Nonnull Object obj, @Nonnull String methodName, boolean newValue) {
        try {
            invoke(obj, methodName, newValue);
        } catch (NoSuchMethodException e) {
            // ignore
        }
    }

    /**
     * Invokes the specified setter method if it exists.
     *
     * @param obj        The object on which to invoke the method.
     * @param methodName The name of the method.
     */
    public static void invokeIfExists(@Nonnull Object obj, @Nonnull String methodName, Class<?> clazz, Object newValue) {
        try {
            invoke(obj, methodName, clazz, newValue);
        } catch (NoSuchMethodException e) {
            // ignore
        }
    }
}
