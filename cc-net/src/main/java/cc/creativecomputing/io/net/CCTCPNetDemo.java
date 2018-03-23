package cc.creativecomputing.io.net;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.net.codec.CCNetDataObjectCodec;

public class CCTCPNetDemo {
	static int i = 0;
	
	public static void main(String[] args) {
		CCTCPServer<CCDataObject> myServer = new CCTCPServer<>(new CCNetDataObjectCodec());
		myServer.connect(new InetSocketAddress( "127.0.0.1", 12345));
		myServer.events.add(message ->  {
				CCLog.info("server receive:" + message.message);
				CCDataObject myObject = (CCDataObject)message.message;
				myObject.put("arg" + i , "client");
				i= i+1;
				myServer.send(myObject);
			}
		);
		
		CCTCPClient<CCDataObject> myClient = new CCTCPClient<>(new CCNetDataObjectCodec(), "127.0.0.1", 12345);
		myClient.connect("127.0.0.1", 12340);
		
		myClient.events().add(message ->  {
				CCDataObject myObject = (CCDataObject)message.message;
				CCLog.info(myObject.toString().getBytes().length + " client receive:" + message.message);
				myObject.put("arg" + i , "client");
				i= i+1;
				myClient.send(myObject);
			}
		);
		
		Timer timer = new Timer();
		

	    // Start in 2 Sekunden
	    timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				CCDataObject myObject = new CCDataObject();
				myObject.put("arg" + i , "client");
				i= i+1;
				myClient.send(myObject);
			}
		}, 500);
	    timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("keep allive");
			}
		}, 500, 1000);
	}
}
