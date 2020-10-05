package de.moldiy.molnet.exchange;

import de.moldiy.molnet.utils.BitVector;

import java.lang.invoke.MethodHandle;

public class RightRestrictedMethod {

    private final Object object;

    private final MethodHandle methodHandle;

    private final BitVector rightsRequired;

    public RightRestrictedMethod(Object object, MethodHandle methodHandle, BitVector rightsRequired) {
        this.object = object;
        this.methodHandle = methodHandle;
        this.rightsRequired = rightsRequired;
    }

    public boolean hasAccess(BitVector rights) {
        return rights.containsAll(this.rightsRequired);
    }

    public void invoke(BitVector rights, Object... args) throws Throwable {
        if(!this.hasAccess(rights)) {
            throw new NoAccessRightsException("Access denied. The rights not equals rightsRequired.");
        }
        this.methodHandle.invoke(this.object, args);
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
