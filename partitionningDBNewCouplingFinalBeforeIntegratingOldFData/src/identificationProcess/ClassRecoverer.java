package identificationProcess;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import models.Attribute;
import models.ManipulatedData;
import models.MyClass;
import partitionningdb.handlers.MethodHandler;


public class ClassRecoverer {

	// Recover the list of classes of the OO application.
	
	private static int nbraccessData=0;
	
	private List<MyClass> setMethodsAndTheirCalledOnes(
			HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods) {
		
		
	    MethodHandler mh= new MethodHandler();
	    List<MyClass> classes= new ArrayList<MyClass>();
		for (MethodDeclaration md: invocationsForMethods.keySet()){
			String declaingClass= md.resolveBinding().getDeclaringClass().getQualifiedName();
			MyClass cl= new MyClass(declaingClass);
			cl.setSimpleName(md.resolveBinding().getDeclaringClass().getName());
			if(!classes.contains( cl)){
				classes.add(cl);
			}
			else{
				cl= classes.get(classes.indexOf(cl));
			}
			
			if (Modifier.isPublic(md.getModifiers()))
				cl.incrementNbPublicMethods();
			cl.addMethod(md);
			for(MethodInvocation mi: invocationsForMethods.get(md)){
				
				MethodDeclaration mdOfMi=null;
				for (MethodDeclaration m: invocationsForMethods.keySet()){
					
					if (mh.isTheCorespondantMethodDeclaration(mi, m)){
						
						mdOfMi= m;
					}
				}
				cl.getCalledMethodByAMethod(md).add(mdOfMi);	
		   }
		}
		return classes;
	}
	
	

		public List<MyClass> getSetClasses(
				HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods,
				HashMap<MethodDeclaration, ArrayList<Attribute>> hashMap) {
			
			
			List<MyClass> classes = setMethodsAndTheirCalledOnes(invocationsForMethods); 
			classes =setAccededFields1(classes, hashMap);
			return classes;
		}

