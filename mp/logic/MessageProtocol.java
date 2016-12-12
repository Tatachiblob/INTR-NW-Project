package networking.mp.logic;
import java.net.*;
import java.io.*;
public class MessageProtocol implements Runnable{
    
    public final static String CHAT = "chat";
    public final static String CONNECT = "enter";
    public final static String DISCONNECT = "exit";
    public final static String WHISPER = "whisper";
    
    private String protocol, message;
    private ChatUser source;
    
    public MessageProtocol(ChatUser source, String protocol, String message){
        this.source = source;
        this.protocol = protocol;
        this.message = message;
    }
    
    public void start(){
        Thread t = new Thread(this);
        t.start();
    }
    
    @Override
    public void run(){
        DataOutputStream dos;
        try{

            dos = new DataOutputStream(source.getSocket().getOutputStream());
            dos.writeUTF(source.getClientName() + ":" + protocol + ":" + message);
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
