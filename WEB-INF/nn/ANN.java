import java.util.Random;

public class ANN {

	private Random random = new Random();
	
	private int L;
	
	private double[][][] neuralNetWeights;

	private double[][] neuralNetBias;

	private double[][] neuralNetActivation;
	private double[][] neuralNetZ;
	
	private double[][] error;

	private double weightsLearningRate = .75;
	private double biasLearningRate = .75;

	int trialCount = 0;
	
	/**
	 * Create an artificial neural network
	 * 
	 * @param inputNodesNum, hiddenNodesNum, outputNodesNum
	 */
	public ANN(int inputNodesNum, int[] hiddenNodesNum, int outputNodesNum) {
		L = hiddenNodesNum.length+2;
		
		neuralNetWeights = new double[L][][];
		neuralNetBias = new double[L][];
		neuralNetActivation = new double[L][];
		neuralNetZ = new double[L][];
		error = new double[L][];

		neuralNetWeights[0] = new double[inputNodesNum][1];
		neuralNetBias[0] = new double[inputNodesNum];
		neuralNetActivation[0] = new double[inputNodesNum];
		neuralNetZ[0] = new double[inputNodesNum];
		error[0] = new double[inputNodesNum];

		neuralNetWeights[1] = new double[hiddenNodesNum[0]][inputNodesNum];
		neuralNetBias[1] = new double[hiddenNodesNum[0]];
		neuralNetActivation[1] = new double[hiddenNodesNum[0]];
		neuralNetZ[1] = new double[hiddenNodesNum[0]];
		error[1] = new double[hiddenNodesNum[0]];

		for(int i = 1; i < L - 2; i++){
			neuralNetWeights[i+1] = new double[hiddenNodesNum[i]][hiddenNodesNum[i - 1]];
			neuralNetBias[i+1] = new double[hiddenNodesNum[i]];
			neuralNetActivation[i+1] = new double[hiddenNodesNum[i]];
			neuralNetZ[i+1] = new double[hiddenNodesNum[i]];
			error[i+1] = new double[hiddenNodesNum[i]];

		}
		
		neuralNetWeights[L - 1] = new double[outputNodesNum][hiddenNodesNum[hiddenNodesNum.length-1]];
		neuralNetBias[L - 1] = new double[outputNodesNum];
		neuralNetActivation[L - 1] = new double[outputNodesNum];
		neuralNetZ[L - 1] = new double[outputNodesNum];
		error[L - 1] = new double[outputNodesNum];
		init();
	}
	
	/**
	 * Initializes weights and biases of the neurons
	 */
	public void init(){
		for(int i = 0; i < neuralNetWeights.length; i++){
			for(int j = 0; j < neuralNetWeights[i].length; j++){
				if(i == 0|| i == neuralNetWeights.length-1) neuralNetBias[0][j] = 0;
				else neuralNetBias[i][j] = random.nextGaussian();
				for(int k = 0; k < neuralNetWeights[i][j].length; k++){
					if(i == 0) neuralNetWeights[i][j][k] = 1;
					else neuralNetWeights[i][j][k] = random.nextGaussian() * 1.0/Math.sqrt(neuralNetWeights[i].length);
				}
			}
		}
	}
	
	/**
	 * Pass an array of matrixes  with labels to train a NN
	 * Returns the average final error, average saturation and final activation signals
	 * 
	 * @param values
	 * @param labels
	 * @return
	 */
	public double[] train(double[][] values, double[][] labels){
		double[] settings = new double[2 + labels[0].length];
		for(int i = 0; i < values.length; i ++){
			test(values[i], labels[i]);
		}
		
		double averageLastError = 0;
		double averageSaturation = 0;
		for(int i = 0; i < labels[0].length; i++){
			averageLastError += Math.abs(labels[labels.length - 1][i] - neuralNetActivation[L-1][i]);
			averageSaturation += Math.pow(Math.abs(labels[labels.length - 1][i] - .5),2) * 2;
			settings[i+2] = neuralNetActivation[L-1][i];
		}
		settings[0] = averageLastError/labels[0].length;
		settings[1] = averageSaturation;

		return settings;
	}
	
