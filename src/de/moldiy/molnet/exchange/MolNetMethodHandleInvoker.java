package de.moldiy.molnet.exchange;

import com.badlogic.gdx.utils.Pools;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MolNetMethodHandleInvoker {

    private final NetworkInterface networkInterface;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public MolNetMethodHandleInvoker(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    public void invokeMethod(BitVector rightBits, MolNetMethodHandle methodHandle, ChannelHandlerContext ctx, ByteBuf byteBuf) throws Throwable {
        if (methodHandle.isAnnotationPresents(Threaded.class)) {
            ByteBuf copiedByteBuff = byteBuf.copy();
            this.executorService.execute(() -> {
                try {
                    this.invoke(rightBits, methodHandle, ctx, copiedByteBuff);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                copiedByteBuff.release();
            });
        } else {
            this.invoke(rightBits, methodHandle, ctx, byteBuf);
        }
    }

    private void invoke(BitVector rightBits, MolNetMethodHandle methodHandle, ChannelHandlerContext ctx, ByteBuf byteBuf) throws Throwable {
            Object message = this.obtainMethodMessageObject(methodHandle, byteBuf);
            methodHandle.invoke(rightBits, this.networkInterface, ctx, message);
            if (!(message instanceof ByteBuf) && message != null) {
                Pools.free(message);
            }
    }

    private Object obtainMethodMessageObject(MolNetMethodHandle methodHandle, ByteBuf byteBuf) {
        Object methodMessageObject;
        if (methodHandle.getMessageParameterType() == ByteBuf.class) {
            methodMessageObject = byteBuf;
        } else {
            methodMessageObject = Pools.obtain(methodHandle.getMessageParameterType());
            DTOSerializer dtoSerializer = (DTOSerializer) methodMessageObject;
            dtoSerializer.deserialize(byteBuf);
        }
        return methodMessageObject;
    }

}
