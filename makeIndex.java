/*
Arguments:
			DATA_PATH(String): the file path of Gowalla_totalCheckins.txt
			INDEX_PATH(String): the output file path of the Gri index
			DATA_PATH_NEW(String): the file path of the dataset without duplicates
  			N(integer): the Gri index size

/Users/alog1024/Desktop/courses/y4 sem2/Adv DB/Assignment 3/Gowalla_totalCheckins.txt
/Users/alog1024/Desktop/courses/y4 sem2/Adv DB/Assignment 3/index_path
/Users/alog1024/Desktop/courses/y4 sem2/Adv DB/Assignment 3/loc.txt
100

java makeIndex /Users/alog1024/Desktop/Gowalla_totalCheckins.txt /Users/alog1024/Desktop/index.txt /Users/alog1024/Desktop/loc.txt 10

*/


import java.util.*;
import java.io.*;

class Pnt {
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
	public Pnt(int id, double x, double y) {
		this.id = id;
		this.x = round(x, 1);
		this.y = round(y, 1);
	}

	/**
	 * return (x,y)
	 */
	public String toString() {
		String str = this.id + "_" + this.x + "_" + this.y;
		return str;
	}
}

class Gri{
	public int x;
	public int y;
	private List<Pnt> Pnts = new ArrayList<Pnt>();
	/**
	 * Default Constructor
	 */
	public Gri() {

	}
	/**
	 * @param x,y: the index of 2D matrix
	 */
	public Gri(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Append a Pnt (x,y) to the Gri
	 * @param x: x-axis
	 * @param y: y-axis
	 */
	public void appendPnt(int id, double x, double y) {
		Pnt newPnt = new Pnt(id, x, y);
		this.appendPnt(newPnt);
	}

	/**
	 * Append a Pnt instance to the Gri
	 * @param Pnt: a new Pnt
	 */
	public void appendPnt(Pnt Pnt) {
		if(Pnts.contains(Pnt))
			System.out.println("This Pnt " + Pnt.toString() + " already exsits!!");
		else
			Pnts.add(Pnt);
	}

	public List<Pnt> getPnts(){
		return this.Pnts;
	}

}

public class makeIndex {

	public static class LocComparator implements Comparator<String>
	{
		public int compare(String a, String b)
		{
			
			String[] cola = a.split("\t");
			String[] colb = b.split("\t");
			if(cola[2].equals(colb[2]) && cola[3].equals(colb[3])){
				return (Integer.parseInt(cola[4])-Integer.parseInt(colb[4]));
			}
			else if (cola[2].equals(colb[2])){
				return cola[3].compareTo(colb[3]);
			}
			return cola[2].compareTo(colb[2]);
			//return a.compareTo(b);	
		}
	}
	
	public static void duplicate_elimination(String data_path, String data_path_new){
		System.out.println("Calling duplicate_elimination!");
		// read the original dataset from data_path
		List<String> lines = new ArrayList<>();
		try {
			FileReader fileReader = new FileReader(data_path); 
			try (BufferedReader reader = new BufferedReader(fileReader)) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					lines.add(line);
				}
			}
		} catch (IOException ex) {}
		System.out.println("File read!");
		

		// eliminate duplicates by deleting the corresponding lines
		LocComparator comparator = new LocComparator();
		Collections.sort(lines, comparator); /* sort arraylist */
		List<String> uniques = new ArrayList<>(); /* arraylist of unique locations */
		int i = 0;
		int j = 1;
		uniques.add(lines.get(i));
		while (j < lines.size()) {
			String[] cola = lines.get(i).split("\t");
			String[] colb = lines.get(j).split("\t");
			if (cola[2].equals(colb[2]) && cola[3].equals(colb[3])) {
				j++;
			} else {
				i++;
				String[] col = uniques.get(uniques.size() - 1).split("\t");
				if(!(col[2].equals(colb[2]) && col[3].equals(colb[3]))){
					uniques.add(lines.get(j));
				}
				j++;
			}
		}
		
		System.out.println("Duplicates eliminated!");

