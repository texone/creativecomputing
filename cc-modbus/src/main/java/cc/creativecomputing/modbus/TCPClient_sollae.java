package cc.creativecomputing.modbus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

@SuppressWarnings("serial")
public class TCPClient_sollae extends JFrame {

	/* DESCRIPTION
	 *	Constructor
	 *  Initializate the Frame
	 */
    public TCPClient_sollae() {
    	super("TCP Client");
        initComponents();
    }

    /* DESCRIPTION
     *	Make the User Interface 
     *  - Frame, Button, TextArea and so on 
     */
    private void initComponents() {

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        cr_check = new JCheckBox();
        lf_check = new JCheckBox();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        send_text = new JTextField();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        ip_text = new JTextField();
        port_text = new JTextField();
        connect_btn = new JButton();
        disconn_btn = new JButton();
        exit_btn = new JButton();
        send_btn = new JButton();
        status = new JTextArea();
        data = new JTextArea();
        jScrollPane1 = new JScrollPane(status);
        jScrollPane2 = new JScrollPane(data);
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("STATUS");
        jLabel2.setText("DATA : HEX | ASCII");
        jLabel3.setText("SEND");

        status.setLineWrap(true);
        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        data.setLineWrap(true);
        jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        jLabel4.setText("CR");
        jLabel5.setText("LF");


        jLabel6.setText("REMOTE IP");
        jLabel7.setText("PORT");

        connect_btn.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        connect_btn.setText("CONNECT");
        // connect button click event
        connect_btn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
            	connect_btnMouseClicked(evt);
            }
        });


        disconn_btn.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        disconn_btn.setText("DISCONNECT");
        // disconnect button click event 
        disconn_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	disconn_btnMouseClicked(evt);
            }
        });

        
        exit_btn.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        exit_btn.setText("CLOSE");

        
        send_btn.setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        send_btn.setText("SEND");
        // send button click event
        send_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	send_btnMouseClicked(evt);
            }
        });
        port_text.setText("1470");
        
        send_btn.setEnabled(false);
        disconn_btn.setEnabled(false);
        connect_btn.setEnabled(true);
 
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(send_text, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 349, Short.MAX_VALUE)
                                .addComponent(cr_check)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(8, 8, 8)
                                .addComponent(lf_check)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5))
                            .addComponent(send_btn))
                        .addGap(13, 13, 13))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ip_text, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(port_text, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connect_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(disconn_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(cr_check)
                    .addComponent(jLabel5)
                    .addComponent(lf_check)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(send_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(send_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ip_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(port_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(connect_btn)
                    .addComponent(disconn_btn))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();

    }
    /* DESCRIPTION
     *	Connect button event method,
     *  Call the connect method
     * ARGUMENTS
     *	ip - Peer IP variable, integer
     *	port - Peer Port variable, integer
     *  dlg - Warning dialog, Dialog
     *  msg - Warning message, Label
     *  ok - confirm button on the warning dialog 
     */
	protected void connect_btnMouseClicked(MouseEvent evt) {
		
        ip = ip_text.getText();
        port = Integer.parseInt(port_text.getText());    
        
        final Dialog dlg = new Dialog(this, "Warning!");
        dlg.setSize(300,100);
        Label msg = new Label("Input the correct Ip address and Port number.", Label.CENTER);
        Button ok = new Button("OK");
        	
        dlg.setLayout(new FlowLayout());
        dlg.add(msg);
        dlg.add(ok);
        	
        ok.addMouseListener(new MouseAdapter(){
        	public void mouseClicked(MouseEvent e){
        		dlg.dispose();
        	}
        });
        
        // if user didn't a peer ip, it shows alarm dialog
        if(ip.equals("")){
		    dlg.setVisible(true);
        }
        
        // Call the connect method
        else{
        	
        	try{
        		connect();
        			            
        	}catch(Exception e){
        			        	
        		dlg.setVisible(true);
        		System.out.println(e);

        	}    	

        }
	}
	

	/* DESCRIPTION
	 *	1. Make a socket and receive buffer
	 *  2. Receiving the packet thread start
	 * ARGUMENTS
	 *	sock - new socket
	 *	pw - output stream 
	 *	br - input stream 
	 *  connect_btn - connect button object
	 *  send_btn - send button object
	 *  disconn_btn - disconnect button object 
	 */	
	private void connect() {	
		
	   	try {
	   		
			sock = new Socket(ip, port);
			status.append("Connected ("+port+" port)\n");
			
			pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			// Start the thread 
			WinInputThread wit = new WinInputThread(sock, br);
			wit.start();
			
			connect_btn.setEnabled(false);
			send_btn.setEnabled(true);
			disconn_btn.setEnabled(true);
    	} 
		catch (IOException e) {
			e.printStackTrace();
			status.append("Warning : Connect Error !!");
			status.append("\n");
		}
	}
	
	/* DESCRIPTION
	 *	Convert String to Hex method
	 * ARGUMENTS
	 *	hex - hexadigit string
	 *	str - ascii string  
	 * RETURN VALUE
	 *  returns result of the conversion
	 */	
    public String StringtoHex(String str)
    {
    	String hex = "";
    	for(int i=0; i<str.length(); i++)
    	{   
    		System.out.println(Integer.toHexString((int)str.charAt(i)));
    		if(Integer.toHexString((int)str.charAt(i)).length() < 2)
        		hex = hex + "0" + Integer.toHexString((int)str.charAt(i)) + " ";
    		else
    			hex = hex + Integer.toHexString((int)str.charAt(i)) + " ";
    	}
    	
		return hex;	
    
    }
    
    /* DESCRIPTION
     *	Send button event method,
     *  Send the data to socket 
     * ARGUMENTS
     *	sendstr - send data, String
     *  hexstr -  send data converted to hex, String 
     *  dlg - Warning dialog, Dialog
     *  msg - Warning message, Label
     *  ok - confirm button on the warning dialog 
     *  pw - output stream
     */
	protected void send_btnMouseClicked(MouseEvent evt) {
	
		String sendstr = "";
		String hexstr = "";
		
		// if the user click the send button when the socket is close, it shows warning dialog
		if(sock == null){
			
        	final Dialog dlg = new Dialog(this, "Warning!");
        	dlg.setSize(300,100);
        	Label msg = new Label("Connect the socket, first.", Label.CENTER);
        	Button ok = new Button("OK");
        	
        	dlg.setLayout(new FlowLayout());
        	dlg.add(msg);
        	dlg.add(ok);
        	
        	ok.addMouseListener(new MouseAdapter(){
        		public void mouseClicked(MouseEvent e){
        			dlg.dispose();
        		}
        	});
        	
        	dlg.setVisible(true);
		}
		
		// send the data to socket 
		else{
				
			sendstr = send_text.getText();
			
			if(sendstr.equals("") && cr_check.isSelected() == false && lf_check.isSelected() == false )
			{
				
	        	final Dialog dlg = new Dialog(this, "Warning!");
	        	dlg.setSize(300,100);
	        	Label msg = new Label("Insert the message", Label.CENTER);
	        	Button ok = new Button("OK");
	        	
	            dlg.setLayout(new FlowLayout());
	            dlg.add(msg);
	            dlg.add(ok);
	            
	        	ok.addMouseListener(new MouseAdapter(){
	        		public void mouseClicked(MouseEvent e){
	        			dlg.dispose();
	        		}
	        	});
	        	
	        	dlg.setVisible(true);	          
			}
			else
			{
				pw.print(sendstr);
							
				if(cr_check.isSelected())
				{
					pw.print("\r");
					sendstr = sendstr + "\r";
				}
				if(lf_check.isSelected())
				{
					pw.print("\n");
					sendstr = sendstr + "\n";
				}
				pw.flush();
				
				hexstr = StringtoHex(sendstr);
				
				data.append("Send data : ");			
				data.append(hexstr+" | ");
				data.append(sendstr);
				data.append("\n");
				
				send_text.setText("");
				sendstr = "";
			}
			
		}
	}

    /* DESCRIPTION
     *	disconnect button event method,
     *  disconnect the socket connection 
     * ARGUMENTS
     *	sendstr - send data, String
     *  hexstr -  send data converted to hex, String 
     *  dlg - Warning dialog, Dialog
     *  msg - Warning message, Label
     *  ok - confirm button on the warning dialog 
     *  pw - output stream
     *  br - input stream
     *  sock - socket 
	 *  connect_btn - connect button object
	 *  send_btn - send button object
	 *  disconn_btn - disconnect button object 
	 */
	protected void disconn_btnMouseClicked(MouseEvent evt) {
		
		try{
			pw.close();
			br.close();
			sock.close();
			sock = null;
			
			status.append("Close\n");
			
			send_btn.setEnabled(false);
			disconn_btn.setEnabled(false);
			connect_btn.setEnabled(true);
			
		}catch(Exception e){
			status.setText("Warning : socket Close error");
			status.append("\n");
		}
	}
	
    /* DESCRIPTION
     *	main method
	 */	
    public static void main(String args[]) {
        TCPClient_sollae TCPClient = new TCPClient_sollae();
        TCPClient.setVisible(true);
    }
    	
	/* DESCRIPTION
	 *	Thread for packet receiving 
	 */	
	class WinInputThread extends Thread{
		private Socket sock = null;
		private BufferedReader br = null;
		
		public WinInputThread(Socket sock, BufferedReader br){
			this.sock = sock;
			this.br = br;
		}

		/* DESCRIPTION
		 *	Convert character to Hex method
		 * ARGUMENTS
		 *	hexstr - hexadigit string
		 *	buf - ascii data array  
		 * RETURN VALUE
		 *  returns result of the conversion
		 */	
		public String charTohex(char[] buf)
		{

			String hexstr = "";
			for(int i=0; i<buf.length; i++){
				if( (int)buf[i] != 0 )
				{
					
		    		if(Integer.toHexString((int)buf[i]).length() < 2)
		    			hexstr = hexstr + "0" + Integer.toHexString((int)buf[i]) + " ";
		    		else
		    			hexstr = hexstr + Integer.toHexString((int)buf[i]) + " ";
				}
			}
			return hexstr;
		}
		
		/* DESCRIPTION
		 *	run the thread method
		 * ARGUMENTS
		 *  buf - ascii data array 
		 *  hexstr - hexadigit string
		 *  read - read buffer monitoring value, int
	     *  sock - socket 
		 *  connect_btn - connect button object
		 *  send_btn - send button object
		 *  disconn_btn - disconnect button object  
		 */			
		public void run(){
			try
			{
				
				char[] buf = new char[4096];
				String hexstr = "";	
				
				int read = 0;
				
				// wait the receiving packet until socket close 
				while((read = br.read(buf)) != 0)
				{
					if(read != -1)
					{
			
						System.out.println("insert to try \n");
						data.append("Receive data : ");
	
						// convert Decimal to HEX 
						hexstr = charTohex(buf);
									
						data.append(hexstr+" | ");
						data.append(new String(buf,0,read));
						data.append("\n");
	
					}
					else
					{
						break;

					}
					
				}
		
				sock.close();
				
				status.append("Socket Closed. \n");
		        jScrollPane2.getVerticalScrollBar().setValue(jScrollPane2.getVerticalScrollBar().getMaximum());
		        
				disconn_btn.setEnabled(false);
				send_btn.setEnabled(false);
				connect_btn.setEnabled(true);
			
			}catch(Exception e){
				
				try {
					// if a problem occurs in socket, It'll close the socket. 
					e.printStackTrace();
					System.out.println("insert to catch \n");
					sock.close();
					
					status.append("Socket Closed. \n");
			        jScrollPane2.getVerticalScrollBar().setValue(jScrollPane2.getVerticalScrollBar().getMaximum());
			        
					disconn_btn.setEnabled(false);
					send_btn.setEnabled(false);
					connect_btn.setEnabled(true);
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				
				
			}finally{
				try{
					if(br != null)
						br.close();
					
				}catch(Exception ex){}

			}
			
		}
	}
	
    // Variables declaration 
    private JButton connect_btn;
    private JButton disconn_btn;
    private JButton exit_btn;
    private JButton send_btn;
    private JCheckBox cr_check;
    private JCheckBox lf_check;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTextField send_text;
    private JTextField ip_text;
    private JTextField port_text;
    private JTextArea status;
    private JTextArea data;
	private BufferedReader br = null;
	private PrintWriter pw = null;
	private String ip = null;
	private int port = 0;
	private Socket sock;
	

}


