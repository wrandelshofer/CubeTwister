/*
 * @(#)GenericListener.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.event;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * The GenericListener creates anonymous listener classes at runtime.
 * <p>
 * Usage:
 * <pre>
 * public class Demo {
 *   JPanel root = new JPanel(new BorderLayout());
 *   JLabel label = new JLabel(" ");
 *
 *   public void myButtonAction(ActionEvent e) {
 *     label.setText("buttonAction");
 *   }
 *
 *   public void myMouseEntered(MouseEvent e) {
 *     label.setText("mouseEntered: "+e.toString());
 *   }
 *
 *   Demo() {
 *     JButton button = new JButton("Button with Dynamic Listener");
 *
 *     //This listener will be generated at run-time, i.e. at run-time
 *     // an ActionListener class will be code-generated and then
 *     // class-loaded.  Only one of these is actually created, even
 *     // if many calls to GenericListener.create(ActionListener.class ...)
 *     // are made.
 *     ActionListener actionListener = (ActionListener)(GenericListener.create(
 *       ActionListener.class,
 *       "actionPerformed",
 *       this,
 *       "myButtonAction")
 *     );
 *     button.addActionListener(actionListener);
 *
 *     // Here's another dynamically generated listener.  This one is
 *     // a little different because the listenerMethod argument actually
 *     // specifies one of many listener methods.  In the previous example
 *     // "actionPerformed" named the one and only ActionListener method.
 *     MouseListener mouseListener = (MouseListener)(GenericListener.create(
 *       MouseListener.class,
 *       "mouseEntered",
 *       this,
 *       "myMouseEntered")
 *     );
 *     button.addMouseListener(mouseListener);
 * </pre>
 *
 * @author Werner Randelshofer
 */
public abstract class GenericListener {
    /**
     * A convenient version of <code>create(listenerMethod, targetObject, targetMethod)</code>.
     * This version looks up the listener and target Methods, so you don't have to.
     */
    @Nonnull
    public static Object create(
            @Nonnull Class<?> listenerInterface,
            @Nonnull String listenerMethodName,
            @Nonnull Object target,
            @Nonnull String targetMethodName) {
        Method listenerMethod = getListenerMethod(listenerInterface, listenerMethodName);

        // Search a target method with the same parameter types as the listener method.
        Method targetMethod =
                getTargetMethod(target, targetMethodName, listenerMethod.getParameterTypes());

        // Nothing found? Search a target method with no parameters
        if (targetMethod == null) {
            targetMethod = getTargetMethod(target, targetMethodName, new Class<?>[0]);
        }

        // Still nothing found? We give up.
        if (targetMethod == null) {
            throw new RuntimeException("no such method "+targetMethodName+" in "+target.getClass());
        }

        return create(listenerMethod, target, targetMethod);
    }


    /**
     * Return an instance of a class that implements the interface that contains
     * the declaration for <code>listenerMethod</code>.  In this new class,
     * <code>listenerMethod</code> will apply <code>target.targetMethod</code>
     * to the incoming Event.
     */
    @Nonnull
    public static Object create(
            @Nonnull final Method listenerMethod,
            final Object target,
            @Nonnull final Method targetMethod) {
        /**
         * The implementation of the create method uses the Dynamic Proxy API
         * introduced in JDK 1.3.
         *
         * Create an instance of the DefaultInvoker and override the invoke
         * method to handle the invoking the targetMethod on the target.
         */
        InvocationHandler handler = new DefaultInvoker() {
            @Nullable
            @Override
            public Object invoke(@Nonnull Object proxy, @Nonnull Method method, Object[] args) throws Throwable {

                // Send all methods except for the targetMethod to
                // the superclass for handling.
                if (listenerMethod.equals(method)) {
                    if (targetMethod.getParameterTypes().length == 0) {
                        // Special treatment for parameterless target methods:
                        return targetMethod.invoke(target, new Object[0]);
                    } else {
                        // Regular treatment for target methods having the same
                        // argument list as the listener method.
                        return targetMethod.invoke(target, args);
                    }
                } else {
                    return super.invoke(proxy, method, args);
                }
            }
        };

        Class<?> cls = listenerMethod.getDeclaringClass();
        ClassLoader cl = cls.getClassLoader();
        return Proxy.newProxyInstance(cl, new Class<?>[]{cls}, handler);
    }

    /**
     * Implementation of the InvocationHandler which handles the basic
     * object methods.
     */
    private static class DefaultInvoker implements InvocationHandler {

