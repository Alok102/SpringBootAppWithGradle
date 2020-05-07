package com.alok.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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
	public static CompilationUnit parseJavaFile(String javaFile) throws IOException {
		System.out.println("javaFile::"+javaFile);
		File file = new File(javaFile);
		if(!file.exists()) {
			System.out.println("File does not exist");
			return null;
		}
		
		ParseResult<CompilationUnit> parseResult = new JavaParser().parse(file);
		CompilationUnit cu = parseResult.getResult().get();
		//printMethodsAndTheirVariables(cu);
		return cu;
		
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
	
	public static List<MethodDetails> printMethodsAndStartAndEndLine(CompilationUnit cu) {
		List<MethodDetails> methods = new ArrayList();
	    for (TypeDeclaration typeDec : cu.getTypes()) {
	        List<BodyDeclaration> members = typeDec.getMembers();
	        if (members != null) {
	            for (BodyDeclaration member : members) {
	                if ( member instanceof MethodDeclaration){
	                    MethodDeclaration field = (MethodDeclaration) member;
	                    MethodDetails method = new MethodDetails();
	                    method.setName(field.getName().asString());
	                    method.setStartLine(field.getBegin().get().line);
	                    method.setEndLine(field.getEnd().get().line);
	                    methods.add(method);
	                }
	            }
	        }
	    }
	    return methods;
	}
}
