/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.model.obj;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import cc.creativecomputing.io.CCParsingException;
import cc.creativecomputing.math.CCMath;


/**
 * <p>
 * An MTL file is an auxillary file containing definitions of materials that may be 
 * accessed by an OBJ file. The OBJ file must specify the name of the MTL file by a command such as
 * 
 * <blockquote><code>mltlib file_name</code></blockquote>
 * 
 * It is presumed that the MTL file names and defines various materials, such as, perhaps, 
 * "shinyred" or "iron". Then, within the OBJ file, the command
 * 
 * <blockquote><code>usemtl shinyred</code></blockquote>
 * indicates that all subsequence faces should be rendered with this material, until a new material is invoked.
 * An MTL file contains a sequence of definitions of materials. Each definition begins with a 
 * newmtl statement that defines the name of the material, followed by lines specifying particular properties.</p>
 * <h3>Example MTL File:</h3>
 * <pre>
 * newmtl shinyred
 * Ka  0.1986  0.0000  0.0000
 * Kd  0.5922  0.0166  0.0000
 * Ks  0.5974  0.2084  0.2084
 * illum 2
 * Ns 100.2237
 * </pre>
 * 
 * <h3>MTL File Characteristics:</h3>
 * 
 * <p>Comments begin with a '#' character in column 1. Blank lines may be inserted for clarity. 
 * Otherwise, the file consists of a sequence of newmtl statements, followed by a definition of various properties for that material.</p>
 * <p>The quantities that may be defined for a material include:
 * <ul>
 * <li>Ka r g b defines the ambient color of the material to be (r,g,b). The default is (0.2,0.2,0.2); </li>
 * <li>Kd r g b defines the diffuse color of the material to be (r,g,b). The default is (0.8,0.8,0.8); </li>
 * <li>Ks r g b defines the specular color of the material to be (r,g,b). This color shows up in highlights. The default is (1.0,1.0,1.0); </li>
 * <li>d alpha defines the transparency of the material to be alpha. The default is 1.0 (not transparent at all) Some formats use Tr instead of d; </li>
 * <li>Tr alpha defines the transparency of the material to be alpha. The default is 1.0 (not transparent at all). Some formats use d instead of Tr; </li>
 * <li>Ns s defines the shininess of the material to be s. The default is 0.0; </li>
 * <li>illum n denotes the illumination model used by the material. <br>
 * illum = 1 indicates a flat material with no specular highlights, so the value of Ks is not used. <br>
 * illum = 2 denotes the presence of specular highlights, and so a specification for Ks is required.</li> 
 * <li>map_Ka filename names a file containing a texture map, which should just be an ASCII dump of RGB values; </li>
    </p>
 * @author texone
 *
 */
class CCMTLParser extends CCAbstractOBJParser{
	
	private final Path _mySourcePath;
	private final HashMap<String,CCMaterial> _myMaterialMap;
	private CCMaterial _myCurrentMaterial;
	
	CCMTLParser(final Path thePath, final HashMap<String,CCMaterial> theMaterialMap)throws CCOBJParsingException, IOException{
		super(Files.newBufferedReader(thePath));
		_mySourcePath = thePath.getParent();
		_myMaterialMap = theMaterialMap;
	}
	
	/**
	 * Reads the Name of the Material, puts a new Material in the material map
	 * @throws CCParsingException
	 */
	private void readName() throws CCOBJParsingException {
		getToken();
		if (ttype == TT_WORD) {
			final String myMaterialName = sval;
			_myCurrentMaterial = new CCMaterial();
			_myMaterialMap.put(myMaterialName, _myCurrentMaterial);
		}
		skipToNextLine();
	}
	
	/**
	 * Reads ambient light settings from the material file
	 * @throws CCParsingException
	 */
	private void readAmbient() throws CCOBJParsingException {
		_myCurrentMaterial.ambient(getFloat(), getFloat(), getFloat());
		skipToNextLine();
	}
	
	/**
	 * Reads diffuse light settings from the material file
	 * @throws CCParsingException
	 */
	private void readDiffuse() throws CCOBJParsingException {
		_myCurrentMaterial.diffuse(getFloat(), getFloat(), getFloat());
		skipToNextLine();
	} 
	
	private void readSpecular() throws CCOBJParsingException {
		_myCurrentMaterial.specular(getFloat(), getFloat(), getFloat());
		skipToNextLine();
	}
	
	/**
	 * denotes the illumination model used by the material. 
	 * illum = 1 indicates a flat material with no specular highlights, so the value of Ks is not used. 
	 * illum = 2 denotes the presence of specular highlights, and so a specification for Ks is required.
	 * @throws CCParsingException
	 */
//	private void readIllum() throws CCOBJParsingException {
////		_myCurrentMaterial.illum = getInt();
//		skipToNextLine();
//	}
	
	private void readAlpha() throws CCOBJParsingException {
		_myCurrentMaterial.alpha(getFloat());
		skipToNextLine();
	}

	private void readShininess() throws CCOBJParsingException {
		_myCurrentMaterial.shininess((int)CCMath.constrain(getFloat(), 1, 128));

		skipToNextLine();
	}
	
	private void readMapKd()throws CCOBJParsingException {
		// Filenames are case sensitive
		lowerCaseMode(false);
		getToken();
		String texname = sval;
		_myCurrentMaterial.texture(_mySourcePath.resolve(texname));
		lowerCaseMode(true);
	}

	@Override
	public void readFile()throws CCOBJParsingException {
		getToken();
		while (ttype != TT_EOF) {
		    if (ttype == TT_WORD) {
			if (sval.equals("newmtl")) {
			    readName();
			} else if (sval.equals("ka")) {
			    readAmbient();
			} else if (sval.equals("kd")) {
			    readDiffuse();
			} else if (sval.equals("ks")) {
			    readSpecular();
			} else if (sval.equals("illum")) {
				//
				skipToNextLine();
			} else if (sval.equals("d")) {
			    readAlpha();
			} else if (sval.equals("ns")) {
			    readShininess();
			} else if (sval.equals("tf")) {
			    skipToNextLine();
			} else if (sval.equals("sharpness")) {
			    skipToNextLine();
			} else if (sval.equals("map_kd")) {
			    readMapKd();
			} else if (sval.equals("map_ka")) {
			    skipToNextLine();
			} else if (sval.equals("map_ks")) {
			    skipToNextLine();
			} else if (sval.equals("map_ns")) {
			    skipToNextLine();
			} else if (sval.equals("bump")) {
			    skipToNextLine();
			}
		    }
		    skipToNextLine();

		    // Get next token
		    getToken();
		}
	}
	
	
}
