/**
 * Music Recommendation System
 * Implements Collaborative Filtering, Content-Based Filtering, and Demographic Filtering
 * Using the FMA (Free Music Archive) Dataset
 */
 
package org.yourcompany.reccomendersys;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Main class for the Music Recommendation System
 */
public class Recommender_sys {
    public static void main(String[] args) {
        System.out.println("Music Recommendation System");
        
        // Load and process the dataset
        DataLoader dataLoader = new DataLoader();
        Dataset dataset = dataLoader.loadDataset("fma_metadata/");
        
        // Create recommender instances
        CollaborativeFilteringRecommender cfRecommender = 
            new CollaborativeFilteringRecommender(dataset);
        ContentBasedRecommender cbRecommender = 
            new ContentBasedRecommender(dataset);
        DemographicRecommender demoRecommender = 
            new DemographicRecommender(dataset);
        
        // Sample user for testing
        int userId = 101;
        
        // Generate recommendations
        List<Track> cfRecommendations = cfRecommender.getRecommendations(userId, 10);
        List<Track> cbRecommendations = cbRecommender.getRecommendations(userId, 10);
        List<Track> demoRecommendations = demoRecommender.getRecommendations(userId, 10);
        
        // Print recommendations
        System.out.println("\nCollaborative Filtering Recommendations:");
        cfRecommendations.forEach(track -> System.out.println(track.getTitle() + " by " + track.getArtist()));
        
        System.out.println("\nContent-Based Recommendations:");
        cbRecommendations.forEach(track -> System.out.println(track.getTitle() + " by " + track.getArtist()));
        
        System.out.println("\nDemographic Recommendations:");
        demoRecommendations.forEach(track -> System.out.println(track.getTitle() + " by " + track.getArtist()));
        
        // Evaluate recommenders
        RecommenderEvaluator evaluator = new RecommenderEvaluator(dataset);
        
        Map<String, Double> cfMetrics = evaluator.evaluate(cfRecommender);
        Map<String, Double> cbMetrics = evaluator.evaluate(cbRecommender);
        Map<String, Double> demoMetrics = evaluator.evaluate(demoRecommender);
        
        // Print evaluation results
        System.out.println("\nEvaluation Results:");
        System.out.println("Collaborative Filtering: " + cfMetrics);
        System.out.println("Content-Based Filtering: " + cbMetrics);
        System.out.println("Demographic Filtering: " + demoMetrics);
        
        // Create hybrid recommender
        HybridRecommender hybridRecommender = new HybridRecommender(
            Arrays.asList(cfRecommender, cbRecommender, demoRecommender),
            Arrays.asList(0.5, 0.3, 0.2) // Weights for each recommender
        );
        
        List<Track> hybridRecommendations = hybridRecommender.getRecommendations(userId, 10);
        System.out.println("\nHybrid Recommendations:");
        hybridRecommendations.forEach(track -> System.out.println(track.getTitle() + " by " + track.getArtist()));
        
        Map<String, Double> hybridMetrics = evaluator.evaluate(hybridRecommender);
        System.out.println("Hybrid Recommender: " + hybridMetrics);
    }
}
 
/**
 * Represents the music dataset with users, tracks, and interactions
 */
class Dataset {
    private Map<Integer, User> users;
    private Map<Integer, Track> tracks;
    private List<Interaction> interactions;
    
    public Dataset() {
        users = new HashMap<>();
        tracks = new HashMap<>();
        interactions = new ArrayList<>();
    }
    
    public void addUser(User user) {
        users.put(user.getId(), user);
    }
    
    public void addTrack(Track track) {
        tracks.put(track.getId(), track);
    }
    
    public void addInteraction(Interaction interaction) {
        interactions.add(interaction);
    }
    
    public User getUser(int userId) {
        return users.get(userId);
    }
    
    public Track getTrack(int trackId) {
        return tracks.get(trackId);
    }
    
    public Map<Integer, User> getUsers() {
        return users;
    }
    
    public Map<Integer, Track> getTracks() {
        return tracks;
    }
    
