package com.mycompany.remote_shell_group_3;

public class Remote_Shell_Group_3 {
    public static void main(String[] args) {
        // HTTP Server
        http_server server = new http_server();
        Thread serverThread = new Thread (server);
        serverThread.start();
    }
}