		private List<MyClass> setAccededFields1(List<MyClass> classes,
				HashMap<MethodDeclaration, ArrayList<Attribute>> hashMap) {
			
			for (MethodDeclaration m: hashMap.keySet()){
				String className = m.resolveBinding().getDeclaringClass().getQualifiedName();
				MyClass cl= findCorrespondingClass(classes, className );
				if (cl== null){
					cl= new MyClass(className);
					classes.add(cl);
				}
				
				for(Attribute att: hashMap.get(m))
					cl.addAccededAttribute(att, m);
				
			}
			return classes;
		}
		
		
		private MyClass findCorrespondingClass(List<MyClass> classes, String className) {
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
		
		
		public void setReadWrittenData(List<MyClass> classes1, List<String> dbClassesNames, HashMap<String, ArrayList<String>> dbClassesForEachControlClass) {
			for(MyClass cl: classes1){
				if( !dbClassesNames.contains(cl.getName())){
					for (MethodDeclaration md: cl.getMethods().keySet()){
						for (MethodDeclaration called: cl.getMethods().get(md)){
							String calledClass= called.resolveBinding().getDeclaringClass().getQualifiedName();
							if(dbClassesNames.contains(calledClass)){
								
								ManipulatedData d= new ManipulatedData(calledClass,1);
								String calledMethdName=called.getName().toString();
								if (isWritingMethod(calledMethdName)){
									nbraccessData++;
									if(cl.getWrittenData().contains(d)){
										int indexdata= cl.getWrittenData().indexOf(d);
										cl.getWrittenData().get(indexdata).incrementFrequency(1);
									}else{
										cl.getWrittenData().add(d);
										if(cl.getReadData().contains(d)){
											int indexData= cl.getReadData().indexOf(d);
											nbraccessData=nbraccessData-cl.getReadData().get(indexData).getFrequency();
											cl.getReadData().remove(d);
											
										}
									}
								}else{
									
									if (isReadingMethod(calledMethdName)){
										
										if (!cl.getWrittenData().contains(d)){
											nbraccessData++;
											if (!cl.getReadData().contains(d)){
												cl.getReadData().add(d);
											}
											else{
												int indexData=cl.getReadData().indexOf(d);
												cl.getReadData().get(indexData).incrementFrequency(1);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			for (String clName: dbClassesForEachControlClass.keySet()){
		 		MyClass myclass= MeasuringMetrics.findCorrespondingClass(classes1, clName);
		 		if(myclass!=null){
		 			int i= classes1.indexOf(myclass);
		 			if (i!=-1){
		 				for(String d: dbClassesForEachControlClass.get(clName)){
		 					ManipulatedData data=new ManipulatedData(d, 1);
		 					if(!classes1.get(i).getWrittenData().contains(data) && 
		 							!classes1.get(i).getReadData().contains(data)){
		 						classes1.get(i).getWrittenData().add(data);
		 					}
		 				}
		 				
		 			}
		 			MeasuringMetrics.dataMatrix.put(myclass, dbClassesForEachControlClass.get(clName));
		 		}		
		 	}
		}
		
		
		public boolean isReadingMethod(String methodName) {
			boolean isReadingMethod=false;
			if(methodName.startsWith("get")|| methodName.startsWith("is")|| methodName.startsWith("contains")
					|| methodName.startsWith("toString")|| methodName.startsWith("compare")|| methodName.startsWith("equals")
					|| methodName.startsWith("hashCode"))
				isReadingMethod= true;
			return isReadingMethod;
		}

		
		private boolean isWritingMethod(String methodName) {
			boolean isWrittingMethod=false;
			if(methodName.startsWith("set")|| methodName.startsWith("add")|| methodName.startsWith("remove")
					|| methodName.startsWith("increment")|| methodName.startsWith("calculate")
					|| methodName.startsWith("create")|| methodName.startsWith("update")|| methodName.startsWith("persist")
					|| methodName.startsWith("init"))
				isWrittingMethod= true;
			return isWrittingMethod;
		}

		public static int getNbrAccesses() {
			return nbraccessData;
		}
		
		public List<String> setDBClassesNames(String projectName) {
			// TODO Auto-generated method stub
			List<String> dataList= new ArrayList<String>();
			if(projectName.equals("jpetstore-6-jpetstore-6.0.2")){	
				dataList.add("org.mybatis.jpetstore.domain.Account");
				dataList.add("org.mybatis.jpetstore.domain.Cart");
				dataList.add("org.mybatis.jpetstore.domain.CartItem");
				dataList.add("org.mybatis.jpetstore.domain.Category");
				dataList.add("org.mybatis.jpetstore.domain.Item");
				dataList.add("org.mybatis.jpetstore.domain.LineItem");
				dataList.add("org.mybatis.jpetstore.domain.Order");
				dataList.add("org.mybatis.jpetstore.domain.Product");
				dataList.add("org.mybatis.jpetstore.domain.Sequence");	
			}
			
			if (projectName.equals("inventory-management-system")){
				dataList.add("com.ca.db.model.ApplicationLog");
				dataList.add("com.ca.db.model.BranchOffice");
				dataList.add("com.ca.db.model.Category");
				dataList.add("com.ca.db.model.CategorySpecifications");
				dataList.add("com.ca.db.model.Department");
				dataList.add("com.ca.db.model.Employee");
				dataList.add("com.ca.db.model.Item");
				dataList.add("com.ca.db.model.ItemReturn");
				dataList.add("com.ca.db.model.LoginUser");
				dataList.add("com.ca.db.model.Nikasa");
				dataList.add("com.ca.db.model.NikasaRequest");
				dataList.add("com.ca.db.model.Person");
				dataList.add("com.ca.db.model.PurchaseOrder");
				dataList.add("com.ca.db.model.Role");
				dataList.add("com.ca.db.model.Specification");
				dataList.add("com.ca.db.model.SUBBCategory");
				dataList.add("com.ca.db.model.SubCategory");
				dataList.add("com.ca.db.model.UnitsString");
				dataList.add("com.ca.db.model.Vendor");	
			}
			
			if (projectName.equals("SpringBlog-master")){
				dataList.add("com.raysmond.blog.models.BaseModel");
				dataList.add("com.raysmond.blog.models.Post");
				dataList.add("com.raysmond.blog.models.Setting");
				dataList.add("com.raysmond.blog.models.Tag");
				dataList.add("com.raysmond.blog.models.User");			
			}
			
			if (projectName.equals("findsportmates")){
			//	dataList.add("com.findsportmates.model.Basketball");
				dataList.add("com.findsportmates.model.Event");
				dataList.add("com.findsportmates.model.User");
			//	dataList.add("com.findsportmates.model.Volleyball");
			//	dataList.add("com.findsportmates.model.EventFactory");
			}
			
			if (projectName.equals("SpringBlog")){
				dataList.add("com.raysmond.blog.models.BaseModel");
				dataList.add("com.raysmond.blog.models.Like");
				dataList.add("com.raysmond.blog.models.Post");
				dataList.add("com.raysmond.blog.models.SeoPostData");
				dataList.add("com.raysmond.blog.models.SeoRobotAgent");
				dataList.add("com.raysmond.blog.models.Setting");
				dataList.add("com.raysmond.blog.models.StoredFile");
				dataList.add("com.raysmond.blog.models.Tag");
				dataList.add("com.raysmond.blog.models.User");	
				dataList.add("com.raysmond.blog.models.Visit");	
				
			}
			return dataList;
		}

}
