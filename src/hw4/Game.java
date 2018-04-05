package hw4;

import java.util.Random;
import java.util.Vector;

public class Game extends Thread{
	private String gameName;
	private boolean isDoublePlayer;
	private boolean isFull;
	private boolean isStart;
	private boolean isEnd = false;
	private boolean isPlayer1Win = false;
	private Vector<Brawler> player1Brawlers = new Vector<Brawler>();
	private Vector<Brawler> player2Brawlers = new Vector<Brawler>();
	private int player1CurrBrawler = 0;
	private int player2CurrBrawler = 0;
	private int player1CurrMove = 0;
	private int player2CurrMove = 0;
	private Random random = new Random();
	
	private ServerThread player1;
	private ServerThread player2;
	private String[] attributes = {"water", "fire", "air", "earth", "lightning"};
	
	public Game(String gameName, boolean isDoublePlayer, ServerThread player1) {
		this.gameName = gameName;
		this.isDoublePlayer = isDoublePlayer;
		this.player1 = player1;
		if(isDoublePlayer) { // two players to wait for another
			isFull = false;
			isStart = false;
		}else { // one player to play with an AI
			isFull = true;
			player2 = null;
			player1.sendMessage("Starting game with an AI...");
			this.start();
		}
	}
	
	public void addPlayer(ServerThread player2) {
		if(this.player2 != null) {
			return;
		}
		this.player2 = player2;
		//isStart = true;
		isFull = true;
		player1.sendMessage("Player 2 connected!");
		broadcast("Starting game...");
		this.start();
	}
	
	public void run() {
		readInBrawlers();
		broadcast("Excellent!");
		sendBrawlers();
		while(!isEnd) {
			battle();
		}
		if(isPlayer1Win) {
			player1.sendMessage("Your opponent is out of brawlers!");
			player1.sendMessage("");
			player1.sendMessage("You Win!");
			sendMessageP2("Your are out of brawlers!");
			sendMessageP2("");
			sendMessageP2("You Lose!");
		}else {
			sendMessageP2("Your opponent is out of brawlers!");
			sendMessageP2("");
			sendMessageP2("You Win!");
			player1.sendMessage("Your are out of brawlers!");
			player1.sendMessage("");
			player1.sendMessage("You Lose!");
		}
	}
	
	private void readInBrawlers() {
		new GameListenThread(this, player1, 1, 0);
		new GameListenThread(this, player2, 2, 0);
		while(player1Brawlers.size() < 3 || player2Brawlers.size() < 3) {
			Thread.yield();
		}
		/*System.out.println("Player1:");
		for(int i = 0; i < 3; i++) {
			System.out.println(player1Brawlers.get(i).getName());
		}
		System.out.println("Player2:");
		for(int i = 0; i < 3; i++) {
			System.out.println(player2Brawlers.get(i).getName());
		}*/
	}
	
	private void sendBrawlers() {
		player1.sendMessage("You send " + player1Brawlers.get(player1CurrBrawler).getName() + "!");
		sendMessageP2("You send " + player2Brawlers.get(player2CurrBrawler).getName() + "!");
		player1.sendMessage("Your opponent plays " + player2Brawlers.get(player2CurrBrawler).getName() + "!");
		sendMessageP2("Your opponent plays " + player1Brawlers.get(player1CurrBrawler).getName() + "!");
		broadcast("");
	}
	
	private void battle() {
		// get input
		new GameListenThread(this, player1, 1, 1);
		new GameListenThread(this, player2, 2, 1);
		while((player1CurrMove == 0) || (player2CurrMove == 0)) {
			//System.out.println("p1cm: " + player1CurrMove + "\tp2cm: " + player2CurrMove);
			Thread.yield();
		}
		// set up variables for easy access
		Brawler brawler1 = player1Brawlers.get(player1CurrBrawler);
		Brawler brawler2 = player2Brawlers.get(player2CurrBrawler);
		player1CurrMove--;
		player2CurrMove--;
		Ability ability1 = brawler1.getAbilities()[player1CurrMove];
		Ability ability2 = brawler2.getAbilities()[player2CurrMove];
		int speed1 = brawler1.getStats().getSpeed();
		int speed2 = brawler2.getStats().getSpeed();
		
		// reset
		player1CurrMove = 0;
		player2CurrMove = 0;

		// decide turns
		int newHealth = 0;
		int attack = 0;
		int option = 0;
		if(speed1 > speed2) {
			option = 0;
		} else if(speed2 > speed1) {
			option = 1;
		} else {
			option = random.nextInt(2);
		}
		
		// first attack
		switch (option) {
		case 0: // 1 attacks 2
			broadcast(brawler1.getName() + " used " + ability1.getName() + "!");
			attack = calcAttack(brawler1, ability1, brawler2);
			newHealth = brawler2.getStats().getHealth() - attack;
			broadcast("It did " + Math.min(attack, brawler2.getStats().getHealth()) + " damage!");
			brawler2.getStats().setHealth(newHealth);
			if(newHealth <= 0) {
				announceDeath(2);
				return;
			}
			break;
		case 1: // 2 attacks 1
			broadcast(brawler2.getName() + " used " + ability2.getName() + "!");
			attack = calcAttack(brawler2, ability2, brawler1);
			newHealth = brawler1.getStats().getHealth() - attack;
			broadcast("It did " + Math.min(attack, brawler1.getStats().getHealth()) + " damage!");
			brawler1.getStats().setHealth(newHealth);
			if(newHealth <= 0) {
				announceDeath(1);
				return;
			}
			break;
		default:
			break;
		}
		
		// second attack
		switch (option) {
		case 0: // 2 attacks 1 back
			broadcast(brawler2.getName() + " used " + ability2.getName() + "!");
			attack = calcAttack(brawler2, ability2, brawler1);
			newHealth = brawler1.getStats().getHealth() - attack;
			broadcast("It did " + Math.min(attack, brawler1.getStats().getHealth()) + " damage!");
			brawler1.getStats().setHealth(newHealth);
			if(newHealth <= 0) {
				announceDeath(1);
				return;
			}
			break;
		case 1: // 1 attacks 2 back
			broadcast(brawler1.getName() + " used " + ability1.getName() + "!");
			attack = calcAttack(brawler1, ability1, brawler2);
			newHealth = brawler2.getStats().getHealth() - attack;
			broadcast("It did " + Math.min(attack, brawler2.getStats().getHealth()) + " damage!");
			brawler2.getStats().setHealth(newHealth);
			if(newHealth <= 0) {
				announceDeath(2);
				return;
			}
		default:
			break;
		}
		
		// normal battle, announce health
		broadcast("");
		player1.sendMessage(brawler1.getName() + " has " + brawler1.getStats().getHealth() + " health.");
		sendMessageP2(brawler2.getName() + " has " + brawler2.getStats().getHealth() + " health.");
	}
	
