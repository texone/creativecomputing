package cc.creativecomputing.app.modules;


public interface CCConsoleLineListener {

	void start(CCConsoleLineReaderModule theModule);
	void onLine(String theLine);
}
