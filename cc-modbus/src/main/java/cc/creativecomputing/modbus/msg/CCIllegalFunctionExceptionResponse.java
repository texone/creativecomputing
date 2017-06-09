/**
 * 
 */
package cc.creativecomputing.modbus.msg;

import cc.creativecomputing.modbus.CCModbusExceptionCode;
import cc.creativecomputing.modbus.CCModbusFunctionCode;

/**
 * @author jfhaugh
 * 
 * @version @version@ (@date@)
 */
public class CCIllegalFunctionExceptionResponse extends CCExceptionResponse {

	/**
	 * 
	 */
	public CCIllegalFunctionExceptionResponse() {
		super(CCModbusExceptionCode.ILLEGAL_FUNCTION_EXCEPTION);
	}

	public CCIllegalFunctionExceptionResponse(CCModbusFunctionCode theFunctionCode) {
		super(theFunctionCode, CCModbusExceptionCode.ILLEGAL_FUNCTION_EXCEPTION);
	}
}
