package networking.mp.gui;
/**
 *
 * @author Yuta 11512709
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
public class Menu extends JFrame implements ActionListener{
    
    private JLabel lbluserName, lblIP, lblPort;
    private JTextField txtName, txtIP, txtPort; 
    private JButton submitbtn;
    
    public Menu(){
        super("INTR-NW Final Project");
        this.lbluserName = new JLabel("User Name: ");
        this.lblIP = new JLabel("IP Adress: ");
        this.lblPort = new JLabel("Port: ");
        
        String address = "", name = "";
        try{
            address = Inet4Address.getLocalHost().getHostAddress();
            name = Inet4Address.getLocalHost().getHostName();
        }catch(Exception e){}
        
        this.txtName = new JTextField(10);
        txtName.setText(name);
        this.txtIP = new JTextField(10);
        txtIP.setText(address);
        this.txtPort = new JTextField(10);
        
        this.submitbtn = new JButton("Connect");
        submitbtn.addActionListener(this);
        
        this.setSize(400, 200);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        initScreen();
        this.setVisible(true);
        
    }
    
    public void initScreen(){
        this.setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();
        con.insets = new Insets(10, 10, 10, 10);
        
        con.gridx = 0; con.gridy = 0;
        con.anchor = GridBagConstraints.EAST;
        p.add(lbluserName, con);
        con.gridx = 1;
        con.anchor = GridBagConstraints.WEST;
        p.add(txtName, con);
        
        con.gridx = 0; con.gridy = 1;
        con.anchor = GridBagConstraints.EAST;
        p.add(lblIP, con);
        con.gridx = 1;
        con.anchor = GridBagConstraints.WEST;
        p.add(txtIP, con);
        
        con.gridx = 0; con.gridy = 2;
        con.anchor = GridBagConstraints.EAST;
        p.add(lblPort, con);
        con.gridx = 1;
        con.anchor = GridBagConstraints.WEST;
        p.add(txtPort, con);
        
        add(p, BorderLayout.CENTER);
        add(submitbtn, BorderLayout.SOUTH);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource().equals(submitbtn)){
            String name = "", ip = "";
            int port = 0;
            name = txtName.getText();
            ip = txtIP.getText();
            port = Integer.parseInt(txtPort.getText());
            this.dispose();
            System.out.println(name);
            System.out.println(ip);
            System.out.println(port);
            
            new ChatApplication(name, ip, port);
        }
    }
    
    
    
    public static void main(String[] args) {
        new Menu();
    }
}
