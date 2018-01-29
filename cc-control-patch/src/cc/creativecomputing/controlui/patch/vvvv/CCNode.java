package cc.creativecomputing.controlui.patch.vvvv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.controlui.patch.vvvv.CCPin.CCPinDirection;
import cc.creativecomputing.controlui.patch.vvvv.CCPin.CCPinType;

public abstract class CCNode {

	/** X position in pixels inside the parent patch */
	public int x = 0;
	/** Y position in pixels inside the parent patch */
	public int y = 0;
	/**
	 * Node width (in a weird unit); use {@link VVVV.Core.Node.getWidth()} for
	 * pixel value
	 */
	public int width = 0;
	/**
	 * Node width (in a weird unit); use {@link VVVV.Core.Node.getHeight()} for
	 * pixel value
	 */
	public int height = 0;
	/** flag indicating whether a node is an IOBox */
	public boolean isIOBox = false;
	/** flag indicating whether a node is a shader node */
	public boolean isShader = false;
	
	public boolean isSubpatch = false;
	/**
	 * the number of subsequent resources (subpatches, shaders, 3rd party libs,
	 * etc.) that are currently being loaded. Is 0 if nothing is pending
	 */
	public int resourcesPending = 0;
	/**
	 * flag indicating if this node should automatically output nil on all
	 * output pins, if a nil value is on any input pin
	 */
	public boolean auto_nil = true;

	/**
	 * a flag indicating if this node should evaluate each frame, no matter if
	 * it's marked dirty or not
	 */
	public boolean auto_evaluate = false;
	public boolean delays_output = false;

	/** a flag indicating if any of this node's input pins has changed */
	public boolean dirty = true;

	/** the node ID */
	public int id;
	/**
	 * the nodename; might be e.g. a name in format NodeName (Category),
	 * SomeShader.fx or a path/to/a/subpatch.v4p
	 */
	public String nodename;
	/** the patch containing this node */
	public CCPatch parentPatch;

	public Map<String, CCPin> inputPins = new HashMap<>();
	public Map<String, CCPin> outputPins = new HashMap<>();
	public Map<String, CCPin> invisiblePins = new HashMap<>();;

	public Map<String, Object[]> defaultPinValues = new HashMap<>();

	/**
	 * @class
	 * @constructor
	 * @param {Integer} id the Node ID
	 * @param {String} nodename the Node Name
	 * @param {VVVV.Core.Patch} [parentPatch] the {@link VVVV.Core.Patch} the
	 *            node is nested
	 */
	public CCNode(int theId, String theNodename, CCPatch theParentPatch) {

		/**
		 * the nodename; might be e.g. a name in format NodeName (Category),
		 * SomeShader.fx or a path/to/a/subpatch.v4p
		 */
		nodename = theNodename;
		id = theId;

		this.parentPatch = theParentPatch;
		if (parentPatch != null)
			parentPatch.nodeMap.put(id, this);

		// this.setupObject() { // had to put this into a method to allow Patch
		// to "derive" from Node. Really have to understand this javascript
		// prototype thing some day ...
		// this.inputPins = {};
		// this.outputPins = {};
		// this.invisiblePins = {} ;
		//
		// this.defaultPinValues = {};
		// };
		// this.setupObject();

	}

	/**
	 * saves a pin value coming from the patch XML for later use
	 * 
	 * @param {String} pinname the pin's name
	 * @param {Array} value the array of values (slices)
	 */
	public void addDefault(String pinname, Object[] value) {
		defaultPinValues.put(pinname, value);
	}

	/**
	 * Creates a new input pin and adds it to the node. If pin values from the
	 * XML have been registered through {@link VVVV.Core.Node.addDefault}, these
	 * values are assigned
	 * 
	 * @param {String} pinname the new pin's name
	 * @param {Array} value the default spread
	 * @param {Object} type see {@link VVVV.PinTypes}
	 * @return {Pin} the new {@link Pin}
	 */
	public <Type> CCPin<Type> addInputPin(String pinname, Type[] value, CCPinType theType) {
		theType = theType != null ? theType : CCPinType.Generic;
		CCPin<Type> pin = new CCPin<Type>(pinname, CCPinDirection.Input, value, this, theType);
		inputPins.put(pinname, pin);
		if (parentPatch != null)
			parentPatch.pinMap.put(this.id + "_in_" + pinname, pin);
		applyPinValuesFromXML(pinname);
		return pin;
	}

