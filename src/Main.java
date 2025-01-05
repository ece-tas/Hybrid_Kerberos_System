package org.example.demo1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;


public class Main extends Application {

    public KDC kdc = new KDC();
    public static final String LOG_FILE_PATH = "src/log.txt";


    @Override
    public void start(Stage primaryStage) throws Exception {






        Label clientLabel = new Label("Client ID:");
        TextField clientField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label serverLabel = new Label("Server ID:");
        TextField serverField = new TextField();
        Label messageLabel = new Label("Message:");
        TextField messageField = new TextField();
        TextArea logArea = new TextArea();
        logArea.setEditable(false);

        Button registerButton = new Button("Register");
        Button loginButton = new Button("LogIn");
        Button communicateButton = new Button("Communicate with Server");

        VBox root = new VBox(10,
                new HBox(10, clientLabel, clientField),
                new HBox(10, passwordLabel, passwordField),
                new HBox(10, serverLabel, serverField),
                new HBox(10, messageLabel, messageField),
                new HBox(10, registerButton, loginButton, communicateButton),
                new Label("Logs:"),
                logArea
        );
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Kerberos Hybrid System");
        primaryStage.show();


        registerButton.setOnAction(e -> {

            String clientId = clientField.getText();
            String password = passwordField.getText();
            String serverId = serverField.getText();

            if (clientId.isEmpty() || password.isEmpty()) {
                logArea.appendText("Client ID or Password cannot be empty.\n");
                writeLog("Client ID or Password cannot be empty.\n");
                return;
            }

            Client client2;
            try {
                client2 = Client.allClients.get(clientId);

                if (Client.allClients.containsKey(clientId) &&
                        client2.getRegisteredServers().containsKey(serverId)) {
                    logArea.appendText("This client is already registered.\n");

                } else {
                    Client client = new Client(clientId, password);
                    Server server = new Server(serverId);
                    Client.allClients.put(clientId, client);
                    Server.allServers.put(serverId, server);

                    client.setRegisteredServers(serverId);
                    server.setRegisteredClients(clientId);

                    writeLog(kdc.registerClient(client));

                    logArea.appendText("Client registered successfully.\n");
                    writeLog(kdc.registerServer(server));

                }

            } catch (NullPointerException ea) {
                Client client = new Client(clientId, password);
                Server server = new Server(serverId);
                Client.allClients.put(clientId, client);
                Server.allServers.put(serverId, server);

                client.setRegisteredServers(serverId);
                server.setRegisteredClients(clientId);

                writeLog(kdc.registerClient(client));

                logArea.appendText("Client registered successfully.\n");
                writeLog(kdc.registerServer(server));
            }


        });


        loginButton.setOnAction(e -> {
            logArea.appendText("Authenticating client...\n");
            writeLog("Authenticating client.\n");

            String clientId = clientField.getText();
            String password = passwordField.getText();
            String serverId = serverField.getText();


            if (clientId.isEmpty() || password.isEmpty()) {
                logArea.appendText("Client ID or Password cannot be empty.\n");
                writeLog("Client ID or Password cannot be empty.\n");
                return;
            }
            // Verify credentials with KDC
            writeLog("The client sends a request to the KDC with its credentials.\nThe KDC checks the credentials.\n");
            if (kdc.authenticateClient(clientId, password, serverId)) {
                logArea.appendText("Authentication successful! Ticket granted.\n");
                writeLog("Authentication successful!\n");
                // Generate session key and ticket here (you can expand as needed)
            } else {
                logArea.appendText("Error: Invalid client credentials.\n");
                writeLog("Error: Invalid client credentials.\n");
            }
        });


        communicateButton.setOnAction(e -> {

            String clientId = clientField.getText();
            String password = passwordField.getText();
            String serverId = serverField.getText();
            String message = messageField.getText();

            if (!Client.allClients.containsKey(clientId)) {
                logArea.appendText("Client isn't registered.\n");
                writeLog("Client isn't registered.");

            } else {
                logArea.appendText("Server: Communication established with the client.\n");
                writeLog("Server: Communication established with the client.\n");

                if (clientId.isEmpty() || password.isEmpty()) {
                    logArea.appendText("Client ID or Password cannot be empty.\n");
                    writeLog("Client ID or Password cannot be empty.\n");
                }
                try {
                    logArea.appendText(Client.allClients.get(clientId).accessToServer(serverId, message, password));
                    writeLog(Client.allClients.get(clientId).accessToServer(serverId, message,password));

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

        });

         }

    // Writes a log message to log.txt
    public static void writeLog(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(message);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
