package tidbscanProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Tidbscan {
	
	public static final int UNCLASSIFIED = 0;

	public static void main(String[] args) {

		ArrayList<Point> dataset = new ArrayList<>();
		
		try {
			dataset = datasetToList("test.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(dataset.size() != 0) {
			ArrayList<Point> clusteredDataset = TI_DBSCAN(dataset, 2, 2);
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
		ArrayList<Point> clusteredPoints = new ArrayList<>();
		double[] coordsZero = {0, 0};
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
		
		// initial cluster
		int currentClusterID = 1;
		int id = 0;
		
		for(Point point: D) {
			if(TI_ExpandCluster(D, clusteredPoints, point, currentClusterID, eps, minPts, id))
				currentClusterID++;
			id++;
		}
		
		
		return clusteredPoints;
	}
	
	public static boolean TI_ExpandCluster(ArrayList<Point> D, ArrayList<Point> clusteredPoints, Point p, int clusterID, double eps, int minPts, int pointId) {
		
		// seeds = NEps(p)\{p}
		ArrayList<Point> seeds = TI_Neighborhood(D, p, eps, pointId);
		
		
		
		return false;
	}
	
	public static ArrayList<Point> TI_Neighborhood(ArrayList<Point> D, Point p, double eps, int pointId){
		
		ArrayList<Point> neighorhood = TI_Backward_Neighborhood(D, p, eps, pointId);
		neighorhood.addAll(TI_Forward_Neighborhood(D, p, eps, pointId));
		
		return neighorhood;
	}
	
	public static ArrayList<Point> TI_Backward_Neighborhood(ArrayList<Point> D, Point p, double eps, int pointId){
		
		ArrayList<Point> seeds = new ArrayList<>();
		double backwardThreshold = p.getDistance() - eps;
		
		// starts with the point preceding p
		for(int i = pointId-1; i >= 0; i--) {
			
			Point q = D.get(i);
			
			if(q.getDistance() < backwardThreshold)
				break;
			
			if(distance(q, p) <= eps)
				seeds.add(q);
		}		
		
		return seeds;
	}
	
	public static ArrayList<Point> TI_Forward_Neighborhood(ArrayList<Point> D, Point p, double eps, int pointId){
		
		ArrayList<Point> seeds = new ArrayList<>();
		double forwardThreshold = p.getDistance() + eps;
		
		// starts with the point following p
		for(int i = pointId+1; i < D.size(); i++) {
			
			Point q = D.get(i);
			
			if(q.getDistance() > forwardThreshold)
				break;
			
			if(distance(q, p) <= eps)
				seeds.add(q);
		}		
		
		return seeds;
	}
		
	// calculates distance between 2 points (2 dimensions)
	public static double distance(Point p, Point r) {
		
		double[] pCoords = p.getCoordinates();
		double[] rCoords = r.getCoordinates();
		
		return Math.sqrt(Math.pow((pCoords[0]-rCoords[0]), 2) + Math.pow((pCoords[1]-rCoords[1]), 2));
	}
	
	// transforms dataset file to arraylist
	public static ArrayList<Point> datasetToList(String datasetName) throws FileNotFoundException{
		
		// reads file
		Scanner scanner = new Scanner(new File("datasets/" + datasetName));
		
		ArrayList<Point> dataset = new ArrayList<>();
		String[] pointData;
		double[] coords;
		String line;
		
		// reads each line
		while(scanner.hasNextLine()) {
			
			// gets all coordinates (we don't use datasets with more information)
			line = scanner.nextLine();
			pointData = line.trim().split("\\s+"); 
			coords = new double[pointData.length];
			int i = 0;
			
			for(String x: pointData) {
				coords[i] = Double.parseDouble(x);
				i++;
			}				
			dataset.add(new Point(coords));
		}
		scanner.close();
		
		return dataset;
	}

}
