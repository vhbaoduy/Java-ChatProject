package server.processing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * server.processing
 * Created by Duy
 * Date 12/10/2021 - 12:14 AM
 * Description: ...
 */
public class MyServer {
    private ServerSocket serverSocket;
    private ArrayList<Socket> userList;

    private String serverHost = "localhost";
    private int Port;

    /**
     * Default constructor
     */
    public MyServer(){
        serverSocket = null;
        userList = null;
    }

    public MyServer(int port){
        this.Port = port;
        userList = null;
    }

    public boolean createServer(){
        try {
            serverSocket = new ServerSocket(Port);
            return true;

        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }

    }




    public int getPort() {
        return Port;
    }


    public void setPort(int port) {
        this.Port = port;
    }

    public boolean closeServer() {
        try {
            serverSocket.close();
            return  true;
        }catch (Exception e){
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        MyServer myServer = new MyServer();
        myServer.setPort(3000);
        myServer.createServer();

        Socket socket = new Socket("localhost", 3000);
    }
}
