package de.moldiy.molnet.exchange;

import de.moldiy.molnet.utils.BitVector;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Method;

public class MolNetMethodHandle {

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private final Object object;

    private MethodHandle methodHandle;

    private final BitVector rightsRequired;

    private final Class<? extends Annotation>[] annotationsClasses;

    public MolNetMethodHandle(Object object, Method m, BitVector rightsRequired) {
        this.object = object;
        this.rightsRequired = rightsRequired;

        Annotation[] annotations = m.getAnnotations();
        this.annotationsClasses = new Class[annotations.length];
        for(int i = 0; i < annotationsClasses.length; i++) {
            this.annotationsClasses[i] = annotations[i].annotationType();
        }

        try {
            methodHandle = lookup.unreflect(m);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // This makes that the invoke accepts an array as argument
        this.methodHandle = methodHandle.asSpreader(Object[].class, m.getParameterCount());
    }

    public boolean hasAccess(BitVector rights) {
        if (rights == null || rights.isEmpty()) {
            return this.rightsRequired.isEmpty();
        }
        return rights.containsAll(this.rightsRequired);
    }

    public void invoke(BitVector rights, Object... args) throws NoAccessRightsException, WrongMethodTypeException, ClassCastException, Throwable {
        if (!this.hasAccess(rights)) {
            throw new NoAccessRightsException("Access denied. The rights not equals rightsRequired.");
        }
        this.methodHandle.invoke(this.object, args);
    }

    public boolean isAnnotationPresents(Class<? extends Annotation> annotationClass) {
        for(int i = this.annotationsClasses.length - 1; i >= 0; i--) {
            if(this.annotationsClasses[i] == annotationClass) {
                return true;
            }
        }
        return false;
    }

    public Object getObject() {
        return object;
    }

    public MethodHandle getMethodHandle() {
        return this.methodHandle;
    }

    public BitVector getRightsRequired() {
        return rightsRequired;
    }

    public static class NoAccessRightsException extends RuntimeException {
        public NoAccessRightsException(String s) {
            super(s);
        }
    }

}
