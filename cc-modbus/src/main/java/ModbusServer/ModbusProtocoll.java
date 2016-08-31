package ModbusServer;

import java.util.Calendar;

public class ModbusProtocoll
{
  public Calendar timeStamp;
  public boolean request;
  public boolean response;
  public int transactionIdentifier;
  public int protocolIdentifier;
  public int length;
  public byte unitIdentifier;
  public byte functionCode;
  public int startingAdress;
  public int quantity;
  public short byteCount;
  public byte exceptionCode;
  public byte errorCode;
  public short[] receiveCoilValues;
  public int[] receiveRegisterValues;
  public int[] sendRegisterValues;
  public boolean[] sendCoilValues;
}