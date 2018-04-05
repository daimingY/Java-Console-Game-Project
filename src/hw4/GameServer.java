package hw4;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import com.google.gson.Gson;

public class GameServer {
	private static List<Brawler> brawlers = new ArrayList<>();
	private Vector<Game> games = new Vector<Game>();  
	
	private Vector<ServerThread> serverThreads;
	public GameServer(int port) {
		ServerSocket serverSocket = null;
		try {
			//System.out.println("Trying to bind to port " + port);
			serverSocket = new ServerSocket(port);
			//System.out.println("Bound to port " + port);
			System.out.println("Success!");
			serverThreads = new Vector<ServerThread>();
			while(true) {
				//System.out.println("Waiting for connection...");
				Socket socket = serverSocket.accept();
				//System.out.println("Connection from " + socket.getInetAddress());
				ServerThread serverThread = new ServerThread(socket, this);
				serverThreads.add(serverThread);
			}
		} catch (IOException ioe) {
			System.out.println("ioe in GameServer constructor: " + ioe.getMessage());
		} finally {
			try {
				if(serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException ioe) {
				System.out.println("ioe in construct GameServer: " + ioe.getMessage());
			}
		}
	}
	
	synchronized public void joinGame(Game game, ServerThread player2) {
		game.addPlayer(player2);
	}
	
	public Game createGame(String gameName, boolean isDoublePlayer, ServerThread player1) {
		gameName = Util.toLower(gameName);
		Game retVal = new Game(gameName, isDoublePlayer, player1);
		games.add(retVal);
		return retVal;
	}
	
	synchronized public Game checkGameName(String gameName) {
		gameName = Util.toLower(gameName);
		for(int i = 0; i < games.size(); ++i) {
			if(gameName.equals(games.get(i).getGameName())) {
				return games.get(i);
			}
		}
		return null;
	}
	
	public void removeServerThread(ServerThread serverThread) {
		serverThreads.remove(serverThread);
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);

		// read in file for brawlers
		try {
			String filename = "";
			Gson gson = new Gson();
			System.out.println("Please enter a valid file:");
			filename = in.nextLine();
			Reader reader = new InputStreamReader(GameServer.class.getResourceAsStream(filename));
			BrawlerList brawlerList = gson.fromJson(reader, BrawlerList.class);
			reader.close();
			
			for(int i = 0; i < brawlerList.Brawlers.length; ++i) {
				brawlers.add(brawlerList.Brawlers[i]);
			}
		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
		}
		
		// read in and bound port number
		while(true) {
			long portNum = 0;
			System.out.println("Please enter a valid port:");
			portNum = in.nextInt();
			if(portNum < 1024 || portNum > 32767) {
				System.out.println("Invalid Port!");
			}else {
				int port = (int) portNum;
				new GameServer(port);
				break;
			}
		}
	}
	public static List<Brawler> getBrawlers() {
		return brawlers;
	}
}