	/**
	 * Creates a new output pin and adds it to the node.
	 * 
	 * @param {String} pinname the new pin's name
	 * @param {Array} value the default spread
	 * @param {Object} type see {@link VVVV.PinTypes}
	 * @return {Pin} the new {@link Pin}
	 */
	public <Type> CCPin<Type> addOutputPin(String pinname, Type[] value, CCPinType theType) {
		theType = theType != null ? theType : CCPinType.Generic;
		CCPin<Type> pin = new CCPin<Type>(pinname, CCPinDirection.Output, value, this, theType);
		outputPins.put(pinname, pin);
		if (parentPatch != null)
			this.parentPatch.pinMap.put(id + "_out_" + pinname, pin);
		return pin;
	}

	/**
	 * Creates a new invisible/config pin and adds it to the node. If pin values
	 * from the XML have been registered through
	 * {@link VVVV.Core.Node.addDefault}, these values are assigned
	 * 
	 * @param {String} pinname the new pin's name
	 * @param {Array} value the default spread
	 * @param {Object} type see {@link VVVV.PinTypes}
	 * @return {Pin} the new {@link Pin}
	 */
	public <Type> CCPin<Type> addInvisiblePin(String pinname, Type[] value, CCPinType theType) {
		theType = theType != null ? theType : CCPinType.Generic;
		CCPin<Type> pin = new CCPin<Type>(pinname, CCPinDirection.Configuration, value, this, theType);
		invisiblePins.put(pinname, pin);
		this.parentPatch.pinMap.put(id + "_inv_" + pinname, pin);
		if (defaultPinValues.containsKey(pinname)) {
			pin.values = defaultPinValues.get(pinname);
			pin.markPinAsChanged();
		}
		return pin;
	}

	/**
	 * deletes an input pin and all incoming links
	 * 
	 * @param pinname the name of the pin to delete
	 */
	public void removeInputPin(String pinname) {
		if (!inputPins.containsKey(pinname))
			return;
		CCLink<?> l = inputPins.get(pinname).links.get(0);
		if (l != null) {
			l.fromPin.connectionChanged();
			l.destroy();
		}
		inputPins.remove(pinname);
		this.dirty = true;
	}

	/**
	 * deletes an output pin and all outgoing links
	 * 
	 * @param pinname the name of the pin to delete
	 */
	public void removeOutputPin(String pinname) {
		if (!outputPins.containsKey(pinname))
			return;
		for (CCLink l : outputPins.get(pinname).links) {

			l.toPin.connectionChanged();
			l.destroy();
		}
		outputPins.remove(pinname);
		dirty = true;
	}

	/**
     * Helper to get the type of IOBox (e.g. Value Advanced, String, Color)
     * @return {String} the type of IOBox
     */
    public String IOBoxType() {        d
      return nodename;
    }

	/**
	 * Returns the input pin of the IOBox which is represented by the IOBox
	 * label
	 * 
	 * @return {Pin} the pin represented by the IOBox label, see {@link Pin}
	 */
	public CCPin IOBoxInputPin() {
		switch (IOBoxType()) {
		case "Value Advanced":
			return inputPins.get("Y Input Value");
		case "String":
			return inputPins.get("Input String");
		case "Color":
			return inputPins.get("Color Input");
		case "Node":
			return inputPins.get("Input Node");
		}
		return null;
	}

	/**
	 * Returns the output pin of the IOBox which is represented by the IOBox
	 * label
	 * 
	 * @return {Pin} the pin represented by the IOBox label, see {@link Pin}
	 */
	public CCPin IOBoxOutputPin() {
		switch (this.IOBoxType()) {
		case "Value Advanced":
			return outputPins.get("Y Output Value");
		case "String":
			return outputPins.get("Output String");
		case "Color":
			return outputPins.get("Color Output");
		case "Node":
			return outputPins.get("Output Node");
		}
		return null;
	}

	/**
	 * Returns the number of visible rows of an IOBox. This is basically a
	 * convenience method for getting the value of the "Rows" pin
	 * 
	 * @return {Integer} the number of visible rows
	 */
	public int IOBoxRows() {
		if (invisiblePins.containsKey("Rows"))
			return (Integer) invisiblePins.get("Rows").getValue(0);
		else
			return 1;
	}

