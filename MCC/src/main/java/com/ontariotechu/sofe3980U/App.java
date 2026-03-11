package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Multi-Class Classification
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		String filePath="model.csv";
		FileReader filereader;
		List<String[]> allData;
		try{
			filereader = new FileReader(filePath); 
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file" );
			return;
		}
		
		// Initialize confusion matrix (5x5 for classes 1-5)
		int[][] confusionMatrix = new int[5][5];
		double sumCE = 0.0;
		
		for (String[] row : allData) { 
			int y_true = Integer.parseInt(row[0]);
			double[] y_predicted = new double[5];
			
			// Read predicted probabilities
			for(int i = 0; i < 5; i++){
				y_predicted[i] = Double.parseDouble(row[i + 1]);
			}
			
			// Calculate Cross Entropy
			// CE = -log(predicted probability of true class)
			double epsilon = 1e-15; // To avoid log(0)
			double prob = Math.max(epsilon, Math.min(1 - epsilon, y_predicted[y_true - 1]));
			sumCE += -Math.log(prob);
			
			// Find predicted class (argmax of probabilities)
			int y_pred_class = 0;
			double maxProb = y_predicted[0];
			for(int i = 1; i < 5; i++){
				if(y_predicted[i] > maxProb){
					maxProb = y_predicted[i];
					y_pred_class = i;
				}
			}
			y_pred_class++; // Convert from 0-indexed to 1-indexed
			
			// Update confusion matrix
			confusionMatrix[y_pred_class - 1][y_true - 1]++;
		}
		
		// Calculate average CE
		double ce = sumCE / allData.size();
		
		// Display results
		System.out.println("CE =" + ce);
		System.out.println("Confusion matrix");
		System.out.println("\t\ty=1\t y=2\t y=3\t y=4\t y=5");
		
		for(int i = 0; i < 5; i++){
			System.out.print("\ty^=" + (i + 1) + "\t");
			for(int j = 0; j < 5; j++){
				System.out.print(confusionMatrix[i][j] + "\t ");
			}
			System.out.println();
		}
	}
}
