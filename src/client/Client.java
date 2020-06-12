package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gui.ClientWindow;
import tool.EncryptRSA;

public class Client 
{
	private ClientWindow clientWindow;	//add the clientGUI
	/** Define a String for the hostname of server */
	private String host = "localhost";
	/** Define a Integer for the port number of server */
	int port = 2018;
	private String id = "Client" + (new java.util.Date().getTime());	//use the time to initiate a unique clientID
	private Socket connection;
	private volatile boolean flag = true;	//this flag is the switch to control on what time the readThread should die
	private Thread writeThread;				//the thread to write message to the socket and send to the server
	private Thread readThread;				//the thread to read message from the socket
	private RSAPrivateKey privatekey;		//the privateKey used for the RSA encrypt
	
	public boolean getFlag()
	{
		return flag;
	}

	/**create the connection to the socket and the server*/
	/**part of the code refers to "Sockets: Basic Client-Server Programming in Java - By Rick Proctor"
	 * https://edn.embarcadero.com/article/31995*/
	public void createConnect() 
	{
		try 
		{
			InetAddress address = InetAddress.getByName(host); // an InetAddress object address containing the host
																// name/IP address pair
			/** Establish a socket connection */
				connection = new Socket(address, port);
				BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
				OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
				PrintWriter pw = new PrintWriter(osw, true);
				String idMessage = id;
				pw.println(idMessage);									//send the clientID to the server at first
				clientWindow.setReceive("Connected to Server.");		//inform the client that the connection has made
				
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
				InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String key = br.readLine();
				setRSAPrivateKey(key);		//get the privateKey of the RSA claimed by Server
											//for the security in information transformation
				createReadThread();			//create the thread for getting message from the server
		} catch (UnknownHostException u) {
			clientWindow.warning("UnknownHostException" + u);
		} catch (IOException f) {
			clientWindow.warning("IOException: " + f);
		}
	}
	/**part of the code refers to "Sockets: Basic Client-Server Programming in Java - By Rick Proctor"
	 * https://edn.embarcadero.com/article/31995*/
	
	/**the child-thread for writing message to the server*/
	/**part of the code refers to "Socket Programming-Chat application in Java"
	 * http://www.coderpanda.com/chat-application-in-java/*/
	public void createWriteThread(String message)
	{
		String[] content = message.split("\n");			//if the message has more than one line, than separate it and send every line
		writeThread = new Thread() 
		{
			public void run()
			{
				try 
				{
					BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
					OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
					PrintWriter pw = new PrintWriter(osw, true);
					for(String line:content)
						pw.println(EncryptRSA.encryptMessage(privatekey, line));		//use EncryptRSA class in the tool bag for encrypt and security
					pw.println(EncryptRSA.encryptMessage(privatekey, "@end"));			//add the "@end" for the Server to know it is the end of one message
				} catch (UnknownHostException u) {
					clientWindow.warning("UnknownHostException:" + u);					/**The probably Exception will be shown in the warning dialog*/
				} catch (IOException f) {
					clientWindow.warning("IOException: " + f);
				} catch (InvalidKeyException e) {
					clientWindow.warning("InvalidKeyException: " + e);
				} catch (NoSuchAlgorithmException e) {
					clientWindow.warning("NoSuchAlgorithmException: " + e);
				} catch (NoSuchPaddingException e) {
					clientWindow.warning("NoSuchPaddingException: " + e);
				} catch (IllegalBlockSizeException e) {
					clientWindow.warning("IllegalBlockSizeException: " + e);
				} catch (BadPaddingException e) {
					clientWindow.warning("BadPaddingException: " + e);
				}
			}
		};
		writeThread.start();
	}
	
	/**the child-thread for reading message from the server*/
	/**part of the code refers to "Socket Programming-Chat application in Java"
	 * http://www.coderpanda.com/chat-application-in-java/*/
	public void createReadThread()
	{
		readThread = new Thread() 
		{
			public void run()
			{
				while(flag)			//this flag is the switch to control on what time the readThread should die 
				{
					try {
						BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
						InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
						BufferedReader br = new BufferedReader(isr);
						String line = br.readLine();
						line = EncryptRSA.decryptMessage(privatekey, line);			//use EncryptRSA class in the tool bag for decrypt
						String message = "";										//store the whole information in a string called message
						while(!line.equals("@end"))
						{
							message += line + "\n";
							line = br.readLine();
							line = EncryptRSA.decryptMessage(privatekey, line);
						}
						message = message.substring(0, message.length() - 1);
						if(message != null)
							clientWindow.setReceive("BackMessage:\n" + message);	//show the information got from server to the client's screen
						if(message.equals("Sorry, You Have Been Kicked Off From The System."))
							kickStop();												//the action match to the kick event
					} catch (SocketException s) {
						clientWindow.warning("SocketException:" + s);				/**The probably Exception will be shown in the warning dialog*/
					} catch (IOException f) {
						clientWindow.warning("IOException: " + f);
					} catch (InvalidKeyException e) {
						clientWindow.warning("InvalidKeyException: " + e);
					} catch (NoSuchAlgorithmException e) {
						clientWindow.warning("NoSuchAlgorithmException: " + e);
					} catch (NoSuchPaddingException e) {
						clientWindow.warning("NoSuchPaddingException: " + e);
					} catch (IllegalBlockSizeException e) {
						clientWindow.warning("IllegalBlockSizeException: " + e);
					} catch (BadPaddingException e) {
						clientWindow.warning("BadPaddingException: " + e);
					}
				}
			}
		};
		readThread.start();
	}
	
	//the action for client to send message to the server
	public void send(String message)
	{
		if(flag)
		{
			createWriteThread(message);
			clientWindow.setReceive("Your Message Has Been Sended.");
		}
	}

	// command {BROADCAST - {content}}
	public void broadcast(String message)
	{
		if(flag)
		{
			createWriteThread("@broadcast" + id + ":\n" + message);
			clientWindow.setReceive("Your Message Has Been Broadcasted.");
		}
	}

	// command {STOP}
	public void stop()
	{
		if(flag)
		{
			createWriteThread("@stop");
			flag = false;
			while(readThread.isAlive())
				;
			closeSocket();
		}
	}

	// command {LIST}
	public void list()
	{
		if(flag)
			createWriteThread("@list");		
	}
	
	// command {KICK - ID}
	public void kick(String id)
	{
		if(flag)
			createWriteThread("@kick" + id);
	}
	
	public void kickStop()
	{
		createWriteThread("kick off");
		flag =  false;
	}

	// command {STATS - ID}
	public void stats(String id)
	{
		if(flag)
			createWriteThread("@stats" + id);
	}
	
	//to close the socket connection between client and server
	public void closeSocket()
	{
		try {
			connection.close();
			//System.out.println("socket connection closed");
		} catch (IOException f) {
			clientWindow.warning("IOException:" + f);
		}
	}

	public void setRSAPrivateKey(String s) 
	{
		Object object = EncryptRSA.stringToObject(s);
		privatekey = (RSAPrivateKey)object;
	}

	//change the status of the readThread in order to close that thread
	public void setFlag(boolean f)
	{
		flag = f;
	}
	
	public static void main(String[] args) 
	{
		Client myclient = new Client();
		myclient.clientWindow = new ClientWindow(myclient.id, myclient);
		myclient.clientWindow.setVisible(true);								//open the window for client
		myclient.createConnect();											//connect to the server
	}
	


}
