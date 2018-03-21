import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Jonathan on 3/17/18.
 */
public class ChatClientPanel extends JPanel {

    private JFrame frame;
    private JSplitPane containerPane;
    private JTextArea chatTextArea;
    private JTextArea inputTextArea;
    private JMenu menu;
    private JMenuBar menuBar;
    private JMenuItem menuItem;
    private ClientConnectionThread clientConnectionThread;
    private boolean isOnline;
    private static Logger logger = LoggerFactory.getLogger(ChatClientPanel.class);

    public ChatClientPanel() {
        createAndShowGui();
    }

    private void createAndShowGui() {
        buildChatClientWindow();
        buildChatClientMenu();
        frame.add(containerPane);
        frame.pack();
        frame.setVisible(true);
    }

    private void buildChatClientWindow() {
        frame = new JFrame("Java Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        inputTextArea = new JTextArea();
        inputTextArea.setEditable(false);
        inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "onEnterPressed");
        inputTextArea.getActionMap().put("onEnterPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newMessage = inputTextArea.getText() + "\n";
                inputTextArea.setText("");
                clientConnectionThread.sendData(newMessage);
            }
        });

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int dividerLocation = (int) dimension.getSize().getWidth() / 2;

        containerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chatTextArea, inputTextArea);
        containerPane.setDividerLocation(dividerLocation);
        containerPane.setPreferredSize(dimension);
        containerPane.setBackground(Color.BLACK);
        containerPane.setBorder(null);
    }

    private void buildChatClientMenu() {
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menuBar.add(menu);
        menuItem = new JMenuItem("Server Options");
        menuItem.addActionListener((ActionEvent actionEvent) -> {
            JLabel hostLabel = new JLabel("Host:");
            JTextField hostField = new JTextField();

            JPanel serverOptionsPanel = new JPanel();
            serverOptionsPanel.add(hostLabel);
            serverOptionsPanel.add(hostField);

            String host = JOptionPane.showInputDialog(serverOptionsPanel, "Enter server location.");
            if (host != null && !"".equals(host) && checkIfValidIP(host)) {
                String[] hostArray = host.split(":");
                clientConnectionThread = new ClientConnectionThread(hostArray[0], hostArray[1]);
                if (clientConnectionThread != null) {
                    isOnline = true;
                    SwingUtilities.invokeLater(() -> {
                        inputTextArea.setEditable(true);
                    });
                }
                Thread thread = new Thread(clientConnectionThread);
                thread.start();
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a valid URL.", "Invalid URL", JOptionPane.ERROR_MESSAGE);
            }

        });
        menu.add(menuItem);
        frame.setJMenuBar(menuBar);
    }

    private boolean checkIfValidIP(String host) {
        String[] hostArray = host.split(":");
        String[] ipArray = hostArray[0].split("\\.");
        if (ipArray.length == 4) {
            for (int index = 0; index < ipArray.length; index++) {
                int currentOctet = Integer.parseInt(ipArray[index]);
                if (currentOctet < 0 || 255 < currentOctet) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private class ClientConnectionThread implements Runnable {
        private Socket socket;
        private BufferedReader bufferedReader;
        private OutputStream outputStream;

        public ClientConnectionThread(String host, String port) {
            try {
                socket = new Socket(host, Integer.parseInt(port));
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outputStream = socket.getOutputStream();
            } catch (IOException exception) {
                logger.error("Error connecting to chat server.");
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String nextMessage = bufferedReader.readLine();
                    SwingUtilities.invokeLater(() -> {
                        ChatClientPanel.this.chatTextArea.append(nextMessage + "\n");
                    });
                }
            } catch (Exception exception) {
                logger.error("Error reading data in from chat server.");
            }
        }

        public void sendData(String message) {
            try {
                outputStream.write(message.getBytes());
            } catch (IOException exception) {
                logger.error("Error writing data out to chat server.");
            }
        }
    }
}