	/**
	 * Tells, if a node is a comment node. Reverse engineering revealed that
	 * this is the case, if a String IOBox has no output pins. Maybe better ask
	 * someone who actually knows.
	 * 
	 * @return {Boolean} true, if the node is a comment, false otherwise.
	 */
	public boolean isComment() {
		return isIOBox && outputPins.size() == 0;
	}

	/**
	 * Returns the text shown inside a node box in the editor. In case of an
	 * IOBox this is the result of {@link VVVV.Core.Node.IOBoxInputPin}; in case
	 * of a subpatch this is "|| SubPatchName" (the .v4p extension stripped); in
	 * case of a normal node, this is the node name.
	 * 
	 * @return {String} the node's representative label
	 */
	public String label() {
      if (isIOBox) {
        if (this.IOBoxInputPin().getValue(0))
          return this.IOBoxInputPin().getValue(0).toString();
        return "";
      }

      if (isSubpatch) {
        return "||"+ nodename;
      }

      String label = this.nodename.replace(/\s\(.+\)/, '');
      label = VVVV.Helpers.translateOperators(label);
      return label;
    }

	/**
	 * Returns the node with in pixels, used for displaying the patch
	 * 
	 * @return {Integer} the node width in pixels
	 */
	public int getWidth() {
		int ret;
		if (this.width == 100 || this.width == 0) {
			if (this.isIOBox)
				ret = 60;
			else
				ret = Math.max(18, (this.label().length() + 2) * 6);
		} else
			ret = this.width / 15;
		ret = Math.max(ret, (_(this.inputPins).size() - 1) * 12 + 4);
		return ret;
	}

	/**
	 * Returns the node height in pixels, used for displaying the patch
	 * 
	 * @return {Integer} the node height in pixels
	 */
	public int getHeight() {
		if (this.isIOBox && this.height == 100)
			return 18 * this.IOBoxRows();
		if (this.height == 100 || this.isSubpatch)
			return 18;
		else
			return Math.max(18, this.height / 15);
	}

	/**
	 * Returns all nodes which are connected to a node's input pins
	 * 
	 * @return {Array} an Array of {@link VVVV.Core.Node} objects
	 */
	public List<CCNode> getUpstreamNodes() {
		List<CCNode> ret = new ArrayList<>();
		for(CCPin myPin:inputPins.values()){
			if(myPin.links.size() > 0){
				ret.add(myPin.links.get(0));
			}
				
		}
      _(this.inputPins).each(function(p) {
        if (p.links.length>0)
          ret.push(p.links[0].fromPin.node);
      });
      return ret;
    }

	/**
	 * Returns all nodes which are connected to a node's output pins
	 * 
	 * @return {Array} an Array of {@link VVVV.Core.Node} objects
	 */
	this.

	getDownstreamNodes() {
      var ret = [];
      _(this.outputPins).each(function(p) {
        for (var j=0; j<p.links.length; j++) {
          ret.push(p.links[j].toPin.node);
        }
      });
      return ret;
    }

	/**
	 * Finds all nodes with a certain name, the node's data eventually flows
	 * into.
	 * 
	 * @param {String} name the name of the node to search for
	 * @result {Array} an Array of {@link VVVV.Core.Node} objects matching the
	 *         search
	 */
	this.findDownstreamNodes(name)

	{
      var ret = [];
      _(this.outputPins).each(function(p) {
        for (var j=0; j<p.links.length; j++) {
          if (p.links[j].toPin.node.nodename==name)
            ret.push(p.links[j].toPin.node);
          else {
            if (p.links[j].toPin.slavePin) {
              // enter subpatch
              ret = ret.concat(p.links[j].toPin.slavePin.node.findDownstreamNodes(name));
            }
            else if (p.links[j].toPin.node.isIOBox && p.links[j].toPin.node.IOBoxOutputPin().slavePin) {
              // leave subpatch
              ret = ret.concat(p.links[j].toPin.node.IOBoxOutputPin().slavePin.node.findDownstreamNodes(name));
            }
            else
              ret = ret.concat(p.links[j].toPin.node.findDownstreamNodes(name));
          }
        }
      });
      return ret;
    }

	/**
	 * Tells, if a node has any nil inputs
	 * 
	 * @return true, if any of the input pins are true, false otherwise
	 */
	

	public boolean hasNilInputs() {
      boolean result = false;
      _(this.inputPins).each(function(p) {
        if (p.getSliceCount()==0 || p.values[0]==undefined)
          result = true;
      });
      return result;
    }

