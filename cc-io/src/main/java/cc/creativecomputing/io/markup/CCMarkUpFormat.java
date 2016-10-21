package cc.creativecomputing.io.markup;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import cc.creativecomputing.io.CCIOFormat;
import cc.creativecomputing.io.CCNIOUtil;

public class CCMarkUpFormat implements CCIOFormat<String, CCMarkUpDocument>{

	@Override
	public CCMarkUpDocument load(Path theDocumentPath, boolean theIgnoreLineFeed, OpenOption... theOptions) {
		try {
			return new CCMarkUpParser().parse(Files.newInputStream(theDocumentPath, theOptions));
		} catch (Exception e) {
			throw new CCMarkUpException(e);
		}
	}

	@Override
	public CCMarkUpDocument parse(String theDocument) {
		try {
			return new CCMarkUpParser().parse(new StringReader(theDocument));
		} catch (Exception e) {
			throw new CCMarkUpException(e);
		}
	}

	@Override
	public void save(CCMarkUpDocument theObject, Path theDocumentUrl, OpenOption... theOptions) {
//		// TODO Auto-generated method stub
//		
	}

	@Override
	public String toFormatType(CCMarkUpDocument theObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		System.out.println(new CCMarkUpFormat().load(CCNIOUtil.dataPath("markup.txt"), true));
	}
}
