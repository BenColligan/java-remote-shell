package com.mycompany.remote_shell_group_3;

// Server initialization
import java.net.ServerSocket;

// Socket acception
import java.net.Socket;

// OutputStream to PrintWriter conversion
import java.io.OutputStream;
import java.io.PrintWriter;

// InputStream to BufferedReader conversion
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

// Grabbing tokens from GET data
import java.util.ArrayList;

// Getting system name
import java.net.InetAddress;
import java.util.regex.Matcher;


// Replacing hex in tokens with ascii
import java.util.regex.Pattern;

public class http_server implements Runnable {
    
    PrintWriter printWriter;
    ArrayList<String> display = new ArrayList<String>();
    
    public void display() {
        // HTTP REQUEST - HEADERS
        printWriter.println("HTTP/1.0 200 OK");
        printWriter.println("Content-Type: text/html; charset=utf-8");
        printWriter.println("Server: MINISERVER");
        printWriter.println(""); 
        
        // Internal CSS - Start
        printWriter.println("<style>");
        
        // Font import
        String fontURL = "@import url('https://fonts.googleapis.com/css2?family=Open+Sans&display=swap');";
        printWriter.println(fontURL);
        
        // Internal CSS - Body
        printWriter.println("body {");
        printWriter.println("background-color: black;");
        printWriter.println("font-color: white;");
        printWriter.println("font-family: 'Open Sans', sans-serif;");
        printWriter.println("}");
        
        // Internal CSS - <p>
        printWriter.println("p {");
        printWriter.println("white-space: pre;");
        printWriter.println("color: white;");
        printWriter.println("font-size: 14px;");
        printWriter.println("padding-left: 10px;");
        printWriter.println("margin-top: 0;");
        printWriter.println("margin-bottom: 0;");
        printWriter.println("}");
        
        // Internal CSS - Input box
        printWriter.println("input {");
        printWriter.println("width: 100em;");
        printWriter.println("}");
        
        // Internal CSS - Footer
        printWriter.println("#footer {");
        printWriter.println("color: white;");
        printWriter.println("position: fixed;");
        printWriter.println("padding: 10px 10px 0px 10px;");
        printWriter.println("bottom: 0;");
        printWriter.println("width: 100%;");
        printWriter.println("height: 30px");
        printWriter.println("}");
        
        printWriter.println("</style>");
        // Internal CSS - End
        
        for (String line : display) {
            printWriter.println("<p>" + line + "</p>");
        }
        
        // Footer (Input box)
        printWriter.println("<div id=\"footer\">");
        printWriter.println("<form name=\"input\" method=\"get\">");
        
        String systemName = "";
        try {
            systemName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            System.out.println("Error whilst getting system name!");
        }
        
        printWriter.println("root@" + systemName + ":" + System.getProperty("user.dir") + "$ <input type=\"text\" name=\"input\">");
        printWriter.println("</div>");
    }
    
    public void run() {
        try {
            // Server initialization
            ServerSocket server = new ServerSocket(8080);
            System.out.print("[" + java.time.LocalDateTime.now() + "]");
            System.out.println(" Server listening on port 8080...");
            
            boolean shutdown = false;
            while (!shutdown) {
                // Socket acception
                Socket socket = server.accept();

                // OutputStream to PrintWriter conversion
                OutputStream outputStream = socket.getOutputStream();
                printWriter = new PrintWriter(outputStream);

                // InputStream to BufferedReader conversion 
                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                
                System.out.print("[" + java.time.LocalDateTime.now() + "]");
                System.out.println(" Socket accepted!");
                
                // Checking for user input
                String inLine;
                inLine = bufferedReader.readLine(); 
                
                while (!inLine.isEmpty()) {
                    // If the shutdown command has been inputted
                    if (inLine.contains("GET /?input=shutdown")) {
                        shutdown = true;
                    }
                    
                    // If there has been a command inputted
                    if (inLine.contains("GET /?input=")) {
                        char[] inLineCharArray = inLine.toCharArray();
                        String currentToken = "";
                        ArrayList<String> tokens = new ArrayList<>();
                        
                        char firstHexDigit = ' ';
                        char secondHexDigit = ' ';
                        
                        for (int n = 12; n < inLineCharArray.length; n++) {
                            switch (inLineCharArray[n]) {
                                case '+' -> {
                                    if (!currentToken.isEmpty()) {
                                        // Converting hex values to ascii
                                        StringBuffer sb = new StringBuffer();
                                        Pattern p = Pattern.compile("[0-9][A-F]");
                                        Matcher m = p.matcher(currentToken);
                                        while(m.find()){           
                                            String hex = m.group();            //find hex values            
                                            int    num = Integer.parseInt(hex.replace("\\x", ""), 16);  //parse to int            
                                            char   bin = (char)num;            // cast int to char
                                            m.appendReplacement(sb, bin+"");   // replace hex with char         
                                        }
                                        m.appendTail(sb);
                                        currentToken = sb.toString();
                                        
                                        // Removing left over '%' characters
                                        currentToken = currentToken.replace("%","");
                                        
                                        tokens.add(currentToken);
                                        System.out.print("[" + java.time.LocalDateTime.now() + "]");
                                        System.out.println(" Token [" + currentToken + "] grabbed from user input");
                                        currentToken = "";
                                    }
                                }
                                case ' ' -> {
                                    // Converting hex values to ascii
                                    StringBuffer sb = new StringBuffer();
                                    Pattern p = Pattern.compile("[0-9][A-F]");
                                    Matcher m = p.matcher(currentToken);
                                    while(m.find()){           
                                        String hex = m.group();            //find hex values            
                                        int    num = Integer.parseInt(hex.replace("\\x", ""), 16);  //parse to int            
                                        char   bin = (char)num;            // cast int to char
                                        m.appendReplacement(sb, bin+"");   // replace hex with char         
                                    }
                                    m.appendTail(sb);
                                    currentToken = sb.toString();
                                    
                                    // Removing left over '%' characters
                                    currentToken = currentToken.replace("%","");
                                    
                                    tokens.add(currentToken);
                                    System.out.print("[" + java.time.LocalDateTime.now() + "]");
                                    System.out.println(" Token [" + currentToken + "] grabbed from user input");
                                }
                                default -> currentToken += inLineCharArray[n];
                            }
                        }

                        // Executing program commands
                        ProcessBuilder processBuilder = new ProcessBuilder();
                        try {
                            processBuilder.command(tokens);
                            var process = processBuilder.start();
                            System.out.print("[" + java.time.LocalDateTime.now() + "] ");
                            System.out.println(tokens +  " executed! (Output below)");
                            
                            // Sending command output to browser
                            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    System.out.print("[" + java.time.LocalDateTime.now() + "]");
                                    System.out.println(" " + line);
                                    display.add(line);
                                }
                            }
                        } catch (IOException e) {
                            System.out.print("[" + java.time.LocalDateTime.now() + "] ");
                            System.out.println(tokens + " NOT executed! (Invalid command)");
                            display.add("Invalid command!");
                        }
                    }
                    // Switch to the next line in the BufferedReader
                    inLine = bufferedReader.readLine();
                }
                display();
                printWriter.close();
                socket.close();
            }
            server.close();
            System.out.print("[" + java.time.LocalDateTime.now() + "]");
            System.out.println(" 'shutdown' command detected - Server closing!");
        } catch (IOException e) {}
    }
}
