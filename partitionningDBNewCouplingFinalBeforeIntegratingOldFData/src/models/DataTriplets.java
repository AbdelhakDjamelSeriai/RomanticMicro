package models;

import java.util.ArrayList;
import java.util.List;

public class DataTriplets {
	
	private String name;
	private List<PairsDFreq> listPairs= new ArrayList<PairsDFreq>();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<PairsDFreq> getListPairs() {
		return listPairs;
	}
	
	public void setListPairs(List<PairsDFreq> listPairs) {
		this.listPairs = listPairs;
	}
	
	public String toString(){
		return "<"+name+","+listPairs+">";
	}
	
	

}
