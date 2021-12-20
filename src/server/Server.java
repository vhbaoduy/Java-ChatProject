package server;

import struct.StructClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * server.ui
 * Created by Duy
 * Date 12/9/2021 - 11:20 PM
 * Description: ...
 */
public class Server extends JFrame implements ActionListener {
    public final int BUFFER_SIZE = 40960;

    // GUI
    final private int WIDTH = 480;
    final private int HEIGHT = 480;

    // Content Pane
    private Container container;

    private JLabel hostText;
    private JTextField hostInput;
    private JLabel portText;
    private JTextField portInput;
    private JButton runButton;
    private JButton closeButton;
    private JTextArea textArea;
    private JList<String> userList;
    private DefaultListModel<String> userModel;


    // Socket
    public static ServerSocket serverSocket;
    private HashMap<String, Socket> clients;
    private HashMap<String, DataOutputStream> clientsDataOutputStream;
    private String serverHost = "localhost";
    private int Port;
    final private String pathToData = "account.txt";
    final private String directoryData = "data";

    public Server() {
        initialFrame();
        createUI();
        setDisplay(true);

    }

    public void initialFrame() {
        setTitle("Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        setResizable(false);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setLocation(400, 100);
        container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
    }

    public void setDisplay(boolean b) {
        pack();
        setVisible(b);
    }

    public void createUI() {
        // title
        JLabel title = new JLabel("Server");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);
        container.add(title);

        // config
        JPanel configPane = new JPanel();
        configPane.setLayout(new BoxLayout(configPane, BoxLayout.Y_AXIS));
        configPane.setBorder(BorderFactory.createTitledBorder("Config Server"));
        configPane.setPreferredSize(new Dimension(WIDTH, 100));
        configPane.setMinimumSize(new Dimension(WIDTH, 50));
        configPane.setMaximumSize(new Dimension(WIDTH, 150));
        configPane.setLocation(0, 0);


        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

        hostText = new JLabel("Host:");
        hostInput = new JTextField();
        hostInput.setMaximumSize(new Dimension(WIDTH, 20));
        hostInput.setEditable(false);
        hostInput.setText("localhost");
        panel1.add(Box.createRigidArea(new Dimension(50, 0)));
        panel1.add(hostText);
        panel1.add(Box.createRigidArea(new Dimension(35, 0)));
        panel1.add(hostInput);
        panel1.add(Box.createRigidArea(new Dimension(50, 0)));

        configPane.add(panel1);
        configPane.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

        portText = new JLabel("Port:");
        portInput = new JTextField();
        portInput.setMaximumSize(new Dimension(WIDTH, 20));
        panel2.add(Box.createRigidArea(new Dimension(50, 0)));
        panel2.add(portText);
        panel2.add(Box.createRigidArea(new Dimension(38, 0)));
        panel2.add(portInput);
        panel2.add(Box.createRigidArea(new Dimension(50, 0)));

        configPane.add(panel2);
        container.add(configPane);
        configPane.add(Box.createRigidArea(new Dimension(0, 10)));


        JPanel panel3 = new JPanel();
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
        connectionPane.setPreferredSize(new Dimension(WIDTH, 250));
        connectionPane.setMinimumSize(new Dimension(WIDTH, 50));
        connectionPane.setMaximumSize(new Dimension(WIDTH, 350));
        connectionPane.setLocation(0, 0);


        JPanel listPane = new JPanel();
        listPane.setLayout(new BorderLayout());
        listPane.setBorder(BorderFactory.createTitledBorder("User Online"));
        listPane.setPreferredSize(new Dimension(150, HEIGHT));
        listPane.setMaximumSize(new Dimension(150, HEIGHT));

        userModel = new DefaultListModel<>();
        userList = new JList<String>(userModel);
        userList.setMaximumSize(new Dimension(100, HEIGHT));
        userList.setLocation(0, 0);

        JScrollPane scrollPane = new JScrollPane(userList);
        listPane.add(scrollPane);


        JPanel textPane = new JPanel();
        textPane.setLayout(new BorderLayout());
        textPane.setBorder(BorderFactory.createTitledBorder("Notification"));
        textPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        textPane.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        textArea = new JTextArea();

        textArea.setPreferredSize(new Dimension(textPane.getWidth(), textPane.getHeight()));
        textArea.setEditable(false);
        JScrollPane scrollPane1 = new JScrollPane(textArea);
        textPane.add(scrollPane1);

        connectionPane.add(Box.createRigidArea(new Dimension(10, 0)));
        connectionPane.add(listPane);
        connectionPane.add(Box.createRigidArea(new Dimension(10, 0)));
        connectionPane.add(textPane);
        connectionPane.add(Box.createRigidArea(new Dimension(10, 0)));
        container.add(connectionPane);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == runButton) {

            int port = Integer.parseInt(portInput.getText());
            try {
                this.Port = port;
                boolean flag = createServer();
                if (flag) {
                    runButton.setEnabled(false);
                    portInput.setEditable(false);
                    JOptionPane.showMessageDialog(this, "Run server successfully!");
                    textArea.append("Server is running at " + hostInput.getText() + ":" + portInput.getText() + "\n");
                    listen();
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        if (e.getSource() == closeButton) {
            boolean flag = false;
            try {
                flag = closeServer();
            } catch (Exception ex) {
                flag = false;
            }
            if (flag) {
                textArea.append("Close server " + hostText.getText() + portInput.getText() + "\n");
                portInput.setText("");
                portInput.setEditable(true);
                runButton.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Close server successfully!");
            }
        }

    }


    public void updateUserModel() {
        userModel = new DefaultListModel<>();
        if (clients != null) {
            String[] users = clients.keySet().toArray(new String[0]);

            for (String user : users) {
                userModel.addElement(user);
            }
        }
        userList.setModel(userModel);
    }


    /**
     * Create server socket
     *
     * @return true if successful, or not
     */
    public boolean createServer() {
        try {
            serverSocket = new ServerSocket(Port);
            clients = new HashMap<>();
//            clientsBufferWriter = new HashMap<>();
            clientsDataOutputStream = new HashMap<>();
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public boolean closeServer() {
        if (serverSocket != null) {
            String[] keys = clients.keySet().toArray(new String[0]);
            for (String key : keys) {
                Socket socket = clients.get(key);
                try {
                    socket.close();
                } catch (Exception e) {
                    return false;
                }
            }
            clients = null;
            updateUserModel();
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }


    /**
     * Check Login of client
     *
     * @param username String of username
     * @param password String of password
     * @return True/False
     */
    public int checkLogin(String username, String password) {
        try {
            if (clients.get(username) != null) {
                return 0;
            }
            File file = new File(directoryData + "/" + pathToData);
            if (!file.exists()) {
                return 2;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            String user = username.toLowerCase(Locale.ROOT);
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] lineList = line.split(";");
                if (lineList.length == 2) {
                    if (user.compareToIgnoreCase(lineList[0]) == 0) {
                        if (password.equals(lineList[1])) {
                            return 1;
                        }else{
                            return -1;
                        }
                    }
                }
            }
            bufferedReader.close();
            return 2;

        } catch (Exception e) {
            return -1;
        }
    }


    /**
     * Send message to client
     *
     * @param dataOutputStream Data Output Stream of client
     * @param data             hash map string - string of data
     */
    public void sendMessageToClient(DataOutputStream dataOutputStream, HashMap<String, String> data) {
        try {
            dataOutputStream.writeUTF(StructClass.pack(data));
            dataOutputStream.flush();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public void respondLogin(DataOutputStream dataOutputStream, String username, int isSucceed) {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", "LoginResponse");
        data.put("isSucceed", String.valueOf(isSucceed));
        data.put("username", username);
        sendMessageToClient(dataOutputStream, data);
    }

    /**
     * Registration an account
     *
     * @param username String of username
     * @param password String of password
     * @return True/False
     */
    public boolean registerAccount(String username, String password) {
        try {
            File dir = new File(directoryData);
            File file = null;
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("Data directory are created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            }
            file = new File(directoryData + "/" + pathToData);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            String user = username.toLowerCase(Locale.ROOT);
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] lineList = line.split(";");
                if (lineList.length == 2) {
                    if (user.compareToIgnoreCase(lineList[0]) == 0) {
                        return false;
                    }
                }
            }
            bufferedReader.close();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write(username + ";" + password + "\n");
            bufferedWriter.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Respond registration to client
     *
     * @param dataOutputStream Data output stream of client
     * @param isSucceed        True/false
     */
    public void respondRegistration(DataOutputStream dataOutputStream, boolean isSucceed) {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", "RegistrationResponse");
        data.put("isSucceed", String.valueOf(isSucceed));

        sendMessageToClient(dataOutputStream, data);
    }


    public void listen() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (serverSocket != null && !serverSocket.isClosed()) {
                    Socket clientSocket;
                    try {
                        clientSocket = serverSocket.accept();
                        textArea.append("Talking to client: [" + clientSocket.getPort() + "]\n");
                        Thread clientThread1 = new Thread(new ReceiveThread(clientSocket));
                        clientThread1.start();

                    } catch (Exception e) {

                    }
                }
            }
        });
        thread.start();
    }

    /**
     * Get user list except user request
     *
     * @param usernameRequest
     * @return
     */
    public String[] getUserList(String usernameRequest) {
        String[] keys = clients.keySet().toArray(new String[0]);
        if (keys.length > 0) {
            String[] list = new String[keys.length - 1];
            int i = 0;
            for (String key : keys) {
                if (!key.equals(usernameRequest)) {
                    list[i++] = key;
                }
            }
            return list;
        }
        return null;
    }


    /**
     * Respond user list to client
     *
     * @param usernameRequest  username of client
     * @param dataOutputStream data output stream of client
     */
    public void respondUserList(String usernameRequest, DataOutputStream dataOutputStream) {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", "UserListResponse");
        String[] users = getUserList(usernameRequest);
        if (users != null && users.length > 0) {
            String str = "";
            for (int i = 0; i < users.length - 1; ++i) {
                str += users[i] + ",";
            }
            str += users[users.length - 1];
            data.put("users", str);
//            System.out.println(data);
            sendMessageToClient(dataOutputStream, data);
        }

    }

    public void checkClientsSocket() {
        String[] users = clients.keySet().toArray(new String[0]);
        for (String user : users) {
            Socket socket = clients.get(user);
            try {
                if (socket.isClosed()) {
                    textArea.append(user + " has disconnected.\n");
                    clients.remove(user, socket);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        updateUserModel();
    }

    /**
     * Broadcast user lis
     */
    public void broadcastUserList() {
        String[] users = clients.keySet().toArray(new String[0]);

        for (String user : users) {
            try {
                respondUserList(user, clientsDataOutputStream.get(user));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Send private chat from sender to receiver
     *
     * @param sender   username of sender
     * @param receiver username of receiver
     * @param message  string of message
     */
    public void sendPrivateChat(String sender, String receiver, String message) {
        try {
            DataOutputStream receiverOutput = clientsDataOutputStream.get(receiver);
            HashMap<String, String> data = new HashMap<>();
            data.put("type", "PrivateChat");
            data.put("sender", sender);
            data.put("message", message);
            sendMessageToClient(receiverOutput, data);

        } catch (Exception e) {
            System.out.println("Send private chat " + e.getMessage());
        }
    }

    public void sendFileRequest(String sender, String receiver, String filename, String size, String path) {
        try {
            DataOutputStream receiverOutput = clientsDataOutputStream.get(receiver);
            HashMap<String, String> data = new HashMap<>();
            data.put("type", "FileRequest");
            data.put("sender", sender);
            data.put("receiver", receiver);
            data.put("fileName", filename);
            data.put("size", size);
            data.put("path", path);
            sendMessageToClient(receiverOutput, data);

        } catch (Exception e) {
            System.out.println("Send private chat " + e.getMessage());
        }
    }

    public void sendFileResponse(String sender, String receiver, String path, String fileName, String accept) {
        try {
            DataOutputStream senderOutput = clientsDataOutputStream.get(sender);
            HashMap<String, String> data = new HashMap<>();
            data.put("type", "FileResponse");
            data.put("sender", sender);
            data.put("receiver", receiver);
            data.put("accept", accept);
            data.put("fileName", fileName);
            data.put("path", path);
            sendMessageToClient(senderOutput, data);

        } catch (Exception e) {
            System.out.println("Send private chat " + e.getMessage());
        }
    }

    public void sendPrivateFile(HashMap<String, String> data) {
        String receiver = data.get("receiver");
        DataOutputStream receiverOutput = clientsDataOutputStream.get(receiver);
        sendMessageToClient(receiverOutput, data);
    }


    public class ReceiveThread implements Runnable {
        private Socket clientSocket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        public ReceiveThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                InputStream inputStream = clientSocket.getInputStream();
//                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                dataInputStream = new DataInputStream(inputStream);
                OutputStream outputStream = clientSocket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);
//                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,StandardCharsets.UTF_8));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            while (clientSocket != null && !clientSocket.isClosed()) {
                String receivedMessage = null;
                checkClientsSocket();
                try {
//                    receivedMessage = bufferedReader.readLine();
                    receivedMessage = dataInputStream.readUTF();
;
                    HashMap<String, String> data = StructClass.unpack(receivedMessage);

                    String type = data.get("type");
                    String username = data.get("username");
                    boolean flag;
                    switch (type) {
                        case "Login":
                            int status = checkLogin(username, data.get("password"));
                            if (status == 1) {
                                clients.put(username, clientSocket);
//                                clientsBufferWriter.put(username,bufferedWriter);
                                clientsDataOutputStream.put(username, dataOutputStream);
                                textArea.append("[LOGIN]-[" + clientSocket.getPort() + "][" + username + "] has been logged in successfully!\n");
                                updateUserModel();
                            }
                            respondLogin(dataOutputStream, data.get("username"), status);
                            broadcastUserList();
                            break;

                        case "Logout":
                            clients.remove(username);
                            clientsDataOutputStream.remove(username);
                            textArea.append("[LOGOUT]-[" + clientSocket.getPort() + "][" + username + "] has been logged out !\n");
                            updateUserModel();
                            broadcastUserList();
                            break;
                        case "Registration":
                            flag = registerAccount(username, data.get("password"));
                            if (flag) {
                                textArea.append("[REGISTRATION]-[" + clientSocket.getPort() + "][" + username + "] has been registered successfully!\n");
                            }
                            respondRegistration(dataOutputStream, flag);
                            break;
                        case "UserListRequest":
                            respondUserList(username, dataOutputStream);
                            break;

                        case "PrivateChat":
                            String sender = data.get("sender");
                            String receiver = data.get("receiver");
                            String message = data.get("message");
                            sendPrivateChat(sender, receiver, message);
                            break;

                        case "FileRequest":
                            sender = data.get("sender");
                            receiver = data.get("receiver");
                            sendFileRequest(sender, receiver, data.get("fileName"), data.get("size"), data.get("path"));
                            break;
                        case "FileResponse":
                            sender = data.get("sender");
                            receiver = data.get("receiver");
                            sendFileResponse(sender, receiver, data.get("path"), data.get("fileName"), data.get("accept"));
                            break;

                        case "PrivateFile":
                            sendPrivateFile(data);
                            break;
                        default:
                            break;
                    }

                } catch (IOException e) {
                    checkClientsSocket();
                    break;
                }

            }

        }
    }


    public static void main(String[] args) {
        Server serverUI = new Server();
//        System.out.println(String.valueOf(true));
    }


}
