/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.control.code.memorycompile;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

import cc.creativecomputing.io.CCNIOUtil;

public class CCInMemoryCompilerSourceCode{
	
	private class CCInMemoryFileObject extends SimpleJavaFileObject{

		protected CCInMemoryFileObject(String theClassName) {
			super(
				URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), 
				JavaFileObject.Kind.SOURCE
			);
		}
		
		@Override
    	public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
			_myLastUpdateTime = CCNIOUtil.lastModified(sourcePath);
			StringBuffer myBuffer = new StringBuffer();
			for(String myLine : CCNIOUtil.loadStrings(sourcePath)){
//				if(myLine.trim().startsWith("package ")){
				myLine = myLine.replace(_myPackageName, "recompile." + _myPackageName);
				
				myBuffer.append(myLine);
				myBuffer.append('\n');
			}
			return myBuffer.toString();
    	}
		
	}
	
	final String className;
	private final String _myPackageName;
	final Path sourcePath;
	
	private final JavaFileObject _myFileObject;
	
	private long _myLastUpdateTime = 0;
	
	public CCInMemoryCompilerSourceCode(Class<?> theClass){
		String myClassName = theClass.getName();
		_myPackageName = myClassName.substring(0, myClassName.lastIndexOf("."));
		String classSimpleName = myClassName.substring(myClassName.lastIndexOf(".") + 1);
		sourcePath = Paths.get("src/main/java", _myPackageName.split("\\.")).resolve(classSimpleName + ".java");
		className = "recompile." + myClassName;
		
		_myFileObject = new CCInMemoryFileObject(className);
	}
	
	public CCInMemoryCompilerSourceCode(Path theSourcePath){
		sourcePath = theSourcePath;
		_myPackageName = sourcePath.getParent().toString().replace("src/main/java/", "").replace("/", ".");
		className = "recompile." + sourcePath.getFileName().toString().replace(".java", "");

		_myFileObject = new CCInMemoryFileObject(className);
	}
	
	public JavaFileObject fileObject(){
		return _myFileObject;
	}
	
	public boolean needsUpdate(){
		return CCNIOUtil.lastModified(sourcePath) > _myLastUpdateTime;
	}
	
}
