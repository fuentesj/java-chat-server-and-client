import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Jonathan on 3/17/18.
 */
public class ChatServerThread implements Runnable {

    private Map<UUID, Socket> socketMap;
    private UUID socketID;
    private BufferedReader bufferedReader;
    private String userName;
    private static Logger logger = LoggerFactory.getLogger(ChatServerThread.class);

    public ChatServerThread(Map<UUID, Socket> socketMap, UUID socketID, String userName) {
        this.socketMap = Objects.requireNonNull(socketMap, "socketMap cannot be null.");
        this.socketID = Objects.requireNonNull(socketID, "socketID cannot be null");
        this.userName = Objects.requireNonNull(userName, "userName cannot be null");
    }

    @Override
    public void run() {
        try {
            Socket socket = socketMap.get(socketID);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String message = bufferedReader.readLine();
                String pattern = "HH:mm";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                String date = "(" + (LocalDateTime.now()).format(formatter) + ")";
                for (Map.Entry<UUID, Socket> entry : socketMap.entrySet()) {
                    OutputStream outputStream = entry.getValue().getOutputStream();
                    String output = userName + " " + date + ": " + message + "\n";
                    outputStream.write(output.getBytes());
                }
            }
        } catch (IOException exception) {
            logger.error("Error establishing connection to chat server.");
        }
    }
}
