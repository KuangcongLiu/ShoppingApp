package ucsd.shoppingApp.models;

import java.util.ArrayList;
 
public class SimilarProductsModel {
	private ArrayList<Integer> product1;
	private ArrayList<Integer> product2;
	private ArrayList<Double> cosineSimilarity;
	
	public SimilarProductsModel (ArrayList<Integer> product1, ArrayList<Integer> product2, ArrayList<Double> cosineSimilarity){
		this.product1 = product1;
		this.product2 = product2;
		this.cosineSimilarity = cosineSimilarity;
	}
	
	public ArrayList<Integer> getProduct1(){
		return this.product1;
	}
	
	public ArrayList<Integer> getProduct2(){
		return this.product2;
	}
	public ArrayList<Double> getCosineSimilarity(){
		return this.cosineSimilarity;
	}
}

