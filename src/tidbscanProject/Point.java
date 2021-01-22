package tidbscanProject;

import java.util.ArrayList;
import java.util.Arrays;
import static tidbscanProject.Tidbscan.UNCLASSIFIED;

public class Point {

	private int clusterID;	// label of a cluster to which the point p belongs, initially assigned to UNCLASSIFIED
	private double distance;	// distance of p to the reference point r
	private int neighborsNo;	// number of neighbors of p found, initially assigned to 1 (itself belongs to its own eps-neighborhood)
	private ArrayList<Point> border;		// information about neighbors of p that are non-core points (not clear if they're noise or border), initially assigned an empty set
	//private String[] metadata;
	private double[] coordinates;
	
	public Point(double[] coord) {	// , String[] metadata
		
		this.clusterID = UNCLASSIFIED;
		this.neighborsNo = 1;
		this.border = new ArrayList<Point>();		
		//if(metadata != null)
		//	this.metadata = metadata;
		if(coord != null)
			this.coordinates = coord;
	}
	
	/*public Point(int[] coord, double dist) {	// , String[] metadata
		
		this.clusterID = UNCLASSIFIED;
		this.distance = dist;
		this.neighborsNo = 1;
		this.border = new ArrayList<Point>();		
		//if(metadata != null)
		//	this.metadata = metadata;
		if(coord != null)
			this.coordinates = coord;
	}*/
	
	public void setClusterID(int id) {
		this.clusterID = id;
	}
	
	public void setDistance(double dist) {
		this.distance = dist;
	}
	
	public void setNeighborsNo(int neigh) {
		this.neighborsNo = neigh;
	}
	
	public void setBorder(ArrayList<Point> border) {
		this.border = border;
	}
	
	public int getClusterID() {
		return this.clusterID;
	}
	
	public double getDistance() {
		return this.distance;
	}
	
	public int getNeighborsNo() {
		return this.neighborsNo;
	}
	
	public ArrayList<Point> getBorder() {
		return this.border;
	}
	
	public double[] getCoordinates() {
		return this.coordinates;
	}
	
	public String toString() {
		
		return Arrays.toString(this.coordinates) + " ClusterID = " + this.clusterID + " Distance = " + this.distance + " NeighborsNo = "
				+ this.neighborsNo + " Border = " + this.border.size() + " points\n";
	}
}
