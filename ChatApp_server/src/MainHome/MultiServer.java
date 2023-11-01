package MainHome;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class MultiServer extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	private ServerSocket serverSocket;
	private HashMap<String, ObjectOutputStream> clientOutputStreams;

	public MultiServer() {
//		setTitle("Multi-Client Server");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setSize(400, 300);
//		textArea = new JTextArea();
//		textArea.setEditable(false);
//		add(textArea, BorderLayout.CENTER);
		clientOutputStreams = new HashMap<>();
		try {
			serverSocket = new ServerSocket(12345);
			System.out.println("Server is running. Waiting for clients...");
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				new ClientHandler(clientSocket, out).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class ClientHandler extends Thread {
		private Socket socket;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private String clientName;

		public ClientHandler(Socket socket, ObjectOutputStream out) {
			this.socket = socket;
			this.out = out;
		}

		@Override
		public void run() {
			try {
				while (true) {
					in = new ObjectInputStream(socket.getInputStream());
					String id = (String) in.readObject();
					String pwd = (String) in.readObject();
					clientName = id; // 클라이언트 이름 설정
					System.out.println(clientName + " connected");
					clientOutputStreams.put(clientName, out);

					while (true) {
						String inp = (String) in.readObject();
						System.out.println(clientName + " sent: " + inp);
						String[] arr = inp.split(":");
						String sendName = arr[0];
						String message = arr[1];
						String recipient = arr[2];
						System.out.println("in / 발신자 : " + sendName + " 메세지 :" + message + " 수신자 :" + recipient);

						// 클라이언트 간 메시지 중계
						for (String client : clientOutputStreams.keySet()) {
							ObjectOutputStream recipientOut = clientOutputStreams.get(client);
							if (recipientOut != null) {
								// 원래의 메시지를 모든 클라이언트에게 보냅니다.
								recipientOut.writeObject(sendName + ":" + message + ":" + recipient);
								System.out.println("out / 발신자 : "+ sendName + " 메세지 : " + message + " 수신자 :" + recipient);
								recipientOut.flush();
							}
						}
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				clientOutputStreams.remove(clientName);
				System.out.println(clientName + " disconnected");
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MultiServer().setVisible(true));
	}
}