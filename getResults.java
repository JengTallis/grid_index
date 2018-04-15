import java.util.*;
import java.io.*;

class Point {
	private int id;
	private double x;
	private double y;

	public static double round(double num, int precision){
		double scale = Math.pow(10, precision);
		return (double) Math.round(num * scale) / scale;
	}
	 
	/**
	 * @param x: x-axis
	 * @param y: y-axis
	 */
	public Point(int id, double x, double y) {
		this.id = id;
		this.x = round(x, 1);
		this.y = round(y, 1);
	}

	public Point(String s) {
		String[] col = s.split("_");
		this.id = Integer.parseInt(col[0]);
		this.x = Double.parseDouble(col[1]);
		this.y = Double.parseDouble(col[2]);
	}

	/**
	 * return (x,y)
	 */
	public String toString() {
		String str = this.id + "_" + this.x + "_" + this.y;
		return str;
	}

	/**
	 * return euclidean distance to (x,y)
	 */
	public double dist(double x, double y){
		double xd = this.x - x;
		double yd = this.y - y;
		return Math.sqrt(xd * xd + yd * yd);
	}

}

class Grid{
	public int x;
	public int y;
	private List<Point> points = new ArrayList<Point>();
	/**
	 * Default Constructor
	 */
	public Grid() {

	}
	/**
	 * @param x,y: the index of 2D matrix
	 */
	public Grid(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Append a point (x,y) to the grid
	 * @param x: x-axis
	 * @param y: y-axis
	 */
	public void appendPoint(int id, double x, double y) {
		Point newPoint = new Point(id, x, y);
		this.appendPoint(newPoint);
	}

	/**
	 * Append a point instance to the grid
	 * @param point: a new point
	 */
	public void appendPoint(Point point) {
		if(points.contains(point))
			System.out.println("This point " + point.toString() + " already exsits!!");
		else
			points.add(point);
	}

	public List<Point> getPoints(){
		return this.points;
	}

}

class GridIndex{
	public int n;
	private double x_min;
	private double y_min;
	private double cell_x;
	private double cell_y;
	private Grid[][] grids = new Grid[n][n];

	public GridIndex() {

	}

	public GridIndex(int n, double x_min, double y_min, double cell_x, double cell_y, Grid[][] grids) {
		this.n = n;
		this.x_min = x_min;
		this.y_min = y_min;
		this.cell_x = cell_x;
		this.cell_y = cell_y;
		this.grids = grids;
	}

	public Grid get_grid(double x, double y){
		int x_idx = 1;
		int y_idx = 1;
		while(this.x_min + x_idx * this.cell_x < x){
			x_idx += 1;
		}
		x_idx -= 1;
		while(this.y_min + y_idx * this.cell_y < y){
			y_idx += 1;
		}
		y_idx -= 1;
		return this.grids[x_idx][y_idx];
	}

}

public class getResults{
	public static String knn_grid(double x, double y, String index_path, int k, int n){
		System.out.println("Calling getResults!");
		// define Grid parameters 
		/* max box */
		double x_min = -90.0;
		double x_max = 405.7;
		double y_min = -176.3;
		double y_max = 177.5;
		/* cell size */
		double cell_x = (x_max - x_min) / n;
		double cell_y = (y_max - y_min) / n;
		
		// load grid index from index_path
		Grid[][] grids = new Grid[n][n];
		try {
			FileReader fileReader = new FileReader(index_path); 
			try (BufferedReader reader = new BufferedReader(fileReader)) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] col = line.split(": ");
					String[] g_str = col[0].split(",");	/* Grid ID */
					int i = Integer.parseInt(g_str[0]);
					int j = Integer.parseInt(g_str[1]);
					Grid grid = new Grid(i, j);
					String p_str = col[1];				/* points */
					String[] points_str = p_str.split(" ");
					for(String s: points_str){
						Point p = new Point(s);
						grid.appendPoint(p);			/* add points to grid */
					}
					grids[i][j] = grid;			/* add grid to grid index */
				}
			}
		} catch (IOException ex) {}
		GridIndex grid_index = new GridIndex(n, x_min, y_min, cell_x, cell_y, grids);
		System.out.println("Grit index loaded!");

		// get the k-NN result with the help of the grid index
		




		
		// store the k-NN results by a String of location ids, like "11, 789, 125, 2, 771"
		return "";
	
	}


	public static String knn_linear_scan(double x, double y, String data_path_new, int k){
		// to get the k-NN result by linear scan
		// Please store the k-NN results by a String of location ids, like "11, 789, 125, 2, 771"

		return "";
	}

	public static void main(String args[]){
		if(args.length != 6){
			System.out.println("Usage: java getResults X Y DATA_PATH_NEW INDEX_PATH K N");
			/*
			X(double): the longitude of the query point q
			Y(double): the latitude of the query point q
			DATA_PATH_NEW(String): the file path of dataset you generated without duplicates
			INDEX_PATH(String): the file path of the grid index
			K(integer): the k value for k-NN search
			N(integer): the grid index size
  			*/
			return;
		}
		long s = System.currentTimeMillis();
		System.out.println("Linear scan results: "+knn_linear_scan(Double.parseDouble(args[0]), Double.parseDouble(args[1]), args[2], Integer.parseInt(args[4])));
		long t = System.currentTimeMillis();
		System.out.println("Linear scan time: "+(t-s));
		
		s = System.currentTimeMillis();
		System.out.println("Grid index search results: "+knn_grid(Double.parseDouble(args[0]), Double.parseDouble(args[1]), args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5])));
		t = System.currentTimeMillis();
		System.out.println("Grid index search time: "+(t-s));
	}
}