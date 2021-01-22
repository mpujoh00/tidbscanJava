package tidbscanProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Tidbscan {

	public static void main(String[] args) {

		ArrayList<Point> dataset = new ArrayList<>();
		
		try {
			dataset = datasetToList("datasetS1.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(dataset.size() != 0) {
			ArrayList<Point> clusteredDataset = TI_DBSCAN(dataset, 0.05, 4);
		}else {
			System.out.println("Incorrect dataset");}

	}
	
	/*
	 * D: set of points that is subject to clustering
	 * eps: radius of the point neighborhood
	 * minPts: required minimal number of points within Eps-neighborhood
	 */
	public static ArrayList<Point> TI_DBSCAN(ArrayList<Point> D, double eps, int minPts){
		
		// D' = empty set of points
		ArrayList<Point> checkedPoints = new ArrayList<>();
		int[] coordsZero = {0, 0};
		Point referencePoint = new Point(coordsZero);
		
		for(Point point: D) {
			point.setDistance(distance(point, referencePoint));
		}
		
		// sorts all points of D in a non decreasing way by distance
		Collections.sort(D, new Comparator<Point>() {		
			@Override
			public int compare(Point x, Point y) {
				return Double.compare(x.getDistance(), y.getDistance());
			}			
		});
		
		
		return null;
	}
	
	// calculates distance between 2 points (2 dimensions)
	public static double distance(Point p, Point r) {
		
		int[] pCoords = p.getCoordinates();
		int[] rCoords = r.getCoordinates();
		
		return Math.sqrt(Math.pow((pCoords[0]-rCoords[0]), 2) + Math.pow((pCoords[1]-rCoords[1]), 2));
	}
	
	// transforms dataset file to arraylist
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
