package cc.creativecomputing.io;

import java.nio.file.OpenOption;
import java.nio.file.Path;

public interface CCIOFormat <InputType, OutputType>{
	OutputType load(Path theDocumentPath, boolean theIgnoreLineFeed, OpenOption... theOptions);
	
	OutputType parse(InputType theDocument);
	
	void save(OutputType theObject, Path theDocumentUrl, OpenOption... theOptions);
	
	InputType toFormatType(OutputType theObject);

}
