package identificationProcess;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.DataTriplets;
import models.ManipulatedData;
import models.MyClass;
import models.PairsDFreq;

public class DFrequencyMatrixBuilder {
	
	public static HashMap<MyClass, ArrayList<DataTriplets>> DFrequnencyMatrix= new HashMap<MyClass,  ArrayList<DataTriplets>>();
	
	public HashMap<MyClass, ArrayList<DataTriplets>> buildMatrix(List<MyClass> classes, List<String> dataList){
		
		for (MyClass cl: classes){
			DFrequnencyMatrix.put(cl, new ArrayList<DataTriplets>());
			for (String d: dataList){
				DataTriplets triplets=createTripletsForData(d,classes,cl);
				DFrequnencyMatrix.get(cl).add(triplets);
				
			}
		}
		
		 printDFrequencyMatrix();
		return DFrequnencyMatrix;
	}

	private void printDFrequencyMatrix() {
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("/home/anfel/Documents/Results/DFrequecyMatrix.txt"));
			
			for(MyClass cl: DFrequnencyMatrix.keySet()){
				for(DataTriplets triplets: DFrequnencyMatrix.get(cl)){
					
					for(PairsDFreq pair: triplets.getListPairs()){
						if(pair.getD()!=0){
							out.println("Class: "+cl.getName());
							out.println("	Triplets: ");
							out.println("         * Data:"+triplets.getName());
							out.println("              - Class:"+pair.getPairClass());
							out.println("              - D:"+pair.getD());
							out.println("              - Frequency:"+pair.getFreq());
							out.println("******************************************");
						}
						
					}
					
					
				}
				out.println("********************************************");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private DataTriplets createTripletsForData(String d, List<MyClass> classes, MyClass cl) {
		DataTriplets triplets= new DataTriplets();
		triplets.setName(d);
		List<PairsDFreq> listPairs= createListPairs(d,classes,cl);
		triplets.setListPairs(listPairs);
		return triplets;
	}

	private List<PairsDFreq> createListPairs(String d, List<MyClass> classes, MyClass cl) {
		List<PairsDFreq> list= new ArrayList<PairsDFreq>();
		for (MyClass c: classes){
			if(!c.getName().equals(cl.getName())){
				PairsDFreq pair= createPair(c,cl,d);
				list.add(pair);
			}		
		}
		return list;
	}

	private PairsDFreq createPair(MyClass c, MyClass cl, String d) {
		PairsDFreq pair= new PairsDFreq();
		double freq= getIntrsectionLists(cl.getWrittenData(),c.getWrittenData(),d);
		double access;
		if(freq!=-1){
			access=1;
		}else{
			freq= getIntrsectionLists(cl.getWrittenData(),c.getReadData(),d);
			if(freq!=-1){
				access=0.5;
			}else{
				freq= getIntrsectionLists(cl.getReadData(),c.getWrittenData(),d);
				if(freq!=-1){
					access=0.5;
				}
				else{
					freq=getIntrsectionLists(cl.getReadData(),c.getReadData(),d);
					if (freq!=-1){
						access=0.25;
					}
					else{
						access=0;
						freq=0;
					}
				}
				
			}
		}
		pair.setD(access);
		pair.setFreq(freq);
		pair.setPairClass(c);
		return pair;
	}

	private double getIntrsectionLists(List<ManipulatedData> list1,
			List<ManipulatedData>list2, String d) {
		double freq=-1;
		int freqDInC= getFreqForData(d, list1);
		int freqDInCl= getFreqForData(d, list2);
		if(freqDInC!=0 && freqDInCl!=0){
			freq= freqDInC+freqDInCl-computeStandarsDev(freqDInC,freqDInCl);
		}
		
		return freq;
	}

	private double computeStandarsDev(double freqDInC, double freqDInCl) {

			double average= (freqDInC+freqDInCl)/2;
			double somme=Math.pow(freqDInC-average,2);
			somme+=Math.pow(freqDInCl-average,2);
			double variation= somme/2;
			return Math.sqrt(variation);
	}

	private int getFreqForData(String d, List<ManipulatedData> list1) {
		
		boolean found= false;
		int i=0;
		int freq=0;
		while(!found && i<list1.size()){
			if(list1.get(i).getName().equals(d)){
				found=true;
				freq=list1.get(i).getFrequency();
			}
			i++;
		}
		
		return freq;
	}
	
}
