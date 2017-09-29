package cc.creativecomputing.io.data.format;

import java.net.URL;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;

import cc.creativecomputing.io.CCIOFormat;
import cc.creativecomputing.io.data.CCDataObject;


public interface CCDataFormat<InputType> extends CCIOFormat<InputType, Map<String, Object>> {
	
	public CCDataObject load(Path theDocumentPath, boolean theIgnoreLineFeed, OpenOption...theOptions);
	
	public CCDataObject load(URL theDocumentPath, boolean theIgnoreLineFeed, String theUser, String theKey);
	
	public CCDataObject parse(InputType theDocument);
	
	public CCDataFormat<InputType> create();
}
