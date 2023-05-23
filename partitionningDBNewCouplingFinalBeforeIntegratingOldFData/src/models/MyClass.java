package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;



public class MyClass implements Comparable<MyClass> {
	private String name;
	private String simpleName;
	private HashMap<MethodDeclaration, ArrayList<MethodDeclaration>> calledMethodsByEachMethod= new  HashMap<MethodDeclaration, ArrayList<MethodDeclaration>>();
	private HashMap<MethodDeclaration, ArrayList<Attribute>>	accededAttributeByEachMethod= new  HashMap<MethodDeclaration, ArrayList<Attribute>>();
	private List< ManipulatedData> ReadData= new ArrayList<ManipulatedData>();
	private List< ManipulatedData> WrittenData= new ArrayList<ManipulatedData>();
	
	public List<ManipulatedData> getReadData() {
		return ReadData;
	}
	
	public void setReadData(List<ManipulatedData> readData) {
		ReadData = readData;
	}
	
	public List<ManipulatedData> getWrittenData() {
		return WrittenData;
	}
	
	public void setWrittenData(List<ManipulatedData> writtenData) {
		WrittenData = writtenData;
	}
	
	private List<FieldAccess> fieldAcesses= new ArrayList<FieldAccess>();
	private int nbpublicMethods;
	
	public void incrementNbPublicMethods(){
		nbpublicMethods++;
	}
	public MyClass(String classname) {
		name= classname;
	}

	public MyClass() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addFieldAcess (FieldAccess fa){
		if (!fieldAcesses.contains(fa))
			fieldAcesses.add(fa);
	}
	
	public void addMethod(MethodDeclaration method){
		if (!calledMethodsByEachMethod.keySet().contains(method.toString()))
			calledMethodsByEachMethod.put(method, new ArrayList<MethodDeclaration>());
	}
	
	public List<MethodDeclaration> getCalledMethodByAMethod(MethodDeclaration method){
		return  calledMethodsByEachMethod.get(method);
	}
	
	public boolean equals(Object obj){
		if (obj instanceof MyClass){
			MyClass cl= (MyClass) obj;
			return name.equals(cl.getName());
		}
		else{
			return name.equals(obj);
		}
	}

	public HashMap<MethodDeclaration, ArrayList<MethodDeclaration>>  getMethods() {
		return calledMethodsByEachMethod;
	}
	
	public HashMap<MethodDeclaration, ArrayList<Attribute>>  getAccededAttributesForMethods() {
		return accededAttributeByEachMethod;
	}

	public List<FieldAccess> getFiedAccess() {
		
		return fieldAcesses;
	}
	
	public String toString(){
		return name;
	}

	public void addAccededAttribute(Attribute att, MethodDeclaration method) {
		if(accededAttributeByEachMethod.get(method)== null){
			List<Attribute> list= new ArrayList<Attribute>();
			list.add(att);
			accededAttributeByEachMethod.put(method,(ArrayList<Attribute>) list);
		}
		else{
			if (!accededAttributeByEachMethod.get(method).contains(att))
				accededAttributeByEachMethod.get(method).add(att);
		}
	}

	public boolean hasAsAccededAttribute(Attribute att1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MyClass o) {
		// TODO Auto-generated method stub
		return name.compareTo(o.getName());
	}

	public int getNbPublicMethods() {
		
		return nbpublicMethods;
	}

	public void setSimpleName(String classSimpleName) {
		// TODO Auto-generated method stub
		simpleName=classSimpleName;
	}
	
	public String getSimpleName() {
		// TODO Auto-generated method stub
		return simpleName;
	}
}
