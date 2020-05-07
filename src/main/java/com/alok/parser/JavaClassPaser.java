package com.alok.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;

public class JavaClassPaser {
	public static void parseJavaFile(String javaFile) throws IOException {
		System.out.println("Ram Ram");
		System.out.println("javaFile::"+javaFile);
		File file = new File(javaFile);
		if(!file.exists()) {
			System.out.println("File does not exist");
			return ;
		}
		
		ParseResult<CompilationUnit> parseResult = new JavaParser().parse(javaFile);
		CompilationUnit cu = parseResult.getResult().get();
		printMethodsAndTheirVariables(cu);
		
	}
	
	public static void printMethodsAndTheirVariables(CompilationUnit cu) {
		System.out.println("printMethodsAndTheirVariables");
		System.out.println("cu.getType::"+cu.getTypes());
		  for ( TypeDeclaration<?> typeDeclaration : cu.getTypes() ) {
              NodeList<BodyDeclaration<?>> members = typeDeclaration.getMembers();
              if (members != null) {
                  for ( BodyDeclaration<?> member : members ) {
                      if (member instanceof MethodDeclaration) {
                          MethodDeclaration method = (MethodDeclaration) member;
                          System.out.println("method::"+method);
                      }
                  }
              }
		  }
	}
	
	public static void printMethodsAndStartAndEndLine(CompilationUnit cu) {
	    for (TypeDeclaration typeDec : cu.getTypes()) {
	        List<BodyDeclaration> members = typeDec.getMembers();
	        if (members != null) {
	            for (BodyDeclaration member : members) {
	                if ( member instanceof MethodDeclaration){
	                    MethodDeclaration field = (MethodDeclaration) member;
	                    System.out.println("Method name: " + field.getName()+" "+field.getBegin()+": "+field.getEnd());	
	                }
	            }
	        }
	    }
	}
}