        @Nullable
        public Object invoke(@Nonnull Object proxy, @Nonnull Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                String methodName = method.getName();
                if ("hashCode".equals(methodName)) {
                    return proxyHashCode(proxy);
                } else if ("equals".equals(methodName)) {
                    return proxyEquals(proxy, args[0]);
                } else if ("toString".equals(methodName)) {
                    return proxyToString(proxy);
                }
            }

            // Although listener methods are supposed to be void, we
            // allow for any return type here and produce null/0/false
            // as appropriate.
            return nullValueOf(method.getReturnType());
        }

        @Nonnull
        protected Integer proxyHashCode(Object proxy) {
            return new Integer(System.identityHashCode(proxy));
        }

        @Nonnull
        protected Boolean proxyEquals(Object proxy, Object other) {
            return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
        }

        @Nonnull
        protected String proxyToString(@Nonnull Object proxy) {
            return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
        }

        private final static Character char_0 = new Character((char) 0);
        private final static Byte byte_0 = new Byte((byte) 0);

        @Nullable
        private final static Object nullValueOf(@Nonnull Class<?> rt) {
            if (!rt.isPrimitive()) {
                return null;
            } else if (rt == void.class) {
                return null;
            } else if (rt == boolean.class) {
                return Boolean.FALSE;
            } else if (rt == char.class) {
                return char_0;
            }
            else {
                // this will convert to any other kind of number
                return byte_0;
            }
        }

    }

    /* Helper methods for "EZ" version of create(): */
    @Nullable
    private static Method getListenerMethod(@Nonnull Class<?> listenerInterface,
                                            @Nonnull String listenerMethodName) {
        // given the arguments to create(), find out which listener is desired:
        Method[] m = listenerInterface.getMethods();
        Method result = null;
        for (int i = 0; i < m.length; i++) {
            if (listenerMethodName.equals(m[i].getName())) {
                if (result != null) {
                    throw new RuntimeException("ambiguous method: " + m[i] + " vs. " + result);
                }
                result = m[i];
            }
        }
        if (result == null) {
            throw new RuntimeException("no such method " + listenerMethodName + " in " + listenerInterface);
        }
        return result;
    }

    @Nullable
    private static Method getTargetMethod(@Nonnull Object target,
                                          @Nonnull String targetMethodName,
                                          @Nonnull Class<?>[] parameterTypes) {
        Method[] m = target.getClass().getMethods();
        Method result = null;
        eachMethod:
        for (int i = 0; i < m.length; i++) {
            if (!targetMethodName.equals(m[i].getName())) {
                continue eachMethod;
            }
            Class<?>[] p = m[i].getParameterTypes();
            if (p.length != parameterTypes.length) {
                continue eachMethod;
            }
            for (int j = 0; j < p.length; j++) {
                if (!p[j].isAssignableFrom(parameterTypes[j])) {
                    continue eachMethod;
                }
            }
                if (result != null) {
                    throw new RuntimeException("ambiguous method: "+m[i]+" vs. "+result);
                }
                result = m[i];
            }
            /*
            if (result == null) {
                throw new RuntimeException("no such method "+targetMethodName+" in "+target.getClass());
            }*/
        if (result == null) {
            return null;
        }

        Method publicResult = raiseToPublicClass(result);
        if (publicResult != null) {
            result = publicResult;
        }

        return result;
    }

    @Nullable
    private static Method raiseToPublicClass(@Nonnull Method m) {
        Class<?> c = m.getDeclaringClass();
        if (Modifier.isPublic(m.getModifiers()) &&
                Modifier.isPublic(c.getModifiers())) {
            return m; // yes!
        }
        // search for a public version which m overrides
        Class<?> sc = c.getSuperclass();
        if (sc != null) {
            Method sm = raiseToPublicClass(m, sc);
            if (sm != null) {
                return sm;
            }
        }
        Class<?>[] ints = c.getInterfaces();
        for (int i = 0; i < ints.length; i++) {
            Method im = raiseToPublicClass(m, ints[i]);
            if (im != null) {
                return im;
            }
        }
        // no public version of m here
        return null;
    }

    @Nullable
    private static Method raiseToPublicClass(@Nonnull Method m, @Nonnull Class<?> c) {
        try {
            Method sm = c.getMethod(m.getName(), m.getParameterTypes());
            return raiseToPublicClass(sm);
        } catch (NoSuchMethodException ee) {
            return null;
        }
    }

    private GenericListener() {
    }
}
