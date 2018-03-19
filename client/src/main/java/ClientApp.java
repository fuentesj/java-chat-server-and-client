/**
 * Created by Jonathan on 3/17/18.
 */
public class ClientApp {

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.connect();
        chatClient.run();
    }
}
