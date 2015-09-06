import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


public class MongoWrapper {//implements IWrapper{

	private String collectionName;
	private ArrayList<String> parameters;
	private MongoClient mc;
	private DB db;
	private DBCollection coll;
	private ANN neuralNetwork;
	private boolean mnist = false;

	public static void main(String[] arg) throws UnknownHostException{
		MongoWrapper mw = new MongoWrapper();
		mw.trainNetwork();
	}
	
	public MongoWrapper() throws UnknownHostException{
		int [] h = {300};
		neuralNetwork = new ANN(28*28, h, 10);
		parameters = new ArrayList<String>();
		parameters.add("label");
		parameters.add("image");
		//Populate list here using input string

		mc = new MongoClient();
		db = mc.getDB("MasterDB");
		coll = db.getCollection("mnist");
		mnist = true;
	}

	public String trainNetwork(){
		
//		DBCursor curs = coll.find();
////		DBObject o = curs.next();
//		String value = curs.next().toString();
//		System.out.println(value);
//		
//		JsonParser parser = new JsonParser();
//		JsonObject o = (JsonObject)parser.parse(value);
//		System.out.println(o.get("label"));
//		System.out.println(o.get("image").isJsonArray());
//		
//		Gson g = new Gson();
//		int[][] image = g.fromJson(o.get("image"), int[][].class);
//		System.out.println(image.length);
		
		Map<String, ArrayList<Double>> trainingData = new HashMap<String, ArrayList<Double>>();
		List<ArrayList<Double>> mnistImages = new ArrayList<ArrayList<Double>>();
		if (mnist){
			JsonParser parser = new JsonParser();
			Gson gson = new Gson();
			trainingData.put("labels", new ArrayList<Double>());
			// Format data specifically for MNIST and set up proper network size
			DBCursor curs = coll.find();
			while (curs.hasNext()){//Load Data
				String data = curs.next().toString();
				JsonObject o = (JsonObject)parser.parse(data);
				double number = o.get("label").getAsInt();
				Double[][] image = gson.fromJson(o.get("image"), Double[][].class);
				
				ArrayList<Double> imgList = new ArrayList<Double>();
				for (Double[] a : image){
					imgList.addAll(Arrays.asList(a));
				}
				mnistImages.add(imgList);
				trainingData.get("labels").add(number);
			}
			//Train NN
			ArrayList<Double> labelList = trainingData.get("labels");
			double[][] labels = new double[labelList.size()][10];
			for (int ii = 0; ii < labels.length; ii++){
				labels[ii][(int)((double)labelList.get(ii))] = 1;
			}
			double[][] data = new double [mnistImages.size()][mnistImages.get(0).size()];
			int iCount = 0;
			for (ArrayList<Double> img : mnistImages){
				for (int ii = 0; ii < img.size(); ii++){
					data[iCount][ii] = img.get(ii);
				}
				iCount++;
			}
			
			
			
			double[] results = neuralNetwork.train(data, labels);
			JsonObject res = new JsonObject();
			res.addProperty("AverageError", results[0]);
			res.addProperty("AverageSaturation", results[1]);
			return res.getAsString();
		} else {
			//General case
			//Assume standardized form where every field is a double
			DBCursor curs = coll.find();
			while (curs.hasNext()){
				DBObject o = curs.next();
				for (String p : parameters){ //First param is labels
					if (!trainingData.containsKey(p)){
						trainingData.put(p, new ArrayList<Double>());
					}
					trainingData.get(p).add((Double) o.get(p));
				}
			}
			// Train network and get prediction
			ArrayList<Double> labels = trainingData.get(0);
			double[][] labelArray = new double [labels.size()][1];
			for (int ii = 0; ii < labels.size(); ii++){
				labelArray[ii][0] = labels.get(ii);
			}
			double[][] featureArray = new double [labels.size()][trainingData.keySet().size()-1];
			int fCount = 0;
			for (String f : trainingData.keySet()){
				ArrayList<Double> vals = trainingData.get(f);
				for (int ii = 0; ii < vals.size(); ii++){
					featureArray[ii][fCount] = vals.get(ii);
				}
			}
			//Train NN
			double[] results = neuralNetwork.train(featureArray, labelArray);
			System.out.println(results[0]);
			JsonObject res = new JsonObject();
			res.addProperty("AverageError", results[0]);
			res.addProperty("AverageSaturation", results[1]);
			return res.getAsString();
		}
	}

	public int predict(){
		return 0;
	}

}
