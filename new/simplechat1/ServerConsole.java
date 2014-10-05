import java.io.*;
import common.*;

/**
 * This class constructs the UI for a server client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ChatConsole.
 *
 * @author Sarmad Hashmi (7249729)
 * @author Samy Abidib (6909624)
 * @version September 2014
 */
public class ServerConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the server that will use this ConsoleChat.
   */
  EchoServer server;

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ServerConsole UI.
   *   * 
   * @param port The port to connect on.
   */
  public ServerConsole(int port) 
  {
	  server = new EchoServer(port, this); 
	  try {
		  server.listen(); // Start listening for connections
	  } catch (IOException e) {
		  System.out.println("Server failed to start.");
	  }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the server's message handler.
   */
  public void accept() 
  {
    try
    {
      BufferedReader fromConsole = 
        new BufferedReader(new InputStreamReader(System.in));
      String message;

      while (true) 
      {
        message = fromConsole.readLine();        
        server.handleMessageFromServerUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println(message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
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
	  ServerConsole serverChat = new ServerConsole(port);
	  serverChat.accept();  //Wait for console data
  }
}
//End of ServerConsole class
