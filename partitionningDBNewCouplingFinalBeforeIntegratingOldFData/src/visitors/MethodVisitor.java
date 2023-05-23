package visitors;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Attribute;
import models.ManipulatedData;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import partitionningdb.handlers.MethodHandler;


public class MethodVisitor extends ASTVisitor{
	
	private HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods = new HashMap
            <MethodDeclaration, ArrayList<MethodInvocation>>();
	private HashMap<MethodDeclaration, ArrayList<Expression>> fieldAccessForMethods = new HashMap
            <MethodDeclaration, ArrayList<Expression>>();
	
	private HashMap<MethodDeclaration, ArrayList<Attribute>> accessdFieldsForMethods = new HashMap
            <MethodDeclaration, ArrayList<Attribute>>();
	Set<String> dataList= new HashSet<String>();
	
	MethodDeclaration activeMethod;
	
	private HashMap<String, ArrayList<ManipulatedData>> readdbClassesForEachClass =  new HashMap
            <String, ArrayList<ManipulatedData>>();
	private HashMap<String, ArrayList<ManipulatedData>> writtendbClassesForEachClass =  new HashMap
            <String, ArrayList<ManipulatedData>>();
	
	
	List<String> readdbClasses= new ArrayList<String>();
	List<String> writtendbClasses= new ArrayList<String>();
	private static int nbraccessData=0;

	
	public boolean visit(MethodDeclaration node){
		
		if (!node.isConstructor()){
			activeMethod= node;
			invocationsForMethods.put(activeMethod, new ArrayList<MethodInvocation>());
			fieldAccessForMethods.put(activeMethod, new ArrayList<Expression>());
			accessdFieldsForMethods.put(activeMethod, new ArrayList<Attribute>());
			String className= activeMethod.resolveBinding().getDeclaringClass().getQualifiedName();
			if(writtendbClassesForEachClass.get(className)== null)
				writtendbClassesForEachClass.put(className, new ArrayList<ManipulatedData>());
			if(readdbClassesForEachClass.get(className)== null)
				readdbClassesForEachClass.put(className, new ArrayList<ManipulatedData>());
		}
		return true;
	}
	
	public boolean visit(FieldAccess node){
		
		if ( activeMethod!= null){
			fieldAccessForMethods.get(activeMethod).add(node);
		}
		return true;
	}
	
	public boolean visit(SuperFieldAccess node){
		
		if ( activeMethod!= null){
			fieldAccessForMethods.get(activeMethod).add(node);
		}
		return true;
	}
	
	public boolean visit(MethodInvocation node){
		
		if ( activeMethod!= null){
			invocationsForMethods.get(activeMethod).add(node);
			
			//Data Partitioning
			String receiverType;
			if(node.getExpression()!= null)
				receiverType= node.getExpression().resolveTypeBinding().getQualifiedName();
			else
				receiverType=node.resolveMethodBinding().getDeclaringClass().getQualifiedName();
			
			if (isSession(receiverType)){
				
				if (isWriteperation(node.getName().toString())){
					addModifiedDBClasse(node);		
				}
			}
			
		}	
		
		
		return true;
	}
	
	private boolean isWriteperation(String name) {
		if (name.equals("save") || name.equals("update") || name.equals("delete") || name.equals("saveOrUpdate"))
			return true;
		else 
			return false;
	}

	private void addModifiedDBClasse(MethodInvocation node) {
		List args= node.arguments();
		for(int i=0; i<args.size(); i++){
			Expression exp=(Expression) args.get(i);
			String manipulatedDbClass= exp.resolveTypeBinding().getQualifiedName().toString();
			if (!manipulatedDbClass.equals("java.lang.Object")){
				String className= activeMethod.resolveBinding().getDeclaringClass().getQualifiedName();
				ManipulatedData d= new ManipulatedData(manipulatedDbClass,1);
				dataList.add(manipulatedDbClass);
				nbraccessData++;
				if (!writtendbClassesForEachClass.get(className).contains(d)){
					writtendbClassesForEachClass.get(className).add(d);
					if(readdbClassesForEachClass.get(className).contains(d))
						readdbClassesForEachClass.get(className).remove(d);
				}	
				else{
					int indexcorrespondingD= writtendbClassesForEachClass.get(className).indexOf(d);
					writtendbClassesForEachClass.get(className).get(indexcorrespondingD).incrementFrequency(1);
				}
			}
		}	
	}

	
	public HashMap<String, ArrayList<ManipulatedData>> getWrittenDbClassesMatrix(){
		return writtendbClassesForEachClass;
	}
	
	private boolean isSession(String receiverType) {
		return receiverType.equals("org.hibernate.Session");
	}

	
	
	public List<String> getwrittenDBClasses(){
		return writtendbClasses;
	}
	
	public void deleteLibraryMethodInvocations() {
		
		// Deleting Library method invocations
		MethodHandler mh= new MethodHandler();
		for (MethodDeclaration md: invocationsForMethods.keySet()){ 
				   List <Integer> toBeDeleted= new ArrayList<Integer>();
				   int numberemovedMethodInvoc =0;
				   for(MethodInvocation mi: invocationsForMethods.get(md)){
					  if (! mh.isUserMethod(mi, invocationsForMethods.keySet())){
						  toBeDeleted.add( invocationsForMethods.get(md).indexOf(mi));
					  }
				   }
				   for (Integer index: toBeDeleted){
					   invocationsForMethods.get(md).remove(index.intValue()-numberemovedMethodInvoc);
					   numberemovedMethodInvoc++;
				   }		  
		}
	}
	
	
	
	public HashMap<MethodDeclaration, ArrayList<MethodInvocation>> getInvocationsForMethods() {
		deleteLibraryMethodInvocations();
	    activeMethod=null;
		return invocationsForMethods;
	}
	
	public HashMap<MethodDeclaration, ArrayList<Expression>> getFieldAccessesForMethods() {
		return fieldAccessForMethods;
	}
	
	public boolean visit(SimpleName node) {
		
		if ( activeMethod!= null){
			
			if (!node.isDeclaration()
				&& node.resolveBinding() instanceof IVariableBinding) {
					if(((IVariableBinding)node.resolveBinding()).isField() ){
						Attribute att;
						
							if(node.resolveTypeBinding()!= null)
								att= new Attribute(node.toString(),node.resolveTypeBinding().getName());
							else
								att= new Attribute(node.toString(),"");
							if (!accessdFieldsForMethods.get(activeMethod).contains(att)){
								accessdFieldsForMethods.get(activeMethod).add(att);
							}
						}
					
			}
		}
		return super.visit(node);
	}


	public HashMap<MethodDeclaration, ArrayList<Attribute>> getAccessedFieldsForMethods() {
		return accessdFieldsForMethods;		
	}

	
	public boolean visit(ExpressionStatement node){
		
		Expression exp= node.getExpression();
		if(exp instanceof Assignment){
			Assignment assignment= (Assignment)exp;
			
			if (assignment.getRightHandSide() instanceof MethodInvocation){
				MethodInvocation mi= (MethodInvocation) assignment.getRightHandSide();
				String receiverType;
				if(mi.getExpression()!= null)
					receiverType= mi.getExpression().resolveTypeBinding().getQualifiedName();
				else
					receiverType=mi.resolveMethodBinding().getDeclaringClass().getQualifiedName();
				
				if ((receiverType.equals("org.hibernate.Query")|| receiverType.equals("org.hibernate.Query")) && mi.getName().toString().equals("list")){
					String dataname= getReadData(assignment.getLeftHandSide().resolveTypeBinding().getQualifiedName());
					
					ManipulatedData data= new ManipulatedData(dataname, 1);
					String className= activeMethod.resolveBinding().getDeclaringClass().getQualifiedName();
					if (data.getName()!= null){
						dataList.add(dataname);
						nbraccessData++;
						if(!writtendbClassesForEachClass.get(className).contains(data)){
							if (!readdbClassesForEachClass.get(className).contains(data)){
								readdbClassesForEachClass.get(className).add(data);	
							}
							
							else{
								int indexcorrespondingD= readdbClassesForEachClass.get(className).indexOf(data);
								readdbClassesForEachClass.get(className).get(indexcorrespondingD).incrementFrequency(1);
							}
						}
						
							
					}
					
				}
			}
		}
		
		return true;
		
	}
	
	

	public boolean visit(VariableDeclarationStatement node){
		
		List<VariableDeclarationFragment> fragments= node.fragments();
		for(VariableDeclarationFragment fragment: fragments){
			if (fragment.getInitializer() instanceof MethodInvocation){
				MethodInvocation mi= (MethodInvocation) fragment.getInitializer();
				String receiverType;
				if(mi.getExpression()!= null)
					receiverType= mi.getExpression().resolveTypeBinding().getQualifiedName();
				else
					receiverType=mi.resolveMethodBinding().getDeclaringClass().getQualifiedName();
				
				String className= activeMethod.resolveBinding().getDeclaringClass().getQualifiedName();
				
				if ((receiverType.equals("org.hibernate.Query") || receiverType.equals("org.hibernate.SQLQuery")) && mi.getName().toString().equals("list")){
					String dataname= getReadData(node.getType().resolveBinding().getQualifiedName());
					
					ManipulatedData data= new ManipulatedData(dataname, 1);
					if (data.getName()!= null){
						nbraccessData++;
						dataList.add(dataname);
						if(!writtendbClassesForEachClass.get(className).contains(data)){
							if (!readdbClassesForEachClass.get(className).contains(data)){
								readdbClassesForEachClass.get(className).add(data);	
							}
							
							else{
								int indexcorrespondingD= readdbClassesForEachClass.get(className).indexOf(data);
								readdbClassesForEachClass.get(className).get(indexcorrespondingD).incrementFrequency(1);
							}
						}
						
							
					}
				}
				
			}
		}
		
			 
	
		return true;
	}

	private String getReadData(String listtype) {
		String[] types= listtype.split("<");
		if(types.length>=2)
			return types[1].replace(">", "");
		else
			return null;
	}

	public HashMap<String, ArrayList<ManipulatedData>> getReadDbClassesMatrix() {
		
		return readdbClassesForEachClass;
	}
	
	public List<String> getDataList(){
		return new ArrayList<String>(dataList);
	}

	public int getNbrAccessData() {
		return nbraccessData;
	}

	
}
