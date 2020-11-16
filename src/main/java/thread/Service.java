package thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.Poker;
import model.User;

public class Service extends Thread{
	private static final long serialVersionUID = 1L;
	public static List<Socket> socketList = new ArrayList<Socket>();
	public static List<ServiceThread> playerList = new ArrayList<ServiceThread>();
	public static List<User> users = new ArrayList<User>();
	public static ArrayList<Poker> pokers = new ArrayList<Poker>();
	public static int count = 0; //已准备的玩家人数
	private ServerSocket server = null;
	public static int weight = 0;
	public static int random_index;
	public static int boss = -1;
	public boolean isCallBoss = false;
	public boolean isDealCards = false;
	public int noCallNum = -1;
	public int discardNum = -1;
	public ServiceThread discardService;
	public static boolean gameEnd = false;
	public static boolean cardSend = false;
	public static boolean firstCall = false;
	public static ServiceThread firstCallService;
	public static int winner = -1;
	
	public static boolean isDiscard = false; //是否出牌
	public static String discard =""; //出牌
	
	public int playerNum = 4; //游戏人数
	
	public Service(Service service) {
		try {
			if(null == service) {
				this.server = new ServerSocket(3000);
				System.out.println("socket start");
			}
		} catch (Exception e) {
			 System.out.println("SocketThread创建socket服务出错");  
		}
	}
	
	public void run() {
		while(!this.isInterrupted()) {
			try {
				while(true){
					if(socketList.size() < playerNum) {
						Socket s= server.accept();
						s.setKeepAlive(true);
						socketList.add(s);
						//每当客户端连接之后启动一条ServerThread线程为该客户端服务
						ServiceThread st = new ServiceThread(s);
						new Thread(st).start();
					}
					System.out.print("");
					if(Service.count == playerNum && isDealCards == false) {
						Service.shuffle(pokers);
						for(int i = 0; i < playerNum; i++) {
							ServiceThread temps = playerList.get(i);
							temps.os.writeUTF("start");
							temps.os.writeUTF(String.valueOf(temps.seatnum));
							for(int j = 0; j < playerNum; j++) {
								if(j!=i) {
									ServiceThread tempsb = playerList.get(j);
									temps.os.writeUTF(String.valueOf(tempsb.seatnum));
									temps.os.writeUTF(tempsb.username);
								}
							}
						}
						int j = 0;
						for(int i = 0; i < playerNum; i++) {
							ServiceThread stt = Service.playerList.get(i);
							//st.os.writeUTF(String.valueOf(i+1));
							//st.os.writeUTF(user.getUsername());
							String cards ="";
							for(int k = j; k < j+25; k++) {
								cards = cards.concat(pokers.get(k).toString()+",");
							}
							j+=25;
							stt.os.writeUTF(cards);
						}
						isDealCards = true;
						Random rand = new Random();
						Service. random_index= rand.nextInt(playerNum);
					}
					
					if(Service.count == playerNum && isCallBoss == false && cardSend) {
						if(!firstCall) {
							firstCallService = Service.playerList.get(Service.random_index);
							firstCallService.os.writeUTF("call");
							firstCall = true;
						}
						String str = null;
						if(firstCallService.isCall.equals("call")) {
							weight++;
							noCallNum = 0;
							boss = Service.random_index;
						}else if(firstCallService.isCall.equals("noCall")) {
							if(noCallNum!=-1) {
								noCallNum++;
							}
						}else {
							continue;
						}
						if( weight == 3 || noCallNum == 3 ) {
							for(int k = 0; k < playerNum; k++) {
								ServiceThread temps = playerList.get(k);
								temps.os.writeUTF("boss:");
								temps.os.writeUTF(String.valueOf(boss));
							}
							String card = "";
							for( int k = 100; k < 108; k++) {
								card = card.concat(Service.pokers.get(k).toString()+",");
							}
							playerList.get(boss).os.writeUTF(card);
							isCallBoss = true;
							discardNum = boss;
							playerList.get(boss).os.writeUTF("newDiscard");
							break;
						}else if(!firstCallService.isCall.equals("abc")){
							firstCallService.isCall = "abc";
							random_index =(random_index+1)%playerNum;
							ServiceThread next = Service.playerList.get(Service.random_index);
							firstCallService = next;
							firstCall = false;
						}
					}
					
					if(!gameEnd && isCallBoss) {
						if(isDiscard) {
							System.out.println("D"+discardNum);
							if(playerList.get(discardNum).myDiscard.equals("pass")) {
								
								discardNum = (discardNum+1)%playerNum;
								System.out.println();
								for(int j = 0; j < playerNum; j++) {
									System.out.println("myDiscard:  "+String.valueOf(playerList.get(j).seatnum)+"    "+playerList.get(j).myDiscard);
								}
								if(discard.equals(playerList.get(discardNum).myDiscard)){
									System.out.println("newDiscard");
									playerList.get(discardNum).os.writeUTF("newDiscard");
									for(int i = 0; i < playerNum; i++) {
										playerList.get(i).myDiscard="";
										discard = "";
									}
									for(int i = 0; i < playerNum; i++) {
										if(i!=discardNum) {
											playerList.get(i).os.writeUTF("lastDiscard");
											playerList.get(i).os.writeUTF("clear");
										}
									}
								}else {
									playerList.get(discardNum).os.writeUTF("discard");
									playerList.get(discardNum).os.writeUTF(discard);
								}
							}else {
								System.out.println("discard:  "+playerList.get(discardNum).myDiscard);
								discardNum = (discardNum+1)%playerNum;
								playerList.get(discardNum).os.writeUTF("discard");
								playerList.get(discardNum).os.writeUTF(discard);
								for(int j = 0; j < playerNum; j++) {
									if(playerList.get(j).seatnum != discardNum) {
										playerList.get(j).os.writeUTF("lastDiscard");
										playerList.get(j).os.writeUTF(discard);
									}
								}
							}
							isDiscard = false;
						}
					}
					if(gameEnd) {
						for(int j = 0; j < playerNum; j++) {
							if(playerList.get(j).win == true) {
								winner = j;
								break;
							}
						}
						for(int j = 0; j < playerNum; j++) {
							if(j != winner) {
								playerList.get(j).os.writeUTF("winner");
								playerList.get(j).os.writeUTF(String.valueOf(winner));
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	//洗牌
	public static void shuffle(ArrayList<Poker> pokers) {
		for(int i = 3; i <= 16; i++) {
			if(i == 15) continue;
			for(int j = 0; j < 4; j++) {
				pokers.add(new Poker(i,Poker.kinds[j]));
				pokers.add(new Poker(i,Poker.kinds[j]));
			}
		}
		pokers.add(new Poker(100,"小王"));
		pokers.add(new Poker(100,"小王"));
		pokers.add(new Poker(200,"大王"));
		pokers.add(new Poker(200,"大王"));
		Collections.shuffle(pokers);
		for(int i = 0; i<50; i++) {
			System.out.println(pokers.get(i).toString());
		}
	}
	   public void closeSocketServer(){  
	       try {  
	            if(null!=server && !server.isClosed())  
	            {  
	             server.close();  
	            }  
	       } catch (IOException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	       }  
	     }  
}
