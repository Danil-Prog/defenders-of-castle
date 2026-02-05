package org.jme.zombies.game.server;

import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.MessageConnection;
import com.jme3.network.Network;
import com.simsilica.es.EntityData;
import com.simsilica.es.client.EntityDataClientService;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkedEntityData {

    private EntityData entityData;

    public NetworkedEntityData(String name, Integer version, String host, Integer port) {
        Client client;
        try {
            client = Network.connectToServer(name, version, host, port, port);
            client.getServices().addService(new EntityDataClientService(MessageConnection.CHANNEL_DEFAULT_RELIABLE));
            this.entityData = client.getServices().getService(EntityDataClientService.class).getEntityData();
            final CountDownLatch startedSignal = new CountDownLatch(1);
            client.addClientStateListener(new DefaultClientListener(startedSignal));
            client.start();

            // Wait for the client to start
            System.out.println("Waiting for connection setup.");

            startedSignal.await();

            System.out.println("Connected.");
            System.out.println("Press Ctrl-C to stop.");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(NetworkedEntityData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EntityData getEntityData() {
        return entityData;
    }

    private record DefaultClientListener(CountDownLatch startedSignal) implements ClientStateListener {

        @Override
        public void clientConnected(Client c) {
            System.out.println("Connected to server with game name: " + c.getGameName());
            startedSignal.countDown();
        }

        @Override
        public void clientDisconnected(Client c, DisconnectInfo info) {
            System.out.println("Client disconnected. Reason: " + info.reason);
        }
    }
}