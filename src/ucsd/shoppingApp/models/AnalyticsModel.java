package ucsd.shoppingApp.models;

import java.util.ArrayList;
import java.util.HashMap;


public class AnalyticsModel {
	ArrayList<Integer> scid;
	ArrayList<String> scname;
	ArrayList<Double> scsum;
	ArrayList<Integer> pid;
	ArrayList<String> pname;
	ArrayList<Double> psum;
	double[][] cells;
	int totalSC;
	int totalProduct;
	
	public AnalyticsModel(ArrayList<Integer> scid, ArrayList<String> scname, ArrayList<Double> scsum, ArrayList<Integer> pid, 
			ArrayList<String> pname, ArrayList<Double> psum, double[][] cells, int totalSC, int totalProduct){
		this.scid = scid;
		this.scname = scname;
		this.scsum = scsum;
		this.pid = pid;
		this.pname = pname;
		this.psum = psum;
		this.cells = cells;
		this.totalSC = totalSC;
		this.totalProduct = totalProduct;
    }
	
	public ArrayList<Integer> getScid() {
        return this.scid;
    }
	
	public ArrayList<String> getScname() {
        return this.scname;
    }
	
	public ArrayList<Double> getScsum() {
        return this.scsum;
    }
	
	public ArrayList<Integer> getPid() {
        return this.pid;
    }
	
	public ArrayList<String> getPname(){
		return this.pname;
	}
	
	public ArrayList<Double> getPsum() {
        return this.psum;
    }
	public double[][] getCells(){
		return this.cells;
	}
	
	public int getTotalSC(){
		return this.totalSC;
	}
	
	public int getTotalProduct(){
		return this.totalProduct;
	}
}
