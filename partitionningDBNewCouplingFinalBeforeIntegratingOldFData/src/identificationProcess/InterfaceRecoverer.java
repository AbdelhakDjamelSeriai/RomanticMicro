package identificationProcess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import models.API;
import models.MicroserviceInterface;
import models.MyClass;
import models.Microservice;


public class InterfaceRecoverer {
	
	private HashMap<String, Microservice> classMicroserviceMap= new  HashMap<String, Microservice>(); 
	public static int IRN=0;
	
	public List<Microservice> getMicroservices(Set<Set<MyClass>>  clustersClasses){
		List<Microservice> microservices= initialiseMicroserviceList(clustersClasses);
		initilizeClassMicroserviceMap(microservices);
		int i=0;
		for (Microservice micro:  microservices){
			for (MyClass cl: micro.getMicroClasses()){
				for (MethodDeclaration method: cl.getMethods().keySet() ){
					for(MethodDeclaration calledMethod: cl.getMethods().get(method)){
						String calledClassName= calledMethod.resolveBinding().getDeclaringClass().getQualifiedName();
						Microservice calledMicro= classMicroserviceMap.get(calledClassName);
						if (calledMicro!=null && calledMicro.equals(micro)){
							i++;
						}
						if (calledMicro!=null && !calledMicro.equals(micro)){
							IRN++;
							API api= new API(calledMethod.getName().toString());
							api.identifyParameters(calledMethod);
							api.identifyDomainNames(calledMethod.getName().toString());
							MicroserviceInterface microInterface= new MicroserviceInterface(calledClassName);
							int index= calledMicro.getMicrointerfaces().indexOf(microInterface);
							
							if (index!=-1){
								microInterface= calledMicro.getMicrointerfaces().get(index);
							}else{
								
								calledMicro.getMicrointerfaces().add(microInterface);
							}
							
							if (!microInterface.hasAPI(api))
								microInterface.addAPI(api);
						}
						
					}
				}
			}
			
		}
		
		return microservices;
		
	}

	

	private List<Microservice> initialiseMicroserviceList(
			Set<Set<MyClass>> clustersClasses) {
		List<Microservice> microservices= new ArrayList<Microservice>();
		for (Set<MyClass> cluster: clustersClasses){
			Microservice micro= new Microservice(new ArrayList(cluster));
			microservices.add(micro);
		}	
		return microservices;
	}

	private void initilizeClassMicroserviceMap(List<Microservice> microservices) {
		
		for (Microservice micro: microservices){
			for (MyClass cl: micro.getMicroClasses()){
				classMicroserviceMap.put(cl.getName(), micro);
			}
		}		
	}



	public int getIRN() {
		return IRN;
	}



	public void setIRN(int iRN) {
		IRN = iRN;
	}

}
