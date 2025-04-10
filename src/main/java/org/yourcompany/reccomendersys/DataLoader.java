package org.yourcompany.reccomendersys;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Handles loading and parsing the FMA dataset
 */
public class DataLoader {
    public Dataset loadDataset(String dataPath) {
        Dataset dataset = new Dataset();
        
        try {
            // Load tracks metadata
            loadTracks(dataset, dataPath + "tracks.csv");
            
            // Load users metadata
            loadUsers(dataset, dataPath + "users.csv");
            
            // Load interactions
            loadInteractions(dataset, dataPath + "interactions.csv");
            
        } catch (IOException e) {
            System.err.println("Error loading dataset: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dataset;
    }
    
    private void loadTracks(Dataset dataset, String filePath) throws IOException {
        try (CSVParser parser = CSVParser.parse(new File(filePath), 
                java.nio.charset.StandardCharsets.UTF_8, 
                CSVFormat.DEFAULT.withHeader())) {
            
            for (CSVRecord record : parser) {
                String trackIdStr = record.get("track_id");
                // Remove 'T' prefix and parse the number
                int id = Integer.parseInt(trackIdStr.substring(1));
                String title = record.get("title");
                String artist = record.get("artist");
                String album = record.get("album");
                int year = Integer.parseInt(record.get("year"));
                String genre = record.get("genre");
                
                Track track = new Track(id, title, artist, album, year, genre);
                
                // Load audio features if available
                if (record.isMapped("features")) {
                    String[] features = record.get("features").split(",");
                    for (String feature : features) {
                        String[] parts = feature.split(":");
                        if (parts.length == 2) {
                            track.addFeature(parts[0], Double.parseDouble(parts[1]));
                        }
                    }
                }
                
                dataset.addTrack(track);
            }
        }
    }
    
    private void loadUsers(Dataset dataset, String filePath) throws IOException {
        try (CSVParser parser = CSVParser.parse(new File(filePath), 
                java.nio.charset.StandardCharsets.UTF_8, 
                CSVFormat.DEFAULT.withHeader())) {
            
            for (CSVRecord record : parser) {
                String userIdStr = record.get("user_id");
                // Remove 'U' prefix and parse the number
                int id = Integer.parseInt(userIdStr.substring(1));
                String username = record.get("username");
                int age = Integer.parseInt(record.get("age"));
                String gender = record.get("gender");
                String location = record.get("location");
                
                User user = new User(id, username, age, gender, location);
                
                // Load additional demographics if available
                if (record.isMapped("demographics")) {
                    String[] demographics = record.get("demographics").split(",");
                    for (String demographic : demographics) {
                        String[] parts = demographic.split(":");
                        if (parts.length == 2) {
                            user.addDemographic(parts[0], parts[1]);
                        }
                    }
                }
                
                dataset.addUser(user);
            }
        }
    }
    
    private void loadInteractions(Dataset dataset, String filePath) throws IOException {
        try (CSVParser parser = CSVParser.parse(new File(filePath), 
                java.nio.charset.StandardCharsets.UTF_8, 
                CSVFormat.DEFAULT.withHeader())) {
            
            for (CSVRecord record : parser) {
                String userIdStr = record.get("user_id");
                String trackIdStr = record.get("track_id");
                
                // Remove 'U' prefix from user_id
                int userId = Integer.parseInt(userIdStr.substring(1));
                
                // Remove 'T' prefix from track_id
                int trackId = Integer.parseInt(trackIdStr.substring(1));
                
                double rating = Double.parseDouble(record.get("rating"));
                String timestampStr = record.get("timestamp");
                LocalDateTime timestamp = LocalDateTime.parse(timestampStr, 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String interactionType = record.get("interaction_type");
                
                Interaction interaction = new Interaction(userId, trackId, rating, timestamp, interactionType);
                dataset.addInteraction(interaction);
            }
        }
    }
} 