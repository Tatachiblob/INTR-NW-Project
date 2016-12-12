package networking.mp.logic;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
public class MyServer {
    
    public final static int DEFAULT_PORT = 9909;
    public final static String HOST  = getHostAddress();
    public final static String PUBLIC_IP = "180.191.140.94";
    
    public static MyServer instance;
    public static ServerSocket server;
    public static HashMap<String, Socket> users = new HashMap<>();
    public static HashMap<String, Runnable> runnables = new HashMap<>();
    public static HashMap<String, String> usernames = new HashMap<>();
    
    private MyServer(){}
    
    public static MyServer getServer(){
        if(instance == null){
            instance = new MyServer();
        }
        return instance;
    }
    
    synchronized public static void startServer(){
        try{
            
            server = new ServerSocket();
            server.bind(new InetSocketAddress(HOST, DEFAULT_PORT));
            System.out.println("SERVER STARTING\n"
                    + "Host Address: " + HOST + "\n"
                    + "Port: " + DEFAULT_PORT);
            
            int key = 0;
            Socket socket = null;
            String strKey = "";
            while(!server.isClosed()){
                
                try{
                    System.out.println("Waiting for new Socket");
                    socket = server.accept();
                    System.out.println("Socket Found");
                    DataInputStream dis = null;
                    DataOutputStream dos = null;
                    String clientName = "";
                    strKey = Integer.toString(key);
                    try{
                        
                        dis = new DataInputStream(socket.getInputStream());
                        clientName = dis.readUTF();
                        
                        dos = new DataOutputStream(socket.getOutputStream());
                        dos.writeUTF(strKey);
                        
                    }catch(IOException e2){
                        e2.printStackTrace();
                        clientName = "Error";
                    }//try catch
                    
                    System.out.println("Name: " + clientName);
                    
                    users.put(strKey, socket);
                    usernames.put(strKey, clientName);
                    runnables.put(strKey, createRunnable(socket));
                    System.out.println("Socket Size: " + users.size());
                    System.out.println("Users Size: " + usernames.size());
                    System.out.println("Runnables Size: " + runnables.size());
                    
                    Thread t = new Thread(runnables.get(strKey));
                    t.start();
                    
                    key++;
                    System.out.println("LOOP COMPLETE");
                    
                }catch(Exception e2){
                    e2.printStackTrace();
                }//try catch
                
                
            }//while
            
            
        }catch(Exception e){
            e.printStackTrace();
        }//try catch
        
    }
    
    public static HashMap<String, Socket> getUsers(){return users;}
    public static HashMap<String, Runnable> getRunnables(){return runnables;}
    public static HashMap<String, String> getUsernames(){return usernames;}
    
    public static void closeServer() throws Exception{
        server.close();
    }
    
    private static String getHostAddress(){
        String host = "";
        try{
            host = Inet4Address.getLocalHost().getHostAddress();
        }catch(Exception e){}
        return host;
    }
    
    public static Runnable createRunnable(Socket socket){
        Runnable run = new Runnable() {
            @Override
            public void run() {
                System.out.println("New Runnable Thread Starting");
                while(!socket.isClosed()){
                    
                    try{
                        
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        String[] line = dis.readUTF().split(":");
                        receiveMessage(socket, line[0], line[1], line[2]);
                        
                    }catch(Exception e){
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };
        return run;
    }
    
    public static void receiveMessage(Socket socekt, String sourceName, String protocol, String value){
        System.out.println("Source: " + sourceName);
        System.out.println("Protocol: " + protocol);
        System.out.println("Value: " + value);
        
        ArrayList<String> socketKey = new ArrayList<>(users.keySet());
        ArrayList<String> runnableKey = new ArrayList<>(runnables.keySet());
        ArrayList<String> namesKey = new ArrayList<>(usernames.keySet());
        Vector<String> list;
        
        switch(protocol){
            case MessageProtocol.CHAT:
                
                for(String a : socketKey){
                    System.out.println("Key: " + a);
                    DataOutputStream dos = null;
                    try{
                        Socket s = users.get(a);
                        System.out.println(s);
                        dos = new DataOutputStream(s.getOutputStream());
                        dos.writeUTF(sourceName + ">" + value);
                        
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }//try catch
                }
                
                break;
            case MessageProtocol.CONNECT:
                System.out.println("Initiated Connect Protocol");
                list = new Vector<>();
                for(String key : usernames.keySet()){
                    System.out.println("ADD: " + list.add(usernames.get(key)));
                }
                System.out.println("BEFORE LOOP");
                
                for(String a : socketKey){
                    System.out.println("Key: " + a);
                    DataOutputStream dos = null;
                    try{
                        Socket s = users.get(a);
                        System.out.println(s);
                        dos = new DataOutputStream(s.getOutputStream());
                        dos.writeUTF("ADD:" + sourceName + " has entered the chat room...");
                        
                        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                        oos.writeObject(list);
                        
                        
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }//try catch
                }
                
                System.out.println("AFTERLOOP");
                
                System.out.println("Ended Connect Protocol");
                break;
            case MessageProtocol.DISCONNECT:
                
                for(String a : socketKey){
                    System.out.println("Key: " + a);
                    DataOutputStream dos = null;
                    try{
                        Socket s = users.get(a);
                        System.out.println(s);
                        dos = new DataOutputStream(s.getOutputStream());
                        dos.writeUTF(sourceName + " has exited the chat room....");
                        
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }//try catch
                    
                }//for
                
                usernames.remove(value);
                runnables.remove(value);
                try {
                    users.remove(value).close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                socketKey = new ArrayList<>(users.keySet());
                runnableKey = new ArrayList<>(runnables.keySet());
                namesKey = new ArrayList<>(usernames.keySet());
                
                list = new Vector<>();
                for(String key : usernames.keySet()){
                    System.out.println("ADD: " + list.add(usernames.get(key)));
                }
                
                for(String a : socketKey){
                    System.out.println("Key: " + a);
                    DataOutputStream dos = null;
                    try{
                        Socket s = users.get(a);
                        System.out.println(s);
                        dos = new DataOutputStream(s.getOutputStream());
                        dos.writeUTF("DELETE:" + sourceName + " has entered the chat room...");
                        
                        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                        oos.writeObject(list);
                        
                        
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }//try catch
                }
                
                break;
                
            case MessageProtocol.WHISPER:
                
                String[] content = value.split(".");
                
                
                break;
        }
    }
    
    public static void main(String[] args) {
        MyServer server = MyServer.getServer();
        server.startServer();
    }
}
