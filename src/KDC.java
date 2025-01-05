package org.example.demo1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import javax.crypto.*;
import java.util.HashMap;


public class KDC {
    String fileName = "src/database.csv";
    protected static HashMap<String, KeyPair> clientKeysDatabase = new HashMap<>();
    protected static HashMap<String, KeyPair> serverKeysDatabase = new HashMap<>();


    private static TicketGrant tgs = new TicketGrant();

    public String registerClient(Client client) {
        clientKeysDatabase.put(client.getClientId(), generateKeys());
        saveToCSV(fileName, client.getClientId(), client.getPassword(), clientKeysDatabase.get(client.getClientId()));
        return client.getClientId() + " with " + client.getPassword() + " sent to KDS for registration.\nKDS registers "
                + client.getClientId() + " with " + client.getPassword() + " and generates public-private keys for the client.\n"
                + "Public key of " + client.getClientId() + ": " + clientKeysDatabase.get(client.getClientId()).getPublic()
                + "\nPrivate key of " + client.getClientId() + ": " + clientKeysDatabase.get(client.getClientId()).getPrivate()
                + "\n";
    }

    public String registerServer(Server server) {
        serverKeysDatabase.put(server.getServerId(), generateKeys());
       return "\nKDS registers the "
               + server.getServerId()  + " and generates public-private keys for the server.\n"
               + "Public key of " + server.getServerId() + ": " + serverKeysDatabase.get(server.getServerId()).getPublic()
               + "\nPrivate key of " + server.getServerId() + ": " + serverKeysDatabase.get(server.getServerId()).getPrivate()
               + "\nClient registered successfully to " + server.getServerId() + "\n";
    }

    private KeyPair generateKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            return pair;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // Save client details to a CSV file
    public void saveToCSV(String fileName,String clientID, String password, KeyPair pair) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            // Convert keys to Base64 for storing as text
            String publicKeyEncoded = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            String privateKeyEncoded = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());

            // Write client data to CSV
            writer.append(clientID).append(",")
                    .append(password).append(",")
                    .append(publicKeyEncoded).append(",")
                    .append(privateKeyEncoded).append("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateClient(String clientId, String password, String serverId) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String storedClientId = parts[0];
                    String storedPassword = parts[1];
                    // Check if the provided clientId and password match
                    if (storedClientId.equals(clientId) && storedPassword.equals(password)) {
                        Client client = Client.allClients.get(clientId);
                        Server server = Server.allServers.get(serverId);

                        if (client.isRegistered(serverId)) {

                            tgs.generateTickets(client, server);
                            return true; // Authentication successful
                        }

                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading database: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false; // Authentication failed
    }

    public static String decryptRSA(String encryptedData, Client client) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, clientKeysDatabase.get(client.getClientId()).getPrivate());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    public static String decryptRSA(String encryptedData, Server server) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, serverKeysDatabase.get(server.getServerId()).getPrivate());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    public static void newTicket(Client client, Server server) throws Exception {
        tgs.generateTickets(client, server);
    }

}




