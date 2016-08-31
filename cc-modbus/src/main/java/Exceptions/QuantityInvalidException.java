package Exceptions;

public class QuantityInvalidException extends ModbusException
{
  public QuantityInvalidException()
  {
  }

  public QuantityInvalidException(String s)
  {
    super(s);
  }
}