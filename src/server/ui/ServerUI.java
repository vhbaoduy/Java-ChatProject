package server.ui;

import server.processing.MyServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * server.ui
 * Created by Duy
 * Date 12/9/2021 - 11:20 PM
 * Description: ...
 */
public class ServerUI extends JFrame implements ActionListener {
    final private int WIDTH = 480;
    final private int HEIGHT = 480;
    
    private Container container;

    private JLabel hostText;
    private JTextField hostInput;
    private JLabel portText;
    private JTextField portInput;
    private JButton runButton;
    private JButton closeButton;

    private JTextArea textArea;
    private JList<String> userList;

    private MyServer server;

    public ServerUI(){
        initialFrame();
        createUI();
        setDisplay(true);

    }

    public void initialFrame(){
        setTitle("Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        setResizable(false);
        setMinimumSize(new Dimension(WIDTH,HEIGHT));
        setLocation(400,100);
        container = getContentPane();
        container.setLayout(new BoxLayout(container,BoxLayout.PAGE_AXIS));
    }

    public void setDisplay(boolean b){
        pack();
        setVisible(b);
    }

    public void createUI(){
        // title
        JLabel title = new JLabel("Server");
        title.setFont(new Font("Arial", Font.BOLD,20));
        title.setAlignmentX(CENTER_ALIGNMENT);
        container.add(title);

        // config
        JPanel configPane = new JPanel();
        configPane.setLayout(new BoxLayout(configPane, BoxLayout.Y_AXIS));
        configPane.setBorder(BorderFactory.createTitledBorder("Config Server"));
        configPane.setPreferredSize(new Dimension(WIDTH,100));
        configPane.setMinimumSize(new Dimension(WIDTH,50));
        configPane.setMaximumSize(new Dimension(WIDTH,150));
        configPane.setLocation(0,0);


        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1,BoxLayout.X_AXIS));

        hostText = new JLabel("Host:");
        hostInput = new JTextField();
        hostInput.setMaximumSize(new Dimension(WIDTH,20));
        hostInput.setEditable(false);
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


        JPanel panel3= new JPanel();
        closeButton = new JButton("Close server");
        closeButton.addActionListener(this);

        runButton = new JButton("Run Server");
        runButton.addActionListener(this);
        panel3.add(closeButton);
        panel3.add(runButton);
        configPane.add(panel3);

        JPanel connectionPane = new JPanel();
        connectionPane.setLayout(new BoxLayout(connectionPane, BoxLayout.X_AXIS));
        connectionPane.setBorder(BorderFactory.createTitledBorder("Connection"));
        connectionPane.setPreferredSize(new Dimension(WIDTH,250));
        connectionPane.setMinimumSize(new Dimension(WIDTH,50));
        connectionPane.setMaximumSize(new Dimension(WIDTH,350));
        connectionPane.setLocation(0,0);


        JPanel listPane = new JPanel();
        listPane.setLayout(new BorderLayout());
        listPane.setBorder(BorderFactory.createTitledBorder("User Online"));
        listPane.setPreferredSize(new Dimension(150,HEIGHT));
        listPane.setMaximumSize(new Dimension(150,HEIGHT));
        DefaultListModel<String> l1 = new DefaultListModel<>();
        userList = new JList<String>(l1);
        userList.setMaximumSize(new Dimension(100,HEIGHT));
        userList.setLocation(0,0);

        JScrollPane scrollPane = new JScrollPane(userList);
        listPane.add(scrollPane);


        JPanel textPane = new JPanel();
        textPane.setLayout(new BorderLayout());
        textPane.setBorder(BorderFactory.createTitledBorder("Notification"));
        textPane.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        textPane.setMaximumSize(new Dimension(WIDTH,HEIGHT));
        textArea = new JTextArea();

        textArea.setPreferredSize(new Dimension(textPane.getWidth(),textPane.getHeight()));
        textArea.setEditable(false);
        JScrollPane scrollPane1 = new JScrollPane(textArea);
        textPane.add(scrollPane1);

        connectionPane.add(Box.createRigidArea(new Dimension(10,0)));
        connectionPane.add(listPane);
        connectionPane.add(Box.createRigidArea(new Dimension(10,0)));
        connectionPane.add(textPane);
        connectionPane.add(Box.createRigidArea(new Dimension(10,0)));
        container.add(connectionPane);



    }



    public static void main(String[] args){
        ServerUI serverUI = new ServerUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == runButton){

            int port = Integer.parseInt(portInput.getText());
            try{
                server = new MyServer(port);
                boolean flag = server.createServer();
                if (flag){
                    runButton.setEnabled(false);
                    portInput.setEditable(false);
                    JOptionPane.showMessageDialog(this,"Run server successfully!");
                    textArea.append("Run server " + hostText.getText() + portInput.getText() + "\n");
                }

            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        if (e.getSource() == closeButton){
            boolean flag = server.closeServer();
            server = null;
            if (flag){
                textArea.append("Close server " + hostText.getText() + portInput.getText() + "\n");
                portInput.setText("");
                portInput.setEditable(true);
                runButton.setEnabled(true);
                JOptionPane.showMessageDialog(this,"Close server successfully!");
            }
        }

    }
}
