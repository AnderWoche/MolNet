package de.moldiy.molnet.exchange;

import de.moldiy.molnet.utils.BitVector;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageExchangerManager {

    private final Map<String, RightRestrictedMethodHandle> idMethods = Collections.synchronizedMap(new HashMap<>());

    private final RightIDFactory rightIDFactory;

    public MessageExchangerManager(RightIDFactory rightIDFactory) {
        this.rightIDFactory = rightIDFactory;
    }

    public synchronized void loadMassageExchanger(Object object) {
        assert object != null;

        for (Method m : object.getClass().getDeclaredMethods()) {
            m.setAccessible(true);

            TrafficID trafficID;
            if ((trafficID = m.getAnnotation(TrafficID.class)) != null) {
                String id = trafficID.id();

                this.idMethods.put(id, new RightRestrictedMethodHandle(object, m, this.getRightsFromMethod(object, m)));
            }
        }
    }

    private BitVector getRightsFromMethod(Object object, Method method) {
        BitVector rightBits = new BitVector();

        Rights allMethodRights = object.getClass().getAnnotation(Rights.class);
        if (allMethodRights != null) {
            this.rightIDFactory.addRightBits(rightBits, allMethodRights.rights());
        }
        Rights methodRights = method.getAnnotation(Rights.class);
        if (methodRights != null) {
            this.rightIDFactory.addRightBits(rightBits, methodRights.rights());
        }

        return rightBits;
    }


    public RightRestrictedMethodHandle getRightRestrictedMethodHandle(String id) {
        RightRestrictedMethodHandle methodHandle = this.idMethods.get(id);
        if(methodHandle == null) {
            throw new TrafficIDNotExists("The TrafficID: " + id + " not exist!");
        }
        return methodHandle;
    }

    /**
     * @param id   the Method id.
     * @param args The arguments from the Method.
     * @throws Throwable throws if something went wrong
     */
    public void exec(String id, BitVector rightBits, Object... args) throws Throwable {
        RightRestrictedMethodHandle methodHandle = this.getRightRestrictedMethodHandle(id);
        methodHandle.invoke(rightBits, args);
    }

    public static class TrafficIDNotExists extends RuntimeException {
        public TrafficIDNotExists(String message) {
            super(message);
        }
    }

}
