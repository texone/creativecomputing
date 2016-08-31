package Exceptions;

public class FunctionCodeNotSupportedException extends ModbusException
{
  public FunctionCodeNotSupportedException()
  {
  }

  public FunctionCodeNotSupportedException(String s)
  {
    super(s);
  }
}