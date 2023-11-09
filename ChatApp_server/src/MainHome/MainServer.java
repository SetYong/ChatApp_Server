package MainHome;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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

//	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

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
			ServerSocket serverSocket = new ServerSocket(5010);
			sop("서버 실행");
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
//		executorService.scheduleAtFixedRate(() -> refreshAllClients(), 0, 10, TimeUnit.SECONDS);
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
		private ArrayList<Server_VO> nameTree;
		private ArrayList<Server_VO> deptList;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private Date today;
		String user_id = null, user_pwd = null, user_name = null;
		private SimpleDateFormat timeDate = new SimpleDateFormat("HH:mm:ss a");
//		private boolean refreshRequested = false;

//		public void sendRefreshSignal() {
//			refreshRequested = true;
//		}

		@Override
		public void run() {
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());

				while (true) {
					String first = (String) ois.readObject();
					sop("first : " + first);
					String[] seperate = first.split("=0$0=");

					if (seperate[0].equals("[reset]")) {
						sop("리셋 진행합니다.");
						String id = (String) ois.readObject();
						String name = (String) ois.readObject();
						sop("초기화 요청 ID : " + id);
						sop("초기화 요청 NAME : " + name);

						pwdup = dao.pwdUp(id, name);
						oos.writeObject(pwdup.get(0).getID());
						oos.writeObject(pwdup.get(0).getPWD());

						sop("-------------------------------------------------------");
						sop("초기화 한 사원 사번 : " + pwdup.get(0).getID());
						sop("초기화 한 사원 이름 : " + pwdup.get(0).getPWD());
						sop("-------------------------------------------------------");
					}

					else if (seperate[0].equals("[SignUpDept]")) {
						sop("signupdept 요청");
						deptList = dao.deptList();
						oos.writeObject(deptList.size());
						String deptName = "";

						for (int i = 0; i < deptList.size(); i++) {
							Server_VO data = (Server_VO) deptList.get(i);
							deptName = data.getName();
							sop(deptName);

							oos.writeObject(deptName);
						}
						sop((String) ois.readObject());
						if (true) {
							sop("newUser 요청 새로운 노예는환영이야");
							String newName = (String) ois.readObject();
							String newCn = (String) ois.readObject();
							String newDept = (String) ois.readObject();
							sop("newName : " + newName + " newCn : " + newCn + " newDept : " + newDept);
							dao.newUser(newName, newCn, newDept);
							sop("사원 계정 생성 완료");
						}
						sop("Signupdept 끝!");
					}

					else if (seperate[0].equals("[login]")) {
						sop("로그인 진행합니다.");
						while (true) {
//							if (refreshRequested) {
//								// 새로고침 동작 또는 메시지 교환
//								oos.writeObject("Refreshed");
//								oos.flush();
//								refreshRequested = false;
//							}
							sop("정보 읽기 ");
							today = new Date();
							user_id = (String) ois.readObject();
							user_pwd = (String) ois.readObject();
							sop("USER_ID : " + user_id);
							sop("USER_PWD : " + user_pwd);

							Login = dao.Login(user_id);
							if (Login.size() != 0 && Login.get(0).getPWD().equals(user_pwd)) {
								pass = "true";
								sop(timeDate.format(today) + " 로그인 성공");
								logintest();
								sop("성공");
								// toDotest();
							} else {
								pass = "false";
								sop(timeDate.format(today) + " 로그인 실패");
								oos.writeObject(pass);
								sop("실패");
								break;
							}

							while (true) {
								String second = (String) ois.readObject();
//								String[] seperateChatbySet = second.split("=0$0=");
								sop("Second : " + second);
//								sop(seperateChatbySet[0]);

								if (second.equals("[setprofile]")) {
									oos.writeObject("[setprofile]");
									sop("프로필 변경 요청이 넘어왔다.");
									String pwd = (String) ois.readObject();
									String phone = (String) ois.readObject();
									String email = (String) ois.readObject();
									sop("프로필 변경 부분 다 읽었당");
									setProfile = dao.setProfile(user_id, pwd, phone, email);
									oos.writeObject(setProfile.get(0).getPWD());
									oos.writeObject(setProfile.get(0).getPhone());
									oos.writeObject(setProfile.get(0).getEmail());
									sop("-------------------------------------------------------");
									sop("변경된 사용자 패스워드 : " + setProfile.get(0).getPWD());
									sop("변경된 사용자 핸드폰번호 : " + setProfile.get(0).getPhone());
									sop("변경된 사용자 이메일 : " + setProfile.get(0).getEmail());
									sop("-------------------------------------------------------");
								} else if (second.equals("[Todo]")) {
									sop("Todo 요청");
									oos.writeObject("[Todo]");

									String userID = (String) ois.readObject();
									String doing = (String) ois.readObject();
									String state = (String) ois.readObject();

									toDoList = dao.toDoList(userID, doing, state);

								} else if (second.equals("[nameTree]")) {
									sop("nameTree 요청이 들어왔다");
									oos.writeObject("[nameTree]");
									sop("nameTree보냈따");
									String inpName = (String) ois.readObject();
									sop(inpName);
									nameTree = dao.nameTree(inpName);
									oos.writeObject(nameTree.size());

									String listname = "";
									for (int i = 0; i < nameTree.size(); i++) {
										Server_VO data = (Server_VO) nameTree.get(i);
										listname = data.getName();
										sop(listname);

										oos.writeObject(listname);
									}
									sop("nametree 끝낫을지도?");
								} else if (second.equals("[chat]")) {
//									while (true) {
									today = new Date();
									String inp = (String) ois.readObject();
									sop("inp : " + inp);
									String[] arr = inp.split(":");
									sop("arr[1] : " + arr[1]);
									String sendName = arr[0];
									String uid = sendName;
									clientOutputStreams.put(uid, oos);
									String message = arr[1];
									String recipient = arr[2];
									sop("메세지 받앗냐?");
									Profile = dao.user_Profile(sendName);
									String name = Profile.get(0).getName();
									sop(timeDate.format(today) + "in / 발신자 : " + sendName + "이름 : " + name + "메세지 :"
											+ message + " 수신자 :" + recipient);

									// 클라이언트 간 메시지 중계
									for (String client : clientOutputStreams.keySet()) {
										if (!client.equals(sendName)) { // 발신자에게는 메시지를 보내지 않도록 변경
											ObjectOutputStream recipientOut = clientOutputStreams.get(client);
											if (recipientOut != null) {
												// 메시지를 모든 클라이언트에게 보냅니다.
												recipientOut.writeObject("[chat]");
												recipientOut.writeObject(sendName + ":" + message + ":" + recipient);
												recipientOut.flush();
												sop(timeDate.format(today) + "out / 발신자 : " + name + " 메세지 : " + message
														+ " 수신자 :" + recipient);
//												}
											}
										}
									}
								}
							}
						}
						System.out.println("aa");
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
				sop("[Error] [" + timeDate.format(today) + "] [" + user_name + " 비정상 종료]");
				sop("==========================================================================================");

				// 변경: 클라이언트 연결 종료 시 clientOutputStreams에서 해당 클라이언트 제거
				clientOutputStreams.remove(user_id);
				sop(user_id + " disconnected");
			}

//			finally {
//				today = new Date();
//				list.remove(this);
//				TextArea.append("[" + timeDate.format(today) + "] [" + socket.getInetAddress() + "] IP 주소의 " + user_name
//						+ "님께서 종료하셨습니다.\n");
//				TextField.setText("남은 사용자 수 : " + list.size());
//				try {
//					socket.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			sop("[" + timeDate.format(today) + "] [" + user_name + " 연결종료]");
		}

		public void imagesend() {

		}

		public void logintest() {
			try {
				oos.writeObject(pass);
				sop("로그인 성공 여부 :" + pass);
				sop("-------------------------------------------------------");

				Profile = dao.user_Profile(user_id);
				user_name = Profile.get(0).getName();
				String name = Profile.get(0).getName();
				String email = Profile.get(0).getEmail();
				String phone = Profile.get(0).getPhone();
				String dept_num = Profile.get(0).getDept_num();
				String userImage = Profile.get(0).getImage();
				System.out.println(userImage);
				


				
				byte[] byteImage;
				File imageFile = new File(userImage);
				BufferedImage buffImage = ImageIO.read(imageFile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(buffImage, "png", baos);
				baos.flush();
				byteImage = baos.toByteArray();
				String reTodo = dao.receivetoDoList(user_id);
				sop(name);
				oos.writeObject(name);
				oos.writeObject(email);
				oos.writeObject(phone);
				oos.writeObject(dept_num);
				oos.writeObject(reTodo);
				oos.writeObject(byteImage);
				sop("사용자 정보 넘어가나?");
			} catch (Exception e) {
				e.printStackTrace();
				sop("로그인 실패");
			}
		}
	}

//	private void refreshAllClients() {
//		for (MultiServerThread clientThread : list) {
//			clientThread.sendRefreshSignal();
//		}
//	}

	public void sop(String text) {
		System.out.println(text);
	}
}