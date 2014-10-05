// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************

  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 


  /**
   * The login id that is to be passed to the server.
   */
  String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, String loginID, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.loginID = loginID;
    this.clientUI = clientUI;
    openConnection();
    sendToServer("#login " + this.loginID);
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {

    //Modified for E.50 (a) client side
    if(message.substring(0, 1).equals("#")){
      //We extract everything after the # symbol
      String[] raw_command_array = message.split("#")[1].split(" ");
      String command = raw_command_array[0];
      String[] param;

      if(raw_command_array.length > 1){
        //We copy all the parameters from the raw_command_array
        //into the param array. The parameters in the 
        //raw_command_array are all the other elements 
        //other than raw_command_array[0].
        param = new String[raw_command_array.length-1];
        for(int i = 0; i < param.length;i++){
          param[i] = raw_command_array[i+1];          
        }
      } else {
        param = new String[1];
        param[0] = "";
      }


      /********* Quit ****************/
      if(command.equals("quit")){
        try{
          this.closeConnection();
        } catch (Exception e){
          clientUI.display("IOException when disconnecting.");
        }
        clientUI.display("Quitting.");        
      /********* Log Off****************/
      } else if(command.equals("logoff")){
        try{
          this.closeConnection();
        } catch (Exception e){
          clientUI.display("IOException when disconnecting.");
        }        
      /********* Set Host****************/
      } else if(command.equals("sethost")){
        if(param[0].equals("")){
          clientUI.display("Usage of #sethost <hostname>");
        }  else {
          if(this.isConnected()){
            clientUI.display("Host cannot be changed while logged into a server.");
          } else {
            this.setHost(param[0]);
            clientUI.display("Host changed to " + param[0]);
          }
        }
      /********* Set Port****************/
      } else if(command.equals("setport")){
        if(param[0].equals("")){
          clientUI.display("Usage of #setport <port>");
        }  else {
          if(this.isConnected()){
            clientUI.display("Host cannot be changed while logged into a server.");
          }  else  {
            int port ;
            try{
              port = Integer.parseInt(param[0]);
              this.setPort(port);
              clientUI.display("Port changed to " + param[0]);
            } catch (NumberFormatException e){
              clientUI.display("The port parameter must be an integer.");
            }
          }
        }
      /********* Login****************/
      } else if(command.equals("login")){
        if(this.isConnected()){
          clientUI.display("Already connected to server at " + this.getHost() + ":" + this.getPort());
        } else {
          try{
            openConnection();
            sendToServer("#login " + this.loginID);
            clientUI.display("Connected to server at " + this.getHost() + ":"+this.getPort());
          } catch (Exception e){
            clientUI.display("Could not connect to server at " + this.getHost() + ":"+this.getPort());
          }
        }
      /********* getHost****************/
      } else if(command.equals("getHost")){
        clientUI.display("Current host is " + this.getHost());
      /********* getPort****************/
      } else if(command.equals("getPort")){
        clientUI.display("Current port is " + this.getPort());
      }

    } 
    else {
      try
      {
        sendToServer(message);
      }
      catch(IOException e)
      {
        clientUI.display
          ("Could not send message to server.  Terminating client.");
        quit();
      }
    }

  }
  

  //Modified for E49 (a)-- Samy Abidib
  
  /**
   * Overrided method from AbstractClient that is called 
   * when the connection to the server is closed.
   */
  protected void connectionClosed() {
      clientUI.display("Connection Lost.");
  }

  /**
   * Overrided Method from AbstractClient that is 
   * called whent here is an exception in the client thread.
   */
  protected void connectionException(Exception exception) {
    // We check if the exception is an EOF, i.e we hit the end 
    // of the socket.
    if(exception instanceof EOFException){
      this.connectionClosed();
      clientUI.display("Quitting.");      
    }
  }


  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}    
  }
}
//End of ChatClient class
