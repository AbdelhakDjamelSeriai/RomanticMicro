package identificationProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import partitionningdb.handlers.PartitionningDB;
import models.API;
import models.MicroserviceInterface;
import models.MyClass;

public class MicrosserviceClassifier {

	private List<List<String>> manualMicroservices;
	private List<List<String>> automaticMicroservices;
	private int badMicroservices;
	private int goodMicroservices;
	private int excellentMicroservices;
	private String marker= "====================================================================================";
	
	public MicrosserviceClassifier(){
		setBadMicroservices(0);
		setGoodMicroservices(0);
		setExcellentMicroservices(0);
	}
	
	// Recover a set of microservice from txt file. Note that, this method was 
	//  implemented based on the used txt file structure in our approach
	public List<List<String>> recoverMicroservicesFromFile(String fileName){
		List<List<String>> microservices= new ArrayList<List<String>>();
		try {
			File file = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st; 
			while ((st = br.readLine()) != null){
				 while (st!=null && !st.trim().equals(marker)){
					 st = br.readLine();
				 }
				 for(int i=0; i<11; i++){
					 st=br.readLine();
				 }
				 List<String> microservice= new ArrayList<String>();
				 if (st!=null){
					 do{
						 microservice.add(st.trim());
						 st = br.readLine();
					 }while(!st.equals(marker));
				 
					 microservices.add(microservice);
				 }
					 
			}	
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	///	printMicroservices(microservices);
		return  microservices;
	}

	// Print the list of recovered microservices from a txt file
	public void printMicroservices(List<List<String>> microservices) {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("Microservices: ");
		for (List<String> micro: microservices){
			System.out.println("**********************************************************************************");
			for(String cl: micro){
				System.out.println("    - "+cl);
			}
			System.out.println("**********************************************************************************");
		}
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	
	// Setters and getters
	public int getExcellentMicroservices() {
		return excellentMicroservices;
	}

	public void setExcellentMicroservices(int excellentMicroservices) {
		this.excellentMicroservices = excellentMicroservices;
	}

	public int getGoodMicroservices() {
		return goodMicroservices;
	}

	public void setGoodMicroservices(int goodMicroservices) {
		this.goodMicroservices = goodMicroservices;
	}

	public int getBadMicroservices() {
		return badMicroservices;
	}

	public void setBadMicroservices(int badMicroservices) {
		this.badMicroservices = badMicroservices;
	}

	public List<List<String>> getAutomaticMicroservices() {
		return automaticMicroservices;
	}

	public void setAutomaticMicroservices(List<List<String>> automaticMicroservices) {
		this.automaticMicroservices = automaticMicroservices;
	}

	public List<List<String>> getManualMicroservices() {
		return manualMicroservices;
	}

	public void setManualMicroservices(List<List<String>> manualMicroservices) {
		this.manualMicroservices = manualMicroservices;
	}

	// Classify the identified microservices automatically by checking how many
	//manual microservices should be composed/decomposed to obtain the automatic
									//ones 
	public void classifyMicroservicesV1() {
		// TODO Auto-generated method stub
		for (List<String> microservice: automaticMicroservices){
			List<List<String>> manualMicrosContaintingAutMicroClasses= getMicroservices(microservice, manualMicroservices);
			int size=manualMicrosContaintingAutMicroClasses.size();
			if (size==1)
				if(manualMicrosContaintingAutMicroClasses.get(0).size()==microservice.size())
					excellentMicroservices++;
				else
					goodMicroservices++;
			else
				if(size<=2)
					goodMicroservices++;
				else
					badMicroservices++;
			
		}
	}
	
	// Classify the identified microservices automatically by checking how many
	//automtic microservices should be composed/decomposed to obtain the manual
										//ones 
	public void classifyMicroservicesV2(String identifiedMicroFile) {
		// TODO Auto-generated method stub
		try {
			
			FileWriter fw = new FileWriter(identifiedMicroFile, true);
			
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println();
		
			
			for (List<String> microservice: manualMicroservices){
				List<List<String>> autoMicrosContaintingManMicroClasses= getMicroservices(microservice, automaticMicroservices);
				int size=autoMicrosContaintingManMicroClasses.size();
				if (size==1){
					if(autoMicrosContaintingManMicroClasses.get(0).size()==microservice.size())
						excellentMicroservices++;
					else
						goodMicroservices++;
				}	
				else{
					if(size<=2)
						goodMicroservices++;
					else{
						badMicroservices++;
						out.println();
						out.println();
						out.println("Bad microservice: ");
						for(String cl: microservice)
							out.println("     - "+cl);
						out.println();
						out.println();
					}
						
				}
				
			
		}
		
		out.println();
		out.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	// Find and get a microservice from a list of micrservices
	private List<List<String>> getMicroservices(List<String> microservice, List<List<String>> microservices) {
		List<List<String>> manualMicrosContaintingAutMicroClasses= new ArrayList<List<String>>();
		int i=0;
		while (manualMicrosContaintingAutMicroClasses.size()<=3 && i<microservice.size()){
			String cl= microservice.get(i);
			List<String> manMicro= findClusterContainingClass(cl,microservices);
			if(manMicro!=null && !manualMicrosContaintingAutMicroClasses.contains(manMicro))
				manualMicrosContaintingAutMicroClasses.add(manMicro);
			
				
			i++;
			
		}
		return manualMicrosContaintingAutMicroClasses;
	}

	// Find the microservice containing a given class
	private List<String> findClusterContainingClass(String cl, List<List<String>> microservices) {
		List<String> micro= null;
		int i=0;
		boolean found= false;
		while (!found && i<microservices.size()){
			if (microservices.get(i).contains(cl)){
				found=true;
				micro=microservices.get(i);
			}
			i++;
		}
	//	if(micro==null)
	//		System.err.println("Class not found: "+cl);
		return micro;
	}

	public void printInFile(String identifiedMicroFile) {
		try {
			
			FileWriter fw = new FileWriter(identifiedMicroFile, true);
			
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println();
			out.println("---- Bad microservices: "+getBadMicroservices());
			out.println("---- Good microservices: "+getGoodMicroservices());
			out.println("---- Excellent microservices: "+getExcellentMicroservices());
			out.println();
			out.println("---- FMicro coefficients: "+MeusuringMetricsNewFdata.lambda+"  "+MeusuringMetricsNewFdata.beta);
			out.println("---- FSem coefficients  : "+MeasuringMetrics.lamda+"  "+MeasuringMetrics.beta);
			out.println("---- FData coefficients : "+FDataComputer.lambda+"  "+FDataComputer.beta);
			out.println();
			if(PartitionningDB.gravityCenters!=null){
				out.println("---- Gravity centers:");
				for(MyClass cl: PartitionningDB.gravityCenters)
					out.println("       "+cl.getName());
			}
			out.println();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
