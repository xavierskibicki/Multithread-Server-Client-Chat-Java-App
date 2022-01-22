// MultiThreadServer.java: The server can communicate with
// multiple clients concurrently using the multiple threads
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MultiThreadServer extends JFrame {

  // Declare GUI objects as public so all classes have access
  public JTextArea txtAMessages = new JTextArea ("");
  public JScrollPane sp = new JScrollPane(txtAMessages); // set up the scroll pane
  public JTextArea txtAClients = new JTextArea ();
  public JTextArea txtAMsgCount = new JTextArea ();
  public JButton btnClear = new JButton("Clear");
  
  // Array list to hold onto all client connections
  ArrayList<HandleAClient> clients = new ArrayList<HandleAClient>();
  
  // Start program
  public static void main(String[] args) 
  {
    new MultiThreadServer();
  }
  
  public MultiThreadServer() 
  {
     // Set the title    
     setTitle("MultiThreadServer");
       
     // Set default exit operation
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
     // Create GUI components that don't need to be accessed anywhere else
     JLabel lblClient = new JLabel ("Clients:");
     JLabel lblMsgs = new JLabel ("Messages:");
     JButton btnShutDown = new JButton ("Shut Down");
     JLabel lblMsgCount = new JLabel ("Message Count:");

     // Adjust size and set layout
     setPreferredSize (new Dimension (387, 364));
     setLayout (null);

     // Add GUI components
     add (txtAClients);
     add (lblClient);
     add (lblMsgs);
     add (txtAMsgCount);
     add (sp);
     add (btnShutDown);
     add (btnClear);
     add (lblMsgCount);
     
     // Make vertical scroll bar always active
     sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

     // Set Absolute Positioning
     txtAClients.setBounds (0, 25, 369, 85);
     lblClient.setBounds (0, 0, 100, 25);
     lblMsgs.setBounds (0, 130, 100, 25);
     txtAMsgCount.setBounds (230, 125, 50, 25);
     sp.setBounds (0, 155, 370, 140);
     btnShutDown.setBounds (0, 295, 185, 30);
     btnClear.setBounds (185, 295, 185, 30);
     lblMsgCount.setBounds (130, 125, 100, 25);
     
     // Make text areas not editable
     txtAClients.setEditable(false);
     txtAMsgCount.setEditable(false);
     txtAMessages.setEditable(false);
    
    // Add shut down button listener
    btnShutDown.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent evt) 
      {
          int msg = JOptionPane.showConfirmDialog(null,
             "Are you sure you want to shutdown?",
             "Server Exit", JOptionPane.YES_NO_OPTION);
          if (msg == 0)
          {
             System.exit(0);
          }
      }
    });
   
   // Pack the contents of the GUI and dispaly
   pack();
   getContentPane().setVisible(true);
   show();
   setLocationRelativeTo(null);  
    
    
    try 
    {
      // Create a socket
      ServerSocket serverSocket = new ServerSocket(8000);
      
      // Main processing loop
      while (true) 
      {
        // Listen for a new connection request
        Socket connectToClient = serverSocket.accept();
        
        // Create a new thread for the client connection and start the thread
        HandleAClient st = new HandleAClient(connectToClient);
        st.start();
        clients.add(st);
      }
    }
    catch(IOException ex) 
    {
      System.err.println(ex);
    }
  }
  
  // Inner class
  // Define the thread class for handling new client connections
  class HandleAClient extends Thread 
  {
    private Socket connectToClient; // A connected socket

    /** Construct a thread */
    public HandleAClient(Socket socket) 
    {
      connectToClient = socket;
    }
    
    // Method to send message to all clients
    public void sendToAll(String msg) {
      for (HandleAClient z : clients) {
         if (z != null) {
            PrintStream osToClient = null;
            try 
            {  
               osToClient = new PrintStream(z.connectToClient.getOutputStream());
               osToClient.println(msg); // send msg
            } 
            catch (IOException e) 
            {
               e.printStackTrace();
            }
         }
      }
    }
    
    // Run thread
    public void run() 
    {
      try 
      {
        // Use a BufferedReader to get data from the client
        BufferedReader isFromClient = new BufferedReader(new InputStreamReader(connectToClient.getInputStream()));

        // Create the buffer writer to send data from the server to the client
        PrintWriter osToClient = new PrintWriter(connectToClient.getOutputStream(), true);
         
        // Constantly update to serve the client
        while (true) 
        {
          // Receive message from the client
          String message = new String(isFromClient.readLine());
          
          // Send messsages to all clients and add to server board
          sendToAll(message);
          txtAMessages.append(message + "\n");
          
          // Get the location of the colon to find the client's username
          int colonLocation = 0;
          colonLocation = message.indexOf(":");
          
          // Make sure client is not already appended to client list
          String clientsCheck = txtAClients.getText(); 
          if (clientsCheck.contains(message.substring(0, colonLocation)))
          {
            // do nothing
          }
          else
          {
            txtAClients.append(message.substring(0, colonLocation) + "\n"); // add client name to client list
          }
          
          // Message counter for server
          String linesCount = txtAMessages.getText();
          String[] lines = linesCount.split("\r\n|\r|\n");
          txtAMsgCount.setText("" + lines.length);
          
          // Clear button to wipe messages from all clients
          btnClear.addActionListener(new ActionListener() 
          {
            public void actionPerformed(ActionEvent evt) 
            {
               sendToAll("clearClientConsoles"); // key word to send to clients to trigger the clearing of the consoles
               txtAMessages.setText(null); // clear server client's chat history
               String[] lines = linesCount.split("\r\n|\r|\n");
               txtAMsgCount.setText("" + ((lines.length)-1)); // set message counter back to 0
            }
          });
        }
      }
      catch(IOException e) 
      {
        // Client name should be removed from the client list here,
        // but I can't figure out how to bring the username down here since
        // it is stored in the above while loop and relies on the buffered reader and printwriter
        System.err.println(e);
      }
    }
  } 
}