package identificationProcess;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import models.Attribute;
import models.MyClass;


public class MeasuringMetrics {
	
	HashMap<MethodDeclaration, Boolean> visited= new HashMap<MethodDeclaration, Boolean>();
	private static Set<Set<String>> directedConnectedMethods = new HashSet<Set<String>>();
	static HashMap<MethodDeclaration, HashSet<Attribute>> allAccededAttributes= new HashMap<MethodDeclaration,  HashSet<Attribute>>();
	public static HashMap<MyClass, ArrayList<String>> dataMatrix= new HashMap<MyClass,  ArrayList<String>>();
	public static List<MyClass> classes;
	static double  maxInternalCoupling=0;
	static double  minInternalCoupling;
	static double  maxCohision;
	static double  minCohision;
	static double  maxExternalCoupling;
	static double  minExternalCoupling;
	static double  maxFFInter;
	static double  maxFFIntra;
	public static double min;
	public static double max;
	
	public static double numberDataManipulatedInCluster;
	public static double numberdataManExter;
	public static double totalNumerCalls;
	public static double lastComputedCohesion;
	public static double lastComputedInternalCoupling;
	public static double lastComputedExternalCoupling;
	public static double lastComputedFSem;
	public static HashMap<MyClass, HashMap<MyClass, Double>> couplingMatrix = new HashMap<MyClass, HashMap<MyClass, Double>>();
	public static int lamda;
	public static int beta;
	
	
	// Initilise maxes and mines of cohesion, external coupling and internal 
	             // coupling used to normalize the compulted values 
	public static void setMaxesMins(double maxCoup, double minCoup, 
		double maxCoh, double minCoh, double maxExtcoup, double minExtcoup){
		maxInternalCoupling=maxCoup;
		minInternalCoupling= minCoup;
		maxCohision=maxCoh;
		minCohision=minCoh;
		maxExternalCoupling=maxExtcoup;
		minExternalCoupling=minExtcoup;
	}
	

	// Measure internal coupling of the classes of a cluster
	
	public static double masureCouplingPair(List<MyClass> clusterClasses){
		double numberOfInsideCalledClasses = 0;
		List<MyClass> calledClasses = new ArrayList<MyClass>();
		if (clusterClasses.get(0).equals(clusterClasses.get(1))){
			for (MethodDeclaration m : clusterClasses.get(0).getMethods().keySet()) {
				for (MethodDeclaration calledM : clusterClasses.get(0).getMethods().get(m)) {
					String className= calledM.resolveBinding().getDeclaringClass().getQualifiedName();
					addClass(calledClasses, className);				
				}
			}
			
		}
		else{
			
			for (MyClass c : clusterClasses) {
				for (MethodDeclaration m : c.getMethods().keySet()) {
					for (MethodDeclaration calledM : c.getMethods().get(m)) {
						String className= calledM.resolveBinding().getDeclaringClass().getQualifiedName();
						if(!className.equals(c.getName()))
							addClass(calledClasses, className);				
					}
				}
			}
		}
		
		for (MyClass calledClass : calledClasses) {
			if (clusterClasses.contains(calledClass)) {
				numberOfInsideCalledClasses++;
			}
		}
		if (calledClasses.size()==0) {
			return 1;
		}
		return numberOfInsideCalledClasses/totalNumerCalls;
	}
	
	// Measure internal coupling
	public static double measureInternalCoupling1(List<MyClass> clusterClasses) {
		
		List<Double> couplingValues= new ArrayList<Double>();
		double coupling=0;
		double nbPossiblePairs;
		if(clusterClasses.size()>1){
			for(int i = 0; i <clusterClasses.size(); i++)
			{   
				MyClass cl= clusterClasses.get(i);
			
					for(int j = i+1; j < clusterClasses.size(); j++)
					{
						MyClass cl2=clusterClasses.get(j);
	                    double pairCoupling= couplingMatrix.get(cl).get(cl2);
	                    couplingValues.add(pairCoupling);
						coupling= coupling+pairCoupling;
						
					}		
			}
			nbPossiblePairs= clusterClasses.size()*(clusterClasses.size()-1)/2;
		}
		else{
			MyClass cl= clusterClasses.get(0);
			coupling=couplingMatrix.get(cl).get(cl);
			nbPossiblePairs= 1;
		}
		double deviation=0;
		if(couplingValues.size()>0)
			deviation=getSommeDeviaition(couplingValues);	
		coupling= (coupling-deviation)/nbPossiblePairs;
		if (coupling>1){
			System.out.println("======== Error coupling: "+coupling);
			if(max<1)
				max=1;
			return 1;
		}
		if(max<coupling)
			max=coupling;
		if(min>coupling)
			min=coupling;
		return coupling;	
	}

