package cc.creativecomputing.io.data.format;

import java.net.URL;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;

import cc.creativecomputing.io.CCIOFormat;
import cc.creativecomputing.io.data.CCDataObject;


public interface CCDataFormat<InputType> extends CCIOFormat<InputType, Map<String, Object>> {
	
	CCDataObject load(Path theDocumentPath, boolean theIgnoreLineFeed, OpenOption... theOptions);
	
	CCDataObject load(URL theDocumentPath, boolean theIgnoreLineFeed, String theUser, String theKey);
	
	CCDataObject parse(InputType theDocument);
	
	CCDataFormat<InputType> create();
}
