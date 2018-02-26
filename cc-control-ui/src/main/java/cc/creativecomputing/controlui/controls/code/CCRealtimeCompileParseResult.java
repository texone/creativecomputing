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

import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;

public class CCRealtimeCompileParseResult implements ParseResult{
	
	private final Parser _myParser;
	private final DiagnosticCollector<JavaFileObject> _myDiagnostics;
	
	public CCRealtimeCompileParseResult(Parser theParser, DiagnosticCollector<JavaFileObject> theDiagnostics){
		_myParser = theParser;
		_myDiagnostics = theDiagnostics;
	}

	@Override
	public Exception getError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFirstLineParsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLastLineParsed() {
		// TODO Auto-generated method stub
		return 3000;
	}

	@Override
	public List<ParserNotice> getNotices() {
		List<ParserNotice> myResult = new ArrayList<ParserNotice>();
		for(Diagnostic<?> myDiagnostic:_myDiagnostics.getDiagnostics()){
			myResult.add(new CCRealtimeCompileParseNotice(_myParser, myDiagnostic));
		}
		return myResult;
	}

	@Override
	public long getParseTime() {
		return System.currentTimeMillis();
	}

	@Override
	public Parser getParser() {
		return _myParser;
	}
	
}
