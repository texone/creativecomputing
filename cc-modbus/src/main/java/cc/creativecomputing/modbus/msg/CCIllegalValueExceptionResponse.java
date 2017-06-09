/**
 * 
 */
package cc.creativecomputing.modbus.msg;

import cc.creativecomputing.modbus.CCModbusExceptionCode;
import cc.creativecomputing.modbus.CCModbusFunctionCode;

/**
 * @author Julie
 *
 * @version @version@ (@date@)
 */
public class CCIllegalValueExceptionResponse extends CCExceptionResponse {

	/**
	 * 
	 */
	public CCIllegalValueExceptionResponse() {
		super(CCModbusExceptionCode.ILLEGAL_VALUE_EXCEPTION);
	}

	public CCIllegalValueExceptionResponse(CCModbusFunctionCode theFunctionCode) {
		super(theFunctionCode, CCModbusExceptionCode.ILLEGAL_VALUE_EXCEPTION);
	}
}
