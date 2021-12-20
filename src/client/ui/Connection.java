package client.ui;

//import client.ClientApplication;
import client.processing.ClientProcessing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * client.ui
 * Created by Duy
 * Date 12/11/2021 - 2:40 PM
 * Description: ...
 */
public class Connection extends JFrame implements ActionListener {
    final private int WIDTH = 360;
    final private int HEIGHT = 240;

    private Container container;

    private JLabel hostText;
    private JTextField hostInput;
    private JLabel portText;
    private JTextField portInput;
    private JButton connectButton;


    public Connection(){
        initialFrame();
        createUI();
        setDisplay(true);
    }
    public void initialFrame(){
        setTitle("Config");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        setResizable(false);
        setMinimumSize(new Dimension(WIDTH,HEIGHT));
        setLocation(500,200);
        container = getContentPane();
        container.setLayout(new BoxLayout(container,BoxLayout.PAGE_AXIS));
    }

    public void createUI(){
        JLabel title = new JLabel("Config to connect server");
        title.setFont(new Font("Arial", Font.BOLD,20));
        title.setAlignmentX(CENTER_ALIGNMENT);
        container.add(Box.createRigidArea(new Dimension(0,20)));
        container.add(title);

        JPanel configPane = new JPanel();
        configPane.setLayout(new BoxLayout(configPane, BoxLayout.Y_AXIS));
        configPane.setBorder(BorderFactory.createTitledBorder("Config Server"));
        configPane.setPreferredSize(new Dimension(WIDTH,100));
        configPane.setMinimumSize(new Dimension(WIDTH,50));
        configPane.setMaximumSize(new Dimension(WIDTH,HEIGHT));
        configPane.setLocation(0,0);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1,BoxLayout.X_AXIS));

        hostText = new JLabel("Host:");
        hostInput = new JTextField();
        hostInput.setMaximumSize(new Dimension(WIDTH,20));
//        hostInput.setEditable(false);
        hostInput.setText("localhost");
        panel1.add(Box.createRigidArea(new Dimension(50,0)));
        panel1.add(hostText);
        panel1.add(Box.createRigidArea(new Dimension(35,0)));
        panel1.add(hostInput);
        panel1.add(Box.createRigidArea(new Dimension(50,0)));

        configPane.add(panel1);
        configPane.add(Box.createRigidArea(new Dimension(0,20)));

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2,BoxLayout.X_AXIS));

        portText = new JLabel("Port:");
        portInput = new JTextField();
        portInput.setMaximumSize(new Dimension(WIDTH,20));
        panel2.add(Box.createRigidArea(new Dimension(50,0)));
        panel2.add(portText);
        panel2.add(Box.createRigidArea(new Dimension(38,0)));
        panel2.add(portInput);
        panel2.add(Box.createRigidArea(new Dimension(50,0)));

        configPane.add(panel2);
        container.add(configPane);
        configPane.add(Box.createRigidArea(new Dimension(0,10)));

        connectButton = new JButton("Connect");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectButton.addActionListener(this);
        configPane.add(connectButton);
    }

    public void setDisplay(boolean b){
        pack();
        setVisible(b);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton){
            try {
                String host = hostInput.getText();
                String port = portInput.getText();
//                int port = Integer.parseInt();
                if (host.equals("") || port.equals("")){
                    JOptionPane.showMessageDialog(this,"Host and port should be filled!!!");
                }else{
                    ClientProcessing client = ClientProcessing.getInstance();
                    boolean flag = client.createSocket(host,Integer.parseInt(port));
                    if (flag){
                        JOptionPane.showMessageDialog(this,"Connect successfully!");
                        setDisplay(false);
                        client.loginForm = new Login();
                        client.receiveFromServer();
                        dispose();
                    }else{
                        JOptionPane.showMessageDialog(this,"Connect failed!!!");
                    }

                }
            }catch (Exception ex){

            }
        }
    }

}
