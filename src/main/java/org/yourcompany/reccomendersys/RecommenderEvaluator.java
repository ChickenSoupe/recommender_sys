package org.yourcompany.reccomendersys;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Evaluates the performance of recommendation systems
 */
public class RecommenderEvaluator {
    private Dataset dataset;
    private static final int TEST_SIZE = 1000; // Number of test cases
    
    public RecommenderEvaluator(Dataset dataset) {
        this.dataset = dataset;
    }
    
    public Map<String, Double> evaluate(Recommender recommender) {
        Map<String, Double> metrics = new HashMap<>();
        
        // Split dataset into training and testing sets
        Map<String, List<Interaction>> split = dataset.splitDataset(0.8);
        List<Interaction> testSet = split.get("testing");
        
        // Limit test set size for performance
        if (testSet.size() > TEST_SIZE) {
            Collections.shuffle(testSet);
            testSet = testSet.subList(0, TEST_SIZE);
        }
        
        // Calculate metrics
        double mae = calculateMAE(recommender, testSet);
        double rmse = calculateRMSE(recommender, testSet);
        double precision = calculatePrecision(recommender, testSet);
        double recall = calculateRecall(recommender, testSet);
        double f1 = 2 * (precision * recall) / (precision + recall);
        
        metrics.put("MAE", mae);
        metrics.put("RMSE", rmse);
        metrics.put("Precision", precision);
        metrics.put("Recall", recall);
        metrics.put("F1", f1);
        
        return metrics;
    }
    
    private double calculateMAE(Recommender recommender, List<Interaction> testSet) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        
        for (Interaction interaction : testSet) {
            int userId = interaction.getUserId();
            int trackId = interaction.getTrackId();
            double actualRating = interaction.getRating();
            
            Map<Integer, Double> predictedRatings = recommender.getPredictedRatings(userId);
            double predictedRating = predictedRatings.getOrDefault(trackId, 3.0); // Default to neutral
            
            stats.addValue(Math.abs(actualRating - predictedRating));
        }
        
        return stats.getMean();
    }
    
    private double calculateRMSE(Recommender recommender, List<Interaction> testSet) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        
        for (Interaction interaction : testSet) {
            int userId = interaction.getUserId();
            int trackId = interaction.getTrackId();
            double actualRating = interaction.getRating();
            
            Map<Integer, Double> predictedRatings = recommender.getPredictedRatings(userId);
            double predictedRating = predictedRatings.getOrDefault(trackId, 3.0); // Default to neutral
            
            stats.addValue(Math.pow(actualRating - predictedRating, 2));
        }
        
        return Math.sqrt(stats.getMean());
    }
    
    private double calculatePrecision(Recommender recommender, List<Interaction> testSet) {
        int relevantAndRecommended = 0;
        int recommended = 0;
        
        for (Interaction interaction : testSet) {
            int userId = interaction.getUserId();
            int trackId = interaction.getTrackId();
            double actualRating = interaction.getRating();
            
            // Consider ratings >= 4 as relevant
            if (actualRating >= 4.0) {
                List<Track> recommendations = recommender.getRecommendations(userId, 10);
                if (recommendations.stream().anyMatch(t -> t.getId() == trackId)) {
                    relevantAndRecommended++;
                }
            }
            
            recommended += 10; // We recommend 10 items per user
        }
        
        return (double) relevantAndRecommended / recommended;
    }
    
    private double calculateRecall(Recommender recommender, List<Interaction> testSet) {
        int relevantAndRecommended = 0;
        int relevant = 0;
        
        for (Interaction interaction : testSet) {
            int userId = interaction.getUserId();
            int trackId = interaction.getTrackId();
            double actualRating = interaction.getRating();
            
            // Consider ratings >= 4 as relevant
            if (actualRating >= 4.0) {
                relevant++;
                List<Track> recommendations = recommender.getRecommendations(userId, 10);
                if (recommendations.stream().anyMatch(t -> t.getId() == trackId)) {
                    relevantAndRecommended++;
                }
            }
        }
        
        return relevant > 0 ? (double) relevantAndRecommended / relevant : 0.0;
    }
} 