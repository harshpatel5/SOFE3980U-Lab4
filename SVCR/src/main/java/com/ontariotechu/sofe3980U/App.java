package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		String[] modelFiles = {"model_1.csv", "model_2.csv", "model_3.csv"};
		double[] mseResults = new double[3];
		double[] maeResults = new double[3];
		double[] mareResults = new double[3];
		
		// Calculate metrics for each model
		for (int i = 0; i < modelFiles.length; i++) {
			String filePath = modelFiles[i];
			try {
				// Read CSV file
				FileReader filereader = new FileReader(filePath); 
				CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
				List<String[]> allData = csvReader.readAll();
				
				// Calculate metrics
				double sumSquaredError = 0.0;
				double sumAbsoluteError = 0.0;
				double sumRelativeError = 0.0;
				int count = 0;
				
				for (String[] row : allData) { 
					double y_true = Double.parseDouble(row[0]);
					double y_predicted = Double.parseDouble(row[1]);
					
					double error = y_true - y_predicted;
					sumSquaredError += error * error;
					sumAbsoluteError += Math.abs(error);
					sumRelativeError += Math.abs(error) / Math.abs(y_true);
					count++;
				}
				
				// Calculate final metrics
				mseResults[i] = sumSquaredError / count;
				maeResults[i] = sumAbsoluteError / count;
				mareResults[i] = sumRelativeError / count;
				
				// Display results
				System.out.println("for " + filePath);
				System.out.println("\tMSE =" + mseResults[i]);
				System.out.println("\tMAE =" + maeResults[i]);
				System.out.println("\tMARE =" + mareResults[i]);
				
			} catch(Exception e) {
				System.out.println("Error reading the CSV file: " + filePath);
				System.out.println("Error message: " + e.getMessage());
			}
		}
		
		// Find best model for each metric
		int bestMSEIndex = 0;
		int bestMAEIndex = 0;
		int bestMAREIndex = 0;
		
		for (int i = 1; i < 3; i++) {
			if (mseResults[i] < mseResults[bestMSEIndex]) {
				bestMSEIndex = i;
			}
			if (maeResults[i] < maeResults[bestMAEIndex]) {
				bestMAEIndex = i;
			}
			if (mareResults[i] < mareResults[bestMAREIndex]) {
				bestMAREIndex = i;
			}
		}
		
		// Display recommendations
		System.out.println("According to MSE, The best model is " + modelFiles[bestMSEIndex]);
		System.out.println("According to MAE, The best model is " + modelFiles[bestMAEIndex]);
		System.out.println("According to MARE, The best model is " + modelFiles[bestMAREIndex]);
    }
}
