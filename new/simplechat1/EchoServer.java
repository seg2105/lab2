// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Sarmad Hashmi (7249729)
 * @author Samy Abidib (6909624)
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version September 2014
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;  
  
  /**
   * The interface type variable.
   * Allows implementation of display method in server.
   */  	
  ChatIF server;
  
  /**
   * Boolean to store whether or not server is closed (all clients disconnected and not listening).
   */
  boolean closed = false;
  
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * @param server The interface type variable.
   */
  public EchoServer(int port, ChatIF server) 
  {
    super(port);   
    this.server = server;
    
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client. Modified for E51 part c (S.H).
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
  	String loginID = (String) client.getInfo("loginid");  	
  	String message = (String) msg;  	
  	if ((loginID != null) && (message.indexOf("#login") > -1)) {		// if a client enters #login and is already logged in
  		try {
			client.sendToClient("ERROR: Already logged in.");
		} catch (IOException e) {
			server.display("Could not send message to client.");
		}
  	}
  	else if (loginID == null) {											// if a client has not yet logged in
  		if (message.indexOf("#login") > -1) {							// if a client enters #login, get their ID
	  		loginID = message.substring(message.indexOf(" ")+1);		// the login id is the string that follows #login
	  	  	client.setInfo("loginid", loginID);
  	  	}
  	  	else {												// terminate client connection if client says something besides #login
  	  		try {
  	  			client.sendToClient("ERROR: First command has to be #login. Terminating client connection now...");
				client.close();
			} catch (IOException e) {
				server.display("Could not close client.");
			}
  	  	}
  	}
  	else {
	    System.out.println("Message received: " + msg + " from " + loginID);    
	    this.sendToAllClients(loginID + ": " + msg);
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
    closed = false;	// set closed to false when server is started
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * This method overrides the one in the superclass. Called
   * when a client disconnects. This is for E49 part c (S.H).
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	 server.display(client.getInfo("loginid") + " has disconnected.");
	 this.sendToAllClients(client.getInfo("loginid") + " has disconnected.");
  }
  
  /**
   * This method overrides the one in the superclass. Called
   * when a client disconnects. This is for E49 part c (S.H).
   */
  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {
	  server.display(client.getInfo("loginid") + " has disconnected.");
	  this.sendToAllClients(client.getInfo("loginid") + " has disconnected.");
  }
  
  
  /**
   * This method overrides the one in the superclass. Called
   * when a client connects. This is for E49 part c (S.H).
   */
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("Client connected: " + client);
  }
  
   /**
   * This method overrides the one in the superclass. Called
   * when a server disconnects all clients and stops listening.
   * This is for E50 part c (S.H).
   */
  protected void serverClosed() {
	  closed = true; // set closed to true whenever close() is called
  }
  
  /**
   * This method handles all data coming from the server console.
   * This has been added for E50 part c (S.H).   
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message)
  {
  	server.display("SERVER MSG> " + message);
  	String messageWithoutHash, cmd, argument = null;
  	String[] msgArr;
  	if (message.indexOf("#") == 0){
  		messageWithoutHash = message.substring(1);
  		msgArr = messageWithoutHash.split(" ");
  		cmd = msgArr[0];
  		if (msgArr.length > 1) {
  			argument = msgArr[1];
  		}
  		/************#quit*******************/
  		if (cmd.equals("quit")) {  			
  			try {  				  				
				this.close();				
				server.display("Closing server...");				
				System.exit(0);
			} catch (IOException e) {
				server.display("Could not close server.");
			}
  		}
  		/************#stop*******************/
  		else if (cmd.equals("stop")) {  			
  			if (this.isListening()) {
  				server.display("Server will now stop listening for clients...");
  				this.sendToAllClients("SERVER MSG> WARNING - The server has stopped listening for connections.");
  				this.stopListening();
  			}  	  			
  		}
  		/************#close*******************/
  		else if (cmd.equals("close")) {
  			server.display("Server will now disconnect all clients and stop listening...");
  			if (!closed){
	  			try {
	  				this.sendToAllClients("SERVER MSG> SERVER SHUTTING DOWN! DISCONNECTING!");
					this.close();
					this.stopListening();
				} catch (IOException e) {
					server.display("Could not close server.");
				}  			
  			}
  			else {
  				server.display("Server is already closed.");
  			}
  		}
  		/************#setport <port>*******************/
  		else if (cmd.equals("setport")) {
  			if (closed) {	  			
	  			try {	  				
	  				this.setPort(Integer.parseInt(argument));
	  				server.display("Port changed to " + argument);
	  			}
	  			catch (NumberFormatException e){
	  				server.display("Port must be an integer.");
	  			}
  			}
  			else {
  				server.display("Server must be closed before changing ports.");
  			}
  		}
  		/************#start*******************/
  		else if (cmd.equals("start")) {  			
  			if (!this.isListening()) {
  				server.display("Server will now start listening for clients...");
				try {
					this.listen();
				} catch (IOException e) {
					server.display("Cannot start listening again.");
				} 
			}
			else {
				server.display("The server is already started.");
			}
  		}
  		/************#getport*******************/
  		else if (cmd.equals("getport")) {
  			server.display("Currently listening on port: " + this.getPort());  			
  		}	
  	}
  	else {
     	this.sendToAllClients("SERVER MSG> " + message);     	     	
    } 
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    ServerConsole sv = new ServerConsole(port);
    
    try 
    {
      sv.accept(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
} 
//End of EchoServer class
