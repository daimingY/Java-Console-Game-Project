# CS 201 Java Console Game Project

- **Name**: Daiming Yang
- **Email**: daimingy@usc.edu

# Game Rules
- Each brawler has stats including health, speed, attack, defense, and type, as well as abilities.
- Each ability has stats including type and damage.
- Types include water, fire, air, earth, and lightning. 
- Abilities are super-powerful if against a brawler of a following type in the order, or under-powerful if against a brawler of a precedent type in the order.

# Game Logic
- The server must start to receive requests from users.
- Each user needs to start a client to connect to server, and start either a one-player or a double-player game. Possible game exceptions were handled.
- Users may choose three brawlers from the brawler pools to either compete with an AI player or another human player connected in the same game.
- Users send out brawlers in the order of choices, and during each turn use an ability of the current brawler.
- Brawler with higher speed makes the first move.
- If a brawler is killed by the opponent's first move, it can't make its move.
- Dead brawlers are switched by the next brawler in order.
- The game ends when one player is out of brawlers, and that player loses while the opponent player wins.
