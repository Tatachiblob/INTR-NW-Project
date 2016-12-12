package networking.mp.logic;
import java.net.*;
import java.io.*;
public class ChatUser implements Runnable {
    
    private String clientName, address;
    private int port;
    private String key;
    private Socket socket;
    public Thread thread;
    
    public ChatUser(String clientName, String address, int port){
        this.clientName = clientName;
        this.address = address;
        this.port = port;
        this.thread = new Thread(this);
        connectToServer();
    }
    
    public Socket getSocket(){return this.socket;}
    public String getClientName(){return this.clientName;}
    public String getKey(){return this.key;}
    
    public void connectToServer(){
        System.out.println("Try to connect to server");
        
        try{
            
            this.socket = new Socket(address, port);
            System.out.println("Connected!!");
            
            DataOutputStream dos = null;
            DataInputStream dis = null;
            try{
                
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(clientName);
                
                dis = new DataInputStream(socket.getInputStream());
                this.key = dis.readUTF();
                
            }catch(Exception e2){
                e2.printStackTrace();
            }
            System.out.println("Primary Key: " + key);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void closeSocket() throws Exception{
        socket.close();
    }

    @Override
    public void run() {
        System.out.println("Client Thread Starting");
        
    }
    
    @Override
    public String toString(){
        return this.clientName;
    }
    
}