	/**
	 * Run a single trial through the ANN
	 * 
	 * @param in
	 * @param out
	 */
	public double[] test(double[] values, double[] labels){
		double[] settings = new double[2 + labels.length];
		feedForward(values);
		backPropogate(labels);
		trialCount++;
		
		double averageLastError = 0;
		double averageSaturation = 0;
		for(int i = 0; i < labels.length; i++){
			
			averageLastError += Math.abs(labels[i] - neuralNetActivation[L-1][i]);
			averageSaturation += Math.pow(Math.abs(labels[i] - .5),2) * 2;
			settings[i+2] = neuralNetActivation[L-1][i];
		}
		
		settings[0] = averageLastError/labels.length;
		settings[1] = averageSaturation;
		


		return settings;
	}
	
	/**
	 * Helper method that does the feed forward
	 * 
	 * @param inputField
	 */
	private void feedForward(double[] inputField){
		for(int i = 0; i < inputField.length; i++){
			neuralNetActivation[0][i] = inputField[i];
		}
		for(int i = 1; (i < L); i++){
			calculateZ(i);
			calculateActivation(i);
		}
	}
	
	/**
	 * Helper function to do the back propagation
	 * 
	 * @param y
	 */
	private void backPropogate(double[] y){
		calculateErrorL(y);
		calculateErrorl();
		updateNet();
	}
	
	/**
	 * Intermediary used in a few calculations
	 * 
	 * @param layer
	 */
	private void calculateZ(int layer){
		for(int j = 0; j < neuralNetWeights[layer].length; j++){
			neuralNetZ[layer][j] = 0;
			for(int k = 0; k < neuralNetWeights[layer][j].length; k++){
				neuralNetZ[layer][j] += neuralNetWeights[layer][j][k] * neuralNetActivation[layer - 1][k];
			}
			neuralNetZ[layer][j] += neuralNetBias[layer][j];
		}
	}
	
	/**
	 * Activation calculation
	 * 
	 * @param layer
	 */
	private void calculateActivation(int layer){
		neuralNetActivation[layer] = sigmoid(neuralNetZ[layer]);
	}
	
	/**
	 * Activation Function
	 * 
	 * @param x
	 * @return f(x)
	 */
	private double[] sigmoid(double[] x){
		double[] out = new double[x.length];
		for(int i = 0; i < x.length; i++)
			out[i] = 1.0/(1.0 + Math.pow(Math.E,-x[i]));
		return out;
	}
	
	/**
	 * Derivative of Activation function
	 * 
	 * @param x
	 * @return f'(x)
	 */
	private double[] sigmoidPrime(double[] x){
		double[] out = new double[x.length];
		for(int i = 0; i < x.length; i++)
			out[i] = Math.pow(Math.E, x[i])/Math.pow((Math.pow(Math.E, x[i])+1),2);
		return out;
	}
	
	/**
	 * Error calculation for output nodes
	 * 
	 * @param y
	 */
	private void calculateErrorL(double[] y){
		error[L - 1] = hadamarProduct(costGradient(L - 1, y),sigmoidPrime(neuralNetZ[L - 1]));
	}
	
	/**
	 * Error calculation for non-output nodes
	 */
	private void calculateErrorl(){
		for(int i = L - 2; i >= 0; i--){
			error[i] = hadamarProduct(arrayProduct(transpose(neuralNetWeights[i + 1]),error[i+1]),sigmoidPrime(neuralNetZ[i]));
		}
	}
	
	/**
	 * Updates the weights and and bias via their gradients
	 */
	private void updateNet(){
		for(int i = 1; i < neuralNetWeights.length; i++){
			for(int j = 0; j < neuralNetWeights[i].length; j++){
				neuralNetBias[i][j] = neuralNetBias[i][j] - biasLearningRate * biasGradient(i,j);
				for(int k = 0; k < neuralNetWeights[i][j].length; k++){
					neuralNetWeights[i][j][k] = neuralNetWeights[i][j][k] - weightsLearningRate * weightGradient(i,j,k);
				}
			}
		}
	}
	
	/**
	 * helper function for bias gradient
	 * 
	 * @param l
	 * @param j
	 * @return gradient
	 */
	private double biasGradient(int l, int j){
		if( l == neuralNetWeights.length-1) return 0;
		return error[l][j];
	}
	
