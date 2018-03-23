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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCPropertyObject;
import cc.creativecomputing.core.util.CCReflectionUtil.CCDirectMember;
import cc.creativecomputing.io.CCNIOUtil;

public abstract class CCShaderObject {
	
	public static class CCShaderUniform{
		String _myUniformName;
		CCNumberPropertyHandle<Double>[] _myProperties;
		
		@SafeVarargs
		private CCShaderUniform(String theUniformName, CCNumberPropertyHandle<Double>...theProperties){
			_myUniformName = theUniformName;
			_myProperties = theProperties;
		}
		
		public String name(){
			return _myUniformName;
		}
		
		public CCNumberPropertyHandle<Double>[] properties(){
			return _myProperties;
		}
		
	}
	
	/**
	 * Takes the given files and merges them to one String. 
	 * This method is used to combine the different shader sources and get rid of the includes
	 * inside the shader files.
	 * @param thePaths
	 * @return
	 */
	public static String[] buildSource(final Path...thePaths) {
		String[] myBuffer = new String[thePaths.length];
		int i = 0;
		for(Path myPath:thePaths) {
			myBuffer[i] = CCNIOUtil.loadString(myPath);
		}
		
		return myBuffer;
	}
	

	public CCEventManager<CCShaderObject> compileEvents = new CCEventManager<>();
	public CCEventManager<CCShaderObject> errorEvents = new CCEventManager<>();
	
	private Path[] _myPaths;
	
	@CCProperty(name = "shaders", hide = true)
	private Map<String, CCShaderSource> _myFiles = new LinkedHashMap<>();
	@CCProperty(name = "save in file")
	private boolean _cSaveInFile = true;
	@CCProperty(name = "auto save")
	private boolean _cAutoSave = true;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@CCProperty(name = "uniforms", hide = true)
	private CCObjectPropertyHandle _myUniformHandles = new CCObjectPropertyHandle(new CCDirectMember( new CCPropertyObject("uniforms", 0, 0)));

	private Map<String,CCShaderUniform> _myUniforms = new HashMap<>();
	
	public CCShaderObject(Path[] theFiles){
		_myPaths = theFiles;
		String[] mySources = buildSource(theFiles);
		for(int i = 0; i < _myPaths.length;i++){
			_myFiles.put(_myPaths[i].getFileName().toString(), new CCShaderFile(this, _myPaths[i], mySources[i]));
		}
	}
	
	public CCShaderObject(String[] theSources){
		_myPaths = null;
		for(int i = 0; i < theSources.length;i++){
			_myFiles.put("source " + i, new CCShaderSource(this, theSources[i]));
		}
	}
	
	public Collection<CCShaderUniform> uniforms(){
		return _myUniforms.values();
	}
	

	private CCNumberPropertyHandle<Double> createHandle(String theName, double theMin, double theMax){
		CCNumberPropertyHandle<Double> myResult = new CCNumberPropertyHandle<Double>(
			_myUniformHandles, 
			new CCDirectMember<CCProperty>(new Double(0), new CCPropertyObject(theName, theMin, theMax)),
			CCPropertyMap.doubleConverter
		);
		_myUniformHandles.children().put(theName, myResult);
		return myResult;
	}
	
