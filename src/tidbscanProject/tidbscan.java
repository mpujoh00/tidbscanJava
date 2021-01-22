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
	public static final int NOISE = -1;

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
		
		for(Point point: D) {
			if(TI_ExpandCluster(D, clusteredPoints, point, currentClusterID, eps, minPts))
				currentClusterID++;
		}
		
		
		return clusteredPoints;
	}
	
	public static boolean TI_ExpandCluster(ArrayList<Point> D, ArrayList<Point> clusteredPoints, Point p, int clusterID, double eps, int minPts) {
		
		// seeds = NEps(p)\{p}
		ArrayList<Point> seeds = TI_Neighborhood(D, p, eps);
		
		p.setNeighborsNo(p.getNeighborsNo() + seeds.size()); // includes p itself
		
		if(p.getNeighborsNo() < minPts) { // noise or border point
			p.setClusterID(NOISE);
			
			for(Point q: seeds) {
				q.addPointToBorder(p);
				q.setNeighborsNo(q.getNeighborsNo() + 1);
			}			
			p.setBorder(new ArrayList<>());
			D.remove(D.indexOf(p));
			clusteredPoints.add(p);
			
			return false; // cluster hasn't been expanded (point is noise)
		}
		else {
			p.setClusterID(clusterID);
			
			for(Point q: seeds) {
				q.setClusterID(clusterID);
				q.setNeighborsNo(q.getNeighborsNo() + 1);
			}
			for(Point q: p.getBorder()) {
				int index = clusteredPoints.indexOf(q);
				if(index == -1)
					clusteredPoints.add(q);
				index = clusteredPoints.indexOf(q);
				clusteredPoints.get(index).setClusterID(clusterID);
			}
			p.setBorder(new ArrayList<>());
			
			while(seeds.size() > 0) {
				Point currentPoint = seeds.get(0);
				ArrayList<Point> currentSeeds = TI_Neighborhood(D, currentPoint, eps);
				currentPoint.setNeighborsNo(currentPoint.getNeighborsNo() + currentSeeds.size());
			}
		}
		
		return false;
	}
	
	public static ArrayList<Point> TI_Neighborhood(ArrayList<Point> D, Point p, double eps){
		
		ArrayList<Point> neighorhood = TI_Backward_Neighborhood(D, p, eps);
		neighorhood.addAll(TI_Forward_Neighborhood(D, p, eps));
		
		return neighorhood;
	}
	
	public static ArrayList<Point> TI_Backward_Neighborhood(ArrayList<Point> D, Point p, double eps){
		
		ArrayList<Point> seeds = new ArrayList<>();
		double backwardThreshold = p.getDistance() - eps;
		
		// starts with the point preceding p
		for(int i = D.indexOf(p)-1; i >= 0; i--) {
			
			Point q = D.get(i);
			
			if(q.getDistance() < backwardThreshold)
				break;
			
			if(distance(q, p) <= eps)
				seeds.add(q);
		}		
		
		return seeds;
	}
	
	public static ArrayList<Point> TI_Forward_Neighborhood(ArrayList<Point> D, Point p, double eps){
		
		ArrayList<Point> seeds = new ArrayList<>();
		double forwardThreshold = p.getDistance() + eps;
		
		// starts with the point following p
		for(int i = D.indexOf(p)+1; i < D.size(); i++) {
			
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
