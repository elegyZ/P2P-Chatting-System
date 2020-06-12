package tool;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.NoSuchAlgorithmException;  
import java.security.interfaces.RSAPrivateKey;  
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;  
import javax.crypto.Cipher;  
import javax.crypto.IllegalBlockSizeException;  
import javax.crypto.NoSuchPaddingException;  
  
public class EncryptRSA 
{  
	/**refer from https://www.cnblogs.com/Free-Thinker/p/5825584.html*/
	private KeyPairGenerator keyPairGen;
	private KeyPair keyPair;
	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;
	
	public EncryptRSA() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		keyPairGen = KeyPairGenerator.getInstance("RSA");  
        keyPairGen.initialize(1024);  
        keyPair = keyPairGen.generateKeyPair();   
        privateKey = (RSAPrivateKey)keyPair.getPrivate();               
        publicKey = (RSAPublicKey)keyPair.getPublic();
	}
	
	public RSAPrivateKey getRSAPrivateKey()
	{
		return privateKey;
	}
	
	public RSAPublicKey getRSAPublicKey()
	{
		return publicKey;
	}
	
	//use the private/public key to encrypt the input bytes
    protected static byte[] encrypt(Key key,byte[] srcBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {  
        if(key!=null)
        {  
            Cipher cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, key);  
            byte[] resultBytes = cipher.doFinal(srcBytes);  
            return resultBytes;  
        }  
        return null;  
    }  
    
    //use the private/public key to decrypt the output bytes
    protected static byte[] decrypt(Key key,byte[] srcBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {  
        if(key!=null){  
            Cipher cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.DECRYPT_MODE, key);  
            byte[] resultBytes = cipher.doFinal(srcBytes);  
            return resultBytes;  
        }  
        return null;  
    }  

    //use the encrypt for the message which will be send
    public static String encryptMessage(Key key, String message) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException 
    {
    	byte[] srcBytes = message.getBytes();
    	byte[] resultBytes = encrypt(key, srcBytes);
    	String encrypt_message = Base64.getEncoder().encodeToString(resultBytes);
    	return encrypt_message;
    }
    
    //use the decrypt for the message which is been received
    public static String decryptMessage(Key key, String message) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException 
    {
    	byte[] srcBytes = Base64.getDecoder().decode(message);
    	byte[] resultBytes = decrypt(key, srcBytes);
    	return new String(resultBytes);
    }
    /**refer from https://www.cnblogs.com/Free-Thinker/p/5825584.html*/
    
    
    
    //transform the Object element to String element
    public static String objectToString(Object object) 
	{
    	byte[] bytes = null;  
    	String s = null;
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;    
        try {        
        	objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);        
            objectOutputStream.flush();         
            bytes = byteArrayOutputStream.toByteArray();      
            objectOutputStream.close();         
            byteArrayOutputStream.close(); 
            s = Base64.getEncoder().encodeToString(bytes);
            return s;
        } catch (IOException ex) {        
            ex.printStackTrace(); 
            return null;
        }
    }
	
    //transform the String element to Object element
	public static Object stringToObject(String objectString) 
	{
		byte[] bytes = Base64.getDecoder().decode(objectString);;  
    	Object object = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
        	byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}  
