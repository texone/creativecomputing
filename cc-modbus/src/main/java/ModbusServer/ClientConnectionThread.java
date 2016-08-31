package ModbusServer;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

class ClientConnectionThread extends Thread
{
  private Socket socket;
  private byte[] inBuffer = new byte[1024];
  ModbusServer easyModbusTCPServer;

  public ClientConnectionThread(Socket socket, ModbusServer easyModbusTCPServer)
  {
    this.easyModbusTCPServer = easyModbusTCPServer;
    this.socket = socket;
  }

  public void run()
  {
    this.easyModbusTCPServer.setNumberOfConnectedClients(this.easyModbusTCPServer.getNumberOfConnectedClients() + 1);
    System.out.println("Connected");
    try
    {
      while ((this.socket.isConnected() & !this.socket.isClosed() & this.easyModbusTCPServer.getServerRunning()))
      {
        this.socket.setSoTimeout(10000);

        InputStream inputStream = this.socket.getInputStream();
        inputStream.read(this.inBuffer);
        new ProcessReceivedDataThread(this.inBuffer, this.easyModbusTCPServer, this.socket).start();
      }
      this.easyModbusTCPServer.setNumberOfConnectedClients(this.easyModbusTCPServer.getNumberOfConnectedClients() - 1);
      this.socket.close();
    }
    catch (Exception e) {
      this.easyModbusTCPServer.setNumberOfConnectedClients(this.easyModbusTCPServer.getNumberOfConnectedClients() - 1);
      try
      {
        this.socket.close();
      }
      catch (Exception exc) {
      }
      e.printStackTrace();
    }
  }
}