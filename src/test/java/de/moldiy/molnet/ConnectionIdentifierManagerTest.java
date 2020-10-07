package de.moldiy.molnet;

import de.moldiy.molnet.server.ConnectionIdentifier;
import de.moldiy.molnet.server.ConnectionIdentifierManager;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import java.util.UUID;

public class ConnectionIdentifierManagerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void connectionIdentifierManagerTest() {
        ConnectionIdentifierManager cim = new ConnectionIdentifierManager();

        cim.registerConnectionIdentifier("uuid", new ConnectionIdentifier<String>() {
            @Override
            protected String initIdentifier(ChannelHandlerContext ctx, String... args) {
                if(args[0].equals("lol")) {
                    return "lolID";
                }
                return UUID.randomUUID().toString();
            }
        });

        cim.addConnection(new CTXTestImpl(), "args");
        cim.addConnection(new CTXTestImpl(), "lol");
        cim.addConnection(new CTXTestImpl(), "args");

        System.out.println("Value - Identifier");
        cim.getIdentifier().forEach((identifierName, identifier) -> {
            identifier.getValueIdentifier().forEach((key, value) -> {
                System.out.println("idName: " + identifierName + "  key: " + key + "  value: " + value);
            });
        });

        System.out.println();
        System.out.println("Identifier - Value");
        cim.getIdentifier().forEach((identifierName, identifier) -> {
            identifier.getIdentifierValue().forEach((key, value) -> {
                System.out.println("idName: " + identifierName + "  key: " + key + "  value: " + value);
            });
        });

        System.out.println("ctx: " + cim.getConnectionFromIdentifier("uuid", "lolID"));

    }

}
