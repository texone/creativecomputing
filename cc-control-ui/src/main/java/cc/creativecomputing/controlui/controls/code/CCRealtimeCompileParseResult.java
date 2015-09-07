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