package ex10_3;

import ex10_2.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA_XOR_app {
    private static ServerSocket socket;
    private static Socket connection = null;
    private static boolean running = true;
    InetAddress adress;
    
    private static String encryptDecrypt(String input, char[] key) {
            StringBuilder output = new StringBuilder();

            for(int i = 0; i < input.length(); i++) {
                    output.append((char) (input.charAt(i) ^ key[i % key.length]));
            }

            return output.toString();
    }
    
    public static void saveToFile(String fileName,
        BigInteger mod, BigInteger exp) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        try {
            oout.writeObject(mod);
            oout.writeObject(exp);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }
    
    static void generateRSAKeys() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(512);
            KeyPair kp = kpg.genKeyPair();
            Key publicKey = kp.getPublic();
            Key privateKey = kp.getPrivate();

            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
            saveToFile("public.key", pub.getModulus(), pub.getPublicExponent());
            saveToFile("private.key", priv.getModulus(), priv.getPrivateExponent());
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException ex) {
            Logger.getLogger(RSA_XOR_app.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("RSA keys generated and saved.");
    }
    
    static Key readKeyFromFile(String keyFileName, boolean ispublic) throws IOException {
        InputStream in = new FileInputStream(keyFileName);
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            KeyFactory fact = KeyFactory.getInstance("RSA");
            if (ispublic) {
                RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
                PublicKey pubKey = fact.generatePublic(keySpec);
                return pubKey;
            } else {
                RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
                PrivateKey priKey = fact.generatePrivate(keySpec);
                return priKey;
            } 
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }
    
    public static byte[] rsaEncrypt(byte[] data) {
        byte[] cipherData = null;
        try {
            PublicKey pubKey = (PublicKey) readKeyFromFile("public.key", true);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            cipherData = cipher.doFinal(data);
            
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException 
                | InvalidKeyException | IllegalBlockSizeException 
                | BadPaddingException ex) {
            Logger.getLogger(RSA_XOR_app.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cipherData;
    }
    
    public static byte[] rsaDecrypt(byte[] data) {
        byte[] cipherData = null;
        try {
            PrivateKey priKey = (PrivateKey) readKeyFromFile("private.key", false);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            cipherData = cipher.doFinal(data);
            
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException 
                | InvalidKeyException | IllegalBlockSizeException 
                | BadPaddingException ex) {
            Logger.getLogger(RSA_XOR_app.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cipherData;
    }
    
    public static void main(String[] args) {
        boolean serverExists = true;
        try {
            // Check if another application is already running
            new FileInputStream("public.key");
        } catch (FileNotFoundException ex) {
            // No key file found, therefore this is the first application running
            serverExists = false;
        }
        
        // ====================================================================== Server running - become CLIENT
        if (serverExists) {
            System.out.println("Public.key found. Assuming client role.");
            char[] key = {'K', 'C', 'Q'}; //Only Client knows key at start, has to send it via RSA
            
            try {
                connection = new Socket("localhost", 1337);

                BufferedReader ClientIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                PrintWriter ClientOut = new PrintWriter(connection.getOutputStream(), true);
                OutputStream ClientOutStream = connection.getOutputStream();
                
                // Exchange XOR key
//                byte[] keydata = rsaEncrypt(new String(key).getBytes());
//                ClientOutStream.write(keydata);
//                ClientOutStream.flush();
                ClientOut.println(key);
                System.out.println("Sent Server the XOR key.");
                
                while (running) {
                        Thread.sleep(500);            
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                        System.out.println("Enter message to send, or \"exit\" to quit:");  
                        String message = br.readLine();
                        
                        // Check if client is supposed to close
                        if (message.compareTo("exit") == 0) {
                            //Delete Keys
                            Files.delete(FileSystems.getDefault().getPath("public.key"));
                            Files.delete(FileSystems.getDefault().getPath("private.key"));
                            running = false;
                        } else {
                            ClientOut.println(encryptDecrypt(message, key));
                            ClientOut.flush();
                            System.out.println("Sent server the encrypted message.");
                            Thread.sleep(500);
                            String decrypted_response = ClientIn.readLine();
                            System.out.println("Server response: " + encryptDecrypt(decrypted_response, key));
                        }
                }
                connection.close();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(RSA_XOR_app.class.getName()).log(Level.SEVERE, null, ex);
            } 
            System.out.println("Client stopped, cleaned up keys.");
        } else { // ============================================================= No server running - become SERVER
            System.out.println("Public.key not found. Assuming server role.");
            // Generate RSA keys first - generate server keys
            generateRSAKeys();
            try {
                socket = new ServerSocket();
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(1337), 10);

                System.out.println("Server waiting for connection");
                connection = socket.accept();
                System.out.println("Server: Connection received from " + connection.getInetAddress().getHostName());
                InputStream ServerInStream = connection.getInputStream();
                BufferedReader ServerIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		PrintWriter ServerOut = new PrintWriter(connection.getOutputStream(), true);
                
                // Accept and store XOR key first
//                byte[] key = new byte[3]; // TODO: Test bigger array
//                ServerInStream.read(key);
                String key_string = ServerIn.readLine();
                System.out.println("Recieved XOR key: " + key_string);
                char[] key = key_string.toCharArray();
                
                while (running) {
                        String encrypted_message = ServerIn.readLine();
                        System.out.println("Recieved message from client: " + encrypted_message);
                        String decrypted_message = encryptDecrypt(encrypted_message, key);
                        System.out.println("Decrypted message: " + decrypted_message);
                        ServerOut.println(encryptDecrypt(decrypted_message, key));
                        ServerOut.flush();
                }

                connection.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: Client might have closed, or an error has occured.");
            }
            System.out.println("Server stopped.");
        }      
    }
}
