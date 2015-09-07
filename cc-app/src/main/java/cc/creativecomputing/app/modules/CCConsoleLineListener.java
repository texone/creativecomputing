package cc.creativecomputing.app.modules;


public interface CCConsoleLineListener {

	public void start (CCConsoleLineReaderModule theModule);
	public void onLine(String theLine);
}
