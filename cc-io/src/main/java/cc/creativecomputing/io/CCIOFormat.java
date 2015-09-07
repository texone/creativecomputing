package cc.creativecomputing.io;

import java.nio.file.OpenOption;
import java.nio.file.Path;

public interface CCIOFormat <InputType, OutputType>{
	public OutputType load(Path theDocumentPath, boolean theIgnoreLineFeed, OpenOption...theOptions);
	
	public OutputType parse(InputType theDocument);
	
	public void save(OutputType theObject, Path theDocumentUrl, OpenOption...theOptions);
	
	public InputType toFormatType(OutputType theObject);

}