    public List<Interaction> getInteractions() {
        return interactions;
    }
    
    public List<Interaction> getUserInteractions(int userId) {
        return interactions.stream()
            .filter(i -> i.getUserId() == userId)
            .collect(Collectors.toList());
    }
    
    public List<Interaction> getTrackInteractions(int trackId) {
        return interactions.stream()
            .filter(i -> i.getTrackId() == trackId)
            .collect(Collectors.toList());
    }
    
    // Get tracks a user has interacted with
    public List<Track> getUserTracks(int userId) {
        return getUserInteractions(userId).stream()
            .map(i -> tracks.get(i.getTrackId()))
            .collect(Collectors.toList());
    }
    
    // Get users who have interacted with a track
    public List<User> getTrackUsers(int trackId) {
        return getTrackInteractions(trackId).stream()
            .map(i -> users.get(i.getUserId()))
            .collect(Collectors.toList());
    }
    
    // Split dataset into training and testing sets
    public Map<String, List<Interaction>> splitDataset(double trainingRatio) {
        Collections.shuffle(interactions);
        int trainingSize = (int) (interactions.size() * trainingRatio);
        
        List<Interaction> trainingSet = interactions.subList(0, trainingSize);
        List<Interaction> testingSet = interactions.subList(trainingSize, interactions.size());
        
        Map<String, List<Interaction>> result = new HashMap<>();
        result.put("training", trainingSet);
        result.put("testing", testingSet);
        
        return result;
    }
}
 
/**
 * Represents a music track with its metadata
 */
class Track {
    private int id;
    private String title;
    private String artist;
    private String album;
    private int year;
    private String genre;
    private Map<String, Double> features;
    
    public Track(int id, String title, String artist, String album, int year, String genre) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.genre = genre;
        this.features = new HashMap<>();
    }
    
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getArtist() {
        return artist;
    }
    
    public String getAlbum() {
        return album;
    }
    
    public int getYear() {
        return year;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public Map<String, Double> getFeatures() {
        return features;
    }
    
    public void addFeature(String featureName, double value) {
        features.put(featureName, value);
    }
    
    @Override
    public String toString() {
        return "Track{id=" + id + ", title='" + title + "', artist='" + artist + "'}";
    }
}
 
/**
 * Represents a user of the music service
 */
class User {
    private int id;
    private String username;
    private int age;
    private String gender;
    private String location;
    private Map<String, String> demographics;
    
    public User(int id, String username, int age, String gender, String location) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.demographics = new HashMap<>();
    }
    
    public int getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public int getAge() {
        return age;
    }
    
    public String getGender() {
        return gender;
    }
    
    public String getLocation() {
        return location;
    }
    
    public Map<String, String> getDemographics() {
        return demographics;
    }
    
    public void addDemographic(String key, String value) {
        demographics.put(key, value);
    }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', age=" + age + "}";
    }
}
 
/**
 * Represents an interaction between a user and a track
 */
class Interaction {
    private int userId;
    private int trackId;
    private double rating;
    private LocalDateTime timestamp;
    private String interactionType; // play, like, download, etc.
    
