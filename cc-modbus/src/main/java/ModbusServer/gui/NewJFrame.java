package ModbusServer.gui;

import ModbusServer.ICoilsChangedDelegator;
import ModbusServer.IHoldingRegistersChangedDelegator;
import ModbusServer.ILogDataChangedDelegator;
import ModbusServer.INumberOfConnectedClientsChangedDelegator;
import ModbusServer.ModbusProtocoll;
import ModbusServer.ModbusServer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.accessibility.AccessibleContext;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class NewJFrame extends JFrame
  implements ICoilsChangedDelegator, IHoldingRegistersChangedDelegator, INumberOfConnectedClientsChangedDelegator, ILogDataChangedDelegator
{
  public ModbusServer modbusServer = new ModbusServer();

  DefaultListModel model = new DefaultListModel();
  private JCheckBox jCheckBox1;
  private JCheckBox jCheckBox2;
  private JCheckBox jCheckBox3;
  private JCheckBox jCheckBox4;
  private JCheckBox jCheckBox5;
  private JCheckBox jCheckBox6;
  private JCheckBox jCheckBox7;
  private JCheckBox jCheckBox8;
  private JCheckBox jCheckBox9;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLabel jLabel3;
  private JLabel jLabel4;
  private JLabel jLabel5;
  private JLabel jLabel6;
  private JLabel jLabel7;
  private JList jList1;
  private JPanel jPanel1;
  private JPanel jPanel2;
  private JPanel jPanel3;
  private JPanel jPanel4;
  private JPanel jPanel5;
  private JScrollBar jScrollBar1;
  private JScrollBar jScrollBar2;
  private JScrollBar jScrollBar3;
  private JScrollBar jScrollBar4;
  private JScrollPane jScrollPane1;
  private JScrollPane jScrollPane2;
  private JScrollPane jScrollPane3;
  private JScrollPane jScrollPane4;
  private JScrollPane jScrollPane5;
  private JTabbedPane jTabbedPane1;
  private JTable jTable1;
  private JTable jTable2;
  private JTable jTable3;
  private JTable jTable4;

  public NewJFrame()
  {
    super("EasyModbusTCP Server Simulator");
    initComponents();

    this.modbusServer.setNotifyCoilsChanged(this);
    this.modbusServer.setNotifyHoldingRegistersChanged(this);
    this.modbusServer.setNotifyNumberOfConnectedClientsChanged(this);
    this.modbusServer.setNotifyLogDataChanged(this);
    try
    {
      this.modbusServer.Listen();
    } catch (Exception e) {
    }
    this.jList1.setModel(this.model);
  }

  private void initTables()
  {
    DefaultTableModel model1 = (DefaultTableModel)this.jTable1.getModel();
    DefaultTableModel model2 = (DefaultTableModel)this.jTable2.getModel();
    DefaultTableModel model3 = (DefaultTableModel)this.jTable3.getModel();
    DefaultTableModel model4 = (DefaultTableModel)this.jTable4.getModel();
    for (int i = 0; i < 25; i++)
    {
      model1.addRow(new Object[] { String.valueOf(i + 1), String.valueOf(false) });
    }
    for (int i = 0; i < 25; i++)
    {
      model2.addRow(new Object[] { String.valueOf(i + 1), String.valueOf(false) });
    }
    for (int i = 0; i < 25; i++)
    {
      model3.addRow(new Object[] { String.valueOf(i + 1), String.valueOf(0) });
    }
    for (int i = 0; i < 25; i++)
    {
      model4.addRow(new Object[] { String.valueOf(i + 1), String.valueOf(0) });
    }
  }

  private void initComponents()
  {
    this.jTabbedPane1 = new JTabbedPane();
    this.jPanel1 = new JPanel();
    this.jScrollPane1 = new JScrollPane();
    this.jTable1 = new JTable();
    this.jScrollBar1 = new JScrollBar();
    this.jPanel2 = new JPanel();
    this.jScrollPane2 = new JScrollPane();
    this.jTable2 = new JTable();
    this.jScrollBar2 = new JScrollBar();
    this.jPanel3 = new JPanel();
    this.jScrollPane3 = new JScrollPane();
    this.jTable3 = new JTable();
    this.jScrollBar3 = new JScrollBar();
    this.jPanel4 = new JPanel();
    this.jScrollPane4 = new JScrollPane();
    this.jTable4 = new JTable();
    this.jScrollBar4 = new JScrollBar();
    this.jLabel1 = new JLabel();
    this.jLabel2 = new JLabel();
    this.jLabel3 = new JLabel();
    this.jPanel5 = new JPanel();
    this.jLabel4 = new JLabel();
    this.jCheckBox1 = new JCheckBox();
    this.jCheckBox2 = new JCheckBox();
    this.jCheckBox3 = new JCheckBox();
    this.jCheckBox4 = new JCheckBox();
    this.jCheckBox5 = new JCheckBox();
    this.jCheckBox6 = new JCheckBox();
    this.jCheckBox7 = new JCheckBox();
    this.jCheckBox8 = new JCheckBox();
    this.jLabel5 = new JLabel();
    this.jLabel6 = new JLabel();
    this.jLabel7 = new JLabel();
    this.jScrollPane5 = new JScrollPane();
    this.jList1 = new JList();
    this.jCheckBox9 = new JCheckBox();

    setDefaultCloseOperation(3);
    setResizable(false);
    setSize(new Dimension(913, 586));
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        NewJFrame.this.formWindowClosing(evt);
      }
    });
    this.jTabbedPane1.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        NewJFrame.this.jTabbedPane1MouseClicked(evt);
      }
    });
    this.jTable1.setModel(new DefaultTableModel(new Object[0][], new String[] { "Address", "Value" }));

    this.jTable1.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        NewJFrame.this.jTable1MouseClicked(evt);
      }
    });
    this.jTable1.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        NewJFrame.this.jTable1PropertyChange(evt);
      }
    });
    this.jScrollPane1.setViewportView(this.jTable1);

    this.jScrollBar1.setBlockIncrement(1);
    this.jScrollBar1.setMaximum(65534);
    this.jScrollBar1.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent evt) {
        NewJFrame.this.jScrollBar1AdjustmentValueChanged(evt);
      }
    });
    GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
    this.jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane1, -1, 225, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollBar1, -2, -1, -2).addContainerGap()));

    jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane1, -1, 456, 32767).addComponent(this.jScrollBar1, -1, -1, 32767));

    this.jTabbedPane1.addTab("Discrete Inputs", this.jPanel1);

    this.jTable2.setModel(new DefaultTableModel(new Object[0][], new String[] { "Address", "Value" }));

    this.jTable2.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        NewJFrame.this.jTable2MouseClicked(evt);
      }
    });
    this.jTable2.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        NewJFrame.this.jTable2PropertyChange(evt);
      }
    });
    this.jScrollPane2.setViewportView(this.jTable2);

    this.jScrollBar2.setBlockIncrement(1);
    this.jScrollBar2.setMaximum(65534);
    this.jScrollBar2.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent evt) {
        NewJFrame.this.jScrollBar2AdjustmentValueChanged(evt);
      }
    });
    GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
    this.jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane2, -2, 228, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollBar2, -2, -1, -2).addContainerGap(-1, 32767)));

    jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane2, -1, 456, 32767).addComponent(this.jScrollBar2, -1, -1, 32767));

    this.jTabbedPane1.addTab("Coils", this.jPanel2);

    this.jTable3.setModel(new DefaultTableModel(new Object[0][], new String[] { "Address", "Value" }));

    this.jTable3.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        NewJFrame.this.jTable3PropertyChange(evt);
      }
    });
    this.jScrollPane3.setViewportView(this.jTable3);

    this.jScrollBar3.setBlockIncrement(1);
    this.jScrollBar3.setMaximum(65534);
    this.jScrollBar3.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent evt) {
        NewJFrame.this.jScrollBar3AdjustmentValueChanged(evt);
      }
    });
    GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
    this.jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane3, -2, 227, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollBar3, -2, -1, -2).addContainerGap(-1, 32767)));

    jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane3, -1, 456, 32767).addComponent(this.jScrollBar3, -1, -1, 32767));

    this.jTabbedPane1.addTab("Input Registers", this.jPanel3);

    this.jPanel4.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        NewJFrame.this.jPanel4PropertyChange(evt);
      }
    });
    this.jTable4.setModel(new DefaultTableModel(new Object[0][], new String[] { "Address", "Value" }));

    this.jTable4.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        NewJFrame.this.jTable4PropertyChange(evt);
      }
    });
    this.jScrollPane4.setViewportView(this.jTable4);

    this.jScrollBar4.setBlockIncrement(1);
    this.jScrollBar4.setMaximum(65534);
    this.jScrollBar4.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent evt) {
        NewJFrame.this.jScrollBar4AdjustmentValueChanged(evt);
      }
    });
    GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
    this.jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane4, -1, 225, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollBar4, -2, -1, -2).addContainerGap()));

    jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane4, -1, 456, 32767).addComponent(this.jScrollBar4, -1, -1, 32767));

    this.jTabbedPane1.addTab("Holding Registers", this.jPanel4);

    this.jLabel1.setFont(new Font("Microsoft Sans Serif", 1, 18));
    this.jLabel1.setForeground(new Color(102, 204, 0));
    this.jLabel1.setText("...Modbus-TCP Server Listening (Port 502)...");

    this.jLabel2.setIcon(new ImageIcon(getClass().getResource("/ModbusServer/gui/PLCLoggerCompact.jpg")));

    this.jLabel3.setText("Version 0.96");
    this.jLabel3.setCursor(new Cursor(0));

    this.jPanel5.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

    this.jLabel4.setText("Supported Function codes:");

    this.jCheckBox1.setSelected(true);
    this.jCheckBox1.setText("FC 01 (Read Coils)");
    this.jCheckBox1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        NewJFrame.this.jCheckBox1StateChanged(evt);
      }
    });
    this.jCheckBox2.setSelected(true);
    this.jCheckBox2.setText("FC 02 (Read Discrete Inputs)");
    this.jCheckBox2.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        NewJFrame.this.jCheckBox2StateChanged(evt);
      }
    });
    this.jCheckBox3.setSelected(true);
    this.jCheckBox3.setText("FC 03 (Read Holding Registers)");
    this.jCheckBox3.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        NewJFrame.this.jCheckBox3StateChanged(evt);
      }
    });
    this.jCheckBox4.setSelected(true);
    this.jCheckBox4.setText("FC 04 (Read Input Registers)");
    this.jCheckBox4.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        NewJFrame.this.jCheckBox4StateChanged(evt);
      }
    });
    this.jCheckBox5.setSelected(true);
    this.jCheckBox5.setText("FC 05 (Write Single Coil)");
    this.jCheckBox5.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        NewJFrame.this.jCheckBox5StateChanged(evt);
      }
    });
    this.jCheckBox6.setSelected(true);
    this.jCheckBox6.setText("FC 06 (Write Single Register)");
    this.jCheckBox6.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        NewJFrame.this.jCheckBox6StateChanged(evt);
      }
    });
    this.jCheckBox7.setSelected(true);
    this.jCheckBox7.setText("FC 15 (Write Multiple Coils)");
    this.jCheckBox7.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        NewJFrame.this.jCheckBox7StateChanged(evt);
      }
    });
    this.jCheckBox8.setSelected(true);
    this.jCheckBox8.setText("FC 16 (Write Multiple Registers)");
    this.jCheckBox8.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        NewJFrame.this.jCheckBox8StateChanged(evt);
      }
    });
    GroupLayout jPanel5Layout = new GroupLayout(this.jPanel5);
    this.jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addGap(44, 44, 44).addComponent(this.jLabel4)).addGroup(jPanel5Layout.createSequentialGroup().addGap(23, 23, 23).addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jCheckBox2).addComponent(this.jCheckBox1).addComponent(this.jCheckBox3).addComponent(this.jCheckBox4).addComponent(this.jCheckBox5).addComponent(this.jCheckBox6).addComponent(this.jCheckBox7).addComponent(this.jCheckBox8)))).addContainerGap(28, 32767)));

    jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel4).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCheckBox1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCheckBox2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCheckBox3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCheckBox4).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCheckBox5).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCheckBox6).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCheckBox7).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jCheckBox8).addContainerGap(11, 32767)));

    this.jLabel5.setText("<html><font color=blue><u>http://www.EasyModbusTCP.net</u></font></html>");
    this.jLabel5.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent evt) {
        NewJFrame.this.jLabel5MousePressed(evt);
      }
    });
    this.jLabel6.setText("Number of connected clients: ");

    this.jLabel7.setText("0");

    this.jScrollPane5.setViewportView(this.jList1);

    this.jCheckBox9.setSelected(true);
    this.jCheckBox9.setText("Show Protocol Informations");

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(15, 15, 15).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jLabel2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel3).addGroup(layout.createSequentialGroup().addComponent(this.jLabel5, -2, -1, -2).addGap(113, 113, 113).addComponent(this.jLabel1)))).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jScrollPane5).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)).addGroup(layout.createSequentialGroup().addComponent(this.jPanel5, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767)).addGroup(layout.createSequentialGroup().addComponent(this.jLabel6).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel7).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 223, 32767).addComponent(this.jCheckBox9).addGap(37, 37, 37))).addComponent(this.jTabbedPane1, -2, 279, -2))).addContainerGap()));

    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(16, 16, 16).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.jLabel2))).addGroup(layout.createSequentialGroup().addGap(24, 24, 24).addComponent(this.jLabel5, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel3))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(layout.createSequentialGroup().addComponent(this.jTabbedPane1, -2, 502, -2).addContainerGap()).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel6).addComponent(this.jLabel7)).addComponent(this.jCheckBox9, GroupLayout.Alignment.TRAILING)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane5).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jPanel5, -2, -1, -2)))));

    this.jLabel5.getAccessibleContext().setAccessibleName("<html><font color=blue><u>http://www.EasyModbusTCP.net</u></font></html>");
    this.jLabel5.getAccessibleContext().setAccessibleDescription("");

    pack();
  }

  private void jLabel5MousePressed(MouseEvent evt)
  {
    try {
      Desktop.getDesktop().browse(new URI("http://www.EasyModbusTCP.net"));
    } catch (Exception ex) {
    }
  }

  private void jCheckBox1StateChanged(ChangeEvent evt) {
    if (this.modbusServer.getFunctionCode1Disabled())
      this.modbusServer.setFunctionCode1Disabled(false);
    else
      this.modbusServer.setFunctionCode1Disabled(true);
  }

  private void jCheckBox2StateChanged(ChangeEvent evt) {
    if (this.modbusServer.getFunctionCode2Disabled())
      this.modbusServer.setFunctionCode2Disabled(false);
    else
      this.modbusServer.setFunctionCode2Disabled(true);
  }

  private void jCheckBox3StateChanged(ChangeEvent evt) {
    if (this.modbusServer.getFunctionCode3Disabled())
      this.modbusServer.setFunctionCode3Disabled(false);
    else
      this.modbusServer.setFunctionCode3Disabled(true);
  }

  private void jCheckBox4StateChanged(ChangeEvent evt) {
    if (this.modbusServer.getFunctionCode4Disabled())
      this.modbusServer.setFunctionCode4Disabled(false);
    else
      this.modbusServer.setFunctionCode4Disabled(true);
  }

  private void jCheckBox5StateChanged(ChangeEvent evt) {
    if (this.modbusServer.getFunctionCode5Disabled())
      this.modbusServer.setFunctionCode5Disabled(false);
    else
      this.modbusServer.setFunctionCode5Disabled(true);
  }

  private void jCheckBox6StateChanged(ChangeEvent evt) {
    if (this.modbusServer.getFunctionCode6Disabled())
      this.modbusServer.setFunctionCode6Disabled(false);
    else
      this.modbusServer.setFunctionCode6Disabled(true);
  }

  private void jCheckBox7StateChanged(ChangeEvent evt) {
    if (this.modbusServer.getFunctionCode15Disabled())
      this.modbusServer.setFunctionCode15Disabled(false);
    else
      this.modbusServer.setFunctionCode15Disabled(true);
  }

  private void jCheckBox8StateChanged(ChangeEvent evt) {
    if (this.modbusServer.getFunctionCode16Disabled())
      this.modbusServer.setFunctionCode16Disabled(false);
    else
      this.modbusServer.setFunctionCode16Disabled(true);
  }

  private void formWindowClosing(WindowEvent evt) {
    this.modbusServer.StopListening();
  }

  private void jScrollBar2AdjustmentValueChanged(AdjustmentEvent evt) {
    DefaultTableModel model2 = (DefaultTableModel)this.jTable2.getModel();
    model2.setNumRows(0);
    for (int i = 0; i < 25; i++)
    {
      if (i + this.jScrollBar2.getValue() + 1 < 65535)
        model2.addRow(new Object[] { String.valueOf(i + this.jScrollBar2.getValue() + 1), String.valueOf(this.modbusServer.coils[(i + this.jScrollBar2.getValue() + 1)]) });
    }
  }

  private void jScrollBar3AdjustmentValueChanged(AdjustmentEvent evt) {
    DefaultTableModel model3 = (DefaultTableModel)this.jTable3.getModel();
    model3.setNumRows(0);
    for (int i = 0; i < 25; i++)
    {
      if (i + this.jScrollBar3.getValue() + 1 < 65535)
        model3.addRow(new Object[] { String.valueOf(i + this.jScrollBar3.getValue() + 1), String.valueOf(this.modbusServer.inputRegisters[(i + this.jScrollBar3.getValue() + 1)]) });
    }
  }

  private void jScrollBar4AdjustmentValueChanged(AdjustmentEvent evt) {
    holdingRegistersChangedEvent();
  }

  private void jScrollBar1AdjustmentValueChanged(AdjustmentEvent evt) {
    DefaultTableModel model1 = (DefaultTableModel)this.jTable1.getModel();
    model1.setNumRows(0);
    for (int i = 0; i < 25; i++)
    {
      if (i + this.jScrollBar1.getValue() + 1 < 65535)
        model1.addRow(new Object[] { String.valueOf(i + this.jScrollBar1.getValue() + 1), String.valueOf(this.modbusServer.discreteInputs[(i + this.jScrollBar1.getValue() + 1)]) });
    }
  }

  private void jTable1PropertyChange(PropertyChangeEvent evt)
  {
    for (int i = 0; i < this.jTable1.getRowCount(); i++)
    {
      this.modbusServer.discreteInputs[(i + this.jScrollBar1.getValue() + 1)] = Boolean.valueOf((String)this.jTable1.getValueAt(i, 1)).booleanValue();
    }
    jScrollBar1AdjustmentValueChanged(null);
  }

  private void jTable2PropertyChange(PropertyChangeEvent evt) {
    for (int i = 0; i < this.jTable2.getRowCount(); i++)
    {
      this.modbusServer.coils[(i + this.jScrollBar2.getValue() + 1)] = Boolean.valueOf((String)this.jTable2.getValueAt(i, 1)).booleanValue();
    }

    jScrollBar2AdjustmentValueChanged(null);
  }

  private void jTable3PropertyChange(PropertyChangeEvent evt) {
    for (int i = 0; i < this.jTable3.getRowCount(); i++)
    {
      this.modbusServer.inputRegisters[(i + this.jScrollBar3.getValue() + 1)] = Integer.valueOf((String)this.jTable3.getValueAt(i, 1)).intValue();
    }

    jScrollBar3AdjustmentValueChanged(null);
  }

  private void jPanel4PropertyChange(PropertyChangeEvent evt)
  {
  }

  private void jTable4PropertyChange(PropertyChangeEvent evt) {
    for (int i = 0; i < this.jTable4.getRowCount(); i++)
    {
      this.modbusServer.holdingRegisters[(i + this.jScrollBar4.getValue() + 1)] = Integer.valueOf((String)this.jTable4.getValueAt(i, 1)).intValue();
    }
    jScrollBar4AdjustmentValueChanged(null);
  }

  private void jTable1MouseClicked(MouseEvent evt) {
    this.modbusServer.discreteInputs[(this.jTable1.getSelectedRow() + this.jScrollBar1.getValue() + 1)] = (this.modbusServer.discreteInputs[(this.jTable1.getSelectedRow() + this.jScrollBar1.getValue() + 1)] == false ? true : false);
    jScrollBar1AdjustmentValueChanged(null);
  }

  private void jTabbedPane1MouseClicked(MouseEvent evt)
  {
  }

  private void jTable2MouseClicked(MouseEvent evt) {
    this.modbusServer.coils[(this.jTable2.getSelectedRow() + this.jScrollBar2.getValue() + 1)] = (this.modbusServer.coils[(this.jTable2.getSelectedRow() + this.jScrollBar2.getValue() + 1)] == false ? true : false);
    jScrollBar2AdjustmentValueChanged(null);
  }

  public void coilsChangedEvent()
  {
    DefaultTableModel model2 = (DefaultTableModel)this.jTable2.getModel();
    model2.setNumRows(0);
    for (int i = 0; i < 25; i++)
    {
      if (i + this.jScrollBar2.getValue() + 1 < 65535)
        model2.addRow(new Object[] { String.valueOf(i + this.jScrollBar2.getValue() + 1), String.valueOf(this.modbusServer.coils[(i + this.jScrollBar2.getValue() + 1)]) });
    }
  }

  public void holdingRegistersChangedEvent()
  {
    DefaultTableModel model4 = (DefaultTableModel)this.jTable4.getModel();
    model4.setNumRows(0);
    for (int i = 0; i < 25; i++)
    {
      if (i + this.jScrollBar4.getValue() + 1 < 65535)
        model4.addRow(new Object[] { String.valueOf(i + this.jScrollBar4.getValue() + 1), String.valueOf(this.modbusServer.holdingRegisters[(i + this.jScrollBar4.getValue() + 1)]) });
    }
  }

  public void NumberOfConnectedClientsChanged()
  {
    this.jLabel7.setText(String.valueOf(this.modbusServer.getNumberOfConnectedClients()));
  }

  public void logDataChangedEvent()
  {
    if (this.jCheckBox9.getSelectedObjects() == null) {
      return;
    }

    for (int i = 0; i < 2; i++)
    {
      if (this.modbusServer.getLogData()[i] == null)
        break;
      if (this.modbusServer.getLogData()[i].request)
      {
        String listBoxData = this.modbusServer.getLogData()[i].timeStamp.getTime() + " Request from Client - Functioncode: " + String.valueOf(this.modbusServer.getLogData()[i].functionCode);
        if (this.modbusServer.getLogData()[i].functionCode <= 4)
        {
          listBoxData = listBoxData + " Starting Address: " + String.valueOf(this.modbusServer.getLogData()[i].startingAdress) + " Quantity: " + String.valueOf(this.modbusServer.getLogData()[i].quantity);
        }
        if (this.modbusServer.getLogData()[i].functionCode == 5)
        {
          listBoxData = listBoxData + " Output Address: " + String.valueOf(this.modbusServer.getLogData()[i].startingAdress) + " Output Value: ";
          if (this.modbusServer.getLogData()[i].receiveCoilValues[0] == 0)
            listBoxData = listBoxData + "False";
          if (this.modbusServer.getLogData()[i].receiveCoilValues[0] == 65280)
            listBoxData = listBoxData + "True";
        }
        if (this.modbusServer.getLogData()[i].functionCode == 6)
        {
          listBoxData = listBoxData + " Starting Address: " + String.valueOf(this.modbusServer.getLogData()[i].startingAdress) + " Register Value: " + String.valueOf(this.modbusServer.getLogData()[i].receiveRegisterValues[0]);
        }
        if (this.modbusServer.getLogData()[i].functionCode == 15)
        {
          listBoxData = listBoxData + " Starting Address: " + String.valueOf(this.modbusServer.getLogData()[i].startingAdress) + " Quantity: " + String.valueOf(this.modbusServer.getLogData()[i].quantity) + " Byte Count: " + String.valueOf(this.modbusServer.getLogData()[i].byteCount) + " Values Received: ";
          for (int j = 0; j < this.modbusServer.getLogData()[i].quantity; j++)
          {
            int shift = j % 16;
            if (((i == this.modbusServer.getLogData()[i].quantity - 1 ? 1 : 0) & (this.modbusServer.getLogData()[i].quantity % 2 != 0 ? 1 : 0)) != 0)
            {
              if (shift < 8)
                shift += 8;
              else
                shift -= 8;
            }
            int mask = 1;
            mask <<= shift;
            if ((this.modbusServer.getLogData()[i].receiveCoilValues[(j / 16)] & mask) == 0)
              listBoxData = listBoxData + " False";
            else
              listBoxData = listBoxData + " True";
          }
        }
        if (this.modbusServer.getLogData()[i].functionCode == 16)
        {
          listBoxData = listBoxData + " Starting Address: " + String.valueOf(this.modbusServer.getLogData()[i].startingAdress) + " Quantity: " + String.valueOf(this.modbusServer.getLogData()[i].quantity) + " Byte Count: " + String.valueOf(this.modbusServer.getLogData()[i].byteCount) + " Values Received: ";
          for (int j = 0; j < this.modbusServer.getLogData()[i].quantity; j++)
          {
            listBoxData = listBoxData + " " + this.modbusServer.getLogData()[i].receiveRegisterValues[j];
          }
        }
        this.model.add(0, listBoxData);
      }

      if (this.modbusServer.getLogData()[i].response)
      {
        if (this.modbusServer.getLogData()[i].exceptionCode > 0)
        {
          String listBoxData = "Response To Client - Error code: " + String.valueOf(this.modbusServer.getLogData()[i].errorCode);
          listBoxData = listBoxData + " Exception Code: " + String.valueOf(this.modbusServer.getLogData()[i].exceptionCode);
          this.model.add(0, listBoxData);
        }
        else
        {
          String listBoxData = this.modbusServer.getLogData()[i].timeStamp.getTime() + " Response To Client - Functioncode: " + String.valueOf(this.modbusServer.getLogData()[i].functionCode);

          if (this.modbusServer.getLogData()[i].functionCode <= 4)
          {
            listBoxData = listBoxData + " Bytecount: " + String.valueOf(this.modbusServer.getLogData()[i].byteCount) + " Values sent: ";
          }
          if (this.modbusServer.getLogData()[i].functionCode == 5)
          {
            listBoxData = listBoxData + " Starting Address: " + String.valueOf(this.modbusServer.getLogData()[i].startingAdress) + " Output Value: ";
            if (this.modbusServer.getLogData()[i].receiveCoilValues[0] == 0)
              listBoxData = listBoxData + "False";
            if (this.modbusServer.getLogData()[i].receiveCoilValues[0] == 65280)
              listBoxData = listBoxData + "True";
          }
          if (this.modbusServer.getLogData()[i].functionCode == 6)
          {
            listBoxData = listBoxData + " Starting Address: " + String.valueOf(this.modbusServer.getLogData()[i].startingAdress) + " Register Value: " + String.valueOf(this.modbusServer.getLogData()[i].receiveRegisterValues[0]);
          }
          if (this.modbusServer.getLogData()[i].functionCode == 15)
          {
            listBoxData = listBoxData + " Starting Address: " + String.valueOf(this.modbusServer.getLogData()[i].startingAdress) + " Quantity: " + String.valueOf(this.modbusServer.getLogData()[i].quantity);
          }
          if (this.modbusServer.getLogData()[i].functionCode == 16)
          {
            listBoxData = listBoxData + " Starting Address: " + String.valueOf(this.modbusServer.getLogData()[i].startingAdress) + " Quantity: " + String.valueOf(this.modbusServer.getLogData()[i].quantity);
          }
          if (this.modbusServer.getLogData()[i].sendCoilValues != null)
          {
            for (int j = 0; j < this.modbusServer.getLogData()[i].sendCoilValues.length; j++)
            {
              listBoxData = listBoxData + String.valueOf(this.modbusServer.getLogData()[i].sendCoilValues[j]) + " ";
            }
          }
          if (this.modbusServer.getLogData()[i].sendRegisterValues != null)
          {
            for (int j = 0; j < this.modbusServer.getLogData()[i].sendRegisterValues.length; j++)
            {
              listBoxData = listBoxData + String.valueOf(this.modbusServer.getLogData()[i].sendRegisterValues[j]) + " ";
            }
          }
          this.model.add(0, listBoxData);
        }
      }
    }
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
      Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedLookAndFeelException ex) {
      Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
    }

    EventQueue.invokeLater(new Runnable() {
      public void run() {
        new NewJFrame().setVisible(true);
      }
    });
  }
}