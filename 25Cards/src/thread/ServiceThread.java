package thread;
 
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import model.Poker;
import model.User;
 
public class ServiceThread implements Runnable {
 
	// 定义当前线程处理的Socket
	Socket socket = null;
	// 该线程所处理的Socket所对应的输入输出流
	DataInputStream is = null;
	DataOutputStream os = null;
	public String myDiscard="123";
	public String username = null,password = null,nickname = null;
	public int seatnum = -1;
	private boolean isReady = false; //游戏准备标志
	public String isCall = "abc";
	public boolean win = false;
	public ServiceThread(Socket socket) {
		this.socket = socket;
		try {
			is = new DataInputStream(socket.getInputStream());
			os = new DataOutputStream(socket.getOutputStream());		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 

	@Override
	public void run() {
			String s= "";
			while(true) {
				try {
					s = is.readUTF();
					User user = null;
					//玩家点击游戏
					if(s.startsWith("enter")) {
						username = is.readUTF();
						password=  is.readUTF();
						nickname = is.readUTF();
						user = new User(username, password, nickname);
						seatnum = Service.users.size();
						Service.users.add(user);
						Service.playerList.add(this);
					}
					
					//向客户端发送已经存在的玩家
					if(s.startsWith("ready")) {
						synchronized(this){
							Service.count++;
						}
					}
					
					//离开
					if(s.startsWith("leave")) {
						synchronized(this){
							Service.playerList.remove(this);
							Service.users.remove(user);
							Service.count--;
						}
					}
					
					//发牌完毕
					if(s.startsWith("cardSendEnd")) {
						Service.cardSend = true;
					}
					
					//叫地主
					if(s.startsWith("call")) {
						isCall="call";
					}
					//不叫地主
					if(s.startsWith("noCall")) {
						isCall ="noCall";
					}
					//不出
					if(s.startsWith("pass")) {
						System.out.println("pass pass");
						myDiscard = "pass";
						Service.isDiscard = true;
					}
					//出牌
					if(s.startsWith("discard")) {
						String p = is.readUTF();
						Service.discard = p;
						myDiscard = p;
						System.out.println("Thread myDiscard: "+myDiscard);
						Service.isDiscard = true;
					}
					
					if(s.startsWith("win")) {
						Service.gameEnd = true;
						win = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		}
 
	}
 
 
	
}