    public Interaction(int userId, int trackId, double rating, LocalDateTime timestamp, String interactionType) {
        this.userId = userId;
        this.trackId = trackId;
        this.rating = rating;
        this.timestamp = timestamp;
        this.interactionType = interactionType;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public int getTrackId() {
        return trackId;
    }
    
    public double getRating() {
        return rating;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getInteractionType() {
        return interactionType;
    }
}
 
/**
 * Interface for recommender systems
 */
interface Recommender {
    List<Track> getRecommendations(int userId, int limit);
    Map<Integer, Double> getPredictedRatings(int userId);
}
 
/**
 * Collaborative Filtering Recommender implementation
 */
class CollaborativeFilteringRecommender implements Recommender {
    private Dataset dataset;
    private Map<Integer, Map<Integer, Double>> userSimilarities;
    private int k = 20; // Number of neighbors
    
    public CollaborativeFilteringRecommender(Dataset dataset) {
        this.dataset = dataset;
        this.userSimilarities = new HashMap<>();
        calculateUserSimilarities();
    }
    
    private void calculateUserSimilarities() {
        Map<Integer, User> users = dataset.getUsers();
        
        for (User user1 : users.values()) {
            int userId1 = user1.getId();
            Map<Integer, Double> similarities = new HashMap<>();
            
            for (User user2 : users.values()) {
                int userId2 = user2.getId();
                if (userId1 != userId2) {
                    double similarity = calculateUserSimilarity(userId1, userId2);
                    similarities.put(userId2, similarity);
                }
            }
            
            userSimilarities.put(userId1, similarities);
        }
    }
    
    private double calculateUserSimilarity(int userId1, int userId2) {
        // Get interactions for both users
        List<Interaction> interactions1 = dataset.getUserInteractions(userId1);
        List<Interaction> interactions2 = dataset.getUserInteractions(userId2);
        
        // Convert to maps for easy lookup, handling duplicate ratings by averaging them
        Map<Integer, Double> ratings1 = interactions1.stream()
            .collect(Collectors.groupingBy(
                Interaction::getTrackId,
                Collectors.averagingDouble(Interaction::getRating)
            ));
        Map<Integer, Double> ratings2 = interactions2.stream()
            .collect(Collectors.groupingBy(
                Interaction::getTrackId,
                Collectors.averagingDouble(Interaction::getRating)
            ));
        
        // Find common tracks
        Set<Integer> commonTracks = new HashSet<>(ratings1.keySet());
        commonTracks.retainAll(ratings2.keySet());
        
        if (commonTracks.isEmpty()) {
            return 0.0; // No common tracks
        }
        
        // Calculate average ratings
        double avg1 = ratings1.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avg2 = ratings2.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        // Calculate Pearson correlation
        double numerator = 0.0;
        double denominator1 = 0.0;
        double denominator2 = 0.0;
        
        for (int trackId : commonTracks) {
            double rating1Diff = ratings1.get(trackId) - avg1;
            double rating2Diff = ratings2.get(trackId) - avg2;
            
            numerator += rating1Diff * rating2Diff;
            denominator1 += Math.pow(rating1Diff, 2);
            denominator2 += Math.pow(rating2Diff, 2);
        }
        
        if (denominator1 == 0.0 || denominator2 == 0.0) {
            return 0.0;
        }
        
        return numerator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
    }
    
    private List<Integer> getNeighbors(int userId) {
        if (!userSimilarities.containsKey(userId)) {
            return Collections.emptyList();
        }
        
        Map<Integer, Double> similarities = userSimilarities.get(userId);
        
        return similarities.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(k)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    @Override
    public Map<Integer, Double> getPredictedRatings(int userId) {
        List<Integer> neighbors = getNeighbors(userId);
        List<Interaction> userInteractions = dataset.getUserInteractions(userId);
        
        // Tracks the user has already interacted with
        Set<Integer> userTrackIds = userInteractions.stream()
            .map(Interaction::getTrackId)
            .collect(Collectors.toSet());
        
        Map<Integer, Double> predictedRatings = new HashMap<>();
        Map<Integer, Double> trackSimilaritySum = new HashMap<>();
        
        // Calculate average rating for the user
        double userAvgRating = userInteractions.stream()
            .mapToDouble(Interaction::getRating)
            .average()
            .orElse(3.0); // Default to neutral if no ratings
        
        for (int neighborId : neighbors) {
            double similarity = userSimilarities.get(userId).get(neighborId);
            if (similarity <= 0) continue; // Skip negative correlations
            
            List<Interaction> neighborInteractions = dataset.getUserInteractions(neighborId);
            double neighborAvgRating = neighborInteractions.stream()
                .mapToDouble(Interaction::getRating)
                .average()
                .orElse(3.0);
                
            for (Interaction interaction : neighborInteractions) {
                int trackId = interaction.getTrackId();
                
                // Skip tracks the user has already interacted with
                if (userTrackIds.contains(trackId)) {
                    continue;
                }
                
                // Adjusted rating
                double ratingDiff = interaction.getRating() - neighborAvgRating;
                
                // Update predicted rating
                predictedRatings.merge(trackId, similarity * ratingDiff, Double::sum);
                trackSimilaritySum.merge(trackId, similarity, Double::sum);
            }
        }
        
        // Normalize predicted ratings
        Map<Integer, Double> normalizedRatings = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : predictedRatings.entrySet()) {
            int trackId = entry.getKey();
            double weightedRatingSum = entry.getValue();
            double similaritySum = trackSimilaritySum.get(trackId);
            
            if (similaritySum > 0) {
                double predictedRating = userAvgRating + (weightedRatingSum / similaritySum);
                // Clamp to range [1, 5]
                predictedRating = Math.max(1.0, Math.min(5.0, predictedRating));
                normalizedRatings.put(trackId, predictedRating);
            }
        }
        
        return normalizedRatings;
    }
    
    @Override
    public List<Track> getRecommendations(int userId, int limit) {
        Map<Integer, Double> predictedRatings = getPredictedRatings(userId);
        
        // Sort tracks by predicted rating (descending)
        return predictedRatings.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(limit)
            .map(e -> dataset.getTrack(e.getKey()))
            .collect(Collectors.toList());
    }
}
 
/**
 * Content-Based Recommender implementation
 */
class ContentBasedRecommender implements Recommender {
    private Dataset dataset;
    private Map<Integer, Map<Integer, Double>> trackSimilarities;
    
    public ContentBasedRecommender(Dataset dataset) {
        this.dataset = dataset;
        this.trackSimilarities = new HashMap<>();
        calculateTrackSimilarities();
    }
    
    private void calculateTrackSimilarities() {
        Map<Integer, Track> tracks = dataset.getTracks();
        
        for (Track track1 : tracks.values()) {
            int trackId1 = track1.getId();
            Map<Integer, Double> similarities = new HashMap<>();
            
            for (Track track2 : tracks.values()) {
                int trackId2 = track2.getId();
                if (trackId1 != trackId2) {
                    double similarity = calculateTrackSimilarity(track1, track2);
                    similarities.put(trackId2, similarity);
                }
            }
            
            trackSimilarities.put(trackId1, similarities);
        }
    }
    
    private double calculateTrackSimilarity(Track track1, Track track2) {
        // Calculate similarity based on genre (simple match for demonstration)
        double genreSimilarity = track1.getGenre().equals(track2.getGenre()) ? 1.0 : 0.0;
        
        // Calculate similarity based on audio features
        double featureSimilarity = 0.0;
        Map<String, Double> features1 = track1.getFeatures();
        Map<String, Double> features2 = track2.getFeatures();
        
        if (!features1.isEmpty() && !features2.isEmpty()) {
            // Find common features
            Set<String> commonFeatures = new HashSet<>(features1.keySet());
            commonFeatures.retainAll(features2.keySet());
            
            if (!commonFeatures.isEmpty()) {
                double dotProduct = 0.0;
                double norm1 = 0.0;
                double norm2 = 0.0;
                
                for (String feature : commonFeatures) {
                    double value1 = features1.get(feature);
                    double value2 = features2.get(feature);
                    
                    dotProduct += value1 * value2;
                    norm1 += Math.pow(value1, 2);
                    norm2 += Math.pow(value2, 2);
                }
                
                if (norm1 > 0 && norm2 > 0) {
                    // Cosine similarity
                    featureSimilarity = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
                }
            }
        }
        
        // Combine similarities (give higher weight to audio features)
        return 0.3 * genreSimilarity + 0.7 * featureSimilarity;
    }
    
    @Override
    public Map<Integer, Double> getPredictedRatings(int userId) {
        // Get tracks the user has interacted with
        List<Interaction> userInteractions = dataset.getUserInteractions(userId);
        
        // Tracks the user has already interacted with
        Set<Integer> userTrackIds = userInteractions.stream()
            .map(Interaction::getTrackId)
            .collect(Collectors.toSet());
        
        // Calculate content-based predictions
        Map<Integer, Double> predictedRatings = new HashMap<>();
        Map<Integer, Double> similaritySum = new HashMap<>();
        
        for (Interaction interaction : userInteractions) {
            int interactedTrackId = interaction.getTrackId();
            double userRating = interaction.getRating();
            
            Map<Integer, Double> similarities = trackSimilarities.getOrDefault(interactedTrackId, Collections.emptyMap());
            
            for (Map.Entry<Integer, Double> entry : similarities.entrySet()) {
                int candidateTrackId = entry.getKey();
                double similarity = entry.getValue();
                
                // Skip tracks the user has already interacted with
                if (userTrackIds.contains(candidateTrackId)) {
                    continue;
                }
                
                // Update predicted rating
                predictedRatings.merge(candidateTrackId, similarity * userRating, Double::sum);
                similaritySum.merge(candidateTrackId, similarity, Double::sum);
            }
        }
        
        // Normalize predicted ratings
        Map<Integer, Double> normalizedRatings = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : predictedRatings.entrySet()) {
            int trackId = entry.getKey();
            double weightedRatingSum = entry.getValue();
            double simSum = similaritySum.get(trackId);
            
            if (simSum > 0) {
                double predictedRating = weightedRatingSum / simSum;
                // Clamp to range [1, 5]
                predictedRating = Math.max(1.0, Math.min(5.0, predictedRating));
                normalizedRatings.put(trackId, predictedRating);
            }
        }
        
        return normalizedRatings;
    }
    
    @Override
    public List<Track> getRecommendations(int userId, int limit) {
        Map<Integer, Double> predictedRatings = getPredictedRatings(userId);
        
        // Sort tracks by predicted rating (descending)
        return predictedRatings.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(limit)
            .map(e -> dataset.getTrack(e.getKey()))
            .collect(Collectors.toList());
    }
}
 
/**
 * Demographic Recommender implementation
 */
class DemographicRecommender implements Recommender {
    private Dataset dataset;
    private Map<String, Map<String, List<Integer>>> demographicTrackIndices;
    
