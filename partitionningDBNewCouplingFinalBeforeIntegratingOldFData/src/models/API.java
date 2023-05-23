package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class API {
	
	private String name;
	private List<String> parameters= new ArrayList<String>();
	private List<String> returnedParameters= new ArrayList<String>();;
	private List<String> domainTerms= new ArrayList<String>();;
	
	public API(String apiName) {
		name= apiName;
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	
	public List<String> getReturnedParameters() {
		return returnedParameters;
	}
	
	public void setReturnedParameters(List<String> returnedParameters) {
		this.returnedParameters = returnedParameters;
	}
	
	public List<String> getDomainTerms() {
		return domainTerms;
	}
	
	public void setDomainTerms(List<String> domainTerms) {
		this.domainTerms = domainTerms;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		return name+" "+parameters+" "+returnedParameters;
	}
	
	public boolean equals(Object obj){
		API api= (API) obj;
		return toString().equals(api.toString());
	}
	
	public void identifyParameters(MethodDeclaration method) {
		for (Object obj: method.parameters()){
			SingleVariableDeclaration s= (SingleVariableDeclaration) obj;
			 parameters.add(s.getType().resolveBinding().getQualifiedName());

		} 
	

		String returnedType= method.getReturnType2().resolveBinding().getQualifiedName();
		returnedParameters.add(returnedType);
		
				
		
	}

	
	public void identifyParametersByNames(MethodDeclaration method) {
		for (Object obj: method.parameters()){
			SingleVariableDeclaration s= (SingleVariableDeclaration) obj;
			 parameters.add(s.getName().toString());
		}

		if (!method.getReturnType2().toString().equals("void") && method.getBody()!=null){
			for (Object s: method.getBody().statements() ){
				System.out.println(s);
				if(s instanceof ReturnStatement){
					ReturnStatement retStatement= (ReturnStatement) s;
					if (retStatement.getExpression() instanceof SimpleName)
						returnedParameters.add(retStatement.getExpression().toString());
					
				}
			}
		}		
		
	}
	public void identifyDomainNames(String methodName) {
		
		int i=0;
		char [] charArray= methodName.toCharArray();
		while (i<charArray.length){
			char c= charArray[i];
			if (Character.isUpperCase(c)){
				String domainTerm= ""+c;
				i++;
				while(i<charArray.length && !Character.isUpperCase(charArray[i])){	
					domainTerm+=charArray[i];	
					i++;
				}
				if (!domainTerms.contains(domainTerm) && !isProposition(domainTerm)){
					domainTerms.add(domainTerm);
				}
					
			}
			else
				i++;
			
		}
	}
	
	private boolean isProposition(String domainTerm) {
		if (domainTerm.equals("By") || domainTerm.equals("And")|| 
				domainTerm.equals("Or") || domainTerm.equals("For")
				|| domainTerm.equals("In") || domainTerm.equals("On")||
				domainTerm.equals("Of"))
			return true;
		else
			return false;
	}

}
