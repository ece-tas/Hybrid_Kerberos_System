package org.example.demo1;


import java.util.HashMap;

public class Client {
    private final String clientId;
    private final String password;
    private HashMap<String, Ticket> registeredServers = new HashMap<>();
    public static HashMap<String, Client> allClients = new HashMap<>();

    public Client(String clientId, String password) {
        this.clientId = clientId;
        this.password = password;

    }
    public String accessToServer(String ServerId,String message, String enteredPassword) throws Exception {

        Server server = Server.allServers.get(ServerId);

        if (this.registeredServers.get(ServerId) != null) {
            if (enteredPassword.equals(password)) {
                if (this.registeredServers.get(ServerId).isValid()) {
                    String decryptedSessionKey = KDC.decryptRSA(this.registeredServers.get(ServerId).getEncrypted(), this);
                    Main.writeLog("The client " + clientId + " sends the ticket to the server " + ServerId + ".\n");
                    return server.requestFromClient(this, decryptedSessionKey, message);
                } else {
                    KDC.newTicket(this, server);
                    Main.writeLog("Ticket is expired and renewed.\n");
                    return "Ticket is renewed.\n" + this.accessToServer(ServerId, message, enteredPassword);
                }
            } else {
                return ("Wrong password.\n");
            }
        } else {

            return ("Client doesn't log in to this server.\n");
        }
    }

    public String getClientId() {
        return clientId;
    }

    public String getPassword() {
        return password;
    }

    public void setTicket(String serverId, Ticket ticket) {
        this.registeredServers.put(serverId, ticket); // Use 'put' to add or update a key-value pair
    }

    public boolean isRegistered(String ServerId) {
         return this.registeredServers.containsKey(ServerId);
    }

    public void setRegisteredServers (String serverId) {
        this.registeredServers.put(serverId, null);
    }

    public HashMap<String, Ticket> getRegisteredServers() {
        return registeredServers;
    }


}