	/**
	 * Returns true if any of the input (or invisible) pins is changed
	 */
	public boolean isDirty() {
      if (dirty)
        return true;
      
      for (String pinname : inputPins.keySet()) {
        if (this.inputPins.get(pinname).pinIsChanged())
          return true;
      }
      for (String pinname : invisiblePins.keySet()) {
        if (invisiblePins.get(pinname).pinIsChanged())
          return true;
      }
      return false;
    }

	/**
	 * Returns the maximum number of slices of a node's input pins
	 * 
	 * @return the maximum number of slices
	 */
	this.

	getMaxInputSliceCount() {
      var ret = 0;
      var pinname;
      for (pinname in this.inputPins) {
        if (this.inputPins[pinname].getSliceCount()>ret)
          ret = this.inputPins[pinname].values.length;
      }
      return ret;
    }

	/**
	 * Applies values from the patch XML to an input pin, if present
	 * 
	 * @param {String} pinname the name of the pin
	 */
	public void applyPinValuesFromXML(String pinname)

	{
		if (!inputPins.containsKey(pinname))
			return;
		CCPin pin = inputPins.get(pinname);
		Object[] values = defaultPinValues.get(pinname);

		if (values == null)
			return;

		// this checks for the case when complex input pins have a value of
		// "||" when not connected.
		// this should not override the default value set by the node with
		// ""
		if (!pin.reset_on_disconnect || values.length > 1 || values[0] != "") {
			for (int i = 0; i < values.length; i++) {
				if (pin.values[i] != values[i]) {
					if (pin.typeName == "Color")
						pin.setValue(i, new VVVV.Types.Color(values[i]));
					else if (pin.typeName == "Value")
						pin.setValue(i, parseFloat(values[i]));
					else
						pin.setValue(i, values[i]);
				}
			}
			pin.setSliceCount(values.length);
		}
	}

	/**
	 * Called, if an IOBox's Descriptive Name inside a subpatch changes, this
	 * method creates and updates the subpatch's in and output pins.
	 * Subsequently triggers connection changed events for the IOBox's input and
	 * output pins.
	 */
	public void registerInterfacePin() {
      var that = this;
      if (this.isIOBox) {
        if (this.parentPatch.parentPatch && this.invisiblePins["Descriptive Name"].getValue(0)!="") {
          var pinname = this.invisiblePins["Descriptive Name"].getValue(0);
          this.IOBoxInputPin().connectionChangedHandlers['subpatchpins']() {
            if (this.links.length>0 && this.masterPin) {
               //if (VVVV_ENV=='development') console.log('deleting '+pinname+' input pin because node has input connection...');
               for (var i=0; i<this.masterPin.links.length; i++) {
                 this.masterPin.links[i].destroy();
               }
               this.disconnect();
               that.parentPatch.removeInputPin(pinname);
               this.masterPin = undefined;
            }
            if (that.IOBoxOutputPin().links.length==0) {
              if (!that.IOBoxOutputPin().slavePin) {
                //if (VVVV_ENV=='development') console.log('interfacing output pin detected: '+pinname);
                var pin = that.parentPatch.outputPins[pinname];
                if (pin==undefined) {
                  var pin = that.parentPatch.addOutputPin(pinname, that.IOBoxOutputPin().values);
                }

                pin.setType(VVVV.PinTypes[that.IOBoxOutputPin().typeName]);

                that.IOBoxOutputPin().slavePin = pin;
                pin.masterPin = that.IOBoxOutputPin();
                pin.connect(that.IOBoxOutputPin())
              }
              else if (that.IOBoxOutputPin().slavePin.pinname!=pinname) { // rename subpatch pin
                if (VVVV_ENV=='development') console.log('renaming '+that.IOBoxOutputPin().slavePin.pinname+" to "+pinname);
                that.parentPatch.outputPins[pinname] = that.parentPatch.outputPins[that.IOBoxOutputPin().slavePin.pinname];
                that.parentPatch.removeOutputPin(that.IOBoxOutputPin().slavePin.pinname);
                that.IOBoxOutputPin().slavePin.pinname = pinname;
              }
            }
            this.node.parentPatch.parentPatch.afterUpdate();
          }
          this.IOBoxInputPin().connectionChanged();

          this.IOBoxOutputPin().connectionChangedHandlers['subpatchpins']() {
            if (this.links.length>0 && this.slavePin) {
               //if (VVVV_ENV=='development') console.log('deleting '+pinname+' output pin because node '+that.id+' has output connection...');
               for (var i=0; i<this.slavePin.links.length; i++) {
                 this.slavePin.links[i].destroy();
               }
               this.slavePin.disconnect(); // not really necessary, as the slavepin gets removed anyway
               that.parentPatch.removeOutputPin(pinname);
               this.slavePin = undefined;
            }
            if (that.IOBoxInputPin().links.length==0) {
              if (!that.IOBoxInputPin().masterPin) {
                //if (VVVV_ENV=='development') console.log('interfacing input pin detected: '+pinname);
                var pin = that.parentPatch.inputPins[pinname];
                if (pin==undefined) {
                  //if (VVVV_ENV=='development') console.log('creating new input pin at parent patch, using IOBox values');
                  var pin = that.parentPatch.addInputPin(pinname, that.IOBoxInputPin().values);
                }

                var savedValues = pin.values.slice();
                pin.setType(VVVV.PinTypes[that.IOBoxInputPin().typeName]);
                if ((pin.unvalidated && VVVV.PinTypes[pin.typeName].primitive) && !pin.isConnected()) {
                  if (pin.typeName[0]=='V') {
                    for (var i=0; i<savedValues.length; i++) {
                      pin.values[i] = parseFloat(savedValues[i]);
                    }
                  }
                  else
                    pin.values = savedValues;
                  pin.markPinAsChanged();
                }
                pin.unvalidated = false;

                pin.slavePin = that.IOBoxInputPin();
                that.IOBoxInputPin().masterPin = pin;
                that.IOBoxInputPin().connect(pin);
              }
              else if (that.IOBoxInputPin().masterPin.pinname!=pinname) { // rename subpatch pin
                console.log('renaming '+that.IOBoxInputPin().masterPin.pinname+" to "+pinname);
                that.parentPatch.inputPins[pinname] = that.parentPatch.inputPins[that.IOBoxInputPin().masterPin.pinname];
                that.parentPatch.removeInputPin(that.IOBoxInputPin().masterPin.pinname);
                that.IOBoxInputPin().masterPin.pinname = pinname;
              }
            }
            this.node.parentPatch.parentPatch.afterUpdate();
          }
          this.IOBoxOutputPin().connectionChanged();
        }
      }
    }

