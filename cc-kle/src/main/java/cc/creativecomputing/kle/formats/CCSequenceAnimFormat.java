package cc.creativecomputing.kle.formats;

import java.io.StringWriter;
import java.nio.file.Path;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;

public class CCSequenceAnimFormat implements CCSequenceFormat{
	
	private static final String  header = 
		"animVersion 1.1;\n"+
		"mayaVersion 2015;\n"+
		"timeUnit pal;\n"+
		"linearUnit cm;\n"+
		"angularUnit deg;\n"+
		"startTime 1;\n";

	private static final String objHeader = 
		"animData {\n"+
		"  input time;\n"+
		"  output linear;\n"+
		"  weighted 0;\n"+
		"  preInfinity constant;\n"+
		"  postInfinity constant;\n"+
		"  keys {\n";
	
	
	public CCSequenceAnimFormat(){
	}
	
	private void writeTranslate(int id, String dim, StringWriter writer, CCSequence data, float mult) {
		int d = 0;
		if (dim.equalsIgnoreCase("y"))
			d = 1;
		if (dim.equalsIgnoreCase("z"))
			d = 2;

		writer.write(
			"anim translate.translate" + dim.toUpperCase() + 
			" translate" + dim.toUpperCase() + 
			" elem" + id + " 0 1 " + d + ";\n"
		);
		writer.write(objHeader);
		for (int i = 0; i < data.length(); i++) {
			writer.write(
				"    " + (i * 5) + " " + 
				data.frame(i).data()[id][0][d] * mult + 
				" auto auto 1 1 0;\n"
			);
		}
		writer.write("  }\n}\n");
	}

	@Override
	public void save(Path thePath, CCSequenceMapping<?> theMapping, CCSequence theSequence) {
		try {
			StringWriter writer = new StringWriter();
			     				
			writer.write(header);
			writer.write("endTime "+theSequence.length()+";\n");
	
			for (int j = 0; j <  theSequence.frame(0).columns();j++) {
				writeTranslate(j, "x", writer, theSequence, 1);
				writeTranslate(j, "y", writer, theSequence, 1);
				writeTranslate(j, "z", writer, theSequence, -1);
			}
			writer.close();
			CCNIOUtil.saveString(thePath, writer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public CCSequence load(Path thePath, CCSequenceMapping<?> theMapping) {
		return null;
	}
	
	@Override
	public String extension() {
		return "anim";
	}
}
