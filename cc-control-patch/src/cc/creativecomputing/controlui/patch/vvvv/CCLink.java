package cc.creativecomputing.controlui.patch.vvvv;

import cc.creativecomputing.io.data.CCDataObject;

public class CCLink<Type>{

	public CCPin<Type> fromPin;
	public CCPin<Type> toPin;

	/**
	 * @class
	 * @constructor
	 * @param fromPin the output pin which is the source of the connection
	 * @param toPin the input pin which is the destination of the connection
	 */
	public CCLink(CCPin<Type> theFromPin, CCPin<Type> theToPin) {
		fromPin = theFromPin;
		toPin = theToPin;

		fromPin.links.add(this);
		toPin.links.add(this);
	}

	/**
	 * deletes resources associated with a link
	 */
	public void destroy() {
		fromPin.links.remove(this);
		toPin.links.remove(this);
		fromPin.node.parentPatch.linkList.remove(this);

		toPin.disconnect();
		if (toPin.reset_on_disconnect)
			toPin.reset();
		// else {
		// var cmd = {syncmode: 'diff', nodes: {}, links: []};
		// var pincmd = {}
		// pincmd[toPin.pinname] = {values: []};
		// cmd.nodes[toPin.node.id] = {pins: pincmd}
		// int i = toPin.getSliceCount();
		// while (i--) {
		// pincmd[toPin.pinname].values[i] = toPin.values[i].toString();
		// }
		// if (fromPin.node.parentPatch.editor &&
		// !fromPin.node.parentPatch.disposing)
		// fromPin.node.parentPatch.editor.update(fromPin.node.parentPatch,
		// cmd);
		// }
	}

	/**
	 * Returns the XML string representing the link. Used for saving the patch
	 * and copying to clipboard
	 */
	public CCDataObject data() {
		// calling it LONK instead of LINK here, because jquery does not make a
		// closing tag for LINK elements
		// renaming it to LINK later ...
		CCDataObject myResult = new CCDataObject();
		myResult.put("srcnodeid", this.fromPin.node.id);
		myResult.put("srcpinname", this.fromPin.pinname);
		myResult.put("dstnodeid", this.toPin.node.id);
		myResult.put("dstpinname", this.toPin.pinname);
		return myResult;
	}

	// this.toJSON() {
	// return {srcnodeid: this.fromPin.node.id, srcpinname:
	// this.fromPin.pinname, dstnodeid: this.toPin.node.id, dstpinname:
	// this.toPin.pinname};
	// }

}
