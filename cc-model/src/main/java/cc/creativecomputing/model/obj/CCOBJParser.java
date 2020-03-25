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
import java.io.StreamTokenizer;
import java.nio.file.Files;
import java.nio.file.Path;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * http://local.wasp.uwa.edu.au/~pbourke/dataformats/obj/
 * 
 * @author texone
 * 
 */
public class CCOBJParser extends CCAbstractOBJParser {

	private final CCModel _myModel;
	private CCObject _myCurrentObject;
	private CCObjectGroup _myCurrentObjectGroup;
	private String _myCurrentMaterial;
	private CCSegment _myCurrentSegment;

	private Path _mySourcePath;

	public CCOBJParser(final Path thePath, final CCModel theModel) throws CCOBJParsingException, IOException {
		super(Files.newBufferedReader(thePath));

		_myModel = theModel;

		// creating the default material
		_myCurrentMaterial = "default";
		_myModel.materialMap().put(_myCurrentMaterial, new CCMaterial());

		_mySourcePath = thePath.getParent();

	}

	private void readVertex() throws CCOBJParsingException {
		if (_myCurrentObject == null) {
			// creating the default group
			_myCurrentObject = new CCObject("default");

			// adding default variables to the global data table
			_myModel.objectMap().put(_myCurrentObject.name(),_myCurrentObject);
			if(_myCurrentObjectGroup != null){
				_myCurrentObjectGroup.objects().add(_myCurrentObject);
			}
		}
		CCVector3 myVertex = new CCVector3(getFloat(), getFloat(), getFloat());
		_myModel.addVertex(myVertex);
		skipToNextLine();
	}

	/**
	 * readNormal
	 */
	private void readNormal() throws CCOBJParsingException {
		_myModel.normals().add(new CCVector3(getFloat(), getFloat(), getFloat()).normalizeLocal().multiply(0.2f));

		skipToNextLine();
	}

	/**
	 * readTexture
	 */
	private void readTexture() throws CCOBJParsingException {
		_myModel.textureCoords().add(new CCVector2(getFloat(), getFloat()));

		skipToNextLine();
	}

	/**
	 * readFace
	 * 
	 * Adds the indices of the current face to the arrays.
	 * 
	 * ViewPoint files can have up to three arrays: Vertex Positions, Texture
	 * Coordinates, and Vertex Normals. Each vertex can contain indices into all
	 * three arrays.
	 */
	private void readFace() throws CCOBJParsingException {
		if (_myCurrentObject == null) {
			// creating the default group
			_myCurrentObject = new CCObject("default");

			// adding default variables to the global data table
			_myModel.objectMap().put(_myCurrentObject.name(), _myCurrentObject);
			if(_myCurrentObjectGroup != null)_myCurrentObjectGroup.objects().add(_myCurrentObject);
		}
		if (_myCurrentSegment == null) {
			_myCurrentSegment = new CCSegment(_myModel);
			_myCurrentSegment.materialName(_myCurrentMaterial);
			_myCurrentObject.segments().add(_myCurrentSegment);
		}
		final CCFace myFace = new CCFace(_myModel);

		int vertIndex, texIndex = 0, normIndex = 0;

		// There are n vertices on each line. Each vertex is comprised
		// of 1-3 numbers separated by slashes ('/'). The slashes may
		// be omitted if there's only one number.

		getToken();

		while (ttype != StreamTokenizer.TT_EOL) {
			// First token is always a number (or EOL)
			pushBack();
			vertIndex = getInt() - 1;
			if (vertIndex < 0)
				vertIndex += _myModel.vertices().size() + 1;
			myFace.vertexIndices().add(vertIndex);

			// Next token is a slash, a number, or EOL. Continue on slash
			getToken();
			if (ttype == '/') {

				// If there's a number after the first slash, read it
				getToken();
				if (ttype == StreamTokenizer.TT_WORD) {
					// It's a number
					pushBack();
					texIndex = getInt() - 1;
					if (texIndex < 0)
						texIndex += _myModel.textureCoords().size() + 1;
					myFace.textureIndices().add(texIndex);
					getToken();
				}

				// Next token is a slash, a number, or EOL. Continue on slash
				if (ttype == '/') {

					// There has to be a number after the 2nd slash
					normIndex = getInt() - 1;
					if (normIndex < 0)
						normIndex += _myModel.normals().size() + 1;
					myFace.normalIndices().add(normIndex);
					getToken();
				}
			}
		}

		_myCurrentSegment.faces().add(myFace);
		_myModel.faces().add(myFace);

		// In case we exited early
		skipToNextLine();
	}

