package models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Microservice {
	
	private List <MyClass> microClasses= new ArrayList<MyClass>();
	private List<MicroserviceInterface> microInterfaces= new ArrayList<MicroserviceInterface>();
	
	public Microservice(List<MyClass> clustersClasses) {
		microClasses=clustersClasses;
	}
	public List <MyClass> getMicroClasses() {
		return microClasses;
	}
	public void ListMicroClasses(List <MyClass> microClasses) {
		this.microClasses = microClasses;
	}
	public List<MicroserviceInterface> getMicrointerfaces() {
		return microInterfaces;
	}
	public void ListMicrointerfaces(List<MicroserviceInterface> microinterfaces) {
		this.microInterfaces = microinterfaces;
	}
	public int getNbrInterfaces() {
		return microInterfaces.size();
	}
	public boolean equals(Object micro){
		
		Microservice myMicro= (Microservice) micro;
		return microClasses.toString().equals(myMicro.getMicroClasses().toString());
	}
	public void  print(){
		System.out.println("Microservice classes: ");
		for (MyClass cl: microClasses)
			System.out.println("           - Class: "+cl.getName());
		System.out.println("Microservice interfaces: ");
		for (MicroserviceInterface microInterface: microInterfaces){
			System.out.println("           - Interface: "+microInterface.getName());
			for (API api: microInterface.getApiSet())
				System.out.println("                   *Operation: "+api);
		}
	}
	
	public void  printInFile(String saveInFile){
		
		try {
			
			FileWriter fw = new FileWriter(saveInFile, true);
			
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println("**********************************************************************************************");
			 out.println("Microservice classes: ");
			 out.println("===============================================================================================");
			for (MyClass cl: microClasses)
				 out.println("           - Class: "+cl.getName());
		         out.println("Microservice interfaces: ");
			for (MicroserviceInterface microInterface: microInterfaces){
				 out.println("           - Interface: "+microInterface.getName());
				for (API api: microInterface.getApiSet())
					 out.println("                   *Operation: "+api);
			}
			out.println("**********************************************************************************************");
			out.println();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