	// Compute the sum of standard diviations
	private static double getSommeDeviaition(List<Double> couplingValues) {
		
		if(couplingValues.size()>1){
			double sommeDev=0;
			for (int i=0; i<couplingValues.size(); i++){
				for (int j=i+1; j<couplingValues.size(); j++){
					List<Double> coupList= new ArrayList<Double>();
					coupList.add(couplingValues.get(i));
					coupList.add(couplingValues.get(j));
					
					double devPair= getDeviaition(coupList);
					sommeDev+= devPair;
				}
			}
			return sommeDev;
		}
		else
		return 0;
	}

	// Compute standard diviation of a set of values
	private static double getDeviaition(List<Double> couplingValues) {
		
		double somme= 0;
		for (int i=0; i<couplingValues.size(); i++){
			somme+= couplingValues.get(i);
		}
		double average= somme/couplingValues.size();
		
		somme=0;
		for (int i=0; i<couplingValues.size(); i++){
			double val= couplingValues.get(i);
			somme+=Math.pow(val-average,2);	 
		}
		
		double variation= somme/couplingValues.size();
		return Math.sqrt(variation);
	}

	// Add a class to the list of classes if it does not belong to it
	private static void addClass(List<MyClass> calledClasses, String className) {
		MyClass cl= findCorrespondingClass(classes, className);
		calledClasses.add(cl);
	}
	
	// Find a class in the OO application classes uting its name
	public static MyClass findCorrespondingClass(List<MyClass> classes, String className) {
		int i=0;
		boolean found= false;
		MyClass cl= null;
		while(i<classes.size() && !found){
			
			if(classes.get(i).getName().equals(className)){
				found= true;
				cl= classes.get(i);
			}
			
			i++;
		}
		return cl;
	}
	
	//measure cohision
	public static  double measureCohision(List<MyClass> cluster) {
	
		directedConnectedMethods = new HashSet<Set<String>>();
		List<MethodDeclaration> methods= prepareToMeasureCohision(cluster);
		double ndc = 0;
		
		for (int i = 0;i<methods.size();i++){
			for (int j = i+1;j<methods.size();j++){
				String methodIClass= methods.get(i).resolveBinding().getDeclaringClass().getQualifiedName().toString();
				String methodJClass= methods.get(j).resolveBinding().getDeclaringClass().getQualifiedName().toString();
				if (!methodIClass.equals(methodJClass))
					if (isDirectlyConnected(cluster, methods.get(i),methods.get(j)) ){
						ndc++;
					}
			}
		}
		
		double np = methods.size() * (methods.size() - 1) /2;
		double internalConnections= getNumberInternalConnections(cluster);
		
		
		
		if (np==0) return 0.0;
		double npexternal= np-internalConnections;
		double result ;
		if (npexternal!=0){
			result= ndc / npexternal;
		}
		else
			result= 0;
		if (result>1){
			System.out.println("======== Error cohesion: "+result);
			
			return 1;
		}
		if(max<result)
			max=result;
		if(min>result)
			min=result;
		return result;
	}

	private static double getNumberInternalConnections(List<MyClass> cluster) {
		double numberInternalConnections=0;
		for(MyClass cl: cluster){
			int nbrPublicMethods= cl.getNbPublicMethods();
			
			numberInternalConnections+=nbrPublicMethods*(nbrPublicMethods-1)/2;
		}
		return numberInternalConnections;
	}
	
