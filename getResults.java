import java.util.*;
import java.io.*;

class Point {
	public int id;
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

class Neighbor implements Comparable<Neighbor>{
	public double dist;
	public Point point;

	public Neighbor(double d, Point p){
		this.dist = d;
		this.point = p;
	}

	public int compareTo(Neighbor n){
		if (this.dist == n.dist){
			return 0;
		}
		else if (this.dist > n.dist) {
			return 1;
		}
		return -1;
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

	public double dlow(double x, double y, double g_xmin, double g_ymin, double cell_x, double cell_y){
		double x_min = g_xmin + this.x * cell_x;
		double x_max = x_min + cell_x;
		double y_min = g_ymin + this.y * cell_y;
		double y_max = y_min + cell_y;

		double dx =  Math.max(Math.max(x_min - x, 0), x - x_max);	/* max(x_min - x, 0, x - x_max) */
		double dy =  Math.max(Math.max(y_min - y, 0), y - y_max);	/* max(y_min - y, 0, y - y_max) */
		return Math.sqrt(dx * dx + dy * dy);
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

	/**
	 * Find the grid for a location
	 * @param (x,y): location longitude and latitude
	 */	
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

	/**
	 * Find the layer of grids k grids from a grid
	 */
	public List<Grid> get_layer(int i, int j, int k){
		List<Grid> layer = new ArrayList<Grid>();
		if(k == 0){
			layer.add(this.grids[i][j]);
			return layer;
		}
		if(j+k <  n){ layer.add(this.grids[i][j+k]); }
		if(j-k >= 0){ layer.add(this.grids[i][j-k]); }
		if(i+k <  n){ layer.add(this.grids[i+k][j]); }
		if(i-k >= 0){ layer.add(this.grids[i-k][j]); }
		for(int a = 0; a < k; a++){
			if(i-a >= 0 && j+k < n){ layer.add(this.grids[i-a][j+k]); }
			if(i+a <  n && j+k < n){ layer.add(this.grids[i+a][j+k]); }
			if(j-a >= 0 && i+k < n){ layer.add(this.grids[i+k][j-a]); }
			if(j+a <  n && i+k < n){ layer.add(this.grids[i+k][j+a]); }
		}
		if(i-k >= 0 && j+k < n){ layer.add(this.grids[i-k][j+k]); }
		if(i+k <  n && j+k < n){ layer.add(this.grids[i+k][j+k]); }
		if(j-k >= 0 && i+k < n){ layer.add(this.grids[i+k][j-k]); }
		if(j+k <  n && i+k < n){ layer.add(this.grids[i+k][j+k]); }
		return layer;
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
		List<Neighbor> queue = new ArrayList<Neighbor>();
		double t = Math.sqrt((x_max-x_min) * (x_max-x_min) + (y_max-y_min) * (y_max-y_min));
		Grid grid = grid_index.get_grid(x, y);
		int layer_count = 0;
		boolean pruned = false;
		while((layer_count < n-1) && (!pruned)){
			List<Grid> layer = grid_index.get_layer(grid.x, grid.y, layer_count);
			int prune = 0;
			for(Grid g: layer){
				double dlow = g.dlow(x, y, x_min, y_min, cell_x, cell_y);
				if (dlow > t){
					prune += 1;
				}else{
					List<Point> points = g.getPoints();
					for (Point p: points){
						double d = p.dist(x, y);
						if (d < t){
							Neighbor nb = new Neighbor(d, p);
							queue.add(nb);
							Collections.sort(queue);
							if (queue.size() > 0){
								t = queue.get(Math.min(queue.size()-1, k-1)).dist;
							}
						}
					}
				}
			}
			if (prune == layer.size()){
				pruned = true;
			}
			layer_count += 1;
		}
		
		// store the k-NN results by a String of location ids, like "11, 789, 125, 2, 771"
		String ret = "" + queue.get(0).point.id;
		for(int i = 1; i < k; i++){
			Neighbor nb = queue.get(i);
			ret = ret + ", " + nb.point.id;
		}
		System.out.println("k-NN computed!");
		return ret;	
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