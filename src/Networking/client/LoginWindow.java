/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LoginWindow.java
 *
 * Created on Oct 6, 2012, 9:10:14 PM
 */

package Networking.client;


import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Enumeration;
import javax.swing.ImageIcon;

/**
 *
 * @author ubuntu
 */
public class LoginWindow extends javax.swing.JFrame
{
    ObjectInputStream Sinput;   // to read the socker
    ObjectOutputStream Soutput; // to write on the socket
    Socket socket;    
    boolean connected = false;
    Color col=new Color (0.7f,0.7f,0.7f,0.5f);
    String HOSTIP;
    int PORT;

    

    /** Creates new form LoginWindow
     * @param port The port on which the LoginWindow should by default be operating on.
     */
    public LoginWindow (int port)
    {
        PORT = port;
	initComponents ();
        getIP();        
    }
    
    //Find Servers on your network
    //-----------------------------------------------   
    
    /**
     * Find Servers on your network, and return the successfully connected socket.
     * @return The socket connected to the server.
     */
    public Socket findServer(){
        
        String IP = getIP();

	String[] nodes = IP.split ("\\D");
	String newIP = "";
	for (int i = 0 ; i < nodes.length - 1 ; i++)
	{
	    newIP = newIP.concat (nodes [i] + ".");	    
	}
        
        //Declares an array used for searching for a server
	Socket[] sktarr = new Socket [255];
	//Search variable
	int i = 0;
	//Search loop
	for (; i < 254 ;)
	{
	    sktarr [i] = new Socket ();
	    try
	    {
		sktarr [i].connect (new InetSocketAddress (newIP + i, PORT), 10);
		return sktarr[i];
	    }
	    catch (IOException ex)
	    {
		i++;
	    }
	}
        return null;	
    }
    
    //Find your network IP address
    //-----------------------------------------------      
    /**
     * Returns your local IP address. Used for finding servers programmatically or manually.
     * @return The String representation of your IP address.
     */
    public String getIP(){
        if(HOSTIP != null){
            return HOSTIP;
        }
        else
        {
            try {
                for (
                    final Enumeration< NetworkInterface > interfaces =
                        NetworkInterface.getNetworkInterfaces( );
                    interfaces.hasMoreElements( );
                )
                {
                    final NetworkInterface cur = interfaces.nextElement( );

                    if ( cur.isLoopback( ) )
                    {
                        continue;
                    }

                    for ( final InterfaceAddress addr : cur.getInterfaceAddresses( ) )
                    {
                        final InetAddress inet_addr = addr.getAddress( );
                        if ( !( inet_addr instanceof Inet4Address ) )
                        {
                            continue;
                        }

                        HOSTIP = inet_addr.getHostAddress();
                        System.out.println("address: " + HOSTIP);
                        
                        jTextField3.setText("Your IP: " + HOSTIP);                        
                        return HOSTIP;
                    }
                }
            } catch (SocketException ex) {
                ex.printStackTrace();
            } 
        }
        
        //default case
        return "";        
    }

    //Connect to a server on your network
    //-----------------------------------------------     
    /**
     * Connect to a server directly given it's IP, or scan.
     * @param IP The IP of the server you are attempting to connect to if not scanning.
     * @param scan Determines whether the client will attempt to scan for server, or connect directly.
     * @return Whether or not you have connected. Returns true if the connection is successful.
     */
    public boolean connect (String IP, boolean scan)
    {   
	try
	{
            if(scan){
                socket = findServer();
                if(socket == null){                            
                    jTextField2.setText("Status: No Server Found");	  
                    return false;
                }
            }
            else{
                socket = new Socket();
                socket.connect (new InetSocketAddress (IP, PORT), 100);
            }
	}
	catch (Exception e)
	{
	    System.out.println ("Error connectiong to server:" + e);
	    return false;
	}
	System.out.println ("Connection accepted " +
		socket.getInetAddress () + ":" +
		socket.getPort ());

	/* Creating both Data Stream */
	try
	{
	    Sinput = new ObjectInputStream (socket.getInputStream ());
	    Soutput = new ObjectOutputStream (socket.getOutputStream ());
	}
	catch (IOException e)
	{
	    System.out.println ("Exception creating new Input/output Streams: " + e);
	    return false;
	}
        connected = true;
        return true;
   }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @ SuppressWarnings ("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");

