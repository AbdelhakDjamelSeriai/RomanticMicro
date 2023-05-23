package models;

import java.util.ArrayList;
import java.util.List;

public class MicroserviceInterface {
	
	private List<API> apiSet= new ArrayList<API>();
	private String name;

	public MicroserviceInterface(String interfaceName) {
		 setName(interfaceName);
	}

	public List<API> getApiSet() {
		return apiSet;
	}

	public void setApiSet(List<API> apiSet) {
		this.apiSet = apiSet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasAPI(API api) {
		return  (apiSet.indexOf(api)!=-1);
			
	}

	public void addAPI(API api) {
		apiSet.add(api);
		
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object obj){
		MicroserviceInterface inter= (MicroserviceInterface) obj;
		return inter.getName().equals(name);
		
	}

	public double getAPINumber() {
		return apiSet.size();
	}

}
