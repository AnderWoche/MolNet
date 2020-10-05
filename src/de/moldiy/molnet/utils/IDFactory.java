package de.moldiy.molnet.utils;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Moldiy (Humann)
 *
 */
public class IDFactory<T> {

    private final Map<T, Integer> ids;
    private final Map<Integer, T> valueFromID = new IdentityHashMap<>();
    private int nextID = 0;
    private final IntDeque freeIDs = new IntDeque();

    /**
     * @param identity if identity true, the Map is a IdentityMap if false, the Map is a HashMap
     */
    public IDFactory(boolean identity) {
        if(identity) {
            this.ids = new IdentityHashMap<>();
        } else {
            this.ids = new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    public Entry<T, Integer> getAll() {
        return (Entry<T, Integer>) ids.entrySet();
    }

    public int getOrCreateID(T object) {
//		Gdx.app.debug("[INFO] [IDFactory]", "hashmap size: " + ids.size() + " freeIDsSize = " + freeIDs.size());
        Integer componentID = ids.get(object);

        if (componentID == null) {
            componentID = this.createID(object);
        }

        return componentID;
    }

    public T getValueFromID(int id) {
        return this.valueFromID.get(id);
    }

    public Integer getID(T object) {
        return this.ids.get(object);
    }

    public boolean containsID(T object) {
        return this.ids.containsKey(object);
//		return ids.get(object) == null ? false : true;
    }

    private synchronized int createID(T c) {
        int ID;
        if (!freeIDs.isEmpty()) {
            ID = freeIDs.popFirst();
//			Gdx.app.debug("[IDFactory]", "Id recyceled = " + ID);
        } else {
            ID = this.nextID++;
//			Gdx.app.debug("[IDFactory]", "Id created = " + ID);
        }


        this.ids.put(c, ID);
        this.valueFromID.put(ID, c);

        return ID;
    }

    /**
     * @param object
     * @return the id that gets resettes
     */
    public synchronized Integer freeID(T object) {
        Integer id = this.ids.remove(object);
        this.valueFromID.remove(id);

        if(id != null) {
            this.freeIDs.add(id);
            return id;
        } else {
            return null;
        }
//		Gdx.app.debug("[IDFactory]", "The id " + id + " is resetet");
    }

    public void clear() {
        this.freeIDs.clear();
        this.ids.clear();
        this.nextID = 0;
    }
}
