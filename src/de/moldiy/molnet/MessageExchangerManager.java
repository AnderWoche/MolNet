package de.moldiy.molnet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MessageExchangerManager {

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private final HashMap<String, MethodHandle> idMethods = new HashMap<>();
    private final HashMap<String, Object> idObjects = new HashMap<>();

    public void loadMassageExchanger(Object massageExchanger) {
        assert massageExchanger != null;
        for (Method m : massageExchanger.getClass().getDeclaredMethods()) {
            m.setAccessible(true);
            TrafficID trafficID = m.getAnnotation(TrafficID.class);
            if (trafficID != null) {
                String id = trafficID.id();
                try {
                    MethodHandle virtual = lookup.unreflect(m);

                    this.idMethods.put(id, virtual);
                    this.idObjects.put(id, massageExchanger);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param id the Method id.
     * @param ctx The Connection
     * @param byteBuf the Message
     * @return return true if exec was successfull
     */
    public boolean exec(String id, ChannelHandlerContext ctx, ByteBuf byteBuf) {
        assert ctx != null;
        assert byteBuf != null;

        MethodHandle methodHandle = this.idMethods.get(id);
        if(methodHandle != null) {
            try {
                Object object = this.idObjects.get(id);

//            warum geht das nicht?
//            Boolean b = (Boolean) methodHandle.invokeExact(massageExchanger, Boolean.class, ctx, byteBuf);

                methodHandle.invoke(object, ctx, byteBuf);

//            this.methods.get(id).invokeWithArguments(ctx, byteBuf);
                return true;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return false;
    }

}
