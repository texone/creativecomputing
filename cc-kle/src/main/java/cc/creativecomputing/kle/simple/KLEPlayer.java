package cc.creativecomputing.kle.simple;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector2;

public class KLEPlayer extends Player {

	CCVector2[][] fullData;
	CCVector2[] frameData;
	
	public KLEPlayer (String theFileName) {
		
		try {

			ZipIn zipin = new ZipIn();
			zipin.unzip(theFileName, "TMP");
			
			sculpture = new Sculpture(CCNIOUtil.dataPath("TMP/META-INF/sculpture.xml"));
			nFrames = getFiles("TMP/frames").size();
			nElements = sculpture.nElements;
			frameData = new CCVector2[nElements];
			fullData = new CCVector2[nFrames][nElements];
			
			for (int e=0; e<nElements; e++) {
				String r0 = "TMP/csv/c"+CCFormatUtil.nf(e, 3)+"_r000.csv";
				String r1 = "TMP/csv/c"+CCFormatUtil.nf(e, 3)+"_r001.csv";

				String[] ropeData0 = CCNIOUtil.loadString(Paths.get(r0)).split(Pattern.quote("\n"));
				String[] ropeData1 = CCNIOUtil.loadString(Paths.get(r1)).split(Pattern.quote("\n"));
				
				for (int f=0; f<nFrames; f++) {
					float x = Float.parseFloat(ropeData0[f].split(",")[1]);
					float y = Float.parseFloat(ropeData1[f].split(",")[1]);
					fullData[f][e] = new CCVector2(x,y);
				}	
			}
			
			Runtime.getRuntime().exec("rm -rf TMP");
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	static List<File> getFiles (String base) {
		File directory = new File(base);
		List<File> files = new ArrayList<File>();
		File[] list = directory.listFiles();
		for (File f: list) {
			if (!f.isDirectory()) files.add(f);
		}
		return files;
	}
	
	public String getViolation() {
		String rep = "";
		for (int e=0; e<sculpture.elements.size(); e++) {
			Element elem = sculpture.elements.get(e);
			if (elem.violate()) {
				rep += e+" "+elem.rope0.v0+" "+elem.rope1.v0+" "+elem.rope0.a0+" "+elem.rope1.a0+"\n";
			}
		}
		return rep;
	}
	
	boolean reverseOrder = true;
	
	@Override
	public CCVector2[] getPositions (float theFrame) {
		
		
		sculpture.setRopes(fullData[(int)theFrame], 0.2f, 0.001f);
		CCVector2[] data = new CCVector2[nElements];
		
		for (int i=0; i<nElements; i++) {
			
			int ii = i;
			if (reverseOrder) ii = nElements-1-i;
			data[ii] = new CCVector2 (sculpture.elements.get(i).getPosition().x, sculpture.elements.get(i).getPosition().y) ;
		}
		return data;
	}
	
	@Override
	public CCVector2[] getRopes (float theFrame) {
		for (int e=0; e<nElements; e++) {
			
			/*
			String r0 = "TMP/csv/c"+CCFormatUtil.nf(e, 3)+"_r000.csv";
			String r1 = "TMP/csv/c"+CCFormatUtil.nf(e, 3)+"_r001.csv";
				
			String[] data0 = CCNIOUtil.loadString(Paths.get(r0)).split(Pattern.quote("\n"));
			String[] data1 = CCNIOUtil.loadString(Paths.get(r1)).split(Pattern.quote("\n"));

			float x = Float.parseFloat(data0[theFrame].split(",")[1]);
			float y = Float.parseFloat(data1[theFrame].split(",")[1]);
			*/
			
			return fullData [(int)theFrame];
		}

		return frameData;
	}
}
