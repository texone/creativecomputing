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
package cc.creativecomputing.control.code;

import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

public class CCShaderFile extends CCShaderSource{

	private Path _myFile;
	private long _myLastModified;
	
	public CCShaderFile(CCShaderObject theShaderObject, Path theFile){
		super(theShaderObject);
		_myFile = theFile;
		_myLastModified = _myFile.toFile().lastModified();
	}
	
	public void save(){
		Path myFixedPath = Paths.get("/");
		
		for(Path myName:_myFile){
			if(myName.toString().equals("target"))continue;
			if(myName.toString().equals("classes")){
				myFixedPath = myFixedPath.resolve("src/main/java");
				continue;
			}
			myFixedPath = myFixedPath.resolve(myName);
			
		}
		CCNIOUtil.saveString(myFixedPath, sourceCode());
	}
	
	@Override
	public boolean isUpdated() {
		return _myFile.toFile().lastModified() > _myLastModified;
	}
	
	@Override
	public String sourceCode() {
		_myLastModified = _myFile.toFile().lastModified();
		return CCNIOUtil.loadString(_myFile);
	}
	
	public static void main(String[] args) {
		Path myPath = CCNIOUtil.classPath(CCShaderFile.class, "");
		Path myFixedPath = Paths.get("/");
		for(Path myName:myPath){
			if(myName.toString().equals("target"))continue;
			if(myName.toString().contains("classes")){
				myFixedPath = myFixedPath.resolve("src/main/java");
				continue;
			}
			myFixedPath = myFixedPath.resolve(myName);
			
		}
		
		CCLog.info(myPath);
		CCLog.info(myFixedPath);

		CCNIOUtil.saveString(myFixedPath.resolve("bla.txt"), "bla");
	}
}
