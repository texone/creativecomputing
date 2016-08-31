package ModbusServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;

public class ModbusServer extends Thread
{
  private int port = 502;
  protected ModbusProtocoll receiveData;
  protected ModbusProtocoll sendData = new ModbusProtocoll();
  private Byte[] bytes = new Byte[2100];
  public int[] holdingRegisters = new int[65535];
  public int[] inputRegisters = new int[65535];
  public boolean[] coils = new boolean[65535];
  public boolean[] discreteInputs = new boolean[65535];
  private int numberOfConnections = 0;
  public boolean udpFlag;
  private int portIn;
  private Thread clientConnectionThread;
  private ModbusProtocoll[] modbusLogData = new ModbusProtocoll[100];
  private boolean functionCode1Disabled;
  private boolean functionCode2Disabled;
  private boolean functionCode3Disabled;
  private boolean functionCode4Disabled;
  private boolean functionCode5Disabled;
  private boolean functionCode6Disabled;
  private boolean functionCode15Disabled;
  private boolean functionCode16Disabled;
  private boolean functionCode23Disabled;
  private boolean serverRunning;
  private ListenerThread listenerThread;
  protected ICoilsChangedDelegator notifyCoilsChanged;
  protected IHoldingRegistersChangedDelegator notifyHoldingRegistersChanged;
  protected INumberOfConnectedClientsChangedDelegator notifyNumberOfConnectedClientsChanged;
  protected ILogDataChangedDelegator notifyLogDataChanged;

  protected void finalize()
  {
    this.serverRunning = false;
    this.listenerThread.stop();
  }

  public void Listen()
    throws IOException
  {
    this.serverRunning = true;
    this.listenerThread = new ListenerThread(this);
    this.listenerThread.start();
  }

  public void StopListening()
  {
    this.serverRunning = false;
    this.listenerThread.stop();
  }

  protected void CreateAnswer(Socket socket)
  {
    switch (this.receiveData.functionCode)
    {
    case 1:
      if (!this.functionCode1Disabled) {
        ReadCoils(socket);
      }
      else {
        this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
        this.sendData.exceptionCode = 1;
        sendException(this.sendData.errorCode, this.sendData.exceptionCode, socket);
      }
      break;
    case 2:
      if (!this.functionCode2Disabled) {
        ReadDiscreteInputs(socket);
      }
      else {
        this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
        this.sendData.exceptionCode = 1;
        sendException(this.sendData.errorCode, this.sendData.exceptionCode, socket);
      }

      break;
    case 3:
      if (!this.functionCode3Disabled) {
        ReadHoldingRegisters(socket);
      }
      else {
        this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
        this.sendData.exceptionCode = 1;
        sendException(this.sendData.errorCode, this.sendData.exceptionCode, socket);
      }

      break;
    case 4:
      if (!this.functionCode4Disabled) {
        ReadInputRegisters(socket);
      }
      else {
        this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
        this.sendData.exceptionCode = 1;
        sendException(this.sendData.errorCode, this.sendData.exceptionCode, socket);
      }

      break;
    case 5:
      if (!this.functionCode5Disabled) {
        WriteSingleCoil(socket);
      }
      else {
        this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
        this.sendData.exceptionCode = 1;
        sendException(this.sendData.errorCode, this.sendData.exceptionCode, socket);
      }

      break;
    case 6:
      if (!this.functionCode6Disabled) {
        WriteSingleRegister(socket);
      }
      else {
        this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
        this.sendData.exceptionCode = 1;
        sendException(this.sendData.errorCode, this.sendData.exceptionCode, socket);
      }

      break;
    case 15:
      if (!this.functionCode15Disabled) {
        WriteMultipleCoils(socket);
      }
      else {
        this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
        this.sendData.exceptionCode = 1;
        sendException(this.sendData.errorCode, this.sendData.exceptionCode, socket);
      }

      break;
    case 16:
      if (!this.functionCode16Disabled) {
        WriteMultipleRegisters(socket);
      }
      else {
        this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 144));
        this.sendData.exceptionCode = 1;
        sendException(this.sendData.errorCode, this.sendData.exceptionCode, socket);
      }

