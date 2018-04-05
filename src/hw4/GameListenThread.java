package hw4;

public class GameListenThread extends Thread{
	private Game game;
	private ServerThread serverThread;
	private int playerNum;
	private int action;
	public GameListenThread(Game game, ServerThread serverThread, int playerNum, int action) {
		this.game = game;
		this.playerNum = playerNum;
		this.serverThread = serverThread;
		this.action = action;
		this.start();
	}
	
	public void run() {
		switch (action) {
		case 0:
			checkSelectBrawlers();
			break;
		case 1:
			getMove();
		default:
			break;
		}
	}
	
	private void checkSelectBrawlers() {
		int maxNum = GameServer.getBrawlers().size();
		int[] brawlerNum = new int[3];
		if(serverThread == null) { // AI player
			for(int i = 0; i < 3; ++i) {
				brawlerNum[i] = game.getRandom().nextInt(maxNum)+1;
			}
		} else {
			// ask for brawler input
			String message = "";
			String[] parts;
			while(true) {
				printBrawlers();
				message = serverThread.readLine();
				serverThread.sendMessage("");
				parts = message.split(",");
				if(parts.length > 3 || parts.length < 3) {
					serverThread.sendMessage("You must select 3 brawlers!");
				}else {
					boolean isValid = true;
					for(int i = 0; i < 3; i++) {
						brawlerNum[i] = Integer.parseInt(parts[i]);
						if(brawlerNum[i] < 1 || brawlerNum[i] > maxNum) {
							serverThread.sendMessage("Invalid!");
							isValid = false;
							break;
						}
					}
					if(!isValid) {
						continue;
					}
					break;
				}
			}
		}
		
		// add brawlers to the game player
		for(int i = 0; i < 3; i++) {
			Brawler tempBrawler = (Brawler) Util.deepClone(GameServer.getBrawlers().get(brawlerNum[i]-1));
			game.getBrawlers(playerNum).add(tempBrawler);
		}
	}
	
	private void printBrawlers() {
		serverThread.sendMessage("Choose 3 Brawlers:");
		int brawlerNum = GameServer.getBrawlers().size();
		for(int i = 0; i < brawlerNum; ++i) {
			String message = "" + (i+1) + ") " + GameServer.getBrawlers().get(i).getName();
			serverThread.sendMessage(message);
		}
	}
	
	private void getMove() {
		int move = 0;
		if (serverThread == null) { // if AI player
			game.setCurrMove(playerNum, game.getRandom().nextInt(2)+1);
			return;
		}
		// if human player
		while(true) {
			serverThread.sendMessage("Choose a move:");
			for(int i = 0; i < 2; ++i) {
				Ability ability = game.getCurrBrawler(playerNum).getAbilities()[i];
				String tempStr = "" + (i+1) + ") " + ability.getName() + ", " + ability.getType() + ", " + ability.getDamage();
				serverThread.sendMessage(tempStr);
			}
			String message = serverThread.readLine();
			serverThread.sendMessage("");
			move = Integer.parseInt(message);
			if(move == 1 || move == 2) {
				break;
			}
			serverThread.sendMessage("Wrong Number Entered!");
		}
		game.setCurrMove(playerNum, move);
	}
}
