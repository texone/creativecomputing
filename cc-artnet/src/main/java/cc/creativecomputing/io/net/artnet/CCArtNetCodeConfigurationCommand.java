package cc.creativecomputing.io.net.artnet;

public enum CCArtNetCodeConfigurationCommand {

	/**
	 * No action
	 */
	NONE(0x00),
	/**
	 * If Node is currently in merge mode, cancel merge mode upon receipt of
	 * next ArtDmx packet. See discussion of merge mode operation.
	 */
	CANCEL(0x01),
	/**
	 * The front panel indicators of the Node operate normally.
	 */
	LED_NORMAL(0x02),
	/**
	 * The front panel indicators of the Node are disabled and switched off.
	 */
	LED_MUTE(0x03),
	/**
	 * Rapid flashing of the Node’s front panel indicators. It is intended as an
	 * outlet locator for large installations.
	 */
	LED_LOCATE(0x04),
	/**
	 * Resets the Node’s Sip, Text, Test and data error flags. If an output
	 * short is being flagged, forces the test to re-run.
	 */
	RESET(0x05),
	/**
	 * Set DMX Port 0 to Merge in LTP mode.
	 */
	MERGE_LTP_O(0x10),
	/**
	 * Set DMX Port 1 to Merge in LTP mode.
	 */
	MERGE_LTP_1(0x11),
	/**
	 * Set DMX Port 2 to Merge in LTP mode.
	 */
	MERGE_LTP_2(0x12),
	/**
	 * Set DMX Port 3 to Merge in LTP mode.
	 */
	MERGE_LTP_3(0x13),
	/**
	 * Set DMX Port 0 to Merge in HTP (default) mode.
	 */
	MERGE_HTP_0(0x50),
	/**
	 * Set DMX Port 1 to Merge in HTP (default) mode.
	 */
	MERGE_HTP_1(0x51),
	/**
	 * Set DMX Port 2 to Merge in HTP (default) mode.
	 */
	MERGE_HTP_2(0x52),
	/**
	 * Set DMX Port 3 to Merge in HTP (default) mode.
	 */
	MERGE_HTP_3(0x53),
	/**
	 * Clear DMX Output buffer for Port 0
	 */
	CLR_0(0x93),
	/**
	 * Clear DMX Output buffer for Port 1
	 */
	CLR_1(0x93),
	/**
	 * Clear DMX Output buffer for Port 2
	 */
	CLR_2(0x93),
	/**
	 * Clear DMX Output buffer for Port 3
	 */
	CLR_3(0x93);

	public final int id;

	private CCArtNetCodeConfigurationCommand(int theID) {
		id = theID;
	}
}