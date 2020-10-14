package de.moldiy.molnet.exchange;

import de.moldiy.molnet.utils.BitVector;
import de.moldiy.molnet.utils.BothSideHashMapWithArray;
import de.moldiy.molnet.utils.MapArray;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public abstract class MessageExchangerManager {

    private final Map<Class<?>, Object> massageExchangerMap = new IdentityHashMap<>();
    private final MapArray<Class<? extends Annotation>, RightRestrictedMethodHandle> annotationToMethodsMap = new MapArray<>();
    private final BothSideHashMapWithArray<String, RightRestrictedMethodHandle> idMethods = new BothSideHashMapWithArray<>(true);

    private final RightIDFactory rightIDFactory;

    public MessageExchangerManager(RightIDFactory rightIDFactory) {
        this.rightIDFactory = rightIDFactory;
    }

    public synchronized void loadMassageExchanger(Object object) {
        assert object != null;

        this.massageExchangerMap.put(object.getClass(), object);

        for (Method m : object.getClass().getDeclaredMethods()) {
            m.setAccessible(true);

            for(Annotation annotation : m.getDeclaredAnnotations()) {
                RightRestrictedMethodHandle methodHandle = this.createRightRestrictedMethodHandle(object, m, this.getRightsFromMethod(object, m));
                this.annotationToMethodsMap.put(annotation.annotationType(), methodHandle);

                if(annotation.annotationType() == TrafficID.class) {
                    String id = ((TrafficID) annotation).id();
                    this.idMethods.put(id, methodHandle);
                }
            }

        }
    }

    protected abstract RightRestrictedMethodHandle createRightRestrictedMethodHandle(Object o, Method m, BitVector bitVector);

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
        RightRestrictedMethodHandle methodHandle = this.idMethods.getValue(id);
        if (methodHandle == null) {
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

    @SuppressWarnings("unchecked")
    public <T> T getMassageExchanger(Class<T> objectClass) {
        return (T) this.massageExchangerMap.get(objectClass);
    }

    public List<RightRestrictedMethodHandle> getMethodsFromAnnotation(Class<? extends Annotation> annotation) {
        return this.annotationToMethodsMap.get(annotation);
    }

    public MapArray<Class<? extends Annotation>, RightRestrictedMethodHandle> getAnnotationToMethodsMap() {
        return annotationToMethodsMap;
    }

    public BothSideHashMapWithArray<String, RightRestrictedMethodHandle> getIdMethods() {
        return idMethods;
    }

    public static class TrafficIDNotExists extends RuntimeException {
        public TrafficIDNotExists(String message) {
            super(message);
        }
    }

}
