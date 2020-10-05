package de.moldiy.molnet.exchange;

import de.moldiy.molnet.utils.BitVector;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MessageExchangerManager {

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private final HashMap<String, RightRestrictedMethod> idMethods = new HashMap<>();
    private final HashMap<String, Object> idObjects = new HashMap<>();

    private final RightIDFactory rightIDFactory;

    public MessageExchangerManager(RightIDFactory rightIDFactory) {
        this.rightIDFactory = rightIDFactory;
    }

    public synchronized void loadMassageExchanger(Object object) {
        assert object != null;
        Class<?> objectClass = object.getClass();

        for (Method m : objectClass.getDeclaredMethods()) {
            m.setAccessible(true);
            TrafficID trafficID = m.getAnnotation(TrafficID.class);
            if (trafficID != null) {
                String id = trafficID.id();
                MethodHandle methodHandle = null;
                try {
                    methodHandle = lookup.unreflect(m);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                this.idMethods.put(id, new RightRestrictedMethod(object, methodHandle, this.getRightFromMethod(object, m)));
                this.idObjects.put(id, object);
            }
        }
    }

    private BitVector getRightFromMethod(Object object, Method method) {
        BitVector rightBits = new BitVector();

        Rights allMethodRights = object.getClass().getAnnotation(Rights.class);
        if (allMethodRights != null) {
            this.rightIDFactory.setRightBitsFromStringRights(rightBits, allMethodRights.rights());
        }
        Rights methodRights = method.getAnnotation(Rights.class);
        if (methodRights != null) {
            this.rightIDFactory.setRightBitsFromStringRights(rightBits, methodRights.rights());
        }

        return rightBits;
    }


    /**
     * @param id   the Method id.
     * @param args The arguments from the Method.
     * @return return true if exec was successful
     * @throws Throwable throws if something went wrong
     */
    public boolean exec(String id, BitVector rightBits, Object... args) throws Throwable {
        RightRestrictedMethod methodHandle = this.idMethods.get(id);
        if (methodHandle != null) {
            Object object = this.idObjects.get(id);

            methodHandle.invoke(rightBits, args);


            return true;
        }
        return false;
    }

}
