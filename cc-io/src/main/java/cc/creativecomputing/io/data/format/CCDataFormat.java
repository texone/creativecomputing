package cc.creativecomputing.io.data.format;

import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;

import cc.creativecomputing.io.CCIOFormat;
import cc.creativecomputing.io.data.CCDataObject;


public interface CCDataFormat<InputType> extends CCIOFormat<InputType, Map<String, Object>> {
	
	public CCDataObject loadAsDataObject(Path theDocumentPath, boolean theIgnoreLineFeed, OpenOption...theOptions);
	
	public CCDataObject parseAsDataObject(InputType theDocument);
}