      break;
    case 7:
    case 8:
    case 9:
    case 10:
    case 11:
    case 12:
    case 13:
    case 14:
    default:
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 1;
      sendException(this.sendData.errorCode, this.sendData.exceptionCode, socket);
    }

    this.sendData.timeStamp = Calendar.getInstance();
  }

  private void ReadCoils(Socket socket)
  {
    this.sendData = new ModbusProtocoll();
    this.sendData.response = true;

    this.sendData.transactionIdentifier = this.receiveData.transactionIdentifier;
    this.sendData.protocolIdentifier = this.receiveData.protocolIdentifier;

    this.sendData.unitIdentifier = this.receiveData.unitIdentifier;
    this.sendData.functionCode = this.receiveData.functionCode;
    if (((this.receiveData.quantity < 1 ? 1 : 0) | (this.receiveData.quantity > 2000 ? 1 : 0)) != 0)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 3;
    }
    if (this.receiveData.startingAdress + 1 + this.receiveData.quantity > 65535)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 2;
    }
    if (this.receiveData.quantity % 8 == 0)
      this.sendData.byteCount = ((short)(byte)(this.receiveData.quantity / 8));
    else {
      this.sendData.byteCount = ((short)(byte)(this.receiveData.quantity / 8 + 1));
    }
    this.sendData.sendCoilValues = new boolean[this.receiveData.quantity];

    System.arraycopy(this.coils, this.receiveData.startingAdress + 1, this.sendData.sendCoilValues, 0, this.sendData.sendCoilValues.length);
    byte[] data;
    if (this.sendData.exceptionCode > 0)
      data = new byte[9];
    else
      data = new byte[9 + this.sendData.byteCount];
    byte[] byteData = new byte[2];

    this.sendData.length = ((byte)(data.length - 6));

    data[0] = ((byte)((this.sendData.transactionIdentifier & 0xFF00) >> 8));
    data[1] = ((byte)(this.sendData.transactionIdentifier & 0xFF));

    data[2] = ((byte)((this.sendData.protocolIdentifier & 0xFF00) >> 8));
    data[3] = ((byte)(this.sendData.protocolIdentifier & 0xFF));

    data[4] = ((byte)((this.sendData.length & 0xFF00) >> 8));
    data[5] = ((byte)(this.sendData.length & 0xFF));

    data[6] = this.sendData.unitIdentifier;

    data[7] = this.sendData.functionCode;

    data[8] = ((byte)(this.sendData.byteCount & 0xFF));

    if (this.sendData.exceptionCode > 0)
    {
      data[7] = this.sendData.errorCode;
      data[8] = this.sendData.exceptionCode;
      this.sendData.sendCoilValues = null;
    }

    if (this.sendData.sendCoilValues != null) {
      for (int i = 0; i < this.sendData.byteCount; i++)
      {
        byteData = new byte[2];
        for (int j = 0; j < 8; j++)
        {
          byte boolValue;
          if (this.sendData.sendCoilValues[(i * 8 + j)])
            boolValue = 1;
          else
            boolValue = 0;
          byteData[1] = ((byte)(byteData[1] | boolValue << j));
          if (i * 8 + j + 1 >= this.sendData.sendCoilValues.length)
            break;
        }
        data[(9 + i)] = byteData[1];
      }
    }
    if ((socket.isConnected() & !socket.isClosed()))
      try {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
  }

  private void ReadDiscreteInputs(Socket socket)
  {
    this.sendData = new ModbusProtocoll();
    this.sendData.response = true;

    this.sendData.transactionIdentifier = this.receiveData.transactionIdentifier;
    this.sendData.protocolIdentifier = this.receiveData.protocolIdentifier;

    this.sendData.unitIdentifier = this.receiveData.unitIdentifier;
    this.sendData.functionCode = this.receiveData.functionCode;
    if (((this.receiveData.quantity < 1 ? 1 : 0) | (this.receiveData.quantity > 2000 ? 1 : 0)) != 0)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 3;
    }
    if (this.receiveData.startingAdress + 1 + this.receiveData.quantity > 65535)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 2;
    }
    if (this.receiveData.quantity % 8 == 0)
      this.sendData.byteCount = ((short)(byte)(this.receiveData.quantity / 8));
    else {
      this.sendData.byteCount = ((short)(byte)(this.receiveData.quantity / 8 + 1));
    }
    this.sendData.sendCoilValues = new boolean[this.receiveData.quantity];
    System.arraycopy(this.discreteInputs, this.receiveData.startingAdress + 1, this.sendData.sendCoilValues, 0, this.receiveData.quantity);
    byte[] data;
    if (this.sendData.exceptionCode > 0)
      data = new byte[9];
    else
      data = new byte[9 + this.sendData.byteCount];
    byte[] byteData = new byte[2];
    this.sendData.length = ((byte)(data.length - 6));

    data[0] = ((byte)((this.sendData.transactionIdentifier & 0xFF00) >> 8));
    data[1] = ((byte)(this.sendData.transactionIdentifier & 0xFF));

    data[2] = ((byte)((this.sendData.protocolIdentifier & 0xFF00) >> 8));
    data[3] = ((byte)(this.sendData.protocolIdentifier & 0xFF));

    data[4] = ((byte)((this.sendData.length & 0xFF00) >> 8));
    data[5] = ((byte)(this.sendData.length & 0xFF));

    data[6] = this.sendData.unitIdentifier;

    data[7] = this.sendData.functionCode;

    data[8] = ((byte)(this.sendData.byteCount & 0xFF));

    if (this.sendData.exceptionCode > 0)
    {
      data[7] = this.sendData.errorCode;
      data[8] = this.sendData.exceptionCode;
      this.sendData.sendCoilValues = null;
    }

    if (this.sendData.sendCoilValues != null) {
      for (int i = 0; i < this.sendData.byteCount; i++)
      {
        byteData = new byte[2];
        for (int j = 0; j < 8; j++)
        {
          byte boolValue;
          if (this.sendData.sendCoilValues[(i * 8 + j)])
            boolValue = 1;
          else
            boolValue = 0;
          byteData[1] = ((byte)(byteData[1] | boolValue << j));
          if (i * 8 + j + 1 >= this.sendData.sendCoilValues.length)
            break;
        }
        data[(9 + i)] = byteData[1];
      }
    }
    if ((socket.isConnected() & !socket.isClosed()))
      try {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
  }

  private void ReadHoldingRegisters(Socket socket)
  {
    this.sendData = new ModbusProtocoll();
    this.sendData.response = true;

    this.sendData.transactionIdentifier = this.receiveData.transactionIdentifier;
    this.sendData.protocolIdentifier = this.receiveData.protocolIdentifier;

    this.sendData.unitIdentifier = this.receiveData.unitIdentifier;
    this.sendData.functionCode = this.receiveData.functionCode;
    if (((this.receiveData.quantity < 1 ? 1 : 0) | (this.receiveData.quantity > 125 ? 1 : 0)) != 0)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 3;
    }
    if (this.receiveData.startingAdress + 1 + this.receiveData.quantity > 65535)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 2;
    }
    this.sendData.byteCount = ((short)(2 * this.receiveData.quantity));

    this.sendData.sendRegisterValues = new int[this.receiveData.quantity];
    System.arraycopy(this.holdingRegisters, this.receiveData.startingAdress + 1, this.sendData.sendRegisterValues, 0, this.receiveData.quantity);

    if (this.sendData.exceptionCode > 0)
      this.sendData.length = 3;
    else
      this.sendData.length = ((short)(3 + this.sendData.byteCount));
    byte[] data;
    if (this.sendData.exceptionCode > 0)
      data = new byte[9];
    else
      data = new byte[9 + this.sendData.byteCount];
    this.sendData.length = ((byte)(data.length - 6));

    data[0] = ((byte)((this.sendData.transactionIdentifier & 0xFF00) >> 8));
    data[1] = ((byte)(this.sendData.transactionIdentifier & 0xFF));

    data[2] = ((byte)((this.sendData.protocolIdentifier & 0xFF00) >> 8));
    data[3] = ((byte)(this.sendData.protocolIdentifier & 0xFF));

    data[4] = ((byte)((this.sendData.length & 0xFF00) >> 8));
    data[5] = ((byte)(this.sendData.length & 0xFF));

    data[6] = this.sendData.unitIdentifier;

    data[7] = this.sendData.functionCode;

    data[8] = ((byte)(this.sendData.byteCount & 0xFF));

    if (this.sendData.exceptionCode > 0)
    {
      data[7] = this.sendData.errorCode;
      data[8] = this.sendData.exceptionCode;
      this.sendData.sendRegisterValues = null;
    }

    if (this.sendData.sendRegisterValues != null) {
      for (int i = 0; i < this.sendData.byteCount / 2; i++)
      {
        data[(9 + i * 2)] = ((byte)((this.sendData.sendRegisterValues[i] & 0xFF00) >> 8));
        data[(10 + i * 2)] = ((byte)(this.sendData.sendRegisterValues[i] & 0xFF));
      }
    }
    if ((socket.isConnected() & !socket.isClosed()))
      try {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
  }

  private void ReadInputRegisters(Socket socket)
  {
    this.sendData = new ModbusProtocoll();
    this.sendData.response = true;

    this.sendData.transactionIdentifier = this.receiveData.transactionIdentifier;
    this.sendData.protocolIdentifier = this.receiveData.protocolIdentifier;

    this.sendData.unitIdentifier = this.receiveData.unitIdentifier;
    this.sendData.functionCode = this.receiveData.functionCode;
    if (((this.receiveData.quantity < 1 ? 1 : 0) | (this.receiveData.quantity > 125 ? 1 : 0)) != 0)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 3;
    }
    if (this.receiveData.startingAdress + 1 + this.receiveData.quantity > 65535)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 2;
    }
    this.sendData.byteCount = ((short)(2 * this.receiveData.quantity));
    this.sendData.sendRegisterValues = new int[this.receiveData.quantity];
    System.arraycopy(this.inputRegisters, this.receiveData.startingAdress + 1, this.sendData.sendRegisterValues, 0, this.receiveData.quantity);

    if (this.sendData.exceptionCode > 0)
      this.sendData.length = 3;
    else
      this.sendData.length = ((short)(3 + this.sendData.byteCount));
    byte[] data;
    if (this.sendData.exceptionCode > 0)
      data = new byte[9];
    else
      data = new byte[9 + this.sendData.byteCount];
    this.sendData.length = ((byte)(data.length - 6));

    data[0] = ((byte)((this.sendData.transactionIdentifier & 0xFF00) >> 8));
    data[1] = ((byte)(this.sendData.transactionIdentifier & 0xFF));

    data[2] = ((byte)((this.sendData.protocolIdentifier & 0xFF00) >> 8));
    data[3] = ((byte)(this.sendData.protocolIdentifier & 0xFF));

    data[4] = ((byte)((this.sendData.length & 0xFF00) >> 8));
    data[5] = ((byte)(this.sendData.length & 0xFF));

    data[6] = this.sendData.unitIdentifier;

    data[7] = this.sendData.functionCode;

    data[8] = ((byte)(this.sendData.byteCount & 0xFF));

    if (this.sendData.exceptionCode > 0)
    {
      data[7] = this.sendData.errorCode;
      data[8] = this.sendData.exceptionCode;
      this.sendData.sendRegisterValues = null;
    }

    if (this.sendData.sendRegisterValues != null) {
      for (int i = 0; i < this.sendData.byteCount / 2; i++)
      {
        data[(9 + i * 2)] = ((byte)((this.sendData.sendRegisterValues[i] & 0xFF00) >> 8));
        data[(10 + i * 2)] = ((byte)(this.sendData.sendRegisterValues[i] & 0xFF));
      }
    }
    if ((socket.isConnected() & !socket.isClosed()))
      try {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
  }

  private void WriteSingleCoil(Socket socket)
  {
    this.sendData = new ModbusProtocoll();
    this.sendData.response = true;

    this.sendData.transactionIdentifier = this.receiveData.transactionIdentifier;
    this.sendData.protocolIdentifier = this.receiveData.protocolIdentifier;

    this.sendData.unitIdentifier = this.receiveData.unitIdentifier;
    this.sendData.functionCode = this.receiveData.functionCode;
    this.sendData.startingAdress = this.receiveData.startingAdress;
    this.sendData.receiveCoilValues = this.receiveData.receiveCoilValues;
    if (((this.receiveData.receiveCoilValues[0] != 0 ? 1 : 0) & (this.receiveData.receiveCoilValues[0] != 65280 ? 1 : 0)) != 0)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 3;
    }
    if (this.receiveData.startingAdress + 1 > 65535)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 2;
    }
    if ((this.receiveData.receiveCoilValues[0] & 0xFF00) > 0)
    {
      this.coils[(this.receiveData.startingAdress + 1)] = true;
    }
    if (this.receiveData.receiveCoilValues[0] == 0)
    {
      this.coils[(this.receiveData.startingAdress + 1)] = false;
    }
    if (this.sendData.exceptionCode > 0)
      this.sendData.length = 3;
    else
      this.sendData.length = 6;
    byte[] data;
    if (this.sendData.exceptionCode > 0)
      data = new byte[9];
    else {
      data = new byte[12];
    }
    this.sendData.length = ((byte)(data.length - 6));

    data[0] = ((byte)((this.sendData.transactionIdentifier & 0xFF00) >> 8));
    data[1] = ((byte)(this.sendData.transactionIdentifier & 0xFF));

    data[2] = ((byte)((this.sendData.protocolIdentifier & 0xFF00) >> 8));
    data[3] = ((byte)(this.sendData.protocolIdentifier & 0xFF));

    data[4] = ((byte)((this.sendData.length & 0xFF00) >> 8));
    data[5] = ((byte)(this.sendData.length & 0xFF));

    data[6] = this.sendData.unitIdentifier;

    data[7] = this.sendData.functionCode;

    if (this.sendData.exceptionCode > 0)
    {
      data[7] = this.sendData.errorCode;
      data[8] = this.sendData.exceptionCode;
      this.sendData.sendRegisterValues = null;
    }
    else
    {
      data[8] = ((byte)((this.receiveData.startingAdress & 0xFF00) >> 8));
      data[9] = ((byte)(this.receiveData.startingAdress & 0xFF));

      data[10] = ((byte)((this.receiveData.receiveCoilValues[0] & 0xFF00) >> 8));
      data[11] = ((byte)(this.receiveData.receiveCoilValues[0] & 0xFF));
    }

    if ((socket.isConnected() & !socket.isClosed()))
      try {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    if (this.notifyCoilsChanged != null)
      this.notifyCoilsChanged.coilsChangedEvent();
  }

  private void WriteSingleRegister(Socket socket)
  {
    this.sendData = new ModbusProtocoll();
    this.sendData.response = true;

    this.sendData.transactionIdentifier = this.receiveData.transactionIdentifier;
    this.sendData.protocolIdentifier = this.receiveData.protocolIdentifier;

    this.sendData.unitIdentifier = this.receiveData.unitIdentifier;
    this.sendData.functionCode = this.receiveData.functionCode;
    this.sendData.startingAdress = this.receiveData.startingAdress;
    this.sendData.receiveRegisterValues = this.receiveData.receiveRegisterValues;

    if (((this.receiveData.receiveRegisterValues[0] < 0 ? 1 : 0) | (this.receiveData.receiveRegisterValues[0] > 65535 ? 1 : 0)) != 0)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 3;
    }
    if (this.receiveData.startingAdress + 1 > 65535)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 2;
    }
    this.holdingRegisters[(this.receiveData.startingAdress + 1)] = this.receiveData.receiveRegisterValues[0];
    if (this.sendData.exceptionCode > 0)
      this.sendData.length = 3;
    else
      this.sendData.length = 6;
    byte[] data;
    if (this.sendData.exceptionCode > 0)
      data = new byte[9];
    else {
      data = new byte[12];
    }
    this.sendData.length = ((byte)(data.length - 6));

    data[0] = ((byte)((this.sendData.transactionIdentifier & 0xFF00) >> 8));
    data[1] = ((byte)(this.sendData.transactionIdentifier & 0xFF));

    data[2] = ((byte)((this.sendData.protocolIdentifier & 0xFF00) >> 8));
    data[3] = ((byte)(this.sendData.protocolIdentifier & 0xFF));

    data[4] = ((byte)((this.sendData.length & 0xFF00) >> 8));
    data[5] = ((byte)(this.sendData.length & 0xFF));

    data[6] = this.sendData.unitIdentifier;

    data[7] = this.sendData.functionCode;

    if (this.sendData.exceptionCode > 0)
    {
      data[7] = this.sendData.errorCode;
      data[8] = this.sendData.exceptionCode;
      this.sendData.sendRegisterValues = null;
    }
    else
    {
      data[8] = ((byte)((this.receiveData.startingAdress & 0xFF00) >> 8));
      data[9] = ((byte)(this.receiveData.startingAdress & 0xFF));

      data[10] = ((byte)((this.receiveData.receiveRegisterValues[0] & 0xFF00) >> 8));
      data[11] = ((byte)(this.receiveData.receiveRegisterValues[0] & 0xFF));
    }

    if ((socket.isConnected() & !socket.isClosed()))
      try {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    if (this.notifyHoldingRegistersChanged != null)
      this.notifyHoldingRegistersChanged.holdingRegistersChangedEvent();
  }

  private void WriteMultipleCoils(Socket socket)
  {
    this.sendData = new ModbusProtocoll();
    this.sendData.response = true;

    this.sendData.transactionIdentifier = this.receiveData.transactionIdentifier;
    this.sendData.protocolIdentifier = this.receiveData.protocolIdentifier;

    this.sendData.unitIdentifier = this.receiveData.unitIdentifier;
    this.sendData.functionCode = this.receiveData.functionCode;
    this.sendData.startingAdress = this.receiveData.startingAdress;
    this.sendData.quantity = this.receiveData.quantity;

    if (((this.receiveData.quantity == 0 ? 1 : 0) | (this.receiveData.quantity > 1968 ? 1 : 0)) != 0)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 3;
    }
    if (this.receiveData.startingAdress + 1 + this.receiveData.quantity > 65535)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 128));
      this.sendData.exceptionCode = 2;
    }
    for (int i = 0; i < this.receiveData.quantity; i++)
    {
      int shift = i % 16;
      if (((i == this.receiveData.quantity - 1 ? 1 : 0) & (this.receiveData.quantity % 2 != 0 ? 1 : 0)) != 0)
      {
        if (shift < 8)
          shift += 8;
        else
          shift -= 8;
      }
      int mask = 1;
      mask <<= shift;
      if ((this.receiveData.receiveCoilValues[(i / 16)] & mask) == 0)
        this.coils[(this.receiveData.startingAdress + i + 1)] = false;
      else {
        this.coils[(this.receiveData.startingAdress + i + 1)] = true;
      }
    }
    if (this.sendData.exceptionCode > 0)
      this.sendData.length = 3;
    else
      this.sendData.length = 6;
    byte[] data;
    if (this.sendData.exceptionCode > 0)
      data = new byte[9];
    else {
      data = new byte[12];
    }
    this.sendData.length = ((byte)(data.length - 6));

    data[0] = ((byte)((this.sendData.transactionIdentifier & 0xFF00) >> 8));
    data[1] = ((byte)(this.sendData.transactionIdentifier & 0xFF));

    data[2] = ((byte)((this.sendData.protocolIdentifier & 0xFF00) >> 8));
    data[3] = ((byte)(this.sendData.protocolIdentifier & 0xFF));

    data[4] = ((byte)((this.sendData.length & 0xFF00) >> 8));
    data[5] = ((byte)(this.sendData.length & 0xFF));

    data[6] = this.sendData.unitIdentifier;

    data[7] = this.sendData.functionCode;

    if (this.sendData.exceptionCode > 0)
    {
      data[7] = this.sendData.errorCode;
      data[8] = this.sendData.exceptionCode;
      this.sendData.sendRegisterValues = null;
    }
    else
    {
      data[8] = ((byte)((this.receiveData.startingAdress & 0xFF00) >> 8));
      data[9] = ((byte)(this.receiveData.startingAdress & 0xFF));

      data[10] = ((byte)((this.receiveData.quantity & 0xFF00) >> 8));
      data[11] = ((byte)(this.receiveData.quantity & 0xFF));
    }

    if ((socket.isConnected() & !socket.isClosed()))
      try {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    if (this.notifyCoilsChanged != null)
      this.notifyCoilsChanged.coilsChangedEvent();
  }

  private void WriteMultipleRegisters(Socket socket)
  {
    this.sendData = new ModbusProtocoll();
    this.sendData.response = true;

    this.sendData.transactionIdentifier = this.receiveData.transactionIdentifier;
    this.sendData.protocolIdentifier = this.receiveData.protocolIdentifier;

    this.sendData.unitIdentifier = this.receiveData.unitIdentifier;
    this.sendData.functionCode = this.receiveData.functionCode;
    this.sendData.startingAdress = this.receiveData.startingAdress;
    this.sendData.quantity = this.receiveData.quantity;

    if (((this.receiveData.quantity == 0 ? 1 : 0) | (this.receiveData.quantity > 1968 ? 1 : 0)) != 0)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 144));
      this.sendData.exceptionCode = 3;
    }
    if (this.receiveData.startingAdress + 1 + this.receiveData.quantity > 65535)
    {
      this.sendData.errorCode = ((byte)(this.receiveData.functionCode + 144));
      this.sendData.exceptionCode = 2;
    }
    for (int i = 0; i < this.receiveData.quantity; i++)
    {
      this.holdingRegisters[(this.receiveData.startingAdress + i + 1)] = this.receiveData.receiveRegisterValues[i];
    }
    if (this.sendData.exceptionCode > 0)
      this.sendData.length = 3;
    else
      this.sendData.length = 6;
    byte[] data;
    if (this.sendData.exceptionCode > 0)
      data = new byte[9];
    else {
      data = new byte[12];
    }
    byte[] byteData = new byte[2];
    this.sendData.length = ((byte)(data.length - 6));

    data[0] = ((byte)((this.sendData.transactionIdentifier & 0xFF00) >> 8));
    data[1] = ((byte)(this.sendData.transactionIdentifier & 0xFF));

    data[2] = ((byte)((this.sendData.protocolIdentifier & 0xFF00) >> 8));
    data[3] = ((byte)(this.sendData.protocolIdentifier & 0xFF));

    data[4] = ((byte)((this.sendData.length & 0xFF00) >> 8));
    data[5] = ((byte)(this.sendData.length & 0xFF));

    data[6] = this.sendData.unitIdentifier;

    data[7] = this.sendData.functionCode;

    if (this.sendData.exceptionCode > 0)
    {
      data[7] = this.sendData.errorCode;
      data[8] = this.sendData.exceptionCode;
      this.sendData.sendRegisterValues = null;
    }
    else
    {
      data[8] = ((byte)((this.receiveData.startingAdress & 0xFF00) >> 8));
      data[9] = ((byte)(this.receiveData.startingAdress & 0xFF));

      data[10] = ((byte)((this.receiveData.quantity & 0xFF00) >> 8));
      data[11] = ((byte)(this.receiveData.quantity & 0xFF));
    }

    if ((socket.isConnected() & !socket.isClosed()))
      try {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    if (this.notifyHoldingRegistersChanged != null)
      this.notifyHoldingRegistersChanged.holdingRegistersChangedEvent();
  }

  private void sendException(int errorCode, int exceptionCode, Socket socket)
  {
    this.sendData = new ModbusProtocoll();
    this.sendData.response = true;

    this.sendData.transactionIdentifier = this.receiveData.transactionIdentifier;
    this.sendData.protocolIdentifier = this.receiveData.protocolIdentifier;

    this.sendData.unitIdentifier = this.receiveData.unitIdentifier;
    this.sendData.errorCode = ((byte)errorCode);
    this.sendData.exceptionCode = ((byte)exceptionCode);

    if (this.sendData.exceptionCode > 0)
      this.sendData.length = 3;
    else
      this.sendData.length = ((short)(3 + this.sendData.byteCount));
    byte[] data;
    if (this.sendData.exceptionCode > 0)
      data = new byte[9];
    else
      data = new byte[9 + this.sendData.byteCount];
    this.sendData.length = ((byte)(data.length - 6));

    data[0] = ((byte)((this.sendData.transactionIdentifier & 0xFF00) >> 8));
    data[1] = ((byte)(this.sendData.transactionIdentifier & 0xFF));

    data[2] = ((byte)((this.sendData.protocolIdentifier & 0xFF00) >> 8));
    data[3] = ((byte)(this.sendData.protocolIdentifier & 0xFF));

    data[4] = ((byte)((this.sendData.length & 0xFF00) >> 8));
    data[5] = ((byte)(this.sendData.length & 0xFF));

    data[6] = this.sendData.unitIdentifier;

    data[7] = this.sendData.errorCode;
    data[8] = this.sendData.exceptionCode;

    if ((socket.isConnected() & !socket.isClosed()))
      try {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
  }

  protected void CreateLogData()
  {
    for (int i = 0; i < 98; i++)
    {
      this.modbusLogData[(99 - i)] = this.modbusLogData[(99 - i - 2)];
    }
    this.modbusLogData[0] = this.receiveData;
    this.modbusLogData[1] = this.sendData;
    if (this.notifyLogDataChanged != null)
      this.notifyLogDataChanged.logDataChangedEvent();
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  public void setFunctionCode1Disabled(boolean functionCode1Disabled)
  {
    this.functionCode1Disabled = functionCode1Disabled;
  }

  public void setFunctionCode2Disabled(boolean functionCode2Disabled)
  {
    this.functionCode2Disabled = functionCode2Disabled;
  }

  public void setFunctionCode3Disabled(boolean functionCode3Disabled)
  {
    this.functionCode3Disabled = functionCode3Disabled;
  }

  public void setFunctionCode4Disabled(boolean functionCode4Disabled)
  {
    this.functionCode4Disabled = functionCode4Disabled;
  }

  public void setFunctionCode5Disabled(boolean functionCode5Disabled)
  {
    this.functionCode5Disabled = functionCode5Disabled;
  }

  public void setFunctionCode6Disabled(boolean functionCode6Disabled)
  {
    this.functionCode6Disabled = functionCode6Disabled;
  }

  public void setFunctionCode15Disabled(boolean functionCode15Disabled)
  {
    this.functionCode15Disabled = functionCode15Disabled;
  }

  public void setFunctionCode16Disabled(boolean functionCode16Disabled)
  {
    this.functionCode16Disabled = functionCode16Disabled;
  }

  public void setNumberOfConnectedClients(int value)
  {
    this.numberOfConnections = value;
    if (this.notifyNumberOfConnectedClientsChanged != null)
      this.notifyNumberOfConnectedClientsChanged.NumberOfConnectedClientsChanged();
  }

  public int getPort()
  {
    return this.port;
  }

  public boolean getFunctionCode1Disabled()
  {
    return this.functionCode1Disabled;
  }

  public boolean getFunctionCode2Disabled()
  {
    return this.functionCode2Disabled;
  }

  public boolean getFunctionCode3Disabled()
  {
    return this.functionCode3Disabled;
  }

  public boolean getFunctionCode4Disabled()
  {
    return this.functionCode4Disabled;
  }

  public boolean getFunctionCode5Disabled()
  {
    return this.functionCode5Disabled;
  }

  public boolean getFunctionCode6Disabled()
  {
    return this.functionCode6Disabled;
  }

  public boolean getFunctionCode15Disabled()
  {
    return this.functionCode15Disabled;
  }

  public boolean getFunctionCode16Disabled()
  {
    return this.functionCode16Disabled;
  }

  public int getNumberOfConnectedClients()
  {
    return this.numberOfConnections;
  }

  public boolean getServerRunning()
  {
    return this.serverRunning;
  }

  public ModbusProtocoll[] getLogData()
  {
    return this.modbusLogData;
  }

  public void setNotifyCoilsChanged(ICoilsChangedDelegator value)
  {
    this.notifyCoilsChanged = value;
  }

  public void setNotifyHoldingRegistersChanged(IHoldingRegistersChangedDelegator value)
  {
    this.notifyHoldingRegistersChanged = value;
  }

  public void setNotifyNumberOfConnectedClientsChanged(INumberOfConnectedClientsChangedDelegator value)
  {
    this.notifyNumberOfConnectedClientsChanged = value;
  }

  public void setNotifyLogDataChanged(ILogDataChangedDelegator value)
  {
    this.notifyLogDataChanged = value;
  }
}