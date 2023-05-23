package codeAnalysis;

	
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

	public class Parser {
		
		public CompilationUnit parse(ICompilationUnit lwUnit) {
			ASTParser parser = ASTParser.newParser(AST.JLS4); 
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(lwUnit); // set source
			parser.setResolveBindings(true); // we need bindings later on
			return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
		}
}