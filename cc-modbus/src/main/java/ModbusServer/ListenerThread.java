package ModbusServer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

class ListenerThread extends Thread
{
  ModbusServer easyModbusTCPServer;

  public ListenerThread(ModbusServer easyModbusTCPServer)
  {
    this.easyModbusTCPServer = easyModbusTCPServer;
  }

  public void run()
  {
    try {
      ServerSocket serverSocket = new ServerSocket(this.easyModbusTCPServer.getPort());

      while ((this.easyModbusTCPServer.getServerRunning() & !isInterrupted()))
      {
        Socket socket = serverSocket.accept();
        new ClientConnectionThread(socket, this.easyModbusTCPServer).start();
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());

      e.printStackTrace();
    }
  }
}