	// Check weather two methods accessed to the same attributes 
				//(directy connected)
	private static boolean isDirectlyConnected(List<MyClass> cluster, MethodDeclaration m1,
			MethodDeclaration m2) {
		
		Boolean isEmpty=intersectionEmpty(getAccessedAttributes(m1), getAccessedAttributes(m2));
		//System.out.println(m1.resolveBinding().getDeclaringClass().getQualifiedName()+"."+m1.getName().toString()+" "+
				//m2.resolveBinding().getDeclaringClass().getQualifiedName()+"."+m2.getName().toString()+" "+isEmpty);
		String classM1=m1.resolveBinding().getDeclaringClass().getQualifiedName();
		String classM2= m2.resolveBinding().getDeclaringClass().getQualifiedName();
		if(!classM1.equals(classM2)){
			if (!isEmpty) { 
			
				Set<String> methods = new HashSet<String>();
				methods.add(m1.resolveBinding().getDeclaringClass().getQualifiedName()+"."+m1.getName().toString());
				methods.add(m2.resolveBinding().getDeclaringClass().getQualifiedName()+"."+m2.getName().toString());
			  //  System.out.println("Connection : "+m1.resolveBinding().getDeclaringClass().getQualifiedName()+"."+m1.getName().toString()+" "+
			    //		m2.resolveBinding().getDeclaringClass().getQualifiedName()+"."+m2.getName().toString());
				if(methods.size()>1){
					directedConnectedMethods.add(methods);
				}
				return true;
			
			}
			else
				return false;
		}
		else
			return false;
	}

	// check weather the intersection between two sets of attributes
					//is empty or not
	private static Boolean intersectionEmpty(HashSet<Attribute> accessedAttributes, HashSet<Attribute> accessedAttributes2) {
		
		boolean isempty=true;
		if ( accessedAttributes.isEmpty() || accessedAttributes2.isEmpty())
			return true;
		else{
			List<Attribute> listaccessedAttributes = new ArrayList<Attribute>(accessedAttributes);
			List<Attribute> listaccessedAttributes2 = new ArrayList<Attribute>(accessedAttributes2);
		
			int i=0;
			while(i<listaccessedAttributes.size() && isempty){
				int j=0;
				while(j<listaccessedAttributes2.size() && isempty){
					if(listaccessedAttributes.get(i).equals(listaccessedAttributes2.get(j)))
					{	isempty=false;
					}
					j++;
				}
				i++;
			}
			
			return isempty;
		}
	}

	
	private static HashSet<Attribute> getAccessedAttributes( MethodDeclaration m1) {
		
	     return allAccededAttributes.get(m1);
	}

	// get the list of methods in the cluster
	public static List<MethodDeclaration> prepareToMeasureCohision(List<MyClass> cluster) {
	
		// get all the methods in the cluster
		List<MethodDeclaration> methods= new ArrayList<MethodDeclaration>();
		for (MyClass cl: cluster){
			for(MethodDeclaration method: cl.getMethods().keySet()){
				if (Modifier.isPublic(method.getModifiers())){
					methods.add(method);
				}
			}
		}
		
		for(MethodDeclaration method: methods){
			getAllAccededAttribute(method,  methods);
		}
		return methods;
	}

	// get all the acceded attributes by a method directly or indirectly (through method invoctions)
	private static HashSet<Attribute> getAllAccededAttribute(MethodDeclaration method, 
			List<MethodDeclaration> methods) {
		
		if (allAccededAttributes.get(method)== null){
			String className= method.resolveBinding().getDeclaringClass().getQualifiedName().toString();
			MyClass cl= findCorrespondingClass(classes, className);
			if (cl!=null){
				if(cl.getAccededAttributesForMethods().get(method)!= null){
					Set set = new HashSet(cl.getAccededAttributesForMethods().get(method));
					allAccededAttributes.put(method,(HashSet<Attribute>) set);
				}
				else{
					allAccededAttributes.put(method,new HashSet());
				}	
				for(MethodDeclaration calledmethod: cl.getMethods().get(method)){
					if(methods.contains(calledmethod))
						allAccededAttributes.get(method).addAll(getAllAccededAttribute(calledmethod, methods));
				}
			}
			else
				return new HashSet<Attribute>();
		}
		return allAccededAttributes.get(method);
		
	}

