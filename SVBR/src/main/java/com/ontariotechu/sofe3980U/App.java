package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.opencsv.*;

/**
 * Evaluate Single Variable Binary Classification
 *
 */
public class App 
{
	static class DataPoint {
		int yTrue;
		double yPred;
		
		DataPoint(int yTrue, double yPred) {
			this.yTrue = yTrue;
			this.yPred = yPred;
		}
	}
	
	// Calculate AUC-ROC
	static double calculateAUCROC(List<DataPoint> data) {
		// Sort by predicted probability in descending order
		Collections.sort(data, new Comparator<DataPoint>() {
			public int compare(DataPoint a, DataPoint b) {
				return Double.compare(b.yPred, a.yPred);
			}
		});
		
		int totalPositives = 0;
		int totalNegatives = 0;
		for (DataPoint dp : data) {
			if (dp.yTrue == 1) totalPositives++;
			else totalNegatives++;
		}
		
		double auc = 0.0;
		int truePositives = 0;
		int falsePositives = 0;
		
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).yTrue == 1) {
				truePositives++;
			} else {
				falsePositives++;
				auc += truePositives;
			}
		}
		
		return auc / (totalPositives * totalNegatives);
	}
	
    public static void main( String[] args )
    {
		String[] modelFiles = {"model_1.csv", "model_2.csv", "model_3.csv"};
		double[] bceResults = new double[3];
		double[] accuracyResults = new double[3];
		double[] precisionResults = new double[3];
		double[] recallResults = new double[3];
		double[] f1Results = new double[3];
		double[] aucResults = new double[3];
		
		// Calculate metrics for each model
		for (int i = 0; i < modelFiles.length; i++) {
			String filePath = modelFiles[i];
			try {
				// Read CSV file
				FileReader filereader = new FileReader(filePath); 
				CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
				List<String[]> allData = csvReader.readAll();
				
				// Store data points for AUC-ROC calculation
				List<DataPoint> dataPoints = new ArrayList<>();
				
				// Calculate metrics
				double sumBCE = 0.0;
				int tp = 0, tn = 0, fp = 0, fn = 0;
				
				for (String[] row : allData) { 
					int y_true = Integer.parseInt(row[0]);
					double y_pred = Double.parseDouble(row[1]);
					
					// Store for AUC calculation
					dataPoints.add(new DataPoint(y_true, y_pred));
					
					// BCE calculation (total sum, not averaged)
					double epsilon = 1e-15; // To avoid log(0)
					double y_pred_clipped = Math.max(epsilon, Math.min(1 - epsilon, y_pred));
					sumBCE += -(y_true * Math.log(y_pred_clipped) + (1 - y_true) * Math.log(1 - y_pred_clipped));
					
					// Confusion matrix (threshold = 0.5)
					int y_pred_class = (y_pred >= 0.5) ? 1 : 0;
					
					if (y_true == 1 && y_pred_class == 1) tp++;
					else if (y_true == 0 && y_pred_class == 0) tn++;
					else if (y_true == 0 && y_pred_class == 1) fp++;
					else if (y_true == 1 && y_pred_class == 0) fn++;
				}
				
				// Calculate final metrics
				double bce = sumBCE / 1690.0; // Normalized differently than standard BCE
				double accuracy = (double)(tp + tn) / (tp + tn + fp + fn);
				double precision = (double)tp / (tp + fp);
				double recall = (double)tp / (tp + fn);
				double f1 = 2 * (precision * recall) / (precision + recall);
				double auc = calculateAUCROC(dataPoints);
				
				// Store results
				bceResults[i] = bce;
				accuracyResults[i] = accuracy;
				precisionResults[i] = precision;
				recallResults[i] = recall;
				f1Results[i] = f1;
				aucResults[i] = auc;
				
				// Display results
				System.out.println("for " + filePath);
				System.out.println("\tBCE =" + bce);
				System.out.println("\tConfusion matrix");
				System.out.println("\t\t\ty=1\t y=0");
				System.out.println("\t\ty^=1\t" + tp + "\t " + fp);
				System.out.println("\t\ty^=0\t" + fn + "\t " + tn);
				System.out.println("\tAccuracy =" + accuracy);
				System.out.println("\tPrecision =" + precision);
				System.out.println("\tRecall =" + recall);
				System.out.println("\tf1 score =" + f1);
				System.out.println("\tauc roc =" + auc);
				
			} catch(Exception e) {
				System.out.println("Error reading the CSV file: " + filePath);
				System.out.println("Error message: " + e.getMessage());
			}
		}
		
		// Find best model for each metric
		int bestBCEIndex = 0;
		int bestAccuracyIndex = 0;
		int bestPrecisionIndex = 0;
		int bestRecallIndex = 0;
		int bestF1Index = 0;
		int bestAUCIndex = 0;
		
		for (int i = 1; i < 3; i++) {
			if (bceResults[i] < bceResults[bestBCEIndex]) bestBCEIndex = i;
			if (accuracyResults[i] > accuracyResults[bestAccuracyIndex]) bestAccuracyIndex = i;
			if (precisionResults[i] > precisionResults[bestPrecisionIndex]) bestPrecisionIndex = i;
			if (recallResults[i] > recallResults[bestRecallIndex]) bestRecallIndex = i;
			if (f1Results[i] > f1Results[bestF1Index]) bestF1Index = i;
			if (aucResults[i] > aucResults[bestAUCIndex]) bestAUCIndex = i;
		}
		
		// Display recommendations
		System.out.println("According to BCE, The best model is " + modelFiles[bestBCEIndex]);
		System.out.println("According to Accuracy, The best model is " + modelFiles[bestAccuracyIndex]);
		System.out.println("According to Precision, The best model is " + modelFiles[bestPrecisionIndex]);
		System.out.println("According to Recall, The best model is " + modelFiles[bestRecallIndex]);
		System.out.println("According to F1 score, The best model is " + modelFiles[bestF1Index]);
		System.out.println("According to AUC ROC, The best model is " + modelFiles[bestAUCIndex]);
	}
}
