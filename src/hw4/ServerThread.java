package hw4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread{
	private Socket socket;
	private GameServer gameServer;
	private Game game;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	public ServerThread(Socket socket, GameServer gameServer) {
		this.socket = socket;
		this.gameServer = gameServer;
		try {
			this.printWriter = new PrintWriter(socket.getOutputStream());
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread constructor: " + ioe.getMessage());
		}
	}
	
	public void run() {
		try {
			// Start or Join Game
			sendMessage("Please make a choice:");
			sendMessage("1): Start Game");
			sendMessage("2): Join Game");
			String line = bufferedReader.readLine();
			line = bufferedReader.readLine();
			sendMessage("");
			while(!line.equals("1") && !line.equals("2")) {
				sendMessage("Invalid Choice!");
				sendMessage("Please make a choice:");
				sendMessage("1): Start Game");
				sendMessage("2): Join Game");
				line = bufferedReader.readLine();
				sendMessage("");
			}
			if(line.equals("1")) { // if start a game
				// read in name
				sendMessage("What will you name your game?");
				String gameName = bufferedReader.readLine();
				sendMessage("");
				while(gameServer.checkGameName(gameName) != null) {
					sendMessage("This game already exists!");
					sendMessage("What will you name your game?");
					gameName = bufferedReader.readLine();
					sendMessage("");
				}
				// read in number of players
				sendMessage("How many players?");
				sendMessage("1 or 2");
				line = bufferedReader.readLine();
				sendMessage("");
				while(line.equals("1") && line.equals("2")) {
					sendMessage("Invalid number of players!");
					sendMessage("How many players?");
					sendMessage("1 or 2");
					line = bufferedReader.readLine();
					sendMessage("");
				}
				// create the game
				if(line.equals("2")) {
					game = gameServer.createGame(gameName, true, this);
					sendMessage("Waiting for players to connect...");
				} else {
					game = gameServer.createGame(gameName, false, this);
				}
			}else { // if join a game
				// read in name
				sendMessage("What's the name of the game you want to join?");
				String gameName = bufferedReader.readLine();
				sendMessage("");
				while(true) {
					game = gameServer.checkGameName(gameName);
					if(game == null) {
						sendMessage("This game doesn't exists!");
					} else if(game.isFull()) {
						sendMessage("This game is already full!");
					} else {
						gameServer.joinGame(game, this);
						break;
					}
					sendMessage("What's the name of the game you want to join?");
					gameName = bufferedReader.readLine();
					sendMessage("");
				}
			}
			// wait to start game
			while(!game.isStart()) {
				Thread.yield();
			}
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread.run(): " + ioe.getMessage());
		} finally {
			gameServer.removeServerThread(this);
			System.out.println("ServerThread Closing things");
			try {
				printWriter.close();
				bufferedReader.close();
				socket.close();
			} catch (IOException ioe) {
				System.out.println("ioe in closing streams: " + ioe.getMessage());
			}
		}
	}
	
	public String readLine() {
		//sendMessage("reading a line:");
		try {
			return bufferedReader.readLine();
		} catch (IOException ioe) {
			//System.out.println("ioe in ServerThread readLine: " + ioe.getMessage());
		}
		return "Exception during readLine()";
	}
	
	public void sendMessage(String message) {
		printWriter.println(message);
		printWriter.flush();
	}
}
