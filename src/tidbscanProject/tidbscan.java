package tidbscanProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Tidbscan {

	public static void main(String[] args) {

		try {
			datasetToList("datasetS1.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * D: set of points that is subject to clustering
	 * eps: radius of the point neighborhood
	 * minPts: required minimal number of points within Eps-neighborhood
	 */
	public static ArrayList<Point> TI_DBSCAN(ArrayList<Point> D, double eps, int minPts){
		
		// D' = empty set of points
		ArrayList<Point> checkedPoints = new ArrayList<>();
		
		for(Point p: D) {
			
			
		}
		
		return null;
	}
	
	public static ArrayList<Point> datasetToList(String datasetName) throws FileNotFoundException{
		
		// reads file
		Scanner scanner = new Scanner(new File("datasets/" + datasetName));
		
		ArrayList<Point> dataset = new ArrayList<>();
		String[] pointData;
		int[] coords;
		String line;
		
		// reads each line
		while(scanner.hasNextLine()) {
			
			// gets all coordinates (we don't use datasets with more information)
			line = scanner.nextLine();
			pointData = line.trim().split("\\s+");
			System.out.println(Arrays.toString(pointData));
			coords = new int[pointData.length];
			int i = 0;
			
			for(String x: pointData) {
				coords[i] = Integer.parseInt(x);
				i++;
			}			
			
			
			dataset.add(new Point(coords));
		}
		scanner.close();
		
		return dataset;
	}

}
