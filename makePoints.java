
// === makePoints.java ===

/*
	Usage: java makePoints N OUTPUT_PATH

	java makePoints 100 /Users/alog1024/Desktop/query.txt

*/

import java.util.*;
import java.io.*;


class QueryPoint {
	public double x;
	public double y;
	 
	public QueryPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		String str = this.x + "\t" + this.y;
		return str;
	}
}

public class makePoints{

	/*
  		Generate n query points within max_box
  		Store the n query points to file output_path
	*/
	public static void makePoints(double x_min, double x_max, double y_min, double y_max, int n, String output_path){
		System.out.println("Calling makePoints!");
		List<QueryPoint> points = new ArrayList<QueryPoint>();
		for (int i = 0; i < n; i++){
			double x = Math.random() * (x_max - x_min) + x_min;
			double y = Math.random() * (y_max - y_min) + y_min;
			QueryPoint p = new QueryPoint(x, y);
			points.add(p);
		}
		System.out.println("Query points generated!");

		// write the dataset without duplicates into data_path_new
		try {
			FileWriter fileWriter = new FileWriter(output_path); 
			try (BufferedWriter writer = new BufferedWriter(fileWriter)) {
				for(QueryPoint p : points){
					writer.write(p.toString());
					writer.newLine();
				}
			}
		} catch (IOException ex) {}
		System.out.println("Output written!");
	}

	public static void main(String[]args){

		if(args.length != 2){
			System.out.println("Usage: java makePoints N OUTPUT_PATH");
			/*
  			N(integer): the number of query points to generate
  			OUTPUT_PATH(String): the output file path to store the query points
			*/
			return;
		}

		/* max box */
		double x_min = -90.0;
		double x_max = 90.0;
		double y_min = -176.30859375;
		double y_max = 177.462490797;

		long s = System.currentTimeMillis();
		makePoints(x_min, x_max, y_min, y_max, Integer.parseInt(args[0]), args[1]);
		long t = System.currentTimeMillis();
		System.out.println("Query point generation time: "+(t-s));
	}
}