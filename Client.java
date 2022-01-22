import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame
{

   public Client(String user)
   { 
      // Save username
      String username = user;
      
      // Set title
      setTitle("Chat Client V1");
      
      // Declare labels, buttons, text areas, and scroll panes
      JLabel msg1LBL;
      JLabel usrLBL;
      JLabel msg2LBL;
      JLabel msg3LBL;
      JButton sendBtn;
      JButton clearBtn;
      JButton exitBtn;
      msg1LBL = new JLabel ("Message:");
      JTextArea txtAUser = new JTextArea ();
      usrLBL = new JLabel ("User:");
      JTextArea txtA1 = new JTextArea ();
      msg2LBL = new JLabel ("Messages Sent:");
      JTextArea txtASent = new JTextArea ();
      JTextArea txtA2 = new JTextArea ("");
      JScrollPane sp = new JScrollPane(txtA2); // add text area to scroll pane
      sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // make sure the scroll bar is always showing
      msg3LBL = new JLabel ("Messages:");
      sendBtn = new JButton ("Send");
      clearBtn = new JButton ("Clear");
      exitBtn = new JButton ("Exit");
      
      // Adjust size and set layout
      setPreferredSize (new Dimension (320, 354));
      setLayout (null);
      
      // Add GUI components
      add (msg1LBL);
      add (txtAUser);
      add (usrLBL);
      add (txtA1);
      add (msg2LBL);
      add (txtASent);
      add (sp);
      add (msg3LBL);
      add (sendBtn);
      add (clearBtn);
      add (exitBtn);
      
      // Set absolute positioning
      msg1LBL.setBounds (5, 35, 100, 25);
      txtAUser.setBounds (120, 5, 100, 25);
      usrLBL.setBounds (60, 5, 100, 25);
      txtA1.setBounds (0, 60, 310, 60);
      msg2LBL.setBounds (40, 125, 100, 25);
      txtASent.setBounds (135, 125, 20, 25);
      sp.setBounds (0, 180, 310, 95);
      msg3LBL.setBounds (0, 155, 100, 25);
      sendBtn.setBounds (0, 275, 105, 40);
      clearBtn.setBounds (105, 275, 100, 40);
      exitBtn.setBounds (205, 275, 105, 40);
      
      // Set areas to uneditable
      txtA2.setEditable(false);
      txtASent.setEditable(false);
      txtAUser.setEditable(false);
      
      // Pack, display, and set the location of the GUI to the center of the screen
      pack();
      getContentPane().setVisible(true);
      show();
      setLocationRelativeTo(null);  
      
      // Set text to uneditable gui components
      txtASent.setText("0");
      txtAUser.setText(username);
      
      // Add button listeners
      clearBtn.addActionListener(e -> { txtA1.setText(null); });
      exitBtn.addActionListener(new ActionListener() 
       {
         public void actionPerformed(ActionEvent evt)
         {
             int msg = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to leave?",
                "Server Exit", JOptionPane.YES_NO_OPTION);
             if (msg == 0)
             {
                JOptionPane.showMessageDialog(null,
                "Bye, have a nice day..",
                "Logout", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
             }
         }
       });
      
      try
      {
         // Create a client socket and BufferedReader
         Socket serverSock = new Socket("localhost",8000);
         BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in), 1);
          
         // Use BufferedReader to get info from the server
         BufferedReader serverData = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
         
	 	 	// Create PrintWriter to send info to the server
         PrintWriter clientData = new PrintWriter(serverSock.getOutputStream(), true);
         
         // Send button to send information to the server
         sendBtn.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               String r = txtA1.getText(); // message body
               String c = txtASent.getText(); // message count
               int count = Integer.parseInt(c); // parse the string as an int
               count = count + 1; // increase the int by 1 per message sent
               c = count + ""; // set string c to new int count
               txtASent.setText(c); // set message counter to new increased number
               clientData.println(username + ": " + r); // send message text + username
               txtA1.append(r + "\n"); // append message to client 
               txtA1.setText(null); // clear message sending area
            }
         });
         
         // Constantly send data to the server and get back data
         while(true)
         {
            // Convert to string
            String a = new String(serverData.readLine());
            
            // clearClientConsoles key word to clear all client's consoles
	    		if (a.equals("clearClientConsoles"))
            {
               txtA2.setText(null);
            }
            else
            {
               txtA2.append(a + "\n"); // add message received from server to client message board
            }
         }
      }
      catch(IOException e1)
      {
         JOptionPane.showMessageDialog(null,
                "The server is not available\nPlease try again later",
                "Alert", JOptionPane.WARNING_MESSAGE); // display warning that server is not available and quit
         
         System.err.println("Client died with excption: " + e1.toString());
         System.exit(0);
      }
   }
   
   public static void main(String[] args)
   {
     // Dispaly dialogue box for username
     String name = JOptionPane.showInputDialog("Enter Name");
     new Client(name); // start client and send in username
   }
}