	private void readProperty(String thePropertyLine, String theUniformLine, Map<String,CCShaderUniform> theUniforms){
		try{
			String myName = null;
			double myMin = -1;
			double myMax = -1;
			int myStartIndex = thePropertyLine.indexOf("(");
			int myEndIndex =  thePropertyLine.indexOf(")");
			if(myStartIndex == -1 || myEndIndex == -1)return;
			String[] myParameters = thePropertyLine.substring(myStartIndex + 1, myEndIndex).split(",");
			for(String myParameter:myParameters){
				if(!myParameter.contains("="))continue;
				String[] myParamPair = myParameter.split("=");
				if(myParamPair.length < 2)continue;
				String myKey = myParamPair[0].trim();
				String myValue = myParamPair[1].trim();
				
				switch(myKey){
				case "name":
					myName = myValue.replace("\"", "");
					break;
				case "min":
					try{
						myMin = Double.parseDouble(myValue);
						break;
					}catch(Exception e){
						
					}
				case "max":
					try{
						myMax = Double.parseDouble(myValue);
						break;
					}catch(Exception e){
						
					}
					break;
				}
			}
			
			if(myName == null || myName.equals(""))return;
			
			String[] myUniformParts = theUniformLine.split(Pattern.quote(" "));
	
			if(myUniformParts.length < 3)return;
			
			String myType = myUniformParts[1];
			String myUniformName = myUniformParts[2].replace(";", "");
			String myKey = myType + ":" + myUniformName + ":" + myName;
			if(_myUniforms.containsKey(myKey)){
				theUniforms.put(myKey, _myUniforms.get(myKey));
				for(CCNumberPropertyHandle<Double> myUniform:_myUniforms.get(myKey)._myProperties){
					myUniform.minMax(myMin, myMax);
					_myUniformHandles.children().put(myUniform.name(), myUniform);
				}
				return;
			}
			switch(myType){
			case "float":
				theUniforms.put(
					myKey,
					new CCShaderUniform(
						myUniformName, 
						createHandle(myName, myMin, myMax)
					)
				);
				break;
			case "vec2":
				theUniforms.put(
					myKey,
					new CCShaderUniform(
						myUniformName, 
						createHandle(myName + ".x", myMin, myMax), 
						createHandle(myName + ".y", myMin, myMax)
					)
				);
				break;
			case "vec3":
				theUniforms.put(
					myKey,
					new CCShaderUniform(
						myUniformName, 
						createHandle(myName + ".x", myMin, myMax), 
						createHandle(myName + ".y", myMin, myMax), 
						createHandle(myName + ".z", myMin, myMax)
					)
				);
				break;
			case "vec4":
				theUniforms.put(
					myKey,
					new CCShaderUniform(
						myUniformName, 
						createHandle(myName + ".x", myMin, myMax), 
						createHandle(myName + ".y", myMin, myMax), 
						createHandle(myName + ".z", myMin, myMax), 
						createHandle(myName + ".w", myMin, myMax)
					)
				);
				break;
			}
		}catch(NumberFormatException nf){
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String preprocessSources(){
		Map<String,CCShaderUniform> myUniforms = new HashMap<>();
		_myUniformHandles.children().clear();
		
		String[] myCleanedSources = new String[sourceCode().size()];
		int mySourceID = 0;
		StringBuffer mySourceBuffer = new StringBuffer();
		for(CCShaderSource mySource:sourceCode().values()){
			String[] myLines = mySource.sourceCode().split(Pattern.quote("\n"));
			String myPropertyLine = null;
			
			
			for(int i = 0; i < myLines.length; i++){
				String myLine = myLines[i];
				myLine = myLine.trim();
				if(myLine.length() == 0){
					mySourceBuffer.append("\n");
					continue;
				}else if(myLine.startsWith("@CCProperty")){
					mySourceBuffer.append("\n");
					myPropertyLine = myLine;
				}else if(myLine.startsWith("@")){
					mySourceBuffer.append("\n");
					continue;
				}else if(myLine.startsWith("uniform")){
					mySourceBuffer.append(myLine + "\n");
					if(myPropertyLine != null)readProperty(myPropertyLine, myLine,myUniforms);
					myPropertyLine = null;
				}else{
					myPropertyLine = null;
					mySourceBuffer.append(myLine + "\n");
				}
			}
			
			myCleanedSources[mySourceID++] = mySource.toString();
		}
		_myUniforms = myUniforms;
		_myUniformHandles.forceChange();
		
		return mySourceBuffer.toString();
	}
	
	public boolean saveInFile(){
		return _cSaveInFile;
	}
	
	public boolean autoSave(){
		return _cAutoSave;
	}
	
	public abstract void update();
	
	public String[] keywords(){
		return new String[]{};
	}
	
	public Path[] templates(){
		return new Path[]{};
	}
	
	public Path[] paths(){
		return _myPaths;
	}
	
	public Map<String, CCShaderSource> sourceCode(){
		return _myFiles;
	}
	
	public abstract String errorLog();

	public String templateSource(Path thePath) {
		return "";
	}
}
