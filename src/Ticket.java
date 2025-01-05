package org.example.demo1;

import java.time.Duration;
import java.time.LocalDateTime;

public class Ticket {
    private final String encrypted;
    private LocalDateTime currentTime;


    public Ticket(String encrypted) {
        this.encrypted = encrypted;
        this.currentTime = LocalDateTime.now();
    }
    public boolean isValid(){
        // Calculate the duration between the current time and the ticket's creation time
        Duration duration = Duration.between(this.currentTime, LocalDateTime.now());

        // Check if the duration is less than 5 minutes (300 seconds)
        return duration.toSeconds() < 5 * 60;
    }
    public String getEncrypted() {
        return encrypted;
    }


    public LocalDateTime getCurrentTime() {
        return currentTime;
    }
}