	/**
	 * Method called immediatly after node creation for setting up common node
	 * settings
	 */
	public void setup(){
      //Add descriptive name for all nodes
      this.addInvisiblePin("Descriptive Name",[""], VVVV.PinTypes.String);
    }

	/**
	 * @abstract
	 */
	public abstract void configure();

	/**
	 * Method called AFTER a node's pins have been created and populated with
	 * values from patch XML, and BEFORE node links are created. This method
	 * should be overwritten by any Node implementation and is useful for e.g.
	 * creating dynamic number of input pins and other initialising code which
	 * should run before first call of {@link VVVV.Core.Node.evaluate}.
	 * 
	 * @abstract
	 */
	public void initialize() {

	}

	/**
	 * Method called each frame, if a node is marked dirty or
	 * {@link VVVV.Core.Node.auto_evaluate} is true. This method should be
	 * overwritten by any Node implementation and usually holds the node's main
	 * logic.
	 * 
	 * @abstract
	 */
	public void evaluate() {
      var that = this;
      _(this.outputPins).each(function(p) {
        p.setValue(0, "not calculated");
      });
    }

	/**
	 * sets all output pin values to nil, if at least one input pin value is
	 * nil, and the node is acting auto_nil
	 * 
	 * @return true, if the output pins were set to nil, false otherwise
	 */
	public boolean dealWithNilInput() {
      if (this.auto_nil && !this.isSubpatch && this.hasNilInputs()) {
        for(String pinname : outputPins.keySet()) {
          this.outputPins[pinname].setSliceCount(0);
        }
        return true;
      }
      return false;
    }

	/**
	 * Method called when a node is being deleted. Should be overwritten by any
	 * Node implementation to free resources and gracefully shut itself down
	 * 
	 * @abstract
	 */
	public void destroy() {
      if (this.isIOBox) {
        if (this.IOBoxInputPin().masterPin) {
          this.parentPatch.removeInputPin(this.IOBoxInputPin().masterPin.pinname);
          this.parentPatch.parentPatch.afterUpdate();
        }
        if (this.IOBoxOutputPin().slavePin) {
          this.parentPatch.removeOutputPin(this.IOBoxOutputPin().slavePin.pinname);
          this.parentPatch.parentPatch.afterUpdate();
        }
      }
    }

