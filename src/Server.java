package org.example.demo1;

import java.util.HashMap;


public class Server {
    private HashMap<String, Ticket> registeredClients = new HashMap<>();
    public static HashMap<String, Server> allServers = new HashMap<>();
    private final String serverId;


    public Server(String serverId) {
        this.serverId = serverId;

    }

    public String getServerId() {
        return serverId;
    }


    public String requestFromClient(Client client, String sessionKey, String message) throws Exception {
        if(this.registeredClients.containsKey(client.getClientId())){

            Main.writeLog("The server " + serverId + " decrypts the session key from the ticket using its private key.\n");
            String decrytedSessionkey = KDC.decryptRSA(this.registeredClients.get(client.getClientId()).getEncrypted(),this);
            if(decrytedSessionkey.equals(sessionKey)){
                return (serverId + " is ready to receive messages.\nServer: Session key is active for communication.\n" +
                        "Server received " + message + "\nMessage is sent and decrypted by the server successfully.\n");

            }
        } return ("Server couldn't access to the client.\n");
    }
    public void setTicket(String ClientId,Ticket ticket){
        this.registeredClients.put(ClientId, ticket);

    }

    public void setRegisteredClients (String clientId) {
        this.registeredClients.put(clientId, null);
    }

    public HashMap<String, Ticket> getRegisteredClients() {
        return registeredClients;
    }
}
