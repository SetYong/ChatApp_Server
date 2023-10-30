package MainHome;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainServer extends JFrame {
	private static final long serialVersionUID = 1L;
	private ArrayList<MultiServerThread> list;
	private Socket socket;
	JTextArea ta;
	JTextField tf;
	private String pass;

	public MainServer() {
		setTitle("메인서버 ver 1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ta = new JTextArea();
		add(new JScrollPane(ta));
		tf = new JTextField();
		tf.setEditable(false);
		add(tf, BorderLayout.SOUTH);
		setSize(500, 500);
		setVisible(true);
		
		// 서버 입장
		list = new ArrayList<MultiServerThread>();
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			MultiServerThread mst = null;
			boolean isStop = false;
			tf.setText("서버 실행\n");
			ta.append("[Server실행] Client 연결대기중...\n");
			while (!isStop) {
				// 클라이언트별 소켓 생성
				socket = serverSocket.accept();
				ta.append(socket.getInetAddress() + " 연결됨\n");
				// 클라이언트 확인
				mst = new MultiServerThread();
				list.add(mst);
				mst.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new MainServer();
	}

	// 내부 클래스
	class MultiServerThread extends Thread {
		private Server_DAO dao = new Server_DAO();
		private ArrayList<Server_VO> Login;
		private ArrayList<Server_VO> Profile;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		@Override
		public void run() {
			boolean isStop = false;
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				
				String user_id = null;
				String user_pwd = null;
				
				user_id = (String) ois.readObject();
				user_pwd = (String) ois.readObject();
				System.out.println(user_id);
				System.out.println(user_pwd);
				
				Login = dao.Login(user_id);
				if(Login.size() != 0 && Login.get(0).getPWD().equals(user_pwd)) {
					pass = "true";
				} else {
					pass = "false";
				}
				oos.writeObject(pass);
//				broadCasting(pass);
				System.out.println(pass);
				
//				name = dao.db_username(user_id);
//				oos.writeObject(name);
//				System.out.println(name);
				
				Profile = dao.user_Profile(user_id);
				String name = Profile.get(0).getName();
				String email = Profile.get(0).getEmail();
				String phone = Profile.get(0).getPhone();
				int dept_num = Profile.get(0).getDept_num();

				oos.writeObject(name);
				oos.writeObject(email);
				oos.writeObject(phone);
				oos.writeObject(dept_num);
			} catch (Exception e) {
				list.remove(this);
				ta.append("[" + socket.getInetAddress() + "] IP 주소의 사용자께서 비정상 종료하셨습니다.\n");
				tf.setText("남은 사용자 수 : " + list.size());
			}
		}

		// 모두에게 전송
//		public void broadCasting(String message) {
//			for (MultiServerThread ct : list) {
//				ct.send(message);
//			}
//		}

		// 한 사용자에게 전송
//		public void send(String message) {
//			try {
//				oos.writeObject(message);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}
}