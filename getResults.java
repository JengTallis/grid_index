public class getResults{
	public static String knn_grid(double x, double y, String index_path, int k, int n){
		// to get the k-NN result with the help of the grid index
		// Please store the k-NN results by a String of location ids, like "11, 789, 125, 2, 771"
		
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