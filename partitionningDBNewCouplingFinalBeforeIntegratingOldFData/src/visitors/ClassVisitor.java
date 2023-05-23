	package visitors;
	
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	
	import org.eclipse.jdt.core.dom.ASTVisitor;
	import org.eclipse.jdt.core.dom.FieldAccess;
	import org.eclipse.jdt.core.dom.ITypeBinding;
	import org.eclipse.jdt.core.dom.ImportDeclaration;
	import org.eclipse.jdt.core.dom.TypeDeclaration;
	
	public class ClassVisitor extends ASTVisitor{
		
		private HashMap<String, ArrayList<String>> classHierarchy = new HashMap<String, ArrayList<String>>();
		private HashMap<String, TypeDeclaration> TypeDeclarations = new HashMap<String, TypeDeclaration>();
		private List<String> qualifiedClassName= new ArrayList<String>();
		private HashMap<TypeDeclaration, ArrayList<FieldAccess>> fieldAcessForMethods = new HashMap
	            <TypeDeclaration, ArrayList<FieldAccess>>();
		
		private HashMap<String, ArrayList<String>> dbClassesForEachControlClass =  new HashMap
	            <String, ArrayList<String>>();
		List<String> dbClasses= new ArrayList<String>();
		
		
		
		TypeDeclaration activeType;
		
		public boolean visit(TypeDeclaration node){
			activeType= node;
			dbClassesForEachControlClass.put(node.resolveBinding().getQualifiedName(), (ArrayList<String>) dbClasses);
			fieldAcessForMethods.put(node, new ArrayList<FieldAccess>());
			ITypeBinding typeBind = node.resolveBinding();
			qualifiedClassName.add(node.resolveBinding().getQualifiedName());
			classHierarchy.put(node.resolveBinding().getQualifiedName(), new ArrayList<String>());
			TypeDeclarations.put(node.resolveBinding().getQualifiedName(), node);
			
			//adding implemented interfaces
			for (ITypeBinding s: typeBind.getInterfaces()){
			      classHierarchy.get(node.resolveBinding().getQualifiedName()).add(s.getQualifiedName());
			}
			
			if (typeBind.getSuperclass()!= null){
				    //adding a supper class
					String superClass=typeBind.getSuperclass().getQualifiedName();
					classHierarchy.get(node.resolveBinding().getQualifiedName()).add(superClass);				
			}
			return true;
		}
		
		public boolean visit(FieldAccess node){
			if ( activeType!= null){
				fieldAcessForMethods.get(activeType).add(node);
			}
			return true;
		}
		
		public boolean visit(ImportDeclaration node){
		
				
			  //Inventory management application
			 
			 	if (node.getName().toString().equals("com.ca.db.model")){
					dbClasses.add("com.ca.db.model.ApplicationLog");
					dbClasses.add("com.ca.db.model.BranchOffice");
					dbClasses.add("com.ca.db.model.Category");
					dbClasses.add("com.ca.db.model.CategorySpecifications");
					dbClasses.add("com.ca.db.model.Department");
					dbClasses.add("com.ca.db.model.Employee");
					dbClasses.add("com.ca.db.model.Item");
					dbClasses.add("com.ca.db.model.ItemReturn");
					dbClasses.add("com.ca.db.model.LoginUser");
					dbClasses.add("com.ca.db.model.Nikasa");
					dbClasses.add("com.ca.db.model.NikasaRequest");
					dbClasses.add("com.ca.db.model.Person");
					dbClasses.add("com.ca.db.model.PurchaseOrder");
					dbClasses.add("com.ca.db.model.Role");
					dbClasses.add("com.ca.db.model.Specification");
					dbClasses.add("com.ca.db.model.SUBBCategory");
					dbClasses.add("com.ca.db.model.SubCategory");
					dbClasses.add("com.ca.db.model.UnitsString");
					dbClasses.add("com.ca.db.model.Vendor");
				}
				else
					if (node.getName().toString().contains("com.ca.db.model"))
						dbClasses.add(node.getName().toString());
		
			 	//JPetStore application
			
			 	if (node.getName().toString().equals("org.mybatis.jpetstore.domain")){
					dbClasses.add("org.mybatis.jpetstore.domain.Account");
					dbClasses.add("org.mybatis.jpetstore.domain.Cart");
					dbClasses.add("org.mybatis.jpetstore.domain.CartItem");
					dbClasses.add("org.mybatis.jpetstore.domain.Category");
					dbClasses.add("org.mybatis.jpetstore.domain.Item");
					dbClasses.add("org.mybatis.jpetstore.domain.LineItem");
					dbClasses.add("org.mybatis.jpetstore.domain.Order");
					dbClasses.add("org.mybatis.jpetstore.domain.Product");
					dbClasses.add("org.mybatis.jpetstore.domain.Sequence");
			 	}		
				else
					if (node.getName().toString().contains("org.mybatis.jpetstore.domain"))
						dbClasses.add(node.getName().toString()); 
				 	
				//find sport mate application
				 
				  if (node.getName().toString().equals("com.findsportmates.model")){
					//  dbClasses.add("com.findsportmates.model.Basketball");
					  dbClasses.add("com.findsportmates.model.Event");
					  dbClasses.add("com.findsportmates.model.User");
					//  dbClasses.add("com.findsportmates.model.Volleyball");
					//  dbClasses.add("com.findsportmates.model.EventFactory");
					
				}
				else
					if (node.getName().toString().equals("com.findsportmates.model.Event") || 
							node.getName().toString().equals("com.findsportmates.model.User"))
						dbClasses.add(node.getName().toString());
			 	
			//Spring blog application
				 
				if (node.getName().toString().equals("com.raysmond.blog.models")){
						dbClasses.add("com.raysmond.blog.models.BaseModel");
						dbClasses.add("com.raysmond.blog.models.Post");
						dbClasses.add("com.raysmond.blog.models.Setting");
						dbClasses.add("com.raysmond.blog.models.Tag");
						dbClasses.add("com.raysmond.blog.models.User");
			 	}		
				else
					if (node.getName().toString().contains("com.raysmond.blog.models") && 
							!node.getName().toString().contains("com.raysmond.blog.models.dto") &&
							!node.getName().toString().contains("com.raysmond.blog.models.support")){
						dbClasses.add(node.getName().toString());
					}
				
			//Springblog FI	 
				if (node.getName().toString().equals("com.raysmond.blog.models")){
					dbClasses.add("com.raysmond.blog.models.BaseModel");
					dbClasses.add("com.raysmond.blog.models.Like");
					dbClasses.add("com.raysmond.blog.models.Post");
					dbClasses.add("com.raysmond.blog.models.SeoPostData");
					dbClasses.add("com.raysmond.blog.models.SeoRobotAgent");
					dbClasses.add("com.raysmond.blog.models.Setting");
					dbClasses.add("com.raysmond.blog.models.StoredFile");
					dbClasses.add("com.raysmond.blog.models.Tag");
					dbClasses.add("com.raysmond.blog.models.User");	
					dbClasses.add("com.raysmond.blog.models.Visit");	
			 	}		
				else
					if (node.getName().toString().contains("com.raysmond.blog.models") && 
							!node.getName().toString().contains("com.raysmond.blog.models.dto") &&
							!node.getName().toString().contains("com.raysmond.blog.models.support")){
						dbClasses.add(node.getName().toString());
					}
						
			return true;
		}
			
		
		public HashMap<String, ArrayList<String>> getClassHierarchy(){
			return classHierarchy;
		}
		
		public HashMap<String, TypeDeclaration> getTypeDeclarations(){
			return TypeDeclarations;
		}
		
		public List<String> getQualifiedClassName(){
			return qualifiedClassName;
		}
		
		public HashMap<TypeDeclaration, ArrayList<FieldAccess>> getFieldAcessForMethods() {
			
			return fieldAcessForMethods;
		}
		
		public HashMap<String, ArrayList<String>> getBDClassesForClasses() {
			
			return dbClassesForEachControlClass;
		}
	
		public void setDBforClasse(){
			dbClasses= new ArrayList<String>();
		}
	}
