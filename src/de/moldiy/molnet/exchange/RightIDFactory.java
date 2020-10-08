package de.moldiy.molnet.exchange;

import de.moldiy.molnet.utils.BitVector;
import de.moldiy.molnet.utils.IDFactory;

public class RightIDFactory extends IDFactory<String> {
    /**
     * @param identity if identity true, the Map is a IdentityMap if false, the Map is a HashMap
     */
    public RightIDFactory(boolean identity) {
        super(identity);
    }

    public BitVector addRightBits(BitVector bitVector, String... rights) {
        for(int i = rights.length - 1; i >= 0; i--) {
            String stringRight = rights[i].toLowerCase();
            int id = super.getOrCreateID(stringRight);
            bitVector.set(id);
        }
        return bitVector;
    }

    public BitVector removeRightsBits(BitVector bitVector, String... rights) {
        for(int i = rights.length - 1; i >= 0; i--) {
            String stringRight = rights[i].toLowerCase();
            int id = super.getOrCreateID(stringRight);
            bitVector.clear(id);
        }
        return bitVector;
    }
}
