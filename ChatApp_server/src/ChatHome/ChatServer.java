package ChatHome;

import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

public class ChatServer extends JFrame {
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(9002);
			while (true) {
				Socket socket = serverSocket.accept();
				Thread thread = new PerClientThread(socket);
				thread.start();
			}
		} catch (Exception e) {
			System.out.println();
		}
	}
}
