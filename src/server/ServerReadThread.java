package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gui.ServerWindow;
import tool.EncryptRSA;

public class ServerReadThread extends Thread 
{
	private Server parent;					//the parent thread which create this child read thread
	private ServerWindow serverWindow;
	private String clientID;				//the client handled by this thread
	private boolean flag;					//this flag is the switch to control on what time the readThread should die
	private Socket connection;				//the connection to the client's socket
	private List<String> messageList = new ArrayList<String>();		//the list stores all the command message

	public ServerReadThread(ServerWindow serverWindow, Socket connection,Server parent,String clientID) 
	{
		this.serverWindow = serverWindow;
		this.connection = connection;
		this.clientID = clientID;
		this.flag = true;
		this.parent = parent;
	}
	
	//change the status of the readThread for close it
	public void setFlag(boolean type)
	{
		flag = type;
	}
	
	//get the status of the readThread
	public boolean getFlag()
	{
		return flag;
	}
	
	//get the socket connects to client
	public Socket getSocket()
	{
		return connection;
	}
	
	public ServerWindow getServerWindow()
	{
		return serverWindow;
	}
	
	//store the message into list
	public void putMessage(String message)
	{
		this.messageList.add(message);
	}
	
	//get all the command message from the list
	public List<String> pullMessage()
	{
		return messageList;
	}
	
	public void run() 
	{
		while(flag && !connection.isClosed())				//this flag is the switch to control on what time the readThread should die 
		{
			try {
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
				InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String line = br.readLine();
				line = EncryptRSA.decryptMessage(parent.getRSAPublicKey(clientID), line);		//use the RSA algorithm to decrypt the message got from client
				String message = "";															//store the whole information in a string called message
				while(!line.equals("@end"))
				{
					message += line + "\n";
					line = br.readLine();
					line = EncryptRSA.decryptMessage(parent.getRSAPublicKey(clientID), line);
				}
				message = message.substring(0, message.length() - 1);	
				serverWindow.setReceive(clientID + ":\n" + message);			//show the message from client to the screen
				if(message != null && message.charAt(0) == '@')					//if the message is a command, than decide which method should be used
				{
					commandType(message);					//decide which command it is
				}
			} catch (SocketException s) {
				serverWindow.warning("SocketException:" + s);			/**The probably Exception will be shown in the warning dialog*/
			} catch (IOException f) {
				serverWindow.warning("IOException: " + f);
			} catch (InvalidKeyException e) {
				serverWindow.warning("InvalidKeyException:" + e);
			} catch (NoSuchAlgorithmException e) {
				serverWindow.warning("NoSuchAlgorithmException:" + e);
			} catch (NoSuchPaddingException e) {
				serverWindow.warning("NoSuchPaddingException:" + e);
			} catch (IllegalBlockSizeException e) {
				serverWindow.warning("IllegalBlockSizeExceptions:" + e);
			} catch (BadPaddingException e) {
				serverWindow.warning("BadPaddingException:" + e);
			}
		}
	}
	
	//this will judge which type the command sent by the client is
	public void commandType(String command)
	{
		if(command.equals("@stop"))
		{
			putMessage(command);					//store the message
			parent.stop(clientID);
			parent.broadcast(clientID, clientID + " Has Left The System.");		//broadcast to all remain clients in the system
		}
		else if(command.startsWith("@broadcast"))
		{
			putMessage(command);
			parent.broadcast(clientID, command.replaceFirst("@broadcast", "").trim());	//broadcast to all clients in the system about the message
		}
		else if(command.equals("@list"))
		{
			putMessage(command);
			parent.listClient(clientID);
		}
		else if(command.startsWith("@kick"))
		{
			putMessage(command);
			String kickClientId = command.replaceFirst("@kick", "").trim();
			parent.kickClient(clientID, kickClientId);
			parent.broadcast(kickClientId, kickClientId + " Has Been Kick Off The System.");	//broadcast to all remain clients in the system
		}
		else if(command.startsWith("@stats"))
		{
			putMessage(command);
			parent.statsClient(clientID, command.replaceFirst("@stats", "").trim());			//return all the command sentences sended by the client
		}
	}
}
