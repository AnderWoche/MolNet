package de.moldiy.molnet.exchange;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MessageExchangerManager {

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private final HashMap<String, MethodHandle> idMethods = new HashMap<>();
    private final HashMap<String, Object> idObjects = new HashMap<>();

    public synchronized void loadMassageExchanger(Object object) {
        assert object != null;
        Class<?> objectClass = object.getClass();

        Rights allMethodRights = objectClass.getAnnotation(Rights.class);

        for (Method m : objectClass.getDeclaredMethods()) {
            m.setAccessible(true);
            TrafficID trafficID = m.getAnnotation(TrafficID.class);
            if (trafficID != null) {
                String id = trafficID.id();
                Rights rights = m.getAnnotation(Rights.class);
                try {
                    MethodHandle methodHandle = lookup.unreflect(m);

                    if(rights != null) {

                    }

                    this.idMethods.put(id, methodHandle);
                    this.idObjects.put(id, object);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     *
     * @param id the Method id.
     * @param args The arguments from the Method.
     * @return return true if exec was successful
     *
     * @throws Throwable throws if something went wrong
     */
    public boolean exec(String id, Object... args) throws Throwable {
        MethodHandle methodHandle = this.idMethods.get(id);
        if(methodHandle != null) {
                Object object = this.idObjects.get(id);

                methodHandle.invoke(object, args);


                return true;
        }
        return false;
    }

}
