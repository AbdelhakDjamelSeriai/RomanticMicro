package identificationProcess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.DataTriplets;
import models.ManipulatedData;
import models.MyClass;
import models.PairsDFreq;


public class FDataComputer {
	
	public static double lastComputedInter;
	public static double lastComputedIntra;
	public static double lastComputedFData;
	public static double maxFIntra;
	public static double minFIntra;
	public static double maxFInter;
	public static double minFInter;
	
	
	private static List<String> getManipulatedDataInCluster(
			List<MyClass> clusterClasses) {
		List<String> dataListMicro= new ArrayList<String>();
		for(MyClass cl: clusterClasses){
			for (ManipulatedData d: cl.getReadData()){
				if(!dataListMicro.contains(d.getName()))
					dataListMicro.add(d.getName());
			}
			for (ManipulatedData d: cl.getWrittenData()){
				if(!dataListMicro.contains(d.getName()))
					dataListMicro.add(d.getName());
			}
		}
		return dataListMicro;
	}


	public static int lambda;
	public  static int beta;
	
	
	public double computeFData(HashMap<MyClass, ArrayList<DataTriplets>> DFrequnencyMatrix, 
			List<MyClass> microservice, int nbrAcessDataApp){

		double FIntra= computeFIntra(DFrequnencyMatrix, microservice,nbrAcessDataApp);
		
		if(MeasuringMetrics.max<FIntra)
			MeasuringMetrics.max=FIntra;
		if(MeasuringMetrics.min>FIntra)
			MeasuringMetrics.min=FIntra;
	//	FIntra=normalize(FIntra,MeasuringMetrics.max,MeasuringMetrics.min);
		double FInter= computeFInter(DFrequnencyMatrix, microservice,nbrAcessDataApp);
		
		if(MeasuringMetrics.max<FInter)
			MeasuringMetrics.max=FInter;
		if(MeasuringMetrics.min>FInter)
			MeasuringMetrics.min=FInter;
	//	FInter=normalize(FInter,MeasuringMetrics.max,MeasuringMetrics.min);
		lastComputedInter=FInter;
		lastComputedIntra=FIntra;
		FileWriter fw;
		try {
			fw = new FileWriter("/home/anfel/Documents/FinalResults/Automatic/FDataNew.txt", true);
		
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println();
			for(MyClass cl: microservice){
				out.println(cl);
			}
			out.println("********** FInter and FIntra" +FInter+ " "+FIntra);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lambda=1;
		beta=1;
		double FData= (lambda*FIntra-beta*FInter)/(lambda+beta);
		lastComputedFData=FData;
		return FData;
	}
	
	//Normalise values normVam= (val-min)/(max-min)
			private static double normalize(double val, double max, double min) {
				double normalizedVal=val;
				if (max!=min){
					normalizedVal= (val-min)/(max-min);
				}else{
					if(max!=0){
						normalizedVal= val/max;
					}
				}
				return normalizedVal;
			}
			

	public double computeFIntra(
			HashMap<MyClass, ArrayList<DataTriplets>> dFrequnencyMatrix,
			List<MyClass> microservice, int nbrAcessDataApp) {
		// TODO Auto-generated method stub
		List<String> dataList= getManipulatedDataInCluster(microservice);
		double DataDependenc=0;
		if (dataList.size()==0){
			return 0;
		}
		if(microservice.size()>1){
			for(int i = 0; i <microservice.size(); i++)
			{   
				MyClass cl= microservice.get(i);
			
					for(int j = i+1; j < microservice.size(); j++)
					{
						MyClass cl2=microservice.get(j);
						
						DataDependenc+= getDataDependenc(cl,cl2,dFrequnencyMatrix, 
	                    		dataList,nbrAcessDataApp);
						
					}		
			}
			double nbr= dataList.size()*nbrAcessDataApp;
			nbr=dataAccessesInMicro(microservice);
			DataDependenc=DataDependenc/dataList.size();
			double nbPossilePairs= (microservice.size()*(microservice.size()-1))/2;
			
			return DataDependenc/nbPossilePairs;
					
		}
		else
			return 1;
		
	}

	private double dataAccessesInMicro(List<MyClass> microservice) {
		double nbAcces=0;
		for(MyClass cl: microservice){
	//		System.out.println(cl);
			for(ManipulatedData d: cl.getReadData())
				nbAcces=nbAcces+d.getFrequency();
			for(ManipulatedData d: cl.getWrittenData())
				nbAcces=nbAcces+d.getFrequency();
		}
	 //   System.out.println(nbAcces);
		return nbAcces;
	}


	public double computeFInter(
			HashMap<MyClass, ArrayList<DataTriplets>> dFrequnencyMatrix,
			List<MyClass> microservice, int nbrAcessDataApp) {
		// TODO Auto-generated method stub
		List<String> dataList= getManipulatedDataInCluster(microservice);
		List<MyClass> externalClasses= getExternalClasses(microservice, new ArrayList<MyClass>(dFrequnencyMatrix.keySet()));
		double DataDependenc=0;
		if (dataList.size()!=0){
			for(int i = 0; i <microservice.size(); i++)
			{   
				MyClass cl= microservice.get(i);
			
					for(int j = 0; j < externalClasses.size(); j++)
					{
						MyClass cl2=externalClasses.get(j);
						DataDependenc+= getDataDependenc(cl,cl2,dFrequnencyMatrix, 
	                    		dataList,nbrAcessDataApp);
					}		
			}
		
			DataDependenc=DataDependenc/dataList.size();
		//	System.out.println(DataDependenc+" "+nbr);
			double nbPossileExternalPairs= microservice.size()*externalClasses.size();
			
			if (nbPossileExternalPairs!=0){
		//		System.out.println(DataDependenc/nbPossileExternalPairs+" "+nbPossileExternalPairs);
				return DataDependenc/nbPossileExternalPairs;	
			}
			else
				return 0;
		}
		else{
			return 0;
		}
		
	}
	
	private static List<MyClass> getExternalClasses(List<MyClass> clusterClasses, List<MyClass> classes) {
		List<MyClass> externalClasses= new ArrayList<MyClass>();
		for(MyClass cl: classes){
			if (!clusterClasses.contains(cl))
				externalClasses.add(cl);
		}
		return externalClasses;
	}

	private double getDataDependenc(MyClass cl, MyClass cl2,
			HashMap<MyClass, ArrayList<DataTriplets>> dFrequnencyMatrix, List<String> dataList, int nbrAcessDataApp) {
		double sumdataDependenc=0;
		
		for(String d: dataList){
			PairsDFreq pair=  getPair(cl,cl2,d,dFrequnencyMatrix);
			
			sumdataDependenc+=pair.getFreq()*pair.getD();
		}
		return sumdataDependenc;
	}

	private PairsDFreq getPair(MyClass cl, MyClass cl2, String d,
			HashMap<MyClass, ArrayList<DataTriplets>> dFrequnencyMatrix) {
		
		List<DataTriplets> list= dFrequnencyMatrix.get(cl);
		boolean found=false;
		int i=0;
		PairsDFreq pair = null;
		while(!found && i<list.size()){
			if(list.get(i).getName().equals(d)){
				List<PairsDFreq> listPairs= list.get(i).getListPairs();
				pair= findPair(listPairs,cl2);
				found=true;
				
				
			}
			i++;
		}
		return pair;
	}

	private PairsDFreq findPair(List<PairsDFreq> listPairs,
			MyClass cl2) {
		boolean found=false;
		int i=0;
		while(!found && i<listPairs.size()){
			
			if (listPairs.get(i).getPairClass().getName().equals(cl2.getName())){
				found=true;
				i--;
			}
			i++;
		}
		return listPairs.get(i);
	}

	public static void setMaxesMins(double mxFIntra, double mnFIntra,
			double mxFInter, double mnFInter) {
		maxFIntra=mxFIntra;
		minFIntra=mnFIntra;
		maxFInter=mxFInter;
		minFInter=mnFInter;
		
	}


	public double computeNormalisedIntra(
			HashMap<MyClass, ArrayList<DataTriplets>> dFrequnencyMatrix,
			ArrayList<MyClass> microservice, int nbrAcessDataApp) {
		double FIntra= computeFIntra(dFrequnencyMatrix, microservice,nbrAcessDataApp);
		FIntra=normalize(FIntra,MeasuringMetrics.max,MeasuringMetrics.min);
		return FIntra;
	}


	public double computeNormalisedFInter(
			HashMap<MyClass, ArrayList<DataTriplets>> dFrequnencyMatrix,
			ArrayList<MyClass> microservice, int nbrAcessDataApp) {
		// TODO Auto-generated method stub
		double FInter= computeFInter(dFrequnencyMatrix, microservice,nbrAcessDataApp);
		FInter=normalize(FInter,MeasuringMetrics.max,MeasuringMetrics.min);
		return FInter;
	}

}
