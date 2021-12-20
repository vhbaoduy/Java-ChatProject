package client.ui;

//import client.ClientApplication;

import client.processing.ClientProcessing;
import struct.StructClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * client.ui
 * Created by Duy
 * Date 12/13/2021 - 8:08 PM
 * Description: ...
 */
public class Login extends JFrame implements ActionListener {
    final private int WIDTH = 480;
    final private int HEIGHT = 240;

    private Container container;

    private JLabel userText;
    private JTextField userInput;
    private JLabel passwordText;
    private JPasswordField passwordInput;
    private JButton loginButton;
    private JButton registerButton;

    public Login() {
        initialFrame();
        createUI();
        setDisplay(true);
    }

    public void initialFrame() {
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                ClientProcessing.getInstance().closeConnection();
                System.exit(0);
            }
            });
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        setResizable(false);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setLocation(500, 200);
        container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
    }


    public void setDisplay(boolean b) {
        pack();
        setVisible(b);
    }

    public void createUI() {
        JLabel title = new JLabel("Login to continue");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(title);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel loginPane = new JPanel();
        loginPane.setLayout(new BoxLayout(loginPane, BoxLayout.Y_AXIS));
        loginPane.setPreferredSize(new Dimension(WIDTH, 100));
        loginPane.setMinimumSize(new Dimension(WIDTH, 50));
        loginPane.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        loginPane.setLocation(0, 0);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        userText = new JLabel("User:");
        userText.setPreferredSize(new Dimension(128, 12));
        userInput = new JTextField();
        userInput.setMaximumSize(new Dimension(WIDTH, 20));
        panel1.add(Box.createRigidArea(new Dimension(50, 0)));
        panel1.add(userText);
        panel1.add(Box.createRigidArea(new Dimension(5, 0)));
        panel1.add(userInput);
        panel1.add(Box.createRigidArea(new Dimension(50, 0)));

        loginPane.add(panel1);
        loginPane.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

        passwordText = new JLabel("Password:");
        passwordText.setPreferredSize(new Dimension(100, 12));
        passwordInput = new JPasswordField();
        passwordInput.setMaximumSize(new Dimension(WIDTH, 20));
        panel2.add(Box.createRigidArea(new Dimension(50, 0)));
        panel2.add(passwordText);
        panel2.add(passwordInput);
        panel2.add(Box.createRigidArea(new Dimension(50, 0)));

        loginPane.add(panel2);
        loginPane.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel panel3 = new JPanel();
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        registerButton = new JButton("Registration");
        registerButton.addActionListener(this);
        panel3.add(registerButton);
        panel3.add(loginButton);

        loginPane.add(panel3);

        container.add(loginPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ClientProcessing client = ClientProcessing.getInstance();
        if (e.getSource() == registerButton) {
            ClientProcessing.registrationForm = new Registration();
            ClientProcessing.registrationForm.setDisplay(true);
        }
        if (e.getSource() == loginButton) {
            String user = userInput.getText();
            String password = String.valueOf(passwordInput.getPassword());
            HashMap<String, String> data = new HashMap<>();
            data.put("type", "Login");
            data.put("username", user);
            data.put("password", password);
            client.sendMessage(StructClass.pack(data));
        }
    }

}