	/**
	 * readPartName
	 */
	private void readGroupName() throws CCOBJParsingException {
		String myGroupName = "default";

		getToken();

		StringBuilder myGroupNameBuilder = new StringBuilder();
		// New faces will be added to the curGroup
		while (ttype != StreamTokenizer.TT_EOL) {
			// if (ttype == OBJParser.TT_WORD)
			myGroupNameBuilder.append(sval);
			getToken();
		}

		if (myGroupNameBuilder.length() > 0)
			myGroupName = myGroupNameBuilder.toString();

		// See if this group has Material Properties yet
		if (_myModel.groupMap().get(myGroupName) == null) {
			// It doesn't - carry over from last group
			final CCObjectGroup myNewGroup = new CCObjectGroup(myGroupName);
			_myModel.groupMap().put(myGroupName, myNewGroup);
		}
		_myCurrentObjectGroup = _myModel.groupMap().get(myGroupName);

		_myCurrentObject = null;
		_myCurrentSegment = null;

		skipToNextLine();
	}

	/**
	 * readPartName
	 */
	private void readObjectName() throws CCOBJParsingException {
		String myObjectName = "default";

		getToken();

		StringBuilder myObjectNameBuilder = new StringBuilder();
		// New faces will be added to the curGroup
		while (ttype != StreamTokenizer.TT_EOL) {
			// if (ttype == OBJParser.TT_WORD)
			myObjectNameBuilder.append(sval);
			getToken();
		}

		if (myObjectNameBuilder.length() > 0)
			myObjectName = myObjectNameBuilder.toString();

		// See if this group has Material Properties yet
		if (_myModel.objectMap().get(myObjectName) == null) {
			// It doesn't - carry over from last group
			final CCObject myNewObject = new CCObject(myObjectName);
			if(_myCurrentObjectGroup != null)_myCurrentObjectGroup.objects().add(myNewObject);
			_myModel.objectMap().put(myObjectName, myNewObject);
		}
		_myCurrentObject = _myModel.objectMap().get(myObjectName);

		_myCurrentSegment = null;

		skipToNextLine();
	}

	/**
	 * readSmoothingGroup Implement smoothing groups
	 */
	private void readSmoothingGroup() throws CCOBJParsingException {
		getToken();
		if (ttype != CCOBJParser.TT_WORD) {
			skipToNextLine();
			return;
		}
		// if (st.sval.equals("off"))
		// curSgroup = "0";
		// else
		// curSgroup = st.sval;
		skipToNextLine();
	}

	/**
	 * loadMaterialFile
	 * 
	 * Both types of slashes are returned as tokens from our parser, so we go
	 * through the line token by token and keep just the last token on the line.
	 * This should be the filename without any directory info.
	 * @throws  
	 */
	private void loadMaterialFile() throws CCOBJParsingException {
		String s = null;

		// Filenames are case sensitive
		lowerCaseMode(false);

		// Get name of material file (skip path)
		do {
			getToken();
			if (ttype == CCOBJParser.TT_WORD)
				s = sval;
		} while (ttype != CCOBJParser.TT_EOL);

		try {
			new CCMTLParser(_mySourcePath.resolve(s), _myModel.materialMap()).readFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		lowerCaseMode(true);
		skipToNextLine();
	}

	/**
	 * readMaterialName
	 */
	private void readMaterialName() throws CCOBJParsingException {
		getToken();
		if (ttype == CCOBJParser.TT_WORD) {
			String myMaterial = sval;
			if (_myCurrentObject == null) {
				// creating the default group
				_myCurrentObject = new CCObject("default");

				// adding default variables to the global data table
				_myModel.objectMap().put(_myCurrentObject.name(),_myCurrentObject);
				if(_myCurrentObjectGroup != null){
					_myCurrentObjectGroup.objects().add(_myCurrentObject);
				}
			}
			_myCurrentSegment = new CCSegment(_myModel);
			_myCurrentSegment.materialName(myMaterial);
			_myCurrentMaterial = myMaterial;
			_myCurrentObject.segments().add(_myCurrentSegment);
		}
		skipToNextLine();
	}

	@Override
	public void readFile() {

		getToken();
		while (ttype != CCOBJParser.TT_EOF) {
			if (ttype == CCOBJParser.TT_WORD) {
				if (sval.equals("v")) {
					readVertex();
				} else if (sval.equals("vn")) {
					readNormal();
				} else if (sval.equals("vt")) {
					readTexture();
				} else if (sval.equals("f")) {
					readFace();
				} else if (sval.equals("fo")) { // Not sure what the dif is
					readFace();
				}  else if (sval.equals("g")) {
					readGroupName();
				} else if (sval.equals("o")) {
					readObjectName();
				}else if (sval.equals("s")) {
					readSmoothingGroup();
				} else if (sval.equals("p")) {
					skipToNextLine();
				} else if (sval.equals("l")) {
					skipToNextLine();
				} else if (sval.equals("mtllib")) {
					loadMaterialFile();
				} else if (sval.equals("usemtl")) {
					readMaterialName();
				} else if (sval.equals("maplib")) {
					skipToNextLine();
				} else if (sval.equals("usemap")) {
					skipToNextLine();
				} else {
					throw new CCOBJParsingException("Unrecognized token, line "
							+ lineno());
				}
			}

			skipToNextLine();

			// Get next token
			getToken();
		}
	}

}
