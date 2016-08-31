package ModbusServer;

import java.net.Socket;
import java.util.Calendar;

class ProcessReceivedDataThread extends Thread
{
  short[] inBuffer;
  ModbusServer easyModbusTCPServer;
  Socket socket;

  public ProcessReceivedDataThread(byte[] inBuffer, ModbusServer easyModbusTCPServer, Socket socket)
  {
    this.socket = socket;
    this.inBuffer = new short[inBuffer.length];
    for (int i = 0; i < inBuffer.length; i++)
    {
      this.inBuffer[i] = ((short)((short)inBuffer[i] & 0xFF));
    }
    this.easyModbusTCPServer = easyModbusTCPServer;
  }

  public void run()
  {
    synchronized (this.easyModbusTCPServer)
    {
      short[] wordData = new short[1];
      short[] byteData = new short[2];
      this.easyModbusTCPServer.receiveData = new ModbusProtocoll();
      this.easyModbusTCPServer.receiveData.timeStamp = Calendar.getInstance();
      this.easyModbusTCPServer.receiveData.request = true;

      byteData[1] = this.inBuffer[0];
      byteData[0] = this.inBuffer[1];
      wordData[0] = ((short)byteArrayToInt(byteData));
      this.easyModbusTCPServer.receiveData.transactionIdentifier = wordData[0];

      byteData[1] = this.inBuffer[2];
      byteData[0] = this.inBuffer[3];
      wordData[0] = ((short)byteArrayToInt(byteData));
      this.easyModbusTCPServer.receiveData.protocolIdentifier = wordData[0];

      byteData[1] = this.inBuffer[4];
      byteData[0] = this.inBuffer[5];
      wordData[0] = ((short)byteArrayToInt(byteData));
      this.easyModbusTCPServer.receiveData.length = wordData[0];

      this.easyModbusTCPServer.receiveData.unitIdentifier = ((byte)this.inBuffer[6]);

      this.easyModbusTCPServer.receiveData.functionCode = ((byte)this.inBuffer[7]);

      byteData[1] = this.inBuffer[8];
      byteData[0] = this.inBuffer[9];
      wordData[0] = ((short)byteArrayToInt(byteData));
      this.easyModbusTCPServer.receiveData.startingAdress = wordData[0];

      if (this.easyModbusTCPServer.receiveData.functionCode <= 4)
      {
        byteData[1] = this.inBuffer[10];
        byteData[0] = this.inBuffer[11];
        wordData[0] = ((short)byteArrayToInt(byteData));
        this.easyModbusTCPServer.receiveData.quantity = wordData[0];
      }
      if (this.easyModbusTCPServer.receiveData.functionCode == 5)
      {
        this.easyModbusTCPServer.receiveData.receiveCoilValues = new short[1];

        byteData[1] = this.inBuffer[10];
        byteData[0] = this.inBuffer[11];
        this.easyModbusTCPServer.receiveData.receiveCoilValues[0] = ((short)byteArrayToInt(byteData));
      }
      if (this.easyModbusTCPServer.receiveData.functionCode == 6)
      {
        this.easyModbusTCPServer.receiveData.receiveRegisterValues = new int[1];

        byteData[1] = this.inBuffer[10];
        byteData[0] = this.inBuffer[11];
        this.easyModbusTCPServer.receiveData.receiveRegisterValues[0] = byteArrayToInt(byteData);
      }
      if (this.easyModbusTCPServer.receiveData.functionCode == 15)
      {
        byteData[1] = this.inBuffer[10];
        byteData[0] = this.inBuffer[11];
        wordData[0] = ((short)byteArrayToInt(byteData));
        this.easyModbusTCPServer.receiveData.quantity = wordData[0];

        this.easyModbusTCPServer.receiveData.byteCount = ((short)(byte)this.inBuffer[12]);

        if (this.easyModbusTCPServer.receiveData.byteCount % 2 != 0)
          this.easyModbusTCPServer.receiveData.receiveCoilValues = new short[this.easyModbusTCPServer.receiveData.byteCount / 2 + 1];
        else {
          this.easyModbusTCPServer.receiveData.receiveCoilValues = new short[this.easyModbusTCPServer.receiveData.byteCount / 2];
        }
        for (int i = 0; i < this.easyModbusTCPServer.receiveData.byteCount; i++)
        {
          if (i % 2 == 1)
            this.easyModbusTCPServer.receiveData.receiveCoilValues[(i / 2)] = ((short)(this.easyModbusTCPServer.receiveData.receiveCoilValues[(i / 2)] + 256 * this.inBuffer[(13 + i)]));
          else
            this.easyModbusTCPServer.receiveData.receiveCoilValues[(i / 2)] = this.inBuffer[(13 + i)];
        }
      }
      if (this.easyModbusTCPServer.receiveData.functionCode == 16)
      {
        byteData[1] = this.inBuffer[10];
        byteData[0] = this.inBuffer[11];
        wordData[0] = ((short)byteArrayToInt(byteData));
        this.easyModbusTCPServer.receiveData.quantity = wordData[0];

        this.easyModbusTCPServer.receiveData.byteCount = ((short)(byte)this.inBuffer[12]);
        this.easyModbusTCPServer.receiveData.receiveRegisterValues = new int[this.easyModbusTCPServer.receiveData.quantity];
        for (int i = 0; i < this.easyModbusTCPServer.receiveData.quantity; i++)
        {
          byteData[1] = this.inBuffer[(13 + i * 2)];
          byteData[0] = this.inBuffer[(14 + i * 2)];
          this.easyModbusTCPServer.receiveData.receiveRegisterValues[i] = byteData[0];
          this.easyModbusTCPServer.receiveData.receiveRegisterValues[i] += (byteData[1] << 8);
        }
      }
      this.easyModbusTCPServer.CreateAnswer(this.socket);
      this.easyModbusTCPServer.CreateLogData();
    }
  }

  public int byteArrayToInt(short[] byteArray)
  {
    int returnValue = byteArray[0];
    returnValue += 256 * byteArray[1];
    return returnValue;
  }
}