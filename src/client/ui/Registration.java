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
 * Date 12/13/2021 - 8:26 PM
 * Description: ...
 */
public class Registration extends JFrame implements ActionListener {
    final private int WIDTH = 480;
    final private int HEIGHT = 240;

    private Container container;

    private JLabel userText;
    private JTextField userInput;
    private JLabel passwordText;
    private JPasswordField passwordInput;
    private JLabel confirmPasswordText;
    private JPasswordField confirmPasswordInput;
    private JButton registerButton;

    public Registration() {
        initialFrame();
        createUI();
        setDisplay(true);
    }

    public void initialFrame() {
        setTitle("Registration");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
        JLabel title = new JLabel("Register an account");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        container.add(title);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel registrationPane = new JPanel();
        registrationPane.setLayout(new BoxLayout(registrationPane, BoxLayout.Y_AXIS));
        registrationPane.setPreferredSize(new Dimension(WIDTH, 100));
        registrationPane.setMinimumSize(new Dimension(WIDTH, 50));
        registrationPane.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        registrationPane.setLocation(0, 0);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        userText = new JLabel("User:");

        userInput = new JTextField();
        userInput.setMaximumSize(new Dimension(WIDTH, 20));
        panel1.add(Box.createRigidArea(new Dimension(50, 0)));
        panel1.add(userText);
        panel1.add(Box.createRigidArea(new Dimension(98, 0)));
        panel1.add(userInput);
        panel1.add(Box.createRigidArea(new Dimension(50, 0)));

        registrationPane.add(panel1);
        registrationPane.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

        passwordText = new JLabel("Password:");
        passwordInput = new JPasswordField();
        passwordInput.setMaximumSize(new Dimension(WIDTH, 20));
        panel2.add(Box.createRigidArea(new Dimension(50, 0)));
        panel2.add(passwordText);
        panel2.add(Box.createRigidArea(new Dimension(70, 0)));
        panel2.add(passwordInput);
        panel2.add(Box.createRigidArea(new Dimension(50, 0)));

        registrationPane.add(panel2);
        registrationPane.add(Box.createRigidArea(new Dimension(0, 20)));


        JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

        confirmPasswordText = new JLabel("Confirm password:");
        confirmPasswordText.setPreferredSize(new Dimension(150, 12));
        confirmPasswordInput = new JPasswordField();
        confirmPasswordInput.setMaximumSize(new Dimension(WIDTH, 20));
        panel3.add(Box.createRigidArea(new Dimension(50, 0)));
        panel3.add(confirmPasswordText);
        panel3.add(confirmPasswordInput);
        panel3.add(Box.createRigidArea(new Dimension(50, 0)));
        registrationPane.add(panel3);
        registrationPane.add(Box.createRigidArea(new Dimension(0, 20)));


        registerButton = new JButton("Registration");
        registerButton.setAlignmentX(CENTER_ALIGNMENT);
        registerButton.addActionListener(this);
        registrationPane.add(registerButton);
        container.add(registrationPane);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ClientProcessing client = ClientProcessing.getInstance();
        if (e.getSource() == registerButton) {
            String username = userInput.getText();
            String password = String.valueOf(passwordInput.getPassword());
            String confirmPassword = String.valueOf(confirmPasswordInput.getPassword());

            if (username.equals("") ||
                    password.equals("") ||
                    confirmPassword.equals("")
            ) {
                JOptionPane.showMessageDialog(this,
                        "User name, password and confirm password can not be left blank!");
            } else {
                if (username.contains(";") ||password.contains(";")){
                    JOptionPane.showMessageDialog(this,
                            "User name and password do not contain character ';' !");
                }else {
                    if (password.equals(confirmPassword)) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("type", "Registration");
                        data.put("username", username);
                        data.put("password", password);
                        data.put("confirmPassword", confirmPassword);
                        client.sendMessage(StructClass.pack(data));

                    } else {
                        JOptionPane.showMessageDialog(this, "Password and confirm password must be matched!");
                    }
                }
            }
        }

    }

}
