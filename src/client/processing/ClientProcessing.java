package client.processing;

import client.ui.*;
import struct.StructClass;

import javax.naming.InsufficientResourcesException;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * client.processing
 * Created by Duy
 * Date 12/11/2021 - 2:50 PM
 * Description: ...
 */
public class ClientProcessing {
    public final int BUFFER_SIZE = 4096;
    public static ClientProcessing instance = null;
    private Socket socket;
    private String host;
    private int port;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String usernameOfClient;


    public static Connection connectionForm = null;
    public static Login loginForm = null;
    public static Registration registrationForm = null;
    public static ClientScreen clientScreen = null;
    public static HashMap<String, PrivateChat> privateChat = null;


    public ClientProcessing(boolean connectForm) {
        socket = null;
        host = "localhost";
        port = 0;
        usernameOfClient = null;
        if (connectForm) {
            connectionForm = new Connection();
        }
    }


    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ClientProcessing(String host, int port) {
        socket = null;
        this.host = host;
        this.port = port;

    }

    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
                dataOutputStream.close();
                dataInputStream.close();
                instance = null;
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Close connection" + e.getMessage());
        }
    }

    public boolean createSocket() {
        try {
            socket = new Socket(host, port);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean createSocket(String host, int port) {
        try {
            socket = new Socket(host, port);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static ClientProcessing getInstance() {
        if (instance == null) {
            instance = new ClientProcessing(false);
            return instance;
        }
        return instance;
    }


    public void sendMessage(String message) {
        try {
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void closeAllForms() {
        connectionForm.dispose();
        loginForm.dispose();
        clientScreen.dispose();
        String[] keys = privateChat.keySet().toArray(new String[0]);
        for (String key : keys) {
            privateChat.get(key).dispose();
        }
    }

    public void respondFileRequest(String sender, String receiver, String fileName, String size, String path, boolean accept) {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", "FileResponse");
        data.put("sender", sender);
        data.put("receiver", receiver);
        data.put("accept", String.valueOf(accept));
        data.put("fileName", fileName);
        data.put("size", size);
        data.put("path", path);
        sendMessage(StructClass.pack(data));
    }

    public void appendTextInPrivateChat(String sender, String message) {
        PrivateChat form = privateChat.get(sender);
        if (form == null) {
            form = new PrivateChat(usernameOfClient, sender);
            privateChat.put(sender, form);
        } else {
            form.setDisplay(true);
        }
        form.appendMessage(sender + ":" + message);
    }

    public void requestUserList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("type", "UserListRequest");
        data.put("username", usernameOfClient);
        sendMessage(StructClass.pack(data));
    }


    public void sendFileInPrivateChat(String receiver, String fileName, String path){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String,String> data = new HashMap<>();
                    data.put("type","PrivateFile");
                    data.put("receiver",receiver);
                    data.put("fileName",fileName);

                    byte[] fileContent = Files.readAllBytes(Path.of(path));
                    long totalPackage = (fileContent.length / (BUFFER_SIZE + 1) + 1);
                    data.put("totalPackage",String.valueOf(totalPackage));
                    long sum = fileContent.length;
                    byte[] buffer;
                    int curPackage = 0;
                    int counter = 0;
                    System.out.println(sum);
                    for (; curPackage <= totalPackage; ++curPackage) {
                        long copyLength = BUFFER_SIZE < sum ? BUFFER_SIZE : (sum % BUFFER_SIZE);
                        sum -= copyLength;
                        buffer = Arrays.copyOfRange(fileContent, counter, (int) (counter + copyLength));
                        counter += copyLength;
                        if(buffer.length > 0) {
                            data.put("currentPackage", String.valueOf(curPackage));
                            data.put("data", StructClass.packBufferToString(buffer));
                            sendMessage(StructClass.pack(data));
                            Thread.sleep(200);
                        }
                    }
                }catch (Exception e){
                    System.out.println("Send private file " + e.getMessage());
                }

            }
        });
        thread.start();
    }


    public void receivePrivateFile(HashMap<String,String >data){

        String fileName = data.get("fileName");
        String bufferString = data.get("data");
        int curPackage = Integer.parseInt(data.get("currentPackage"));
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }else{
                // overwrite
                if (curPackage == 0) {
                    file.delete();
                    file.createNewFile();
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);

            byte[] buffer = StructClass.unpackBufferFromString(bufferString);
            fileOutputStream.write(buffer);
            fileOutputStream.close();

        }catch (Exception e){
            System.out.println("Receive: " + e.getMessage());
        }
    }

    public void receiveFromServer() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                String receivedMessage = null;
                while (socket != null && !socket.isClosed()) {
                    try {
                        receivedMessage = dataInputStream.readUTF();
                        HashMap<String, String> data = StructClass.unpack(receivedMessage);
//                        System.out.println(data);
                        String type = data.get("type");
                        String username = data.get("username");
                        String flag;
                        switch (type) {
                            case "LoginResponse":
                                int status = Integer.parseInt(data.get("isSucceed"));
                                if (status == 1) {
                                    JOptionPane.showMessageDialog(loginForm, "Login successfully!");
                                    loginForm.setDisplay(false);
                                    loginForm.dispose();
                                    usernameOfClient = username;
                                    clientScreen = new ClientScreen(username);
                                    privateChat = new HashMap<>();

                                } else if (status == 0) {
                                    JOptionPane.showMessageDialog(loginForm, "Account has been logged in!!!");
                                } else if (status == 2) {
                                    JOptionPane.showMessageDialog(loginForm, "Account isn't existed!!!");
                                } else {
                                    JOptionPane.showMessageDialog(loginForm,
                                            "Login failed!" +
                                                    "\nUsername of password is incorrect!!!");
                                }
                                break;
                            case "RegistrationResponse":
                                flag = data.get("isSucceed");
                                if (flag.equals("true")) {
                                    JOptionPane.showMessageDialog(registrationForm, "Register an account successfully!");
                                    registrationForm.setDisplay(false);
                                    registrationForm.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(registrationForm, "User name is existed!!!");
                                }
                                break;
                            case "UserListResponse":
                                String[] users = StructClass.getUserListByString(data.get("users"));
                                clientScreen.setUserModel(users);
                                break;

                            case "PrivateChat":
                                String sender = data.get("sender");
                                String message = data.get("message");
                                appendTextInPrivateChat(sender, message);
                                break;
                            case "FileRequest":
                                sender = data.get("sender");
                                String receiver = data.get("receiver");
                                String fileName = data.get("fileName");
                                String size = data.get("size");
                                String path = data.get("path");
                                String text = " request to send file " + fileName + "(" + size + " bytes)" + " to you";
                                appendTextInPrivateChat(sender, text);
                                String confirmText = "Do you want to receive " + fileName + "(" + size + " bytes)" + " from " + sender + "?";
                                PrivateChat form = privateChat.get(sender);
                                if (JOptionPane.showConfirmDialog(null, confirmText, "Notification", JOptionPane.YES_NO_OPTION) == 0) {
                                    respondFileRequest(sender, receiver, fileName, size, path, true);
                                    form.appendMessage("You accept ...");

                                } else {
                                    respondFileRequest(sender, receiver, fileName, size, path, false);
                                    form.appendMessage("You refuse ...");
                                }
                                break;
                            case "FileResponse":
                                sender = data.get("sender");
                                if (sender.equals(usernameOfClient)) {
                                    receiver = data.get("receiver");
                                    form = privateChat.get(receiver);
                                    String accept = data.get("accept");
                                    if (accept.equalsIgnoreCase("true")) {
                                        form.appendMessage(receiver + " accept ...");
                                        sendFileInPrivateChat(receiver,data.get("fileName"),data.get("path"));
                                    } else {
                                        form.appendMessage(receiver + " refuse ...");

                                    }
                                }
                                break;

                            case "PrivateFile":
                                receivePrivateFile(data);
                                break;

                            default:
                                break;
                        }

                    } catch (Exception e) {
                        System.out.println("received bug " + e.getMessage());
                        closeAllForms();
                        JOptionPane.showMessageDialog(null, "Server closed connection!");
                        System.exit(0);
                        return;
                    }
                }
            }
        });
        thread.start();
    }

//    public static void main(String[] args){
//        try {
//            File file = new File("heello.txt");
//            if (!file.exists()){
//                if (file.mkdirs()) {
//                    System.out.println("Multiple directories are created!");
//                } else {
//                    System.out.println("Failed to create multiple directories!");
//                }
//            }
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
}