	// Compute FSem 
	public static double measureFFSem(List<MyClass>clusterClasses) {
		double coupling= measureInternalCoupling1(clusterClasses);	
		double externalCoupling= measureExternalCoupling1(clusterClasses);
		double cohesion= measureCohision(clusterClasses);	
		lamda=1; 
		beta=0;
		double FFSem;
		
	//	coupling=normalize(coupling,max,min);
	//	externalCoupling=normalize(externalCoupling,max,min);
	//	cohesion=normalize(cohesion,max,min);
	
		
		
		lastComputedCohesion=cohesion;
		lastComputedInternalCoupling=coupling;
		lastComputedExternalCoupling=externalCoupling;
		
		double FOne=(coupling+cohesion)/2;
        FFSem=(lamda*FOne-beta*externalCoupling)/(lamda+beta);
        lastComputedFSem=FFSem;
		return FFSem;
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

	// Measure external coupling
	public static double measureExternalCoupling1(List<MyClass> clusterClasses) {
		double coupling=0;
		double nbPossiblePairs;
		List<Double> couplingValues= new ArrayList<Double>();
		List<MyClass> externalClasses= getExternalClasses(clusterClasses);
		if(externalClasses.size()==0){
			lastComputedExternalCoupling=0;
			if(max<0)
				max=coupling;
			if(min>0)
				min=coupling;
			return 0;
		}
		else{
			for(int i = 0; i <clusterClasses.size(); i++)
			{   
				MyClass cl= clusterClasses.get(i);
			
					for(int j = 0; j < externalClasses.size(); j++)
					{
						MyClass cl2=externalClasses.get(j);
	                    double pairCoupling= couplingMatrix.get(cl).get(cl2);
	                    couplingValues.add(pairCoupling);
						coupling= coupling+pairCoupling;
						
					}		
			}
			nbPossiblePairs= clusterClasses.size()*externalClasses.size();
			double deviation=0;
			if(couplingValues.size()>0)
				deviation=getSommeDeviaition(couplingValues);
			coupling= (coupling-deviation)/nbPossiblePairs;
			if(max<coupling)
				max=coupling;
			if(min>coupling)
				min=coupling;
			return coupling;
		}
	}

	private static List<MyClass> getExternalClasses(List<MyClass> clusterClasses) {
		List<MyClass> externalClasses= new ArrayList<MyClass>();
		for(MyClass cl: classes){
			if (!clusterClasses.contains(cl))
				externalClasses.add(cl);
		}
		return externalClasses;
	}

	public static void createCouplingMatrix(List<MyClass> classes1) {
		
		for(int i = 0; i <classes1.size(); i++)
		{   
			MyClass cl= classes1.get(i);
			
			
			couplingMatrix.put(classes1.get(i), new HashMap<MyClass, Double>());
			for(int j = 0; j < classes1.size(); j++)
			{
				List<MyClass> cluster= new ArrayList<MyClass>();
				cluster.add(cl);
				MyClass cl2= classes1.get(j);
				cluster.add(cl2);
				double coupling= masureCouplingPair(cluster);
				couplingMatrix.get(cl).put(cl2,  coupling);
			}		
		}
	}


	public static double measureNormalisedCohision(ArrayList<MyClass> clusterClasses) {
		
		double cohesion= measureCohision(clusterClasses);	
		cohesion=normalize(cohesion,max,min);
		return cohesion;
	}


	public static double measureNormalisedInternalCoupling1(
			ArrayList<MyClass> clusterClasses) {
		// TODO Auto-generated method stub
		double coupling= measureInternalCoupling1(clusterClasses);	
		coupling=normalize(coupling,max ,min);
		return coupling;
	}


	public static double measureNormalisedExternalCoupling1(
			ArrayList<MyClass> clusterClasses) {
		// TODO Auto-generated method stub
		double externalcoupling= measureExternalCoupling1(clusterClasses);	
		externalcoupling=normalize(externalcoupling,max,min);
		return externalcoupling;
	}	
	
	
}
