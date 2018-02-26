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
package cc.creativecomputing.controlui.controls.code;

import java.awt.Color;

import javax.tools.Diagnostic;

import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;

public class CCRealtimeCompileParseNotice implements ParserNotice{
	
	private final Parser _myParser;
	private final Diagnostic<?> _myDiagnostic;
	
	public CCRealtimeCompileParseNotice(Parser theParser, Diagnostic<?> theDiagnostic){
		_myParser = theParser;
		_myDiagnostic = theDiagnostic;
	}

	@Override
	public int compareTo(ParserNotice theO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean containsPosition(int thePosition) {
		return thePosition >= _myDiagnostic.getStartPosition() && thePosition <= _myDiagnostic.getEndPosition();
	}

	@Override
	public Color getColor() {
		switch(_myDiagnostic.getKind()){
		case ERROR:
			return Color.RED;
		case WARNING:
		case MANDATORY_WARNING:
			return Color.YELLOW;
		case NOTE:
		case OTHER:
			return Color.BLUE;
		}
		return Color.BLUE;
	}

	@Override
	public boolean getKnowsOffsetAndLength() {
		return true;
	}

	@Override
	public int getLength() {
		return (int)(_myDiagnostic.getEndPosition() - _myDiagnostic.getStartPosition());
	}

	@Override
	public Level getLevel() {
		switch(_myDiagnostic.getKind()){
		case ERROR:
			return Level.ERROR;
		case WARNING:
		case MANDATORY_WARNING:
			return Level.WARNING;
		case NOTE:
		case OTHER:
			return Level.INFO;
		}
		return Level.INFO;
	}

	@Override
	public int getLine() {
		return (int)_myDiagnostic.getLineNumber();
	}

	@Override
	public String getMessage() {
		return _myDiagnostic.getMessage(null);
	}

	@Override
	public int getOffset() {
		return (int)_myDiagnostic.getStartPosition();
	}

	@Override
	public Parser getParser() {
		return _myParser;
	}

	@Override
	public boolean getShowInEditor() {
		return true;
	}

	@Override
	public String getToolTipText() {
		return "ERROR";
	}
	
}
