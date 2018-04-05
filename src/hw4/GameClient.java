package hw4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClient extends Thread{
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	Socket socket = null;
	public GameClient() {
		connectServer();
	}
	
	private void connectServer() {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		try {
			while(true) {
				System.out.println("Please enter an IP address: ");
				String hostname = in.nextLine();
				System.out.println();
				System.out.println("Please enter a port:");
				int port = in.nextInt();
				System.out.println();
				while(port < 1024) {
					System.out.println("Invalid port!");
					System.out.println("Please enter a port:");
					port = in.nextInt();
					System.out.println();
				}
				//System.out.println("Trying to connect to " + hostname + ":" + port);
				socket = new Socket(hostname, port);
				//System.out.println("Connected to " + hostname + ":" + port);
				this.printWriter = new PrintWriter(socket.getOutputStream());
				this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.start();
				while(true) {
					String line = in.nextLine();
					printWriter.println(line);
					printWriter.flush();
				}
			}
		} catch (IOException ioe) {
			//System.out.println("ioe in GameClient constructor: " + ioe);
			System.out.println("Unable to connect!");
			connectServer();
		}
	}
	
	public void run() {
		try {
			while(true) {
				String message = bufferedReader.readLine();
				System.out.println(message);
			}
		} catch (IOException ioe) {
			System.out.println("ioe in GameClient run(): " + ioe.getMessage());
		}
	}
	
	public static void main(String [] args) {
		new GameClient();
	}
}
