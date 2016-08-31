package ModbusClientExample;

import ModbusClient.ModbusClient;
import ModbusClient.ReceiveDataChangedListener;
import ModbusClient.SendDataChangedListener;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class EasyModbusTCPClientExampleGUI extends JFrame
  implements ReceiveDataChangedListener, SendDataChangedListener
{
  private ModbusClient modbusClient;
  private JButton jButton1;
  private JButton jButton2;
  private JButton jButton3;
  private JButton jButton4;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLabel jLabel3;
  private JLabel jLabel4;
  private JLabel jLabel5;
  private JLabel jLabel6;
  private JLabel jLabel8;
  private JList<String> jList1;
  private JPanel jPanel1;
  private JScrollPane jScrollPane1;
  private JScrollPane jScrollPane2;
  private JTextArea jTextArea1;
  private JTextField jTextFieldIPAddress;
  private JTextField jTextFieldNumberOfValues;
  private JTextField jTextFieldPort;
  private JTextField jTextFieldStartingAddress;

  public EasyModbusTCPClientExampleGUI()
  {
    initComponents();
    this.modbusClient = new ModbusClient();
    this.modbusClient.addReveiveDataChangedListener(this);
    this.modbusClient.addSendDataChangedListener(this);
  }

  public void ReceiveDataChanged()
  {
    this.jTextArea1.append("Rx:");
    for (int i = 0; i < this.modbusClient.receiveData.length; i++)
    {
      this.jTextArea1.append(" ");
      if (this.modbusClient.receiveData[i] < 16)
        this.jTextArea1.append("0");
      this.jTextArea1.append(Integer.toHexString(this.modbusClient.receiveData[i]));
    }

    this.jTextArea1.append("\n");
  }

  public void SendDataChanged()
  {
    this.jTextArea1.append("Tx:");
    for (int i = 0; i < this.modbusClient.sendData.length; i++)
    {
      this.jTextArea1.append(" ");
      if (this.modbusClient.sendData[i] < 16)
        this.jTextArea1.append("0");
      this.jTextArea1.append(Integer.toHexString(this.modbusClient.sendData[i]));
    }
    this.jTextArea1.append("\n");
  }

  private void initComponents()
  {
    this.jLabel1 = new JLabel();
    this.jLabel3 = new JLabel();
    this.jLabel4 = new JLabel();
    this.jTextFieldIPAddress = new JTextField();
    this.jTextFieldPort = new JTextField();
    this.jButton1 = new JButton();
    this.jButton2 = new JButton();
    this.jButton3 = new JButton();
    this.jButton4 = new JButton();
    this.jPanel1 = new JPanel();
    this.jLabel5 = new JLabel();
    this.jTextFieldStartingAddress = new JTextField();
    this.jLabel6 = new JLabel();
    this.jTextFieldNumberOfValues = new JTextField();
    this.jScrollPane1 = new JScrollPane();
    this.jList1 = new JList();
    this.jLabel8 = new JLabel();
    this.jLabel2 = new JLabel();
    this.jScrollPane2 = new JScrollPane();
    this.jTextArea1 = new JTextArea();

    setDefaultCloseOperation(3);

    this.jLabel1.setToolTipText("");
    this.jLabel1.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        EasyModbusTCPClientExampleGUI.this.jLabel1MouseClicked(evt);
      }
    });
    this.jLabel3.setText("IP-Address");

    this.jLabel4.setText("Port");

    this.jTextFieldIPAddress.setText("127.0.0.1");
    this.jTextFieldIPAddress.setToolTipText("");

    this.jTextFieldPort.setText("502");

    this.jButton1.setText("Read Coils - FC1");
    this.jButton1.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        EasyModbusTCPClientExampleGUI.this.jButton1MouseClicked(evt);
      }
    });
    this.jButton2.setText("Read Discrete Inputs - FC2");
    this.jButton2.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        EasyModbusTCPClientExampleGUI.this.jButton2MouseClicked(evt);
      }
    });
    this.jButton3.setText("Read Holding Registers - FC3");
    this.jButton3.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        EasyModbusTCPClientExampleGUI.this.jButton3MouseClicked(evt);
      }
    });
    this.jButton4.setText("Read Input Registers - FC4");
    this.jButton4.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        EasyModbusTCPClientExampleGUI.this.jButton4MouseClicked(evt);
      }
    });
    this.jLabel5.setText("Starting Address");

    this.jTextFieldStartingAddress.setText("1");

    this.jLabel6.setText("Number of Values");

    this.jTextFieldNumberOfValues.setText("1");

    this.jScrollPane1.setViewportView(this.jList1);

    this.jLabel8.setIcon(new ImageIcon(getClass().getResource("/ModbusClientExample/Rossmann-Engineering_Logo_klein.png")));

    this.jLabel2.setForeground(new Color(0, 0, 204));
    this.jLabel2.setText("http://www.EasyModbusTCP.net");

    GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
    this.jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel5).addComponent(this.jLabel6).addComponent(this.jTextFieldStartingAddress, -2, 48, -2).addComponent(this.jTextFieldNumberOfValues, -2, 48, -2)).addGap(10, 10, 10).addComponent(this.jScrollPane1, -2, 0, 32767)).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addGap(0, 0, 32767).addComponent(this.jLabel2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel8))).addContainerGap()));

    jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel8).addComponent(this.jLabel2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jLabel5).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jTextFieldStartingAddress, -2, -1, -2).addGap(18, 18, 18).addComponent(this.jLabel6).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jTextFieldNumberOfValues, -2, -1, -2)).addComponent(this.jScrollPane1, -2, 155, -2)).addContainerGap(-1, 32767)));

    this.jTextArea1.setEditable(false);
    this.jTextArea1.setBackground(new Color(204, 204, 204));
    this.jTextArea1.setColumns(20);
    this.jTextArea1.setRows(5);
    this.jScrollPane2.setViewportView(this.jTextArea1);

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTextFieldIPAddress, -2, 122, -2).addComponent(this.jLabel3)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel4).addComponent(this.jTextFieldPort, -2, 46, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(this.jButton4, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.jButton1, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.jButton2, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.jButton3, GroupLayout.Alignment.LEADING, -1, -1, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767))).addComponent(this.jPanel1, -2, -1, -2)).addComponent(this.jScrollPane2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.jLabel1)));

    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(this.jLabel1).addGap(318, 318, 318)).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel3).addComponent(this.jLabel4)).addGap(18, 18, 18).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jTextFieldIPAddress, -2, -1, -2).addComponent(this.jTextFieldPort, -2, -1, -2)).addGap(18, 18, 18).addComponent(this.jButton1).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jButton2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jButton3).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jButton4)).addComponent(this.jPanel1, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane2, -1, 103, 32767).addContainerGap()));

    pack();
  }

  private void jLabel1MouseClicked(MouseEvent evt) {
    if (Desktop.isDesktopSupported())
    {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.BROWSE))
      {
        try
        {
          desktop.browse(new URI("www.easymodbustcp.net"));
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
          e.printStackTrace();
        }
      }
    }
  }

  private void jButton1MouseClicked(MouseEvent evt) {
    if (!this.modbusClient.isConnected())
    {
      this.modbusClient.setipAddress(this.jTextFieldIPAddress.getText());
      this.modbusClient.setPort(Integer.valueOf(this.jTextFieldPort.getText()).intValue());
      try
      {
        this.modbusClient.Connect();
      }
      catch (Exception e)
      {
        JOptionPane.showMessageDialog(null, "Connection failed", "Connection failed", 2);
      }
    }
    DefaultListModel listModel = new DefaultListModel();
    int startingAddress = Integer.valueOf(this.jTextFieldStartingAddress.getText()).intValue() - 1;
    int numberOfValues = Integer.valueOf(this.jTextFieldNumberOfValues.getText()).intValue();
    try
    {
      boolean[] serverResponse = this.modbusClient.ReadCoils(startingAddress, numberOfValues);
      for (int i = 0; i < serverResponse.length; i++)
        listModel.addElement(Boolean.valueOf(serverResponse[i]));
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(null, "Server response error", "Connection failed", 2);
    }
    this.jList1.setModel(listModel);
  }

  private void jButton2MouseClicked(MouseEvent evt) {
    if (!this.modbusClient.isConnected())
    {
      this.modbusClient.setipAddress(this.jTextFieldIPAddress.getText());
      this.modbusClient.setPort(Integer.valueOf(this.jTextFieldPort.getText()).intValue());
      try
      {
        this.modbusClient.Connect();
      }
      catch (Exception e)
      {
        JOptionPane.showMessageDialog(null, "Connection failed", "Connection failed", 2);
      }
    }
    DefaultListModel listModel = new DefaultListModel();
    int startingAddress = Integer.valueOf(this.jTextFieldStartingAddress.getText()).intValue() - 1;
    int numberOfValues = Integer.valueOf(this.jTextFieldNumberOfValues.getText()).intValue();
    try
    {
      boolean[] serverResponse = this.modbusClient.ReadDiscreteInputs(startingAddress, numberOfValues);
      for (int i = 0; i < serverResponse.length; i++)
        listModel.addElement(Boolean.valueOf(serverResponse[i]));
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(null, "Server response error ", "Connection failed", 2);
    }
    this.jList1.setModel(listModel);
  }

  private void jButton3MouseClicked(MouseEvent evt) {
    if (!this.modbusClient.isConnected())
    {
      this.modbusClient.setipAddress(this.jTextFieldIPAddress.getText());
      this.modbusClient.setPort(Integer.valueOf(this.jTextFieldPort.getText()).intValue());
      try
      {
        this.modbusClient.Connect();
      }
      catch (Exception e)
      {
        JOptionPane.showMessageDialog(null, "Connection failed", "Connection failed", 2);
      }
    }
    DefaultListModel listModel = new DefaultListModel();
    int startingAddress = Integer.valueOf(this.jTextFieldStartingAddress.getText()).intValue() - 1;
    int numberOfValues = Integer.valueOf(this.jTextFieldNumberOfValues.getText()).intValue();
    try
    {
      int[] serverResponse = this.modbusClient.ReadHoldingRegisters(startingAddress, numberOfValues);
      for (int i = 0; i < serverResponse.length; i++)
        listModel.addElement(Integer.valueOf(serverResponse[i]));
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(null, "Server response error", "Connection failed", 2);
    }
    this.jList1.setModel(listModel);
  }

  private void jButton4MouseClicked(MouseEvent evt) {
    if (!this.modbusClient.isConnected())
    {
      this.modbusClient.setipAddress(this.jTextFieldIPAddress.getText());
      this.modbusClient.setPort(Integer.valueOf(this.jTextFieldPort.getText()).intValue());
      try
      {
        this.modbusClient.Connect();
      }
      catch (Exception e)
      {
        JOptionPane.showMessageDialog(null, "Connection failed", "Connection failed", 2);
      }
    }
    DefaultListModel listModel = new DefaultListModel();
    int startingAddress = Integer.valueOf(this.jTextFieldStartingAddress.getText()).intValue() - 1;
    int numberOfValues = Integer.valueOf(this.jTextFieldNumberOfValues.getText()).intValue();
    try
    {
      int[] serverResponse = this.modbusClient.ReadInputRegisters(startingAddress, numberOfValues);
      for (int i = 0; i < serverResponse.length; i++)
        listModel.addElement(Integer.valueOf(serverResponse[i]));
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(null, "Server response error", "Connection failed", 2);
    }
    this.jList1.setModel(listModel);
  }

  public static void main(String[] args)
  {
    try
    {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
    }
    catch (ClassNotFoundException ex) {
      Logger.getLogger(EasyModbusTCPClientExampleGUI.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      Logger.getLogger(EasyModbusTCPClientExampleGUI.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(EasyModbusTCPClientExampleGUI.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedLookAndFeelException ex) {
      Logger.getLogger(EasyModbusTCPClientExampleGUI.class.getName()).log(Level.SEVERE, null, ex);
    }

    EventQueue.invokeLater(new Runnable() {
      public void run() {
        new EasyModbusTCPClientExampleGUI().setVisible(true);
      }
    });
  }
}