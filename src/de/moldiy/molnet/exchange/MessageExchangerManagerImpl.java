package de.moldiy.molnet.exchange;

import de.moldiy.molnet.utils.BitVector;

import java.lang.reflect.Method;

public class MessageExchangerManagerImpl extends MessageExchangerManager {

    public MessageExchangerManagerImpl(RightIDFactory rightIDFactory) {
        super(rightIDFactory);
    }

    @Override
    protected MolNetMethodHandle createMolNetMethodHandle(Object o, Method m, BitVector bitVector) {
        return new MolNetMethodHandle(o, m, bitVector);
    }
}
