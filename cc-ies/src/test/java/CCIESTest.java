import org.junit.Assert;
import org.junit.Test;

import cc.creativecomputing.ies.CCIESData;
import cc.creativecomputing.ies.CCIESDataFormat;
import cc.creativecomputing.ies.CCIESMeasurementUnits;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.ies.CCIES;

public class CCIESTest {
	@Test
	public void read(){
		CCIESData myData = CCIES.read(CCNIOUtil.dataPath("TEST.IES"));
		
		Assert.assertEquals(myData.format(), CCIESDataFormat.IESNA_95);
		Assert.assertEquals(myData.units(), CCIESMeasurementUnits.FEET);
		
		Assert.assertEquals(myData.dimensions().width(), 0.5, 0.000001);
		Assert.assertEquals(myData.dimensions().length(), 0.6, 0.000001);
		Assert.assertEquals(myData.dimensions().height(), 0., 0.000001);
		
		Assert.assertEquals(myData.electricalData().ballastFactor(), 1.0, 0.000001);
		Assert.assertEquals(myData.electricalData().blpFactor(), 1.0, 0.000001);
		Assert.assertEquals(myData.electricalData().inputWatts(), 495, 0.000001);
		
		Assert.assertArrayEquals(myData.photometricalData().verticalAngles(), new double[]{0, 22.5f, 45, 67.5f, 90}, 0.000001f);
		Assert.assertArrayEquals(myData.photometricalData().horizontalAngles(), new double[]{0, 45, 90}, 0.000001f);
		Assert.assertArrayEquals(myData.photometricalData().candelaValues()[0], new double[]{10000, 50000, 25000, 10000, 5000}, 0.000001f);
		Assert.assertArrayEquals(myData.photometricalData().candelaValues()[1], new double[]{10000, 35000, 16000, 8000, 3000}, 0.000001f);
		Assert.assertArrayEquals(myData.photometricalData().candelaValues()[2], new double[]{10000, 20000, 10000, 5000, 1000}, 0.000001f);
	}
}
