package cc.creativecomputing.kle.simple;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.image.format.CCPNGImage;
import cc.creativecomputing.io.CCFileOutputChannel;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;


public class Export {

	double[][][] ropeData;
	double[][][] posData;
	
	int nFrames = 0;
	int nElements = 0;
	
	double maxRope = 0f;
	double minRope = Float.MAX_VALUE;
	
	
	
	public Export (int theNElements, int theNFrames) {
		nFrames = theNFrames;
		nElements = theNElements;
		posData = new double[nElements][nFrames][2];
		ropeData = new double[nElements][nFrames][2];
	}
	
	public void saveC4D (String path) {
		
		int ceil = 1400;
		
		for (int e=0; e<nElements; e++) {
			String stringData = "";
			
			for (int f=0; f<nFrames; f++) {
				stringData += posData[e][f][0]+" "+(ceil+posData[e][f][1])+" 0.0 0.0 0.0\n";
			}
			CCNIOUtil.saveString(Paths.get(path+"/e"+CCFormatUtil.nf(e, 3)+".txt"), stringData);		
		}
	}
	
	public void saveCsv (Path file) {
		try{
			CCNIOUtil.createDirectories(file);
			for (int e = 0; e<nElements; e++) {
				for (int r=0; r<2; r++) {
					PrintWriter writer = new PrintWriter(new File(file + "/c" + CCFormatUtil.nf(e, 3) + "_r" + CCFormatUtil.nf(r, 3) + ".csv"));
					
					for (int f=0; f<nFrames; f++) {
						double dat = ropeData[e][f][r];
						double data16bit = (dat - minRope) / (maxRope - minRope);
						data16bit = CCMath.floor(data16bit * (CCMath.pow(2, 16) - 1));
						writer.write(f + "," + dat*10 + "\n");
					}
					writer.close();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void savePng (Path file) {

		CCNIOUtil.createDirectories(file);
		for (int f=0; f<nFrames; f++) {
			CCPNGImage img = new CCPNGImage (nElements, 2, 16, true, false);
			
			for (int e=0; e<nElements; e++) {
				for (int r=0; r<2; r++) {
				    
					double dat = ropeData[e][f][r];
					double data16bit = (dat - minRope) / (maxRope - minRope);
					data16bit = CCMath.floor(data16bit * (CCMath.pow(2, 16) - 1));
					data16bit /= CCMath.pow(2, 16);
					try {
						img.pixel(e, r, data16bit);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
					String numString = CCFormatUtil.nf(f, 5);
					img.write(Paths.get(file +"/frame_" +numString+ ".png"));
				}
			}
		}
	}
	
	public void saveBin (String file) {
		CCFileOutputChannel fileChannel = new CCFileOutputChannel(Paths.get(file));

		for (int f=0; f<nFrames; f++) {
			for (int x=0; x<nElements; x++) {
				for (int y=0; y<2; y++) {
					fileChannel.write(ropeData[x][f][y]);
				}
			}
		}
	}
	
	public void saveMeta (Path file, List<Element> elements) {
		CCNIOUtil.createDirectories(file);
		CCXMLElement e = new CCXMLElement("mapping");
		for (int i=0; i<nElements; i++) {
			for (int r=0; r<2; r++) {
				CCXMLElement c = new CCXMLElement("channel");
				c.addAttribute("id", i*2+r);
				c.addAttribute("column", i);
				c.addAttribute("row", r);
				c.addAttribute("min", minRope*10);
				c.addAttribute("max", maxRope*10);
				
				e.addChild(c);
			}
		}
		CCXMLIO.saveXMLElement(e, file.resolve("setup.xml"));
		

		CCXMLElement sculpture = new CCXMLElement("sculpture");
		CCXMLElement elementsXML = new CCXMLElement("elements");
		
		for (int i=0; i<nElements; i++) {
			CCXMLElement elem = new CCXMLElement("element");
			elem.addAttribute("id", i);
			CCXMLElement motors = new CCXMLElement("motors");
			
			CCXMLElement m = new CCXMLElement("motor");
			m.addAttribute("id", i*2);
			m.addAttribute("x", elements.get(i).ceil0.x*10);
			m.addAttribute("y", elements.get(i).ceil0.y*10);
			m.addAttribute("z", elements.get(i).ceil0.z*10);
			motors.addChild(m);
			
			m = new CCXMLElement("motor");
			m.addAttribute("id", i*2+1);
			m.addAttribute("x", elements.get(i).ceil1.x*10);
			m.addAttribute("y", elements.get(i).ceil1.y*10);
			m.addAttribute("z", elements.get(i).ceil1.z*10);
			motors.addChild(m);
			
			
			
			CCXMLElement bounds = new CCXMLElement("bounds");
			
			for (CCVector2 point: elements.get(i).bounds) {
				CCXMLElement p = new CCXMLElement("point");
				p.addAttribute("x", point.x*10);
				p.addAttribute("y", point.y*(-10));
				bounds.addChild(p);
			}
			elem.addChild(motors);
			elem.addChild(bounds);	
			elementsXML.addChild(elem);
		}
		
		sculpture.addChild(elementsXML);
		CCXMLIO.saveXMLElement(sculpture, file.resolve("sculpture.xml"));
	}
	
	// TODO: individual bounds for each element
	public void saveAll (Path file, List<Element> elements) {
		CCLog.info(file);
		saveCsv(file.resolve("csv"));
		savePng(file.resolve("frames"));
//		saveBin("raw.bin");
		CCVector2 b0 = elements.get(0).bounds.get(0);
		CCVector2 b1 = elements.get(0).bounds.get(1);
		CCVector2 b2 = elements.get(0).bounds.get(2);
		CCVector2 b3 = elements.get(0).bounds.get(3);
		saveMeta(file.resolve("META-INF"), elements);
		
		Zip zip = new Zip();
		zip.generateFileList(new File("csv"));
		zip.generateFileList(new File("frames"));
		zip.generateFileList(new File("raw.bin"));
		zip.generateFileList(new File("META-INF"));
    	zip.zipIt(file+".kle");
    	
    	try {
    		Runtime.getRuntime().exec("rm -rf csv");
    		Runtime.getRuntime().exec("rm -rf frames");
    		Runtime.getRuntime().exec("rm -rf META-INF");
    		Runtime.getRuntime().exec("rm -rf raw.bin");
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
	public void recPos (int theElemId, int cnt, Element theElement) {
		
		double w = CCMath.abs(theElement.ceil0.x - theElement.ceil1.x);
		CCVector2 ropes = pos2Rope(w, theElement.getPosition().x, theElement.getPosition().y);
		
		if (ropes.x>maxRope) maxRope = ropes.x;
		if (ropes.y>maxRope) maxRope = ropes.y;
		if (ropes.x<minRope) minRope = ropes.x;
		if (ropes.y<minRope) minRope = ropes.y;
		
		ropeData[theElemId][cnt][0] = ropes.x;
		ropeData[theElemId][cnt][1] = ropes.y;

		posData[theElemId][cnt][0] = theElement.pos.x;
		posData[theElemId][cnt][1] = theElement.pos.y;
	}
	
	public static CCVector2 pos2Rope (double w, double x, double y) {
		
		x += w/2;
		CCVector2 ret = new CCVector2();
		ret.x = CCMath.sqrt(x*x + y*y);
		ret.y = CCMath.sqrt((w-x)*(w-x) + y*y);
		
		return ret;
	}
}
