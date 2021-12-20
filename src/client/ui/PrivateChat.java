package client.ui;

import client.processing.ClientProcessing;
import struct.StructClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

/**
 * client.ui
 * Created by Duy
 * Date 12/13/2021 - 9:13 PM
 * Description: ...
 */
public class PrivateChat extends JFrame implements ActionListener {
    final private int WIDTH = 480;
    final private int HEIGHT = 480;

    private Container container;
    private JTextArea content;

    private JButton clearButton;
    private JButton fileChosenButton;
    private JTextField message;
    private JButton sendButton;

    private String sender;
    private String receiver;

    public PrivateChat(String sender, String receiver){
        this.sender = sender;
        this.receiver = receiver;
        initialFrame("Box chat (" +sender+"-"+receiver+")");
        createUI();
        setDisplay(true);
    }
    public void initialFrame(String text){
        setTitle(text);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
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
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1,BoxLayout.X_AXIS));
        panel1.setBorder(BorderFactory.createTitledBorder("Content"));
        panel1.setPreferredSize(new Dimension(WIDTH,360));

        content = new JTextArea();
//        content.setMaximumSize(new Dimension(WIDTH,340));
//        content.setMinimumSize(new Dimension(WIDTH,300));
//        content.setPreferredSize(new Dimension(WIDTH,340));
        content.setBounds(0,0,WIDTH,340);
        content.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel1.add(scrollPane);
        container.add(panel1);

        JPanel panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createTitledBorder("Option"));
        clearButton = new JButton("Clear screen");
        clearButton.addActionListener(this);
        fileChosenButton = new JButton("Choose file");
        fileChosenButton.addActionListener(this);
        panel2.add(clearButton);
        panel2.add(Box.createRigidArea(new Dimension(150,0)));
        panel2.add(fileChosenButton);

        container.add(panel2);


        JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3,BoxLayout.X_AXIS));
        panel3.setBorder(BorderFactory.createTitledBorder("Type here"));

        message = new JTextField();
        message.setMaximumSize(new Dimension(WIDTH-50,50));

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        panel3.add(Box.createRigidArea(new Dimension(10,0)));
        panel3.add(message);
        panel3.add(Box.createRigidArea(new Dimension(5,0)));
        panel3.add(sendButton);
        panel3.add(Box.createRigidArea(new Dimension(10,0)));

        container.add(panel3);


    }

    public void appendMessage(String message){
        content.append(message + "\n");
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearButton){
            content.setText("");
        }
        if (e.getSource() == fileChosenButton){
            JFileChooser fileChooser = new JFileChooser();
            int choice = fileChooser.showOpenDialog(this);
            fileChooser.setDialogTitle("Open file to send");
            if (choice == JFileChooser.APPROVE_OPTION){
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                String name = fileChooser.getSelectedFile().getName();
                File file = new File(path);
                long size = file.length();

                String message= "Do you want to send " + name + "(" + size +" bytes)" + " to " + receiver +"?";
                int confirm = JOptionPane.showConfirmDialog(null,message,"Notification",JOptionPane.YES_NO_OPTION);
                if (confirm == 0){
                    HashMap<String, String> data = new HashMap<>();
                    data.put("type","FileRequest");
                    data.put("sender", sender);
                    data.put("receiver",receiver);
                    data.put("fileName",name);
                    data.put("size",String.valueOf(size));
                    data.put("path",path);
                    ClientProcessing.getInstance().sendMessage(StructClass.pack(data));
                    String text = "You request to send file " + name + "(" + size +" bytes)" + " to " + receiver;
                    appendMessage(text);
                }
            }
        }
        if (e.getSource()== sendButton){
            String mess = message.getText();
            content.append("You: "+ mess+"\n");
            HashMap<String,String> data = new HashMap<>();
            data.put("type","PrivateChat");
            data.put("sender",sender);
            data.put("receiver",receiver);
            data.put("message",mess);
            ClientProcessing.getInstance().sendMessage(StructClass.pack(data));
            message.setText("");
        }

    }


}
