package ucsd.shoppingApp.models;

import java.util.ArrayList;


public class AnalyticsModel2 {
	ArrayList<String> oldStateName;
	ArrayList<Double> oldStateSum;
	ArrayList<String> oldProductName; 
	ArrayList<Double> oldProductSum;
	double[][] oldCells;
	ArrayList<Integer> oldStateId;
	ArrayList<Integer> oldProductId;
	
	public AnalyticsModel2(ArrayList<String> stateName, ArrayList<Double> stateSum, ArrayList<String> productName, 
			ArrayList<Double> productSum, double[][] cells, ArrayList<Integer> oldStateId, ArrayList<Integer> oldProductId){
		this.oldStateName = stateName;
		this.oldStateSum = stateSum;
		this.oldProductName = productName;
		this.oldProductSum = productSum;
		this.oldCells = cells;
		this.oldStateId=oldStateId;
		this.oldProductId=oldProductId;
    }
	

	
	public ArrayList<String> getOldStateName() {
        return this.oldStateName;
    }
	
	public ArrayList<Double> getOldStateSum() {
        return this.oldStateSum;
    }
	
	public ArrayList<String> getOldProductName() {
        return this.oldProductName;
    }
	
	public ArrayList<Double> getOldProductSum(){
		return this.oldProductSum;
	}
	
	public ArrayList<Integer> getOldProductId(){
		return this.oldProductId;
	}
	
	public ArrayList<Integer> getOldStateId(){
		return this.oldStateId;
	}

	public double[][] getOldCells(){
		return this.oldCells;
	}
	
	
}