		// write the dataset without duplicates into data_path_new
		try {
			FileWriter fileWriter = new FileWriter(data_path_new); 
			try (BufferedWriter writer = new BufferedWriter(fileWriter)) {
				for(String line : uniques){
					String[] col = line.split("\t");
						writer.write(col[2]);
						writer.write("\t");
						writer.write(col[3]);
						writer.write("\t");
						writer.write(col[4]);
						writer.newLine();
				}
			}
		} catch (IOException ex) {}
		System.out.println("Output written!");
		
	}

	public static double round(double num, int precision){
		double scale = Math.pow(10, precision);
		return (double) Math.round(num * scale) / scale;
	}

	public static int get_1d_index(double value, double min, double cell_len){
		int index = 1;
		while(min + index * cell_len < value){
			index += 1;
		}
		index -= 1;
		return index;
	}

	public static void create_index(String data_path_new, String index_path, int n){
		// To create a Gri index and save it to file on "index_path".
		// The output file should contain exactly n*n lines. 
		// If there is no Pnt in the cell, just leave it empty after ":".
		System.out.println("Calling create_index!");
		List<Double> xs = new ArrayList<>();
		List<Double> ys = new ArrayList<>();
		// read data from data_path_new
		List<String> lines = new ArrayList<>();
		try {
			FileReader fileReader = new FileReader(data_path_new); 
			try (BufferedReader reader = new BufferedReader(fileReader)) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					lines.add(line);
					String[] col = line.split("\t");
					xs.add(Double.parseDouble(col[0]));
					ys.add(Double.parseDouble(col[1]));
				}
			}
		} catch (IOException ex) {}
		System.out.println("File read!");

		// find max box
		double x_min = round(Collections.min(xs), 1);
		double x_max = round(Collections.max(xs), 1);
		double y_min = round(Collections.min(ys), 1);
		double y_max = round(Collections.max(ys), 1);
		
		// create Gri index
		double cell_x = (x_max - x_min) / n;
		double cell_y = (y_max - y_min) / n;

		List<Gri> GriIndex = new ArrayList<>();
		/* initialize empty Gri index */
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				Gri Gri = new Gri(i, j);
				GriIndex.add(Gri);
			}
		}

		/* add Pnts to Gri index */
		for(String line: lines){
			String[] col = line.split("\t");
			double x = Double.parseDouble(col[0]);
			double y = Double.parseDouble(col[1]);
			int id = Integer.parseInt(col[2]);
			Pnt p = new Pnt(id, x, y);
			int x_idx = get_1d_index(x, x_min, cell_x);
			int y_idx = get_1d_index(y, y_min, cell_y);
			GriIndex.get(x_idx * n + y_idx).appendPnt(p);
		}
		System.out.println("Gri Index created!");

		// write Gri index to index_path
		try {
			FileWriter fileWriter = new FileWriter(index_path); 
			try (BufferedWriter writer = new BufferedWriter(fileWriter)) {
				for(int i = 0; i < n; i++){
					for(int j = 0; j < n; j++){
						writer.write(i + "," + j + ":");
						Gri Gri = GriIndex.get(i * n + j);
						for (Pnt p : Gri.getPnts()){
							writer.write(" ");
							writer.write(p.toString());
						}
						writer.newLine();
					}	
				}
			}					
		} catch (IOException ex) {}
		System.out.println("Output written!");
		
	}

	public static void main(String[]args){
		if(args.length != 4){
			System.out.println("Usage: java makeIndex DATA_PATH INDEX_PATH DATA_PATH_NEW N");
			/*
			DATA_PATH(String): the file path of Gowalla_totalCheckins.txt
			INDEX_PATH(String): the output file path of the Gri index
			DATA_PATH_NEW(String): the file path of the dataset without duplicates
  			N(integer): the Gri index size
			*/
			return;
		}
		duplicate_elimination(args[0], args[2]);
		long s = System.currentTimeMillis();
		create_index(args[2], args[1], Integer.parseInt(args[3])); //create_index(args[2], args[1], args[3]);
		long t = System.currentTimeMillis();
		System.out.println("Index construction time: "+(t-s));
	}
}