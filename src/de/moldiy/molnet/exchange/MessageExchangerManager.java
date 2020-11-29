package de.moldiy.molnet.exchange;

import de.moldiy.molnet.utils.BitVector;
import de.moldiy.molnet.utils.BothSideHashMapWithArray;
import de.moldiy.molnet.utils.MapArray;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public abstract class MessageExchangerManager {

    private Class<? extends Annotation> filterType;
    private final Map<Class<?>, Object> messageExchangerMap = new IdentityHashMap<>();
    private final MapArray<Class<? extends Annotation>, MolNetMethodHandle> annotationToMethodsMap = new MapArray<>();
    private final BothSideHashMapWithArray<String, MolNetMethodHandle> idMethods = new BothSideHashMapWithArray<>(true);

    private final RightIDFactory rightIDFactory;

    public MessageExchangerManager(RightIDFactory rightIDFactory) {
        this.rightIDFactory = rightIDFactory;
    }

    public synchronized void loadMessageExchanger(Object object) {
        assert object != null;

        this.messageExchangerMap.put(object.getClass(), object);

        for (Method m : object.getClass().getDeclaredMethods()) {
            m.setAccessible(true);

            if (this.approveFilter(m)) {
                for (Annotation annotation : m.getDeclaredAnnotations()) {
                    MolNetMethodHandle methodHandle = this.createMolNetMethodHandle(object, m, this.getRightsFromMethod(object, m));
                    this.annotationToMethodsMap.put(annotation.annotationType(), methodHandle);

                    Class<? extends Annotation> annotationType = annotation.annotationType();


                    if (annotationType == TrafficID.class) {
                        String id = ((TrafficID) annotation).id();
                        this.idMethods.put(id, methodHandle);
                    }
                }
            }

        }
    }

    public void setClientFilter() {
        this.filterType = ClientOnly.class;
    }

    public void setServerFilter() {
        this.filterType = ServerOnly.class;
    }

    private boolean approveFilter(Method m) {
        if (!m.isAnnotationPresent(ClientOnly.class) && !m.isAnnotationPresent(ServerOnly.class)) {
            return true;
        }
        if (m.isAnnotationPresent(ClientOnly.class)) {
            if (this.filterType == ClientOnly.class) {
                return true;
            }
        }
        if (m.isAnnotationPresent(ServerOnly.class)) {
            return this.filterType == ServerOnly.class;
        }
        return false;
    }

    protected abstract MolNetMethodHandle createMolNetMethodHandle(Object o, Method m, BitVector bitVector);

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


    public MolNetMethodHandle getRightRestrictedMethodHandle(String id) {
        MolNetMethodHandle methodHandle = this.idMethods.getValue(id);
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
        MolNetMethodHandle methodHandle = this.getRightRestrictedMethodHandle(id);
        methodHandle.invoke(rightBits, args);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMassageExchanger(Class<T> objectClass) {
        return (T) this.messageExchangerMap.get(objectClass);
    }

    public Collection<Object> getAllMessageExchanger() {
        return this.messageExchangerMap.values();
    }

    public List<MolNetMethodHandle> getMethodsFromAnnotation(Class<? extends Annotation> annotation) {
        return this.annotationToMethodsMap.get(annotation);
    }

    public MapArray<Class<? extends Annotation>, MolNetMethodHandle> getAnnotationToMethodsMap() {
        return annotationToMethodsMap;
    }

    public BothSideHashMapWithArray<String, MolNetMethodHandle> getIdMethods() {
        return idMethods;
    }

    public static class TrafficIDNotExists extends RuntimeException {
        public TrafficIDNotExists(String message) {
            super(message);
        }
    }

}
