package cc.creativecomputing.controlui.patch.vvvv;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.events.CCListenerManager;

public class CCPin<Type> {

	public static enum CCPinDirection {
		Input, Output, Configuration
	}

	public static enum CCPinType {
		
		/**
		 * The default pin type, used if no further specified
		 * @memberof VVVV.PinTypes
		 * @mixin
		 * @property {String} typeName Generic
		 * @property {Boolean} reset_on_disconnect true
		 * @property {String} defaultValue '0'
		 */
		Generic (true,"0", false),
	
		
		/**
		 * Value Pin Type
		 * 
		 * @memberof VVVV.PinTypes
		 * @mixin
		 * @property {String} typeName Value
		 * @property {Boolean} reset_on_disconnect false
		 * @property {String} defaultValue 0
		 * @property {Boolean} primitive true
		 */
		Value(false, 0, true),
		

		/**
		 * Value Pin Type
		 * 
		 * @memberof VVVV.PinTypes
		 * @mixin
		 * @property {String} typeName Value
		 * @property {Boolean} reset_on_disconnect false
		 * @property {String} defaultValue 0
		 * @property {Boolean} primitive true
		 */
		Color(false, 0, true),

		/**
		 * String Pin Type
		 * 
		 * @memberof VVVV.PinTypes
		 * @mixin
		 * @property {String} typeName String
		 * @property {Boolean} reset_on_disconnect false
		 * @property {String} defaultValue ''
		 * @property {Boolean} primitive true
		 */
		String(false, "", true),

		/**
		 * Enum Pin Type
		 * 
		 * @memberof VVVV.PinTypes
		 * @mixin
		 * @property {String} typeName Enum
		 * @property {Boolean} reset_on_disconnect false
		 * @property {String} defaultValue ''
		 * @property {Boolean} primitive true
		 */
		Enum(false, "", true);

		private CCPinType(boolean theResetOnDisConnect, Object theDefaultValue, boolean theIsPrimitive) {
			reset_on_disconnect = theResetOnDisConnect;
			defaultValue = theDefaultValue;
			primitive = theIsPrimitive;
		}

		public boolean reset_on_disconnect;
		public Object defaultValue;
		public boolean primitive;
	}

	public static interface CCConnectionChanged {
		public void connectionChanged(CCPin thePin);
	}

	/** @member */
	public boolean active = false;
	public boolean reset_on_disconnect = false;
	/**
	 * if the pin is a subpatch's input pin, the slavePin is the corresponding
	 * IOBox input pin INSIDE the subpatch
	 */
	public CCPin slavePin = null;
	/**
	 * if the pin is a subpatch's output pin, the masterPin is the corresponding
	 * IOBox output pin INSIDE the subpatch
	 */
	public CCPin masterPin = null;
	/**
	 * contains a row of named callback functions, each fired if the pin's
	 * connection has changed
	 */
	public CCListenerManager<CCConnectionChanged> connectionChangedHandlers = CCListenerManager.create(CCConnectionChanged.class);
	// /** contains the options used if the pin is of type {@link
	// VVVV.PinTypes.Enum} */
	// this.enumOptions = [];

	public boolean auto_reset = false;

	public String pinname;

	public CCPinType typeName;

	/** @member */
	public List<CCLink<?>> links = new ArrayList<>();

	/** @member */
	public CCNode node;

	/** @member */
	public List<Type> values;
	public int valuesChangedAt = 0;
	
	public boolean unvalidated = false;

	CCPinDirection direction;

