import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jonathan on 3/17/18.
 */
public class ChatServer {

    private final int PORT_NUMBER = 7443;
    private ExecutorService executorService;
    private Map<UUID, Socket> socketMap;
    private Set<String> userSet;
    private UserNameGenerator userNameGenerator;
    private ServerSocket serverSocket;

    public ChatServer() {
        executorService = Executors.newFixedThreadPool(10);
        socketMap = new ConcurrentHashMap<>();
        userSet = new ConcurrentSkipListSet<>();
        userNameGenerator = new UserNameGenerator();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                UUID socketID = UUID.randomUUID();
                socketMap.put(socketID, clientSocket);
                String userName = "Anonymous " + userNameGenerator.generateName();
                while (userSet.contains(userName)) {
                    userName = "Anonymous " + userNameGenerator.generateName();
                }
                userSet.add(userName);
                executorService.submit(new ChatServerThread(socketMap, socketID, userName));
            }
        } catch (IOException exception) {
            System.out.println("The server failed to start: " + exception.getMessage());
        }
    }
}