	/**
	 * Helper function for weight gradient
	 * 
	 * @param l
	 * @param j
	 * @param k
	 * @return gradient
	 */
	private double weightGradient(int l, int j, int k){
		if(l == 1|| l == 0) return 1;
		return neuralNetActivation[l - 1][k] * error[l][j];
	}
	
	/**
	 * Calculates Cost Gradient
	 * 
	 * @param layer
	 * @param y
	 * @return cost gradient
	 */
	private double[] costGradient(int layer, double[] y){
        double out[] = new double[y.length]; 
		for(int i = 0; i < y.length; i++){
			out[i] = neuralNetActivation[layer][i] - y[i];
		}
		return out;
	}
	
	/**
	 * Hadamar Product
	 * 
	 * @param first
	 * @param second
	 * @return double[] of Hadamar Product
	 */
	private double[] hadamarProduct(double[] first, double[] second){
		double[] out = new double[first.length];
		for(int i = 0; i < out.length; i++){
			out[i] = first[i] * second[i];
		}
		return out;
	}
	
	/**
	 * Matrix transpose
	 * 
	 * @param a
	 * @return the transpose
	 */
	private double[][] transpose(double[][] a){
		double[][] out = new double[a[0].length][a.length];

		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				double temp = a[i][j];
	            out[j][i] = temp;
	        }
	    }
		
		return out;
	}

	/**
	 * Matrix multiplication method
	 * 
	 * @param first
	 * @param second
	 * @return A * B
	 */
	private double[] arrayProduct(double[][] first, double[] second){
        double out[] = new double[first.length]; 
        for(int i = 0; i < first.length; i++){
        	double sum = 0;
        	for(int k = 0; k < second.length; k++){
        		
        		sum += first[i][k] * second[k];
        	}
        	out[i] = sum;
        }

		return out;
	}
	
	/**
	 * Prints weights
	 * 
	 * @return weights
	 */
	public String toStringWeights(){
		String out = "";
		for(int i = 0; i < neuralNetWeights.length; i++){
			for(int j = 0; j < neuralNetWeights[i].length; j++){
				for(int k = 0; k < neuralNetWeights[i][j].length; k++){
					out += neuralNetWeights[i][j][k] + " ";
				}
				out += "\n";
			}
			out += "\n";
		}
		
		return out;
	}
	
	/**
	 * Prints bias
	 * 
	 * @return bias
	 */
	public String toStringBias(){
		String out = "";
		for(int i = 0; i < neuralNetBias.length; i++){
			for(int j = 0; j < neuralNetBias[i].length; j++){
				out += neuralNetBias[i][j] + " ";
				out += "\n";
			}
			out += "\n";
		}
		
		return out;
	}
	
	/**
	 * Prints Activation Values
	 * 
	 * @return Activation Values
	 */
	public String toStringActivationFunction(){
		String out = "";
		for(int i = 0; i < neuralNetActivation.length; i++){
			for(int j = 0; j < neuralNetActivation[i].length; j++){
				out += neuralNetActivation[i][j] + " ";
				out += "\n";
			}
			out += "\n";
		}
		
		return out;
	}
	
	/**
	 * Prints Z
	 * 
	 * @return Z
	 */
	public String toStringZ(){
		String out = "";
		for(int i = 0; i < neuralNetZ.length; i++){
			for(int j = 0; j < neuralNetZ[i].length; j++){
				out += neuralNetZ[i][j] + " ";
				out += "\n";
			}
			out += "\n";
		}
		
		return out;
	}

	
	/**
	 * @return the weightsLearningRate
	 */

	public double getWeightsLearningRate() {
		return weightsLearningRate;
	}


	/**
	 * @param weightsLearningRate the weightsLearningRate to set
	 */
	public void setWeightsLearningRate(double weightsLearningRate) {
		this.weightsLearningRate = weightsLearningRate;
	}

	
	/**
	 * @return the biasLearningRate
	 */
	public double getBiasLearningRate() {
		return biasLearningRate;
	}


	/**
	 * @param biasLearningRate the biasLearningRate to set
	 */
	public void setBiasLearningRate(double biasLearningRate) {
		this.biasLearningRate = biasLearningRate;
	}
	
	
	
}
