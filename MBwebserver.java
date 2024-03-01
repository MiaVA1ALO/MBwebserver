import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService; // a very overlooked class which is incredible for easy threading.
import java.util.concurrent.Executors;

public class MBwebserver
{
    private final ServerSocket serverSocket;
    private final int packetSize = 2048; // too much is super slow, not enough is also super slow.
                                         // i used to use 1024 bytes, but I think much prefer 2048.
    private static String rootDirectory = "site";

    // allows asynchronous processing of requests rather than (in this context) slower, buggy, sync which
    // would be better for game servers and whatnot.
    private final ExecutorService executorService;
    // also allows multiple people to attempt to access a file at once, so that loading a page works instantly,
    // instead of waiting for there to be space. it will still wait if maxThreads is exceeded, but it is
    // unlikely that many people would try to connect at once.
    // there is not limit to site users at once, only site users who are trying to actively load a file.

    public MBwebserver(int port, String rootDirectory) throws IOException
    {
        this.serverSocket = new ServerSocket(port);
        MBwebserver.rootDirectory = rootDirectory;

        // max requests at once; *NOT* max users at once.
        int maxThreads = 5;
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    // comes from Thread(), which the class MBwebserver is an extension of.
    public void run()
    {
        System.out.println("Server is running on port " + serverSocket.getLocalPort() + "\nServer start at " + LocalDateTime.now());
        while (true)
        {
            // a sigh of relief as it finally works...
            executorService.submit(() ->
            {
                try
                {
                    handleClientRequest(serverSocket.accept());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void handleClientRequest(Socket socket)
    {
        BufferedReader reader = null;

        try
        {
            String clientIP = socket.getInetAddress().getHostAddress();

            // Get the requested file
            String requestedFile;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request = reader.readLine();
            if (request != null)
            {
                String[] requestParts = request.split(" ");
                if (requestParts.length == 3)
                {
                    String method = requestParts[0];
                    String path = requestParts[1];
                    requestedFile = path.substring(1);

                    if (method.equals("GET"))
                    {
                        if (path.equals("/"))
                        {
                            // default file is site/index.html
                            requestedFile = "index.html";
                        }
                        System.out.println("Connected: " + clientIP + " at " + requestedFile);
                        sendFile(socket, new File(rootDirectory, requestedFile));
                    }
                }
            }
        }
        catch (IOException ignored)
        {
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
                socket.close();
            }
            catch (IOException ignored)
            {
            }
        }
    }

    private void sendFile(Socket socket, File file)
    {
        try
        {
            if (file.exists())
            {
                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream outputStream = socket.getOutputStream())
                {
                    byte[] buffer = new byte[packetSize];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1)
                    {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
            else
            {
                sendFile(socket, new File(rootDirectory + "/errors", "404.html"));
            }
        }
        catch (IOException ignored)
        {
        }
    }

    public static void main(String[] args)
    {
        try
        {
            int port = 80; // recommended port 80, otherwise clients have to include the port while accessing the site.
            MBwebserver server = new MBwebserver(port, rootDirectory);
            // because MBwebserver extends Thread
            server.run();
        }
        catch (IOException ignored) {
        }
    }
}
