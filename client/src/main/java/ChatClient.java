import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

/**
 * Created by Jonathan on 3/17/18.
 */
public class ChatClient extends JPanel {

    private Socket socket;
    private BufferedReader bufferedReader;
    private OutputStream outputStream;
    private JTextArea chatTextArea;
    private JTextArea inputTextArea;
    private JFrame frame;

    public ChatClient() {
        createAndShowGui();
    }

    public boolean connect() {
        try {
            socket = new Socket("localhost", 7443);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = socket.getOutputStream();
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    public void run() {
        try {
            while (true) {
                String nextMessage = bufferedReader.readLine();
                java.awt.EventQueue.invokeLater(() -> {
                    chatTextArea.append(nextMessage);
                    chatTextArea.append("\n");
                    chatTextArea.validate();
                });
            }
        } catch (Exception exception) {

        }
    }

    private void createAndShowGui() {
        frame = new JFrame("Java Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        inputTextArea = new JTextArea();
        inputTextArea.setEditable(true);
        inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "onEnterPressed");
        inputTextArea.getActionMap().put("onEnterPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String newMessage = inputTextArea.getText() + "\n";
                    outputStream.write(newMessage.getBytes());
                    inputTextArea.setText("");
                } catch (IOException exception) {
                    System.out.println("Problem sending message to server");
                }
            }
        });

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int dividerLocation = (int) dimension.getSize().getWidth() / 2;

        JSplitPane containerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chatTextArea, inputTextArea);
        containerPane.setDividerLocation(dividerLocation);
        containerPane.setPreferredSize(dimension);
        containerPane.setBackground(Color.BLACK);
        containerPane.setBorder(null);

        frame.add(containerPane);
        frame.pack();
        frame.setVisible(true);
    }
}
