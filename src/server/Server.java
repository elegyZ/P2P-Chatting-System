package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gui.ServerWindow;
import tool.EncryptRSA;

public class Server 
{
	private ServerWindow serverWindow;		//add the serverGUI
	/** Define a Integer for the port number of server */
	protected final static int port = 2018;
	private Socket connection;
	private ServerSocket socket;
	private List<String> clientList = new ArrayList<String>();							//the List stored all clients'ID 
	private Map<String,EncryptRSA> rsaList = new HashMap<String,EncryptRSA>();			//the map stored all the RSP keyPairs and their clientID
	private Map<String,ServerReadThread> readThreadMap = new HashMap<String,ServerReadThread>();	//the map stored all ServerReadThreads and their clientID
	
	/**create the connection to the socket and the client*/
	/**part of this refers to "Sockets: Basic Client-Server Programming in Java - By Rick Proctor"
	 * https://edn.embarcadero.com/article/31995*/
	public void createConnect()
	{
		try {
			socket = new ServerSocket(port);
			serverWindow.setReceive("Server Initialized.");				//inform the user the server has open a socket
			while(true)													//suppose that the server will never closed
			{
				connection = socket.accept();
				serverWindow.setReceive("Connected to a new client");								//inform a new connection has been built
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
				InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String clientID = br.readLine();													//get the clientID from the initiation information of client
				
				EncryptRSA rsa = new EncryptRSA();
				BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
				OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
				PrintWriter pw = new PrintWriter(osw, true);
				String key = EncryptRSA.objectToString(rsa.getRSAPrivateKey());				
				pw.println(key);													//produce a RSP keyPair for the latest connected client and send back its privateKey
				
				broadcast(null, clientID + " joined in.");		//inform all the client users in the system that a new client has join in the chat group
				rsaList.put(clientID, rsa);						//add the new keyPair used for RSA encryption to the List
				clientList.add(clientID);						//add the latest client's ID to the clientList
				serverWindow.setClientList(clientID);
				serverWindow.setReceive(clientID + " joined in.");		//show information on the server's screen
				//create a new read thread to solve a specific client
				ServerReadThread readThread = new ServerReadThread(serverWindow, connection, this, clientID);
				readThreadMap.put(clientID, readThread);
				readThread.start();
			}
		} catch (IOException f) {
			serverWindow.warning("IOException: " + f);					/**The probably Exception will be shown in the warning dialog*/
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
	
	//get the specific PublicKey of the target clientID
	public RSAPublicKey getRSAPublicKey(String clientID)
	{
		EncryptRSA rsa = rsaList.get(clientID);
		return rsa.getRSAPublicKey();
	}
	
	//command {BROADCAST - {content}} 
	public void broadcast(String requestClientID, String content)
	{
		if(!clientList.isEmpty())
		{
			for(String clientID:clientList)
			{
				if(!clientID.equals(requestClientID))
				{
					ServerReadThread clientReadThread = readThreadMap.get(clientID);
					ServerWriteThread clientThread = new ServerWriteThread(clientReadThread, getRSAPublicKey(clientID), content);
					clientThread.start();
				}
			}
			serverWindow.setReceive("Message \""+ content +"\" Has Been Broadcasted.");
		}
	}
	
	//command {stop}
	public void stop(String requestClientID)
	{
		ServerReadThread clientReadThread = readThreadMap.get(requestClientID);
		ServerWriteThread clientWriteThread = new ServerWriteThread(clientReadThread, getRSAPublicKey(requestClientID), "Stop From Server");
		clientReadThread.setFlag(false);
		clientWriteThread.start();
		readThreadMap.remove(requestClientID);
		clientList.remove(requestClientID);
		serverWindow.freshClientList(clientList);
	}
	
	//command {LIST} 
	public void listClient(String requestClientID)
	{
		if(!clientList.isEmpty())
		{
			String content = "The List Of ClientIDs Connect To System:";
			for(String clientID:clientList)
				content += ("\n" + clientID);
			content += "\n";
			ServerReadThread clientReadThread = readThreadMap.get(requestClientID);
			ServerWriteThread clientWriteThread = new ServerWriteThread(clientReadThread, getRSAPublicKey(requestClientID), content);
			clientWriteThread.start();
		}
	}
	
	//command {KICK - ID}
	public void kickClient(String requestClientID, String clientID)
	{
		if(!readThreadMap.containsKey(clientID))
		{
			ServerReadThread clientReadThread = readThreadMap.get(requestClientID);
			ServerWriteThread clientWriteThread = new ServerWriteThread(clientReadThread, getRSAPublicKey(requestClientID), "Warning: No Such Client!");
			clientWriteThread.start();
		}
		else
		{
			ServerReadThread targetReadThread = readThreadMap.get(clientID);
			ServerWriteThread targetWriteThread = new ServerWriteThread(targetReadThread, getRSAPublicKey(clientID), "Sorry, You Have Been Kicked Off From The System.");
			targetReadThread.setFlag(false);
			targetWriteThread.start();
			readThreadMap.remove(clientID);
			clientList.remove(clientID);
			serverWindow.freshClientList(clientList);
		}
	}
	
	//command {STATS - ID}
	public void statsClient(String requestClientID, String clientID)
	{
		if(!readThreadMap.containsKey(clientID))
		{
			ServerReadThread clientReadThread = readThreadMap.get(requestClientID);
			ServerWriteThread clientWriteThread = new ServerWriteThread(clientReadThread, getRSAPublicKey(requestClientID), "Warning: No Such Client!");
			clientWriteThread.start();
		}
		else
		{
			ServerReadThread targetThread = readThreadMap.get(clientID);
			List<String> message = targetThread.pullMessage();
			String content = "The Commands Sended By " + clientID + ":";
			for(String command:message)
				content += ("\n" + command);
			content += "\n";
			ServerReadThread clientReadThread = readThreadMap.get(requestClientID);
			ServerWriteThread clientWriteThread = new ServerWriteThread(clientReadThread, getRSAPublicKey(requestClientID), content);
			clientWriteThread.start();
		}
	}
	
	//send message to a specific client
	public void send(String clientID, String message)
	{
		ServerReadThread clientReadThread = readThreadMap.get(clientID);
		ServerWriteThread clientWriteThread = new ServerWriteThread(clientReadThread, getRSAPublicKey(clientID), message);
		clientWriteThread.start();
	}
	
	public static void main(String[] args) 
	{
		Server myserver = new Server();
		myserver.serverWindow = new ServerWindow(myserver);
		myserver.serverWindow.setVisible(true);
		myserver.createConnect();
	}
}
