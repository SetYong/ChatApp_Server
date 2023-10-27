package Server_Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class MultiClient {
   private String serverAddress = "14.42.124.35";
   private int serverPort = 12345;
   private String clientName;
   private JFrame frame;
   private JTextArea textArea;
   private JTextField messageField;
   private ObjectOutputStream out;

   public MultiClient() {
      clientName = JOptionPane.showInputDialog("Enter your name:");
      frame = new JFrame(clientName + "'s Chat");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(400, 300);

      textArea = new JTextArea();
      textArea.setEditable(false);
      frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

      messageField = new JTextField();
      frame.add(messageField, BorderLayout.SOUTH);
      messageField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            sendMessage(messageField.getText());
         }
      });

      try {
         Socket socket = new Socket(serverAddress, serverPort);
         out = new ObjectOutputStream(socket.getOutputStream());
         out.writeObject(clientName);

         new Thread(new ClientListener(socket)).start();
      } catch (IOException e) {
         e.printStackTrace();
      }

      frame.setVisible(true);
   }

   private void sendMessage(String message) {
      try {
         out.writeObject(message);
         out.writeObject(JOptionPane.showInputDialog("Enter recipient's name:"));
         messageField.setText("");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private class ClientListener implements Runnable {
      private Socket socket;

      public ClientListener(Socket socket) {
         this.socket = socket;
      }

      public void run() {
         try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while (true) {
               String message = (String) in.readObject();
               textArea.append(message + "\n");
            }
         } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
         }
      }
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> new MultiClient());
   }
}
