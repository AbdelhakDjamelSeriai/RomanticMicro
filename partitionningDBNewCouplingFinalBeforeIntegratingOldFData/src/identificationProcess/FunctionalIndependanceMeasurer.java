package identificationProcess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.API;
import models.Microservice;
import models.MicroserviceInterface;
import models.MyClass;

public class FunctionalIndependanceMeasurer {
	
	double totalNumberMicroservicesInterfaces;
	public List<Double> measure(List<Microservice> microservices, String saveInFileMicroWithInterface){
		
		List<Double> metricsList= new ArrayList<Double>();
		double IFN= computeIFN(microservices);
		metricsList.add(IFN);
		double CHM= computeCHM(microservices);
		metricsList.add(CHM);
		double CHD= computeCHD(microservices);
		metricsList.add(CHD);
		double OPN= computeOPN(microservices);
		metricsList.add(OPN);
		double IRN= computeIRN(microservices);
		metricsList.add(IRN);
		System.out.println("=========================================================");
		System.out.println("           Measuring Functional Independance");
		System.out.println("=========================================================");
		System.out.println("CHM: "+CHM);
		System.out.println("CHD: "+CHD);
		System.out.println("IFN: "+IFN);
		System.out.println("OPN: "+OPN);
		System.out.println("IRN: "+IRN);
		System.out.println("=========================================================");
		printInFile(saveInFileMicroWithInterface,metricsList);
		return metricsList;
	}

	private void printInFile(String saveInFileMicroWithInterface,
			List<Double> metricsList) {
		try {
			
			FileWriter fw = new FileWriter(saveInFileMicroWithInterface, true);
			
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println();
			out.println("=========================================================");
			out.println("           Measuring Functional Independance");
			out.println("=========================================================");
			out.println("CHM: "+metricsList.get(1));
			out.println("CHD: "+metricsList.get(2));
			out.println("IFN: "+metricsList.get(0));
			out.println("OPN: "+metricsList.get(3));
			out.println("IRN: "+metricsList.get(4));
			out.println("=========================================================");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private double computeIRN(List<Microservice> microservices) {
		return InterfaceRecoverer.IRN;
	}

	private double computeOPN(List<Microservice> microservices) {
		
		double OPN=0;
		for (Microservice micro: microservices){
			
			for(MicroserviceInterface microInter: micro.getMicrointerfaces()){
				OPN+= microInter.getAPINumber();
			}
			
				
		}
		
		return OPN;
	}

	private double computeIFN(List<Microservice> microservices) {
		int k= microservices.size();
		totalNumberMicroservicesInterfaces=0;
		for (int i=0; i<k; i++){
			 totalNumberMicroservicesInterfaces+= microservices.get(i).getNbrInterfaces();
		}
		double IFN= totalNumberMicroservicesInterfaces/k;
		return IFN;
	}

	private double computeCHD(List<Microservice> microservices) {
		double CHD=0;
		for (Microservice micro: microservices){
			for (MicroserviceInterface microInter: micro.getMicrointerfaces()){
				List<API> apiList= microInter.getApiSet();
				CHD+= computeCHDInterface(apiList);	
			}
		}
		CHD=CHD/totalNumberMicroservicesInterfaces;
		return CHD;
	}

	private double computeCHDInterface(List<API> apiList) {
		if (apiList.size()>1){
			double CHDInterface= 0;
			for (int i=0; i<apiList.size();i++){
				for(int j=i+1; j<apiList.size();j++){
					double fdsim= FDSim(apiList.get(i),apiList.get(j));
					CHDInterface+=fdsim;
				}
			}
			
			double nbrPossibleCombinations= apiList.size()*(apiList.size()-1)/2;
			CHDInterface= CHDInterface/nbrPossibleCombinations;
			return CHDInterface;
		}
		else{
			return 1;
		}
	}

	private double FDSim(API api, API api2) {
		double intersectionDomainTerms= getIntersectionDomainTerms(api.getDomainTerms(), api2.getDomainTerms());
		double unionDomainTerms= getUnionDomainTerms(api.getDomainTerms(), api2.getDomainTerms());
		if (unionDomainTerms!=0)
			return intersectionDomainTerms/unionDomainTerms;	
		else
			return 1;
	}

	private double getIntersectionDomainTerms(List<String> domainTerms,
			List<String> domainTerms2) {
		
		int nbrIntersection=0;
		List<String> tempList= new ArrayList<String>();
		for (String s: domainTerms)
			for (String s1: domainTerms2){
				if (s.contains(s1) || s1.contains(s)){
					tempList.add(s);
					nbrIntersection++;
				}
			}
		return nbrIntersection;
	}
	
	private double getUnionDomainTerms(List<String> domainTerms,
			List<String> domainTerms2) {
		
		
		List<String> tempList= new ArrayList<String>();
		tempList.addAll(domainTerms);
		boolean added;
		for (String s1: domainTerms2){
			added=false;
			for (String s: domainTerms){
				if (s.contains(s1) ||  s1.contains(s)){
					
					added=true;
				}
			}
			if (!added)
				tempList.add(s1);
		}
		return tempList.size();
	}

	private double computeCHM(List<Microservice> microservices) {
		double CHM=0;
		for (Microservice micro: microservices){
			for (MicroserviceInterface microInter: micro.getMicrointerfaces()){
				List<API> apiList= microInter.getApiSet();
				CHM+= computeCHMInterface(apiList);	
			}
		}
		CHM=CHM/totalNumberMicroservicesInterfaces;
		return CHM;
	}

	private double computeCHMInterface(List<API> apiList) {
		if (apiList.size()>1){
			double CHMInterface= 0;
			for (int i=0; i<apiList.size();i++){
				for(int j=i+1; j<apiList.size();j++){
					double fsim= FSim(apiList.get(i),apiList.get(j));
					
					CHMInterface+=fsim;
				}
			}
			double nbrPossibleCombinations= apiList.size()*(apiList.size()-1)/2;
			CHMInterface= CHMInterface/nbrPossibleCombinations;
			return CHMInterface;
		}
		else{
			return 1;
		}
		
	}

	private double FSim(API api, API api2) {
		double intersectionParam= getIntersection(api.getParameters(), api2.getParameters());
		double unionParam= getUnion(api.getParameters(), api2.getParameters());
		double intersectionReturns= getIntersection(api.getReturnedParameters(), api2.getReturnedParameters());
		double unionReturns= getUnion(api.getReturnedParameters(), api2.getReturnedParameters());
		if (unionParam!=0)
			if (unionReturns!=0){
				return (intersectionParam/unionParam+intersectionReturns/unionReturns)/2.0;
			}
				
			else
				return (intersectionParam/unionParam)/2.0;
			
		else
				if (unionReturns!=0){
				
					return (intersectionReturns/unionReturns)/2.0;
				}
				
				else
					return 1;
		
	}

	private int getIntersection(List<String> parameters, List<String> parameters2) {
		
		Set<String> setParam1= new HashSet<String>(parameters);
		Set<String> setParam2= new HashSet<String>(parameters2);
		setParam1.retainAll(setParam2);
		return setParam1.size();
		
	}

	private int getUnion(List<String> parameters,
			List<String> parameters2) {
		Set<String> setParam1= new HashSet<String>(parameters);
		Set<String> setParam2= new HashSet<String>(parameters2);
		setParam1.addAll(setParam2);
		return setParam1.size();
	}

}