    public DemographicRecommender(Dataset dataset) {
        this.dataset = dataset;
        this.demographicTrackIndices = new HashMap<>();
        buildDemographicIndices();
    }
    
    private void buildDemographicIndices() {
        // Create indices for age groups
        Map<String, List<Integer>> ageGroupIndices = new HashMap<>();
        demographicTrackIndices.put("ageGroup", ageGroupIndices);
        
        // Create indices for gender
        Map<String, List<Integer>> genderIndices = new HashMap<>();
        demographicTrackIndices.put("gender", genderIndices);
        
        // Create indices for location
        Map<String, List<Integer>> locationIndices = new HashMap<>();
        demographicTrackIndices.put("location", locationIndices);
        
        // Process all interactions
        for (Interaction interaction : dataset.getInteractions()) {
            int userId = interaction.getUserId();
            int trackId = interaction.getTrackId();
            double rating = interaction.getRating();
            
            // Only consider positive interactions (rating >= 4)
            if (rating >= 4.0) {
                User user = dataset.getUser(userId);
                if (user != null) {
                    // Age group
                    String ageGroup = getAgeGroup(user.getAge());
                    ageGroupIndices.computeIfAbsent(ageGroup, k -> new ArrayList<>()).add(trackId);
                    
                    // Gender
                    String gender = user.getGender();
                    genderIndices.computeIfAbsent(gender, k -> new ArrayList<>()).add(trackId);
                    
                    // Location
                    String location = user.getLocation();
                    locationIndices.computeIfAbsent(location, k -> new ArrayList<>()).add(trackId);
                }
            }
        }
    }
    
