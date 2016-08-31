package Exceptions;

public class StartingAddressInvalidException extends ModbusException
{
  public StartingAddressInvalidException()
  {
  }

  public StartingAddressInvalidException(String s)
  {
    super(s);
  }
}