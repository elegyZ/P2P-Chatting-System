package server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gui.ServerWindow;
import tool.EncryptRSA;

public class ServerWriteThread extends Thread 
{
	private ServerReadThread serverReadThread;
	private ServerWindow serverWindow;
	private Socket connection;
	private String content = "";				//the content is the message send to the client
	private RSAPublicKey publickey;				//the publicKey used to encrypt message, which matches to the client's privateKey
	
	public ServerWriteThread(ServerReadThread Thread, RSAPublicKey publickey, String message) 
	{
		this.serverReadThread = Thread;
		this.serverWindow = serverReadThread.getServerWindow();
		this.connection = serverReadThread.getSocket();
		this.content = message;
		this.publickey = publickey;
	}
	
	public Socket getSocket()
	{
		return connection;
	}
	
	public void setContent(String s)
	{
		this.content = s;
	}
	
	public void run() 
	{
		String[] lines = content.split("\n");			//if the message has more than one line, than separate it and send every line
		if(!content.isEmpty())							//if there is a message to send than run the WriteThread
		{
			try {
				BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
				OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
				PrintWriter pw = new PrintWriter(osw, true);
				for(String line:lines)
					pw.println(EncryptRSA.encryptMessage(publickey,line));			//use EncryptRSA class in the tool bag for encrypt and security
				pw.println(EncryptRSA.encryptMessage(publickey,"@end"));			//add the "@end" for the Server to know it is the end of one message
				//System.out.println("Message sended.");
			} catch (UnknownHostException u) {
				serverWindow.warning("UnknownHostException:" + u);					/**The probably Exception will be shown in the warning dialog*/
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
			content = "";		//after the message been sent set the content to empty
		}
	}
}
