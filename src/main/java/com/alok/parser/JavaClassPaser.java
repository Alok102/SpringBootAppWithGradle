package com.alok.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

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
	
	 public static void getInheritanceInformation(String javaSourceCode) {
		 CompilationUnit compilationUnit = getCompilationUnit(javaSourceCode);
	        Optional<String> className = Optional.empty();
	        if(compilationUnit!=null) {
	            NodeList<TypeDeclaration<?>> types = compilationUnit.getTypes();
	            for (TypeDeclaration type : types) {
	                if (type instanceof ClassOrInterfaceDeclaration) {
	                    System.out.println("Type::"+((ClassOrInterfaceDeclaration) type).getExtendedTypes());
	                    System.out.println("Type::"+((ClassOrInterfaceDeclaration) type).getImplementedTypes());
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
	
	  /**
     * Helper method to get the compilationUnit(AST of class )
     *
     * @param javaSourceCode It is source code path
     * @return CompilationUnit
     */
    private static CompilationUnit getCompilationUnit(String javaSourceCode) {
        File javaFile = new File(javaSourceCode);
        CompilationUnit compilationUnit = null;
        if (javaFile.exists()) {
            try {
                ParseResult<CompilationUnit> parseResult = new JavaParser().parse(javaFile);
                if(parseResult!=null ){
                    if(parseResult.getResult()!=null){
                        if(parseResult.getResult().isPresent()) {
                            compilationUnit = parseResult.getResult().get();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Java File doe not exist");
        }
        return compilationUnit;
    }
    
    /**
     * This Method is used to get Interface implementation Information;
     * @param javaSourceCode
     */
    public static List<String> getInterfaceImplementationDetails(String javaSourceCode) {
        CompilationUnit compilationUnit = getCompilationUnit(javaSourceCode);
        Optional<String> className = Optional.empty();
        if(compilationUnit!=null) {
            NodeList<TypeDeclaration<?>> types = compilationUnit.getTypes();
            for (TypeDeclaration type : types) {
                if (type instanceof ClassOrInterfaceDeclaration) {
                    NodeList<ClassOrInterfaceType>implementedInterfaceList=((ClassOrInterfaceDeclaration) type).getImplementedTypes();
                    if(implementedInterfaceList!=null){
                        return implementedInterfaceList.stream()
                                .map((interfaceName)->(interfaceName.getName().asString()))
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        return null;
    }

    /**
     * This Method is used to get Interface implementation Information;
     * @param javaSourceCode
     */
    public static List<String> getClassExtendsDetails(String javaSourceCode) {
        CompilationUnit compilationUnit = getCompilationUnit(javaSourceCode);
        Optional<String> className = Optional.empty();
        if(compilationUnit!=null) {
            NodeList<TypeDeclaration<?>> types = compilationUnit.getTypes();
            for (TypeDeclaration type : types) {
                if (type instanceof ClassOrInterfaceDeclaration) {
                    NodeList<ClassOrInterfaceType>supperClassList=((ClassOrInterfaceDeclaration) type).getExtendedTypes();
                    if(supperClassList!=null){
                        return supperClassList.stream()
                                .map((superClass)->(superClass.getName().asString()))
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        return null;
    }
}
