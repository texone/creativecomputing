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
public class CCIllegalAddressExceptionResponse extends CCExceptionResponse {

	/**
	 * 
	 */
	public CCIllegalAddressExceptionResponse() {
		super(CCModbusExceptionCode.ILLEGAL_ADDRESS_EXCEPTION);
	}

	public CCIllegalAddressExceptionResponse(CCModbusFunctionCode theFunctionCode) {
		super(theFunctionCode, CCModbusExceptionCode.ILLEGAL_ADDRESS_EXCEPTION);
	}
}
