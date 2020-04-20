package com.alok.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class JavaClassPaser {
	public static void parseJavaFile(String javaFile) {
		File file = new File(javaFile);
		if(!file.exists()) {
			System.out.println("File does not exist");
			return ;
		}
		try {
			CompilationUnit cu  = JavaParser.parse(file);
			printMethodsAndTheirVariables(cu);
			
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void printMethodsAndTheirVariables(CompilationUnit cu) {
	    for (TypeDeclaration typeDec : cu.getTypes()) {
	        List<BodyDeclaration> members = typeDec.getMembers();
	        if (members != null) {
	            for (BodyDeclaration member : members) {
	                if ( member instanceof MethodDeclaration){
	                    MethodDeclaration field = (MethodDeclaration) member;
	                    System.out.println("Method name: " + field.getName());	                    
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
	                    System.out.println("Method name: " + field.getName()+" "+field.getBeginLine()+": "+field.getEndLine());	
	                }
	            }
	        }
	    }
	}
}
