package MainHome;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainServer extends JFrame {
	private static final long serialVersionUID = 1L;
	private ArrayList<MultiServerThread> list;
	private HashMap<String, ObjectOutputStream> clientOutputStreams = new HashMap<String, ObjectOutputStream>();
	private Socket socket;
	private String pass;
	JTextArea TextArea;
	JTextField TextField;
	String user_id = null;
	String user_pwd = null;

	public MainServer() {
		setTitle("메인서버 ver 1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TextArea = new JTextArea();
		add(new JScrollPane(TextArea));
		TextField = new JTextField();
		TextField.setEditable(false);
		add(TextField, BorderLayout.SOUTH);
		setSize(500, 500);
		setVisible(true);

		// 서버 입장
		list = new ArrayList<MultiServerThread>();
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			System.out.println("서버 실행");
			MultiServerThread mst = null;

			boolean isStop = false;
			TextField.setText("서버 실행\n");
			TextArea.append("[Server실행] Client 연결대기중...\n");
			while (!isStop) {
				// 클라이언트별 소켓 생성
				socket = serverSocket.accept();
				TextArea.append(socket.getInetAddress() + " 연결됨\n");
				// 클라이언트 확인
				mst = new MultiServerThread();
				list.add(mst);
				mst.start();
				TextField.setText("남은 사용자 수 : " + list.size());
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
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());

				while (true) {
					user_id = (String) ois.readObject();
					user_pwd = (String) ois.readObject();
					System.out.println("USER_ID : " + user_id);
					System.out.println("USER_PWD : " + user_pwd);

					Login = dao.Login(user_id);
					if (Login.size() != 0 && Login.get(0).getPWD().equals(user_pwd)) {
						pass = "true";
						System.out.println("성공");
						logintest();
					} else {
						pass = "false";
						System.out.println("실패");
					}

					while (true) {
						String inp = (String) ois.readObject();
						String[] arr = inp.split(":");
						String sendName = arr[0];
						String uid = sendName;
						clientOutputStreams.put(uid, oos);
						System.out.println(uid + " sent: " + inp);
						String message = arr[1];
						String recipient = arr[2];
						System.out.println("in / 발신자 : " + sendName + " 메세지 :" + message + " 수신자 :" + recipient);

						// 클라이언트 간 메시지 중계
						// 클라이언트 간 메시지 중계
						for (String client : clientOutputStreams.keySet()) {
							if (!client.equals(sendName)) { // 발신자에게는 메시지를 보내지 않도록 변경
								ObjectOutputStream recipientOut = clientOutputStreams.get(client);
								if (recipientOut != null) {
									// 메시지를 모든 클라이언트에게 보냅니다.
									recipientOut.writeObject(sendName + ":" + message + "123" + ":" + recipient);
									recipientOut.flush();
									System.out.println(
											"out / 발신자 : " + sendName + " 메세지 : " + message + " 수신자 :" + recipient);
								}
							}
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				list.remove(this);
				TextArea.append("[" + socket.getInetAddress() + "] IP 주소의 사용자께서 비정상 종료하셨습니다.\n");
				TextField.setText("남은 사용자 수 : " + list.size());

				// 변경: 클라이언트 연결 종료 시 clientOutputStreams에서 해당 클라이언트 제거
				clientOutputStreams.remove(user_id);
				System.out.println(user_id + " disconnected");
			}
		}

		public void logintest() {
			try {
				oos.writeObject(pass);
				System.out.println("넘어간다!");
				System.out.println("로그인 성공 여부 :" + pass);
				System.out.println("-------------------------------------------------------");

				Profile = dao.user_Profile(user_id);
				String name = Profile.get(0).getName();
				String email = Profile.get(0).getEmail();
				String phone = Profile.get(0).getPhone();
				String dept_num = Profile.get(0).getDept_num();
				System.out.println(name);
				oos.writeObject(name);
				oos.writeObject(email);
				oos.writeObject(phone);
				oos.writeObject(dept_num);

				System.out.println("정보 넘어가나?");
			} catch (Exception e) {
				System.out.println("로그인 실패");
			}
		}
	}

}