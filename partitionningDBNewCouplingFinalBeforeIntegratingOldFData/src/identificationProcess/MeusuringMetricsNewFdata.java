package identificationProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.DataTriplets;
import models.MyClass;

public class MeusuringMetricsNewFdata {

	public static HashMap<MyClass, ArrayList<String>> readdataMatrix= new HashMap<MyClass,  ArrayList<String>>();
	public static HashMap<MyClass, ArrayList<String>> writtendataMatrix= new HashMap<MyClass,  ArrayList<String>>();
	public static List<MyClass> classes;
	public static double totalDataManipulatedInCluster;
	public static  double totalDataManipulatedExternaly;
	public static HashMap<MyClass, ArrayList<DataTriplets>> DFrequnencyMatrix;
	public static List<String> dataList;
	public static int nbrAcessDataApp;
	public static int lambda;
	public static int beta;

	
	public static double FFMicro(List<MyClass> clusterClasses){
		
		double fsem= MeasuringMetrics.measureFFSem(clusterClasses);
		FDataComputer  fdatacomputer= new FDataComputer();
		double fdata= fdatacomputer.computeFData(DFrequnencyMatrix, clusterClasses, nbrAcessDataApp);
		lambda=1;
		beta=2;
	//	System.out.println("fdata "+fdata);
	//	System.out.println("Fsem "+fsem);
		double f= (lambda*fsem+beta*fdata)/(lambda+beta);
	//	f=fsem;
		return f;
	}	
}
