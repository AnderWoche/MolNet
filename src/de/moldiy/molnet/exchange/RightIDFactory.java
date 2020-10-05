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

    public BitVector setRightBitsFromStringRights(BitVector bitVector, String... rights) {
        for(int i = rights.length - 1; i >= 0; i--) {
            bitVector.set(super.getOrCreateID(rights[i]));
        }
        return bitVector;
    }
}
