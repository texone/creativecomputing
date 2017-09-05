package cc.creativecomputing.control.handles;

public enum CCPresetHandling {
	SELFCONTAINED("save selfcontained"),
	UPDATED("save with updated presets"),
	RESTORED("safe with restored presets");
	
	public final String desc;
	
	private CCPresetHandling(String theDesc) {
		desc = theDesc;
	}
}
