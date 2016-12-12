package networking.mp.gui;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import networking.mp.logic.*;
public class ChatApplication extends JFrame implements ActionListener, KeyListener, Runnable {
    
    private String clientName, hostAddress;
    private int port;
    private ChatUser clientUser;
    
    private JLabel lblName;
    public static JList<String> userList = new JList<>();
    private JTextField namefield, messagefield;
    private JTextArea messageArea;
    private JButton sendBtn, logoutBtn, whisperBtn;
    
    public ChatApplication(String clientName, String hostAddress, int port){
        super(clientName);
        this.clientName = clientName;
        this.hostAddress = hostAddress;
        this.port = port;
        
        this.lblName = new JLabel("User Name: ");
        this.namefield = new JTextField(20);
        namefield.setText(clientName);
        this.messagefield = new JTextField(25);
        messagefield.addKeyListener(this);
        this.messageArea = new JTextArea();
        messageArea.setOpaque(true);
        messageArea.setEditable(false);
        this.sendBtn = new JButton("Send");
        sendBtn.addActionListener(this);
        this.logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(this);
        this.whisperBtn = new JButton("Whisper");
        whisperBtn.addActionListener(this);
        
        this.addKeyListener(this);
        this.setFocusable(true);
        
        //Rethink how to do this
        //userNames.addElement(clientName);
        //userList.setModel(userNames);
        //
        
        this.clientUser = new ChatUser(clientName, hostAddress, port);
        Thread t = new Thread(this);
        t.start();
        
        new MessageProtocol(clientUser, MessageProtocol.CONNECT, clientName).start();
        
        this.setSize(800, 600);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        initScreen();
        this.setVisible(true);
    }
    
    public void initScreen(){
        this.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(userList);
        JPanel top = new JPanel(new FlowLayout());
        JPanel left = new JPanel(new BorderLayout());
        JPanel center = new JPanel();
        JPanel buttom = new JPanel(new BorderLayout());
        
        top.add(lblName);
        top.add(namefield);
        top.add(logoutBtn);
        
        left.add(new JLabel("Users Available"), BorderLayout.NORTH);
        left.add(scroll, BorderLayout.CENTER);
        left.add(whisperBtn, BorderLayout.SOUTH);
        
        buttom.add(messagefield, BorderLayout.CENTER);
        buttom.add(sendBtn, BorderLayout.EAST);
        
        this.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        this.getContentPane().add(top, BorderLayout.NORTH);
        this.getContentPane().add(left, BorderLayout.WEST);
        this.getContentPane().add(buttom, BorderLayout.SOUTH);
        
    }
    
    @Override
    public void run(){
        System.out.println("ChatApplication Thread Starting");
        DataInputStream dis;
        while(clientUser.getSocket().isConnected()){
            try{
                
                dis = new DataInputStream(clientUser.getSocket().getInputStream());
                String message = dis.readUTF();
                
                if(message.split(":").length == 2){
                    if(message.split(":")[0].equals("DELETE")){
                        System.out.println("deleting");
                        message = "";
                    }
                    else{
                        message = message.split(":")[1];
                    }
                    
                    ObjectInputStream ois = new ObjectInputStream(clientUser.getSocket().getInputStream());
                    Vector<String> tempList = new Vector<>((Vector<String>)ois.readObject());
                    userList.setListData(tempList);
                }
                
                if(!message.isEmpty())
                    messageArea.append(message + "\n");
            
            }catch(Exception e){
                e.printStackTrace();
                break;
            }
        }
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        MessageProtocol protocol;
        if(e.getSource().equals(sendBtn)){
            if(!messagefield.getText().isEmpty()){
                try{
                    
                    protocol = new MessageProtocol(clientUser, MessageProtocol.CHAT, messagefield.getText());
                    protocol.start();
                    
                }catch(Exception e2){
                    e2.printStackTrace();
                }
                messagefield.setText("");
            }//if text field
        }//if e.getSource
        
        if(e.getSource().equals(logoutBtn)){
            try{
                
                protocol = new MessageProtocol(clientUser, MessageProtocol.DISCONNECT, clientUser.getKey());
                protocol.start();
                
            }catch(Exception e2){
                e2.printStackTrace();
            }
            
            this.dispose();
        }
        
        if(e.getSource().equals(whisperBtn)){
            System.out.println("WHISPERING");
            if(!messagefield.getText().isEmpty()){
                try{
                    
                    ArrayList<String> a = (ArrayList<String>) userList.getSelectedValuesList();
                    String users = "";
                    for(String s : a){
                        users += s + ",";
                    }
                    users = users.substring(0, users.length() - 1);
                    
                    System.out.println(users);
                    protocol = new MessageProtocol(clientUser, MessageProtocol.WHISPER, users + "." + messagefield.getText());
                    protocol.start();
                    
                }catch(Exception e2){
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public void close() throws Exception{
        clientUser.closeSocket();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        MessageProtocol protocol;
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            if(!messagefield.getText().isEmpty()){
                try{
                    
                    protocol = new MessageProtocol(clientUser, MessageProtocol.CHAT, messagefield.getText());
                    protocol.start();
                    
                }catch(Exception e2){
                    e2.printStackTrace();
                }
                messagefield.setText("");
            }//if text field
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