    private String getAgeGroup(int age) {
        if (age < 18) return "under18";
        if (age < 25) return "18-24";
        if (age < 35) return "25-34";
        if (age < 45) return "35-44";
        if (age < 55) return "45-54";
        return "55+";
    }
    
    @Override
    public Map<Integer, Double> getPredictedRatings(int userId) {
        User user = dataset.getUser(userId);
        if (user == null) {
            return Collections.emptyMap();
        }
        
        // Get user demographics
        String ageGroup = getAgeGroup(user.getAge());
        String gender = user.getGender();
        String location = user.getLocation();
        
        // Tracks the user has already interacted with
        Set<Integer> userTrackIds = dataset.getUserInteractions(userId).stream()
            .map(Interaction::getTrackId)
            .collect(Collectors.toSet());
        
        // Count track occurrences by demographic match
        Map<Integer, Integer> trackCounts = new HashMap<>();
        
        // Age group matches
        List<Integer> ageGroupTracks = demographicTrackIndices.get("ageGroup").getOrDefault(ageGroup, Collections.emptyList());
        for (int trackId : ageGroupTracks) {
            if (!userTrackIds.contains(trackId)) {
                trackCounts.merge(trackId, 1, Integer::sum);
            }
        }
        
        // Gender matches
        List<Integer> genderTracks = demographicTrackIndices.get("gender").getOrDefault(gender, Collections.emptyList());
        for (int trackId : genderTracks) {
            if (!userTrackIds.contains(trackId)) {
                trackCounts.merge(trackId, 1, Integer::sum);
            }
        }
        
        // Location matches
        List<Integer> locationTracks = demographicTrackIndices.get("location").getOrDefault(location, Collections.emptyList());
        for (int trackId : locationTracks) {
            if (!userTrackIds.contains(trackId)) {
                trackCounts.merge(trackId, 1, Integer::sum);
            }
        }
        
        // Convert counts to ratings (0-3 matches)
        Map<Integer, Double> predictedRatings = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : trackCounts.entrySet()) {
            int trackId = entry.getKey();
            int count = entry.getValue();
            
            // Scale rating based on number of demographic matches (1-3)
            double rating = 3.0 + (count * 0.66); // Scale to 3.0-5.0 range
            predictedRatings.put(trackId, Math.min(5.0, rating));
        }
        