	/**
	 * @class
	 * @constructor
	 * @param {String} pinname Pin Name
	 * @param {String} direction see {@link CCPinDirection}
	 * @param {Array} init_values the array of initial values
	 * @param {VVVV.Core.Node} node the node this pin is attached to
	 * @param {Object} [type] the PinType, default is
	 *            {@link VVVV.PinTypes.Generic), see {@link VVVV.PinTypes}
	 */
	public CCPin(String thePinname, CCPinDirection theDirection, Type[] init_values, CCNode theNode, CCPinType theType) {
		/** @member */
		pinname = thePinname;
		/** see {@link CCPinDirection} */
		direction = theDirection;
		node = theNode;
		typeName = theType;
		
		 if (theType==null)
			 theType = CCPinType.Generic;
		 if (theType == CCPinType.Generic)
		      this.unvalidated = true;
		    setType(theType);

		    if (init_values != null && init_values.length > 0) { // override PinType's default value with values from constructor, if it isn't []
		    
		      for(int i = 0; i< init_values.length;i++){
		        this.setValue(i, init_values[i]);
		      }
		    }

	}

	/**
	 * retrieves pin's slices
	 * 
	 * @param i the slice/bin number
	 * @param [binSize] the bin size, default is 1
	 * @return if binSize is 1, the value of the slice is returned; if binSize
	 *         is > 1, an array with the slice values is returned
	 */
	public List<Type> getValue(int i, int binSize) {
		return values.subList(i, i + binSize);
	}

	public Type getValue(int i) {
		return values.get(i % values.size());
	}

	/**
	 * set a pin's slice value; if an output pin, it also sets the values of
	 * connected input pins. If the pin is a subpatch input pin, it also sets
	 * the slavePin inside the subpatch
	 * 
	 * @param {Integer} i the slice number
	 * @param v the value to set
	 * @param {Boolean} [stopPropagation] default is false; if true, the
	 *            function does not update slavePins to avoid infinite loops;
	 *            this parameter should not be used in node implementations
	 */
	public void setValue(int i, Type v) {
		if (direction == CCPinDirection.Output || !isConnected()) {
			if (typeName == null || !typeName.primitive || values.get(i) != v || this.typeName == CCPinType.Color)
				markPinAsChanged();
			values.set(i, v);
		}

		if (this.node.isIOBox && this.pinname.equals("Descriptive Name")	&& this.node.invisiblePins.get("Descriptive Name") != null) {
			if (this.node.parentPatch.domInterface)
				this.node.parentPatch.domInterface.connect(this.node);
			else if (this.node.parentPatch.parentPatch != null)
				this.node.registerInterfacePin();
		}

		if (this.direction == CCPinDirection.Configuration) {
			this.node.configure();
		}
	}

	/**
	 * used to mark a pin as changed without actually using
	 * {@link VVVV.Core.Pin#setValue}
	 */
	public void markPinAsChanged() {
		if (node.parentPatch.mainloop != null)
			valuesChangedAt = this.node.parentPatch.mainloop.frames();
	}

	/**
	 * used to find out if a pin has changed since last evaluation
	 * 
	 * @return {Boolean} true if changed, false if not changed
	 */
	public boolean pinIsChanged() {
		if (this.node.parentPatch.mainloop != null)
			return (valuesChangedAt == this.node.parentPatch.mainloop.frames());
		return true;
	}

	public void connect(CCPin<Type> other_pin) {
		this.values = other_pin.values;
		if (direction == CCPinDirection.Output) { // this is the case when a
													// subpatch output pin gets
													// connected to the
													// interface pin in the
													// subpatch
			for (CCLink myLink : links) {
				myLink.toPin.values = values;
			}
		}
		if (slavePin != null)
			slavePin.values = values;

		markPinAsChanged();
	}

	public void disconnect() {
      if (this.typeName==CCPinType.Color) {
        var v = [];
        for (var i=0; i<this.values.size(); i++) {
          v[i] = new VVVV.Types.Color("0,0,0,0");
          this.values[i].copy_to(v[i]);
        }
        this.values = v;
      }
      else
        this.values = this.values.slice(0);
      if (slavePin != null)
        slavePin.values = this.values;
      markPinAsChanged();
    }

	/**
	 * used do find out if a pin is connected
	 * 
	 * @return true, if there are incoming or outgoing links to or from this pin
	 *         (and its masterPin, if preset)
	 */
	public boolean isConnected() {
		return (this.links.size() > 0 || (masterPin != null && this.masterPin.isConnected())
				|| (slavePin != null && slavePin.links.size() > 0));
	}

