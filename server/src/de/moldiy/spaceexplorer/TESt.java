package de.moldiy.spaceexplorer;

import de.moldiy.spaceexplorer.client.ClientMassageExchanger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TESt extends ClientMassageExchanger {

    @TrafficID(id = "cords")
    public void setcords(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        System.out.println("ctx: " + ctx);
        System.out.println("ByteBuf: " + byteBuf);
    }

    public static void main(String[] args) {
        for(Method m : TESt.class.getDeclaredMethods()) {
            m.setAccessible(true);
                TrafficID trafficID = m.getAnnotation(TrafficID.class);
                if(trafficID != null) {
                    String id = trafficID.id();
                    System.out.println("id: " + id);
                    try {
                        MethodHandles.Lookup lookup = MethodHandles.lookup();

                        MethodHandle virtual = lookup.findVirtual(TESt.class, m.getName(), null); // MethodType.methodType()
                        virtual = virtual.asType(MethodType.methodType(ChannelHandlerContext.class, ByteBuf.class));

                        virtual.invokeExact(null, null);


                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
        }
    }


}