        jButton1.setBackground(col);
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("LanScan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setBackground(col);
        jTextField1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Username:");

        jTextField2.setBackground(col);
        jTextField2.setForeground(new java.awt.Color(255, 255, 255));
        jTextField2.setText("Status: Ready");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Password:");

        jPasswordField1.setBackground(col);
        jPasswordField1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Manual Connection");

        jButton2.setBackground(col);
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Start Server");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField3.setEditable(false);
        jTextField3.setBackground(col);
        jTextField3.setForeground(new java.awt.Color(255, 255, 255));
        jTextField3.setText("Your IP:");

        jButton3.setBackground(col);
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Connect ");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextField4.setBackground(col);
        jTextField4.setForeground(new java.awt.Color(255, 255, 255));
        jTextField4.setText("127.0.0.1");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Automatic Connection");

        jTextField5.setBackground(col);
        jTextField5.setForeground(new java.awt.Color(255, 255, 255));
        jTextField5.setText("15001");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Server IP");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Port #");

        jLabel7.setFont(new java.awt.Font("Impact", 0, 15)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("PsiBot");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(new javax.swing.ImageIcon("C:\\Users\\Aidan\\Desktop\\Dropbox\\Computer Science ISU\\ISUGame\\res\\images\\blue-techno-background.jpg")); // NOI18N
        jLabel8.setText("jLabel8");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel4))
            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel7))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1))
            .addGroup(layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(203, 203, 203)
                .addComponent(jButton3))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel3))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel2))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(128, 128, 128)
                .addComponent(jLabel4)
                .addGap(35, 35, 35)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel7))
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel1))
            .addGroup(layout.createSequentialGroup()
                .addGap(241, 241, 241)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(244, 244, 244)
                .addComponent(jLabel6))
            .addGroup(layout.createSequentialGroup()
                .addGap(292, 292, 292)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(269, 269, 269)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(212, 212, 212)
                .addComponent(jButton3))
            .addGroup(layout.createSequentialGroup()
                .addGap(312, 312, 312)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(148, 148, 148)
                .addComponent(jButton1))
            .addGroup(layout.createSequentialGroup()
                .addGap(192, 192, 192)
                .addComponent(jLabel3))
            .addGroup(layout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel2))
            .addGroup(layout.createSequentialGroup()
                .addGap(216, 216, 216)
                .addComponent(jLabel5))
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jButton1ActionPerformed (java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:        

        loginthread logthread = new loginthread();
        logthread.IP = getIP();
        logthread.scan = true;
        logthread.start();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:

        loginthread logthread = new loginthread();
        logthread.IP = jTextField4.getText();
        logthread.scan = false;
        PORT = Integer.parseInt(jTextField5.getText());
        logthread.start();


    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jButton2.setEnabled(false);
        PORT = Integer.parseInt(jTextField5.getText());
        jButton2.setText("Server Running on Port: " + PORT);
        final Thread serverThread = new Thread() {

            public void run() {
                Networking.server.GameServer server = new Networking.server.GameServer(PORT);
            }
        };
        serverThread.start();

    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * The login process. Sends the username and password input by the user to
     * the server for verification. If they are accepted, it performs post-login
     * actions.
     *
     * @return Whether or not the login was successful.
     */
    public boolean login() {
        String Username = jTextField1.getText();
        String Password = String.valueOf(jPasswordField1.getPassword());

        String reply = "";

        try {
            Soutput.writeObject("login " + Username + " " + Password);
            Soutput.flush();
        } catch (IOException e) {
            System.out.println("Error writting to the socket: " + e);
            return false;
        }

        try {
            reply = (String) Sinput.readObject();
        } catch (Exception e) {
            System.out.println("login error");
            System.out.println("Error reading from the socket: " + e);
            return false;
        }

        if (!reply.equals("loggedin")) {
            return false;
        }

        //USER HAS LOGGED IN SUCCESSFULLY
        //Perform post-login actions
        final String accepted = Username;
        Thread gameThread = new Thread() {

            public void run() {
                //Create the game here
                Game.GameMain.createGame(Sinput, Soutput, accepted);
                //PlayerSelect psel = new PlayerSelect(Sinput, Soutput);
                //psel.setVisible(true);
            }
        };
        gameThread.start();

        dispose();
        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new LoginWindow(1500).setVisible(true);

            }
        });
    }

    class loginthread extends Thread {
        String IP;
        Boolean scan;

        public void run() {
            if (!connected) {
                if (connect(IP, scan)) {
                    if (!login()) {
                        jTextField2.setText("Status: Login Failed");
                    }
                }
            }
        }
    };
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
