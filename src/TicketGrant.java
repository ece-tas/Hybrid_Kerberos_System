package org.example.demo1;

import javax.crypto.SecretKey;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;
import java.security.PublicKey;


public class TicketGrant extends KDC{

     public void generateTickets(Client client, Server server) throws Exception {
        Main.writeLog("KDC generates a session key and encrypts it.\n");
        SecretKey sessionKey = generateAESKey();

        // Encrypt the session key for the client using RSA
        String encryptedForClient = encryptRSA(
                Base64.getEncoder().encodeToString(sessionKey.getEncoded()),
                clientKeysDatabase.get(client.getClientId()).getPublic()
                 // Assuming client has a public key getter
        );

        // Encrypt the session key for the server using RSA
        String encryptedForServer = encryptRSA(
                Base64.getEncoder().encodeToString(sessionKey.getEncoded()),
                serverKeysDatabase.get(server.getServerId()).getPublic()
                 // Assuming server has a public key getter
        );
        Main.writeLog("Encrypted session key for server is: " + encryptedForServer + "\n");
         Main.writeLog("Encrypted session key for client is: " + encryptedForClient + "\n");

        // Create tickets with expiration time (5 minutes)
        Ticket clientTicket = new Ticket(encryptedForClient);
        Ticket serverTicket = new Ticket(encryptedForServer);

        client.setTicket(server.getServerId(), clientTicket);
        server.setTicket(client.getClientId(), serverTicket);

    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // AES key size (128 bits)
        return keyGen.generateKey();
    }

    public static String encryptRSA(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // Return Base64 encoded string
    }

}