	private void announceDeath(int playerNum) {
		broadcast("");
		Brawler brawler1 = player1Brawlers.get(player1CurrBrawler);
		Brawler brawler2 = player2Brawlers.get(player2CurrBrawler);
		if(playerNum == 1) {
			sendMessageP2(brawler2.getName() + " has " + brawler2.getStats().getHealth() + " health!");
			sendMessageP2("");
			broadcast(brawler1.getName() + " was defeated!");
			player1CurrBrawler++;
			if(player1CurrBrawler > 2) {
				isPlayer1Win = false;
				isEnd = true;
			} else {
				brawler1 = player1Brawlers.get(player1CurrBrawler);
				player1.sendMessage("You sent out " + brawler1.getName() + "!");
				sendMessageP2("Your opponent sent out " + brawler1.getName() + "!");
			}
		} else {
			player1.sendMessage(brawler1.getName() + " has " + brawler1.getStats().getHealth() + " health!");
			player1.sendMessage("");
			broadcast(brawler2.getName() + " was defeated!");
			player2CurrBrawler++;
			if(player2CurrBrawler > 2) {
				isPlayer1Win = true;
				isEnd = true;
			} else {
				brawler2 = player2Brawlers.get(player2CurrBrawler);
				player1.sendMessage("Your opponent sent out " + brawler2.getName() + "!");
				sendMessageP2("You sent out " + brawler2.getName() + "!");
			}
		}
		broadcast("");
	}
	
	private int calcAttack(Brawler brawler1, Ability ability, Brawler brawler2) {
		double temp = ((brawler1.getStats().getAttack()*(ability.getDamage()/(double)brawler2.getStats().getDefense()))/5);
		int effect = checkEffective(ability.getType(), brawler2.getType());
		if(effect == 0) {
			temp *= 2;
		} else if(effect == 1) {
			temp /= 2;
		}
		int retVal = (int) Math.floor(temp);
		return retVal;
	}
	
	
	public Vector<Brawler> getBrawlers(int playerNum) {
		if(playerNum == 1) {
			return player1Brawlers;
		} else {
			return player2Brawlers;
		}
	}
	public Brawler getCurrBrawler(int playerNum) {
		if(playerNum == 1) {
			return player1Brawlers.get(player1CurrBrawler);
		} else {
			return player2Brawlers.get(player2CurrBrawler);
		}
	}
	public void setCurrMove(int playerNum, int move) {
		if(playerNum == 1) {
			player1CurrMove = move;
		}else {
			player2CurrMove = move;
		}
	}
	private int checkEffective(String attribute1, String attribute2) { // remeber to divide by 
		int num1 = 0, num2 = 0;
		for(int i = 0; i < 5; i++) {
			if(attribute1.equals(attributes[i])) {
				num1 = i;
			}
			if(attribute2.equals(attributes[i])) {
				num2 = i;
			}
		}
		if((num1 - num2) == 1 || (num2 - num1) == 4) {
			broadcast("It was not very effective!");
			return 1;
		}else if((num2 - num1 == 1) || (num1 - num2) == 4) {
			broadcast("It was super effective!");
			return 0;
		}else {
			return 2;
		}
	}

	public void broadcast(String message) {
		player1.sendMessage(message);
		sendMessageP2(message);
	}
	public void sendMessageP2(String message) {
		if(player2 != null) {
			player2.sendMessage(message);
		}
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public boolean isDoublePlayer() {
		return isDoublePlayer;
	}
	public void setDoublePlayer(boolean isDoublePlayer) {
		this.isDoublePlayer = isDoublePlayer;
	}
	public boolean isFull() {
		return isFull;
	}
	public void setFull(boolean isFull) {
		this.isFull = isFull;
	}
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	public Random getRandom() {
		return random;
	}
}
