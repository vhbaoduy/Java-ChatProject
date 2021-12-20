package client.ui;

import client.Client;
import client.processing.ClientProcessing;
import struct.StructClass;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * client.ui
 * Created by Duy
 * Date 12/13/2021 - 8:46 PM
 * Description: ...
 */
public class ClientScreen extends JFrame implements ActionListener, MouseListener {
    final private int WIDTH = 360;
    final private int HEIGHT = 240;

    private Container container;
    private JList<String> userList;
    private JButton chatButton;
    private JButton logoutButton;
    private String clientName;
    private DefaultListModel<String> userModel;


    public ClientScreen(String username){
        clientName = username;
        initialFrame(username);
        createUI();
        setDisplay(true);
    }

    public void initialFrame(String userName){
        setTitle(userName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                logOut();
                System.exit(0);
            }
        });
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        setResizable(false);
        setMinimumSize(new Dimension(WIDTH,HEIGHT));
        setLocation(500,200);
        container = getContentPane();
        container.setLayout(new BoxLayout(container,BoxLayout.PAGE_AXIS));
    }
    public void setDisplay(boolean b){
        pack();
        setVisible(b);
    }

    public void createUI(){
        JPanel listPane = new JPanel();
        listPane.setLayout(new BorderLayout());
        listPane.setBorder(BorderFactory.createTitledBorder("User Online"));
        userModel = new DefaultListModel<>();
        userList = new JList<String>(userModel);
        userList.setLocation(0,0);
        userList.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(userList);
        listPane.add(scrollPane);
        container.add(listPane);
        container.add(Box.createRigidArea(new Dimension(0,5)));

        JPanel buttonPane = new JPanel();
//        buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.X_AXIS));
        chatButton = new JButton("Chat");
//        chatButton.setAlignmentX(CENTER_ALIGNMENT);
        chatButton.addActionListener(this);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        buttonPane.add(chatButton);
        buttonPane.add(Box.createRigidArea(new Dimension(100,0)));
        buttonPane.add(logoutButton);

        container.add(buttonPane);
        container.add(Box.createRigidArea(new Dimension(0,5)));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chatButton){
            if (userList.isSelectionEmpty()){
                JOptionPane.showMessageDialog(this,"You need to select users to chat!!!");
            }else {
                String string = userList.getSelectedValue();

                PrivateChat privateChat = new PrivateChat(clientName,string);
            }
        }

        if (e.getSource() == logoutButton){
            int choice = JOptionPane.showConfirmDialog(this,
                    "Do you want to logout?",
                    "Notification",
                    JOptionPane.YES_NO_OPTION
                    );
            if (choice == 0){
                logOut();
                new ClientProcessing(true);
            }
        }
    }
    private void logOut(){
        HashMap<String, String> data =new HashMap<>();
        data.put("type","Logout");
        data.put("username",clientName);
        ClientProcessing.getInstance().sendMessage(StructClass.pack(data));
        ClientProcessing.getInstance().closeConnection();
    }
    public void setUserModel(String [] list){
        userModel = new DefaultListModel<>();
        for (String str:list){
            userModel.addElement(str);
        }
        userList.setModel(userModel);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == userList) {
            if (e.getClickCount() == 2) {
                if (!userList.isSelectionEmpty()) {
                    String receiver = userList.getSelectedValue();
//                JOptionPane.showMessageDialog(this,receiver);
                    PrivateChat form = ClientProcessing.privateChat.get(receiver);
                    if (form == null) {
                        form = new PrivateChat(clientName, receiver);
                        ClientProcessing.privateChat.put(receiver, form);
                    } else {
                        form.setDisplay(true);
                    }
                }else{
                    JOptionPane.showMessageDialog(this,"You need to select users to chat!!!");

                }
            }
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
