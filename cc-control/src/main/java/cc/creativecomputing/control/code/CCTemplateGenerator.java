package cc.creativecomputing.control.code;

import java.lang.reflect.Method;

public class CCTemplateGenerator {
	private void appendImport(StringBuffer theBuffer, Class<?> theClass){
		if(theClass == null)return;
		if(theClass.isPrimitive())return;
		
		if(theClass.isArray()){
			theBuffer.append("import " + theClass.getComponentType().getName().replaceAll("\\$", ".") + ";\n");
		}else{
			theBuffer.append("import " + theClass.getName().replaceAll("\\$", ".") + ";\n");
		}
	}
	
	private void appendReturn(StringBuffer theBuffer, Class<?> theClass){
		if(theClass != null){
			if(!theClass.isPrimitive()){
				theBuffer.append("\t\treturn null;\n");
			}else if(theClass == Float.TYPE){
				theBuffer.append("\t\treturn 0f;\n");
			}else if(theClass == Double.TYPE){
				theBuffer.append("\t\treturn 0.0;\n");
			}else if(theClass == Integer.TYPE){
				theBuffer.append("\t\treturn 0;\n");
			}else if(theClass == Long.TYPE){
				theBuffer.append("\t\treturn 0;\n");
			}else if(theClass == Short.TYPE){
				theBuffer.append("\t\treturn 0;\n");
			}else if(theClass == Boolean.TYPE){
				theBuffer.append("\t\treturn false;\n");
			}
		}else{
			theBuffer.append("\t\t\n");
		}
	}
	public String codeTemplate(Class<?> theClass, String theClassPath){
		String _myPackage = theClassPath.substring(0, theClassPath.lastIndexOf("."));
		String _myClassName = theClassPath.substring(theClassPath.lastIndexOf(".") + 1);
		StringBuffer myTemplateBuffer = new StringBuffer();
		myTemplateBuffer.append("package " + _myPackage + ";\n");
		myTemplateBuffer.append("\n");
		
		StringBuffer myImportBuffer = new StringBuffer();
		myImportBuffer.append("import " + theClass.getName().replaceAll("\\$", ".") + ";\n");
		
		StringBuffer myCodeBuffer = new StringBuffer();
		if(theClass.isInterface()){
			myCodeBuffer.append("public class " + _myClassName + " implements " + theClass.getSimpleName() +"{");
			myCodeBuffer.append("\n");
			myCodeBuffer.append("\n");
			
			for(Method myMethod:theClass.getMethods()){
				
				myCodeBuffer.append("\tpublic ");
				if(myMethod.getReturnType() != null){
					myCodeBuffer.append(myMethod.getReturnType().getSimpleName());
				}else{
					myCodeBuffer.append("void");
				}
				myCodeBuffer.append(" " + myMethod.getName()+"(");
				for(Class<?> myClass:myMethod.getParameterTypes()){
					myCodeBuffer.append(myClass.getSimpleName() + " the" + myClass.getSimpleName()+", ");
					appendImport(myImportBuffer, theClass);
				}
				appendImport(myImportBuffer, myMethod.getReturnType());
				
				if(myMethod.getParameterTypes().length > 0)myCodeBuffer.delete(myCodeBuffer.length() - 2, myCodeBuffer.length());
				myCodeBuffer.append("){\n");
				
				appendReturn(myCodeBuffer, myMethod.getReturnType());
				
				myCodeBuffer.append("\t}\n");
				myCodeBuffer.append("\n");
			}
			myCodeBuffer.append("\n");
		}else{
			myCodeBuffer.append("public class " + _myClassName + " extends " + theClass.getSimpleName() +"{");
		}
		myCodeBuffer.append("");
		myCodeBuffer.append("}");
		
		myTemplateBuffer.append(myImportBuffer);

		myTemplateBuffer.append("\n");
		
		myTemplateBuffer.append(myCodeBuffer);
		
		return myTemplateBuffer.toString();
	}
}