	/**
	 * Creates the XML code representing the node and its pins. Called by
	 * {@link VVVV.Core.Patch.toXML} on serializing a patch and directly by the
	 * editor when nodes are being copied to clipboard
	 * 
	 * @return {String} the node's XML code
	 */
	this.

	serialize() {
      var $node = $("<NODE>");
      $node.attr("id", this.id);
      $node.attr("nodename", this.nodename);
      $node.attr("systemname", this.nodename);
      if (this.shaderFile) {
        $node.attr("filename", this.shaderFile.replace(".vvvvjs.fx", ".fx").replace("%VVVV%/effects", "%VVVV%/lib/nodes/effects"));
      }
      if (this.isSubpatch) {
        $node.attr("filename", this.nodename);
        $node.attr("systemname", this.nodename.match("(.*)\.v4p$")[1])
      }
      if (this.isIOBox)
        $node.attr("componentmode", "InABox");
      else
        $node.attr("componentmode", "Hidden");

      var $bounds = $("<BOUNDS>");
      if (this.isIOBox)
        $bounds.attr("type", "Box");
      else
        $bounds.attr("type", "Node");
      $bounds.attr("left", parseInt(this.x * 15));
      $bounds.attr("top", parseInt(this.y * 15));
      $bounds.attr("width", parseInt(this.width));
      $bounds.attr("height", parseInt(this.height));
      $node.append($bounds);

      var that = this;

      _(this.inputPins).each(function(p) {
        var $pin = $("<PIN>");
        $pin.attr("pinname", p.pinname);
        $pin.attr("visible", "1");
        if ((!p.isConnected() || p.masterPin) && VVVV.PinTypes[p.typeName].primitive && that.defaultPinValues[p.pinname]) {
          $pin.attr("values", _(that.defaultPinValues[p.pinname]).map(function(v) { return "|"+v.toString().replace(/\|/g, "||")+"|"; }).join(","));
        }
        $node.append($pin);
      })

      _(this.invisiblePins).each(function(p) {
        var $pin = $("<PIN>");
        $pin.attr("pinname", p.pinname);
        $pin.attr("visible", "0");
        if (VVVV.PinTypes[p.typeName].primitive) {
          $pin.attr("values", _(p.values).map(function(v) { return "|"+v.toString().replace(/\|/g, "||")+"|"; }).join(","));
        }
        $node.append($pin);
      })

      return $node;
    }

	this.

	toJSON() {
      var obj = {pins: {}};
      var that = this;
      for ( var prop in this ) {
        switch (prop) {
          case "width":
          case "height":
          case "nodename": obj[prop] = this[prop]; break;
          case "x": obj.x = this.x * 15; break;
          case "y": obj.y = this.y * 15; break;
          case "inputPins":
            for (pinname in this.inputPins) {
              var p = this.inputPins[pinname];
              obj.pins[pinname] = {};
              if ((!p.isConnected() || p.masterPin) && VVVV.PinTypes[p.typeName].primitive && that.defaultPinValues[p.pinname])
                obj.pins[pinname].values = that.defaultPinValues[p.pinname];
            }
            break;
          case "invisiblePins":
            for (pinname in this.invisiblePins) {
              var p = this.invisiblePins[pinname];
              obj.pins[pinname] =  {values: p.values, visible: 0};
            }
            break;
          default:
        }
      }
      if (this.shaderFile) {
        obj.filename = this.shaderFile.replace(".vvvvjs.fx", ".fx").replace("%VVVV%/effects", "%VVVV%/lib/nodes/effects");
      }
      if (this.isSubpatch) {
        obj.filename = this.nodename;
      }
      return obj;
    }

	this.

	sharedRessources() {
      if (!VVVVContext.sharedRessourceStores[this.parentPatch.getPatchIdentifier()+"/"+this.id]) {
        var SharedRessourceStore = require('core/vvvv.core.shared_ressources');
        VVVVContext.sharedRessourceStores[this.parentPatch.getPatchIdentifier()+"/"+this.id] = new SharedRessourceStore();
      }
      return VVVVContext.sharedRessourceStores[this.parentPatch.getPatchIdentifier()+"/"+this.id];
    }

}

}