        return predictedRatings;
    }
    
    @Override
    public List<Track> getRecommendations(int userId, int limit) {
        Map<Integer, Double> predictedRatings = getPredictedRatings(userId);
        
        // Sort tracks by predicted rating (descending)
        return predictedRatings.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(limit)
            .map(e -> dataset.getTrack(e.getKey()))
            .collect(Collectors.toList());
    }
}
 
/**
 * Hybrid Recommender implementation combining multiple recommenders
 */
class HybridRecommender implements Recommender {
    private List<Recommender> recommenders;
    private List<Double> weights;
    
    public HybridRecommender(List<Recommender> recommenders, List<Double> weights) {
        if (recommenders.size() != weights.size()) {
            throw new IllegalArgumentException("Number of recommenders must match number of weights");
        }
        
        this.recommenders = recommenders;
        this.weights = weights;
        
        // Normalize weights
        double sum = weights.stream().mapToDouble(Double::doubleValue).sum();
        for (int i = 0; i < weights.size(); i++) {
            weights.set(i, weights.get(i) / sum);
        }
    }
    
    @Override
    public Map<Integer, Double> getPredictedRatings(int userId) {
        Map<Integer, Double> hybridRatings = new HashMap<>();
        
        // Get predictions from each recommender
        for (int i = 0; i < recommenders.size(); i++) {
            Recommender recommender = recommenders.get(i);
            double weight = weights.get(i);
            
            Map<Integer, Double> ratings = recommender.getPredictedRatings(userId);
            
            // Combine ratings with appropriate weights
            for (Map.Entry<Integer, Double> entry : ratings.entrySet()) {
                int trackId = entry.getKey();
                double rating = entry.getValue();
                
                hybridRatings.merge(trackId, rating * weight, Double::sum);
            }
        }
        
        return hybridRatings;
    }
    
    @Override
    public List<Track> getRecommendations(int userId, int limit) {
        Map<Integer, Double> predictedRatings = getPredictedRatings(userId);
        
        // Sort tracks by predicted rating (descending)
        return predictedRatings.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(limit)
            .map(e -> {
                Track track = recommenders.get(0).getRecommendations(userId, 1).get(0);
                return track;
            })
            .collect(Collectors.toList());
    }
}