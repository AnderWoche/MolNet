package de.moldiy.molnet;

import org.junit.Test;

import java.util.UUID;

public class ConnectionIdentifierManagerTest {

    @Test
    public void connectionIdentifierManagerTest() {
        ChannelIdentifierManager cim = new ChannelIdentifierManager();

        cim.setIdentifier(null ,"uuid", UUID.randomUUID().toString());
        cim.setIdentifier(null, "uuid", UUID.randomUUID().toString());
        cim.setIdentifier(null, "uuid", UUID.randomUUID().toString());

        System.out.println("Value - Identifier");
        cim.getAllIdentifier().forEach((identifierName, identifier) -> {
            identifier.getValueIdentifier().forEach((key, value) -> {
                System.out.println("idName: " + identifierName + "  key: " + key + "  value: " + value);
            });
        });

        System.out.println();
        System.out.println("Identifier - Value");
        cim.getAllIdentifier().forEach((identifierName, identifier) -> {
            identifier.getIdentifierValue().forEach((key, value) -> {
                System.out.println("idName: " + identifierName + "  key: " + key + "  value: " + value);
            });
        });

        System.out.println("ctx: " + cim.getChannel("uuid", "lolID"));

    }

}
