package tidbscanProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Tidbscan {
	
	public static final int UNCLASSIFIED = 0;
	public static final int NOISE = -1;
	private static ArrayList<Point> dataset;
	private static ArrayList<Point> clusteredPoints;

	public static void main(String[] args) {

		try {
			dataset = datasetToList("datasetS1.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(dataset.size() != 0) {
			ArrayList<Point> clusteredDataset = TI_DBSCAN(2, 2);
			
			System.out.println("CLUSTERED DATASET");
			for(Point p: clusteredDataset)
				System.out.println(p);
			
		}else {
			System.out.println("Incorrect dataset");}

	}
	
	/*
	 * eps: radius of the point neighborhood
	 * minPts: required minimal number of points within Eps-neighborhood
	 */
	public static ArrayList<Point> TI_DBSCAN(double eps, int minPts){
		
		// D' = empty set of points (clusteredPoints)
		clusteredPoints = new ArrayList<>();
		double[] coordsZero = {0, 0};
		Point referencePoint = new Point(coordsZero);
		
		for(Point point: dataset) {
			point.setDistance(distance(point, referencePoint));
		}
		
		// sorts all points of D in a non decreasing way by distance
		Collections.sort(dataset, new Comparator<Point>() {		
			@Override
			public int compare(Point x, Point y) {
				return Double.compare(x.getDistance(), y.getDistance());
			}			
		});
		
		// initial cluster
		int currentClusterID = 1;
		
		for(int i=0; i<dataset.size(); i++) {
			Point point = dataset.get(i);
			if(point != null) {
				if(TI_ExpandCluster(point, currentClusterID, eps, minPts))
					currentClusterID++; }
		}
		while(dataset.remove(null)) {}
		
		return clusteredPoints;
	}
	
	public static boolean TI_ExpandCluster(Point p, int clusterID, double eps, int minPts) {
		
		ArrayList<Point> D = dataset; 
		// seeds = NEps(p)\{p}
		ArrayList<Point> seeds = TI_Neighborhood(p, eps);
		
		p.setNeighborsNo(p.getNeighborsNo() + seeds.size()); // includes p itself
		
		if(p.getNeighborsNo() < minPts) { // noise or border point
			
			p.setClusterID(NOISE);	
			
			for(Point q: seeds) {
				q.addPointToBorder(p);
				q.setNeighborsNo(q.getNeighborsNo() + 1);
			}			
			p.setBorder(new ArrayList<>());
			D.set(D.indexOf(p), null); // moves p from D to D'
			clusteredPoints.add(p);
			
			dataset = D;
			
			return false; // cluster hasn't been expanded (point is noise)
		}
		else {
			p.setClusterID(clusterID);
			
			for(Point q: seeds) {
				q.setClusterID(clusterID);
				q.setNeighborsNo(q.getNeighborsNo() + 1);
			}
			for(Point q: p.getBorder()) {
				int index = clusteredPoints.indexOf(q); // assigns cluster id to q in D'
				if(index == -1)
					clusteredPoints.add(q);
				index = clusteredPoints.indexOf(q);
				clusteredPoints.get(index).setClusterID(clusterID);
			}
			p.setBorder(new ArrayList<>());
			D.set(D.indexOf(p), null); // moves p from D to D'
			clusteredPoints.add(p);	// clusteredPoints stores analyzed points
			
			while(seeds.size() > 0) {
				Point currentPoint = seeds.get(0); // first point
				ArrayList<Point> currentSeeds = TI_Neighborhood(currentPoint, eps);
				currentPoint.setNeighborsNo(currentPoint.getNeighborsNo() + currentSeeds.size());
				
				if(currentPoint.getNeighborsNo() < minPts) { // currentPoint is a border point
					for(Point q: currentSeeds)
						q.setNeighborsNo(q.getNeighborsNo() + 1);
				}
				else {	// currentPoint is a core point
					for(Point q: currentSeeds) {
						q.setNeighborsNo(q.getNeighborsNo() + 1);
						
						if(q.getClusterID() == UNCLASSIFIED) {
							q.setClusterID(clusterID);
							currentSeeds.remove(currentSeeds.indexOf(q)); // moves currentPoint from currentSeeds to seeds
							seeds.add(q);
						}
						else {
							currentSeeds.remove(currentSeeds.indexOf(q)); // deletes currentPoint from currentSeeds
						}
						if(currentSeeds.size() == 0)
							break;
					}
					for(Point q: currentPoint.getBorder()) {
						int index = clusteredPoints.indexOf(q); // assigns a cluster id to q in D'
						if(index == -1)
							clusteredPoints.add(q);
						index = clusteredPoints.indexOf(q);
						clusteredPoints.get(index).setClusterID(clusterID);
					}						
				}
				currentPoint.setBorder(new ArrayList<>());
				D.set(D.indexOf(currentPoint), null); // moves currentPoint from D to D'
				clusteredPoints.add(currentPoint);
				seeds.remove(seeds.indexOf(currentPoint));
			}
			dataset = D;
			return true; // the cluster has been expanded
		}
	}
	
	// gets p's eps-neighborhood
	public static ArrayList<Point> TI_Neighborhood(Point p, double eps){
		
		ArrayList<Point> neighorhood = TI_Backward_Neighborhood(p, eps);
		neighorhood.addAll(TI_Forward_Neighborhood(p, eps));
		
		return neighorhood;
	}
	
	// gets part of p's eps-neighborhood from its preceding points in D
	public static ArrayList<Point> TI_Backward_Neighborhood(Point p, double eps){
		
		ArrayList<Point> D = dataset;
		ArrayList<Point> seeds = new ArrayList<>();
		double backwardThreshold = p.getDistance() - eps;
		
		// starts with the point preceding p
		for(int i = D.indexOf(p)-1; i >= 0; i--) {
			
			Point q = D.get(i);
			if(q != null) {
				if(q.getDistance() < backwardThreshold)
					break;
				
				if(distance(q, p) <= eps)
					seeds.add(q);
			}
		}		
		dataset = D;
		
		return seeds;
	}
	
	// gets part of p's eps-neighborhood from its following points in D
	public static ArrayList<Point> TI_Forward_Neighborhood(Point p, double eps){
		
		ArrayList<Point> D = dataset;
		ArrayList<Point> seeds = new ArrayList<>();
		double forwardThreshold = p.getDistance() + eps;
		
		// starts with the point following p
		for(int i = D.indexOf(p)+1; i < D.size(); i++) {
			
			Point q = D.get(i);
			
			if(q != null) {
				if(q.getDistance() > forwardThreshold)
					break;
				
				if(distance(q, p) <= eps)
					seeds.add(q);
			}
		}		
		dataset = D;
		
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