	/**
	 * @return {Integer} the number of slices
	 */
	public int getSliceCount() {
		return values.size();
	}

	/**
	 * sets the number of slices; also sets the slice number of connected
	 * downstream pins and the slavePin if present; absolutely necessary if the
	 * slice number decreases
	 * 
	 * @param len the slice count
	 */
	public void setSliceCount(int len) {
		if (len < 0)
			len = 0;
		if (values.size() == len)
			return;
		if (direction == CCPinDirection.Output || !isConnected()) {
//			this.values.size() = len;
		}
		this.markPinAsChanged();
	}

	/**
     * used to change the pin's type during runtime. Also sets the value to the new pin type's default value
     * @param {Object} newType the new type, see {@link VVVV.PinTypes}
     */
    public void setType(CCPinType newType) {
    	if (newType == typeName)
    		return;
    	
      //delete this.connectionChangedHandlers['nodepin'];
      delete this.connectionChangedHandlers['webglresource'];
      _(newType.connectionChangedHandlers).each(function(handler, key) {
        that.connectionChangedHandlers[key] = newType.connectionChangedHandlers[key];
      });
      	typeName = newType.typeName;
      this.defaultValue = newType.defaultValue;

      if (direction == CCPinDirection.Input && this.defaultValue && !this.isConnected()) {
        this.setValue(0, this.defaultValue());
        setSliceCount(1);
      }

      if (newType.reset_on_disconnect!=undefined)
        this.reset_on_disconnect = newType.reset_on_disconnect;
    }

	public void reset() {
		values = new ArrayList<>();
		if (slavePin != null)
			slavePin.values = this.values;
		if (this.defaultValue) {
			this.setValue(0, defaultValue());
			this.setSliceCount(1);
		} else {
			values = init_values.slice(0);
			markPinAsChanged();
		}
		markPinAsChanged();
	}

	/**
	 * called when the pin gets connected or disconnected; subsequently calls
	 * the callbacks registered in
	 * {@link VVVV.Core.Pin#connectionChangedHandlers}
	 */
	public void connectionChanged() {
		connectionChangedHandlers.proxy().connectionChanged(this);
	}

	/**
	 *
	 */

	public String generateStaticCode(boolean checkForChanges) {
		String subcode = "";
		String dirtycode = "if (";
		String nilcode = "if (";

		for (int j = 0; j < this.values.incomingPins.length; j++) {
			var pin = this.values.incomingPins[j];
			dirtycode += "patch.nodeMap[" + pin.node.id + "].inputPins['" + pin.pinname + "'].pinIsChanged() || ";
			nilcode += "patch.nodeMap[" + pin.node.id + "].inputPins['" + pin.pinname + "'].values[0]==undefined || ";
			subcode = "Math.max(patch.nodeMap[" + pin.node.id + "].inputPins['" + pin.pinname + "'].getSliceCount(), "
					+ subcode;
		}
		dirtycode += "false) {\n";
		nilcode += "false) { patch.nodeMap[" + this.node.id + "].outputPins['Output'].setSliceCount(0); }\n else {";
		subcode += "0)";
		for (int j = 0; j < this.values.incomingPins.length - 1; j++) {
			subcode += ")";
		}
		subcode += ";\n";
		String code = nilcode;
		if (checkForChanges)
			code += dirtycode;
		code += "  var iii = ";
		code += subcode;
		code += "  patch.nodeMap[" + this.node.id + "].outputPins['Output'].setSliceCount(iii);";
		code += "  while (iii--) {\n";
		code += "    patch.nodeMap[" + this.node.id + "].outputPins['Output'].setValue(iii, " + this.values.code
				+ ");\n";
		code += "  }\n";
		if (checkForChanges)
			code += "}\n"; // dirty check
		code += "}\n"; // nil check
		return code;
	}

}
