package cc.creativecomputing.io.net.codec.osc;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import cc.creativecomputing.io.netty.codec.osc.CCOSCCodec;
import cc.creativecomputing.io.netty.codec.osc.CCOSCEncoder;
import cc.creativecomputing.io.netty.codec.osc.CCOSCMessage;
import cc.creativecomputing.io.netty.codec.osc.CCOSCDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CCOSCEncodeDecode {

	@Test
	public void encodeDecodeMessage(){
		CCOSCMessage myMessage = new CCOSCMessage(
			"/test/addressPattern", 
			0d, 
			"Y=Y=Y0", 
			true, 
			0,
			0f
		);
		
		CCOSCEncoder myEncode = new CCOSCEncoder();
		ByteBuf myBuf = Unpooled.buffer();
		myEncode.encode(myMessage, myBuf);
		
		List<Object> myResult = new ArrayList<>();
		CCOSCDecoder myDecoder = new CCOSCDecoder();
		myDecoder.decode(null, myBuf, myResult);
		
		CCOSCMessage myMessageOut = (CCOSCMessage)myResult.get(0);
		Assert.assertEquals(myMessage.address(), myMessageOut.address());
		Assert.assertEquals(myMessage.doubleArgument(0), myMessageOut.doubleArgument(0), 0);
		Assert.assertEquals(myMessage.stringArgument(1), myMessageOut.stringArgument(1));
		Assert.assertEquals(myMessage.booleanArgument(2), myMessageOut.booleanArgument(2));
		Assert.assertEquals(myMessage.intArgument(3), myMessageOut.intArgument(3), 0);
	}
	
	@Test
	public void encodeDecodeBundle(){
		
	}
}
