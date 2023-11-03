package MainHome;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainServer extends JFrame {
	private static final long serialVersionUID = 1L;
	private ArrayList<MultiServerThread> list;
	private HashMap<String, ObjectOutputStream> clientOutputStreams = new HashMap<String, ObjectOutputStream>();
	private Socket socket;
	private String pass = "false";
	JTextArea TextArea;
	JTextField TextField;
	private Date today = new Date();;
	private SimpleDateFormat timeDate = new SimpleDateFormat("HH:mm:ss a");

	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

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
			TextArea.append("[" + timeDate.format(today) + "] [Server실행] Client 연결대기중...\n");
			while (!isStop) {
				// 클라이언트별 소켓 생성
				socket = serverSocket.accept();
				today = new Date();
				TextArea.append("[" + timeDate.format(today) + "] [" + socket.getInetAddress() + "] 새로운 사용자 연결됨\n");
				// 클라이언트 확인
				mst = new MultiServerThread();
				list.add(mst);
				mst.start();
				TextField.setText("남은 사용자 수 : " + list.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		executorService.scheduleAtFixedRate(() -> refreshAllClients(), 0, 10, TimeUnit.SECONDS);
	}

	public static void main(String[] args) {
		new MainServer();
	}

	// 내부 클래스
	class MultiServerThread extends Thread {
		private Server_DAO dao = new Server_DAO();
		private ArrayList<Server_VO> Login;
		private ArrayList<Server_VO> Profile;
		private ArrayList<Server_VO> toDoList;
		private ArrayList<Server_VO> pwdup;
		private ArrayList<Server_VO> setProfile;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private Date today;
		String user_id = null, user_pwd = null, user_name = null;
		private SimpleDateFormat timeDate = new SimpleDateFormat("HH:mm:ss a");
		private boolean refreshRequested = false;

		public void sendRefreshSignal() {
			refreshRequested = true;
		}

		@Override
		public void run() {
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());

				while (true) {
					String first = (String) ois.readObject();
					System.out.println(first);
					String[] seperate = first.split("=0$0=");
					if (seperate[0].equals("[reset]")) {
						System.out.println("리셋 진행합니다.");
						String id = (String) ois.readObject();
						String name = (String) ois.readObject();
						System.out.println("초기화 요청 ID : " + id);
						System.out.println("초기화 요청 NAME : " + name);

						pwdup = dao.pwdUp(id, name);
						oos.writeObject(pwdup.get(0).getID());
						oos.writeObject(pwdup.get(0).getPWD());

						System.out.println("-------------------------------------------------------");
						System.out.println("초기화 한 사원 사번 : " + pwdup.get(0).getID());
						System.out.println("초기화 한 사원 이름 : " + pwdup.get(0).getPWD());
						System.out.println("-------------------------------------------------------");
					} else if (seperate[0].equals("[login]")) {
						System.out.println("로그인 진행합니다.");
						while (true) {
//							if (refreshRequested) {
//								// 새로고침 동작 또는 메시지 교환
//								oos.writeObject("Refreshed");
//								oos.flush();
//								refreshRequested = false;
//							}
							System.out.println("정보 읽기 ");
							today = new Date();
							user_id = (String) ois.readObject();
							user_pwd = (String) ois.readObject();
							System.out.println("USER_ID : " + user_id);
							System.out.println("USER_PWD : " + user_pwd);

							Login = dao.Login(user_id);
							if (Login.size() != 0 && Login.get(0).getPWD().equals(user_pwd)) {
								pass = "true";
								System.out.println(timeDate.format(today) + "로그인 성공");
								logintest();
//							toDotest();
							} else {
								pass = "false";
								System.out.println(timeDate.format(today) + "로그인 실패");
							}

							while (true) {
								String second = (String) ois.readObject();
								String[] seperateChatbySet = second.split("=0$0=");
								System.out.println(second);
								
								if (seperateChatbySet[0].equals("[setprofile]")) {
									System.out.println("프로필 변경 요청이 넘어왔다.");
									String pwd = (String) ois.readObject();
									String phone = (String) ois.readObject();
									String email = (String) ois.readObject();
									System.out.println("프로필 변경 부분 다 읽었당");
									setProfile = dao.setProfile(user_id, pwd, phone, email);
									oos.writeObject(setProfile.get(0).getPWD());
									oos.writeObject(setProfile.get(0).getPhone());
									oos.writeObject(setProfile.get(0).getEmail());
									System.out.println("-------------------------------------------------------");
									System.out.println("변경된 사용자 패스워드 : " + setProfile.get(0).getPWD());
									System.out.println("변경된 사용자 핸드폰번호 : " + setProfile.get(0).getPhone());
									System.out.println("변경된 사용자 이메일 : " + setProfile.get(0).getEmail());
									System.out.println("-------------------------------------------------------");
								} else if (seperateChatbySet[0].equals("[chat]")) {
									while (true) {
										today = new Date();
										String[] arr = seperateChatbySet[1].split(":");
										String sendName = arr[0];
										String uid = sendName;
										clientOutputStreams.put(uid, oos);
										System.out.println(uid + " sent: " + seperateChatbySet[1]);
										String message = arr[1];
										String recipient = arr[2];
										System.out.println("메세지 받앗냐?");
										Profile = dao.user_Profile(sendName);
										String name = Profile.get(0).getName();
										System.out.println(timeDate.format(today) + "in / 발신자 : " + sendName + "이름 : "
												+ name + "메세지 :" + message + " 수신자 :" + recipient);

										// 클라이언트 간 메시지 중계
										for (String client : clientOutputStreams.keySet()) {
											if (!client.equals(sendName)) { // 발신자에게는 메시지를 보내지 않도록 변경
												ObjectOutputStream recipientOut = clientOutputStreams.get(client);
												if (recipientOut != null) {
													// 메시지를 모든 클라이언트에게 보냅니다.
													recipientOut
															.writeObject(sendName + ":" + message + ":" + recipient);
													recipientOut.flush();
													System.out.println(timeDate.format(today) + "out / 발신자 : " + name
															+ " 메세지 : " + message + " 수신자 :" + recipient);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			catch (Exception e) {
				e.printStackTrace();
				today = new Date();
				list.remove(this);
				TextArea.append("[ERROR] [" + timeDate.format(today) + "] [" + socket.getInetAddress() + "] IP 주소의 "
						+ user_name + "님께서 비정상 종료하셨습니다.\n");
				TextField.setText("남은 사용자 수 : " + list.size());
				System.out.println("[Error] [" + timeDate.format(today) + "] [" + user_name + " 비정상 종료]");
				System.out.println(
						"==========================================================================================");

				// 변경: 클라이언트 연결 종료 시 clientOutputStreams에서 해당 클라이언트 제거
				clientOutputStreams.remove(user_id);
				System.out.println(user_id + " disconnected");
			}

			finally {
				today = new Date();
				list.remove(this);
				TextArea.append("[" + timeDate.format(today) + "] [" + socket.getInetAddress() + "] IP 주소의 " + user_name
						+ "님께서 종료하셨습니다.\n");
				TextField.setText("남은 사용자 수 : " + list.size());
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("[" + timeDate.format(today) + "] [" + user_name + " 연결종료]");
		}

		public void logintest() {
			try {
				oos.writeObject(pass);
				System.out.println("로그인 성공 여부 :" + pass);
				System.out.println("-------------------------------------------------------");

				Profile = dao.user_Profile(user_id);
				user_name = Profile.get(0).getName();
				String name = Profile.get(0).getName();
				String email = Profile.get(0).getEmail();
				String phone = Profile.get(0).getPhone();
				String dept_num = Profile.get(0).getDept_num();
				System.out.println(name);
				oos.writeObject(name);
				oos.writeObject(email);
				oos.writeObject(phone);
				oos.writeObject(dept_num);

				System.out.println("사용자 정보 넘어가나?");
			} catch (Exception e) {
				System.out.println("로그인 실패");
			}
		}

		public void toDotest() {
			try {
				oos.writeObject(pass);
				System.out.println("TODOTEST");

				toDoList = dao.toDoList(user_id);
				String cn = toDoList.get(0).getID();
				String doing = toDoList.get(0).getDoing();
				String done = toDoList.get(0).getDone();
				int todoIndex = toDoList.get(0).toDoIndex();

				oos.writeObject(cn);
				oos.writeObject(doing);
				oos.writeObject(done);
				oos.writeInt(todoIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void refreshAllClients() {
		for (MultiServerThread clientThread : list) {
			clientThread.sendRefreshSignal();
		}
	}
}