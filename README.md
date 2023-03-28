# RiskGame

Implementation of the strategy board game "Risk" in Java, coded for fun. 
The game rules in the internet seem to differ, I used https://www.dienstac.de/adi/spiele/risiko.pdf as a reference.

Basic explanation of the game:

2-6 players can play, two game modes are available: Standard Risk (Mission for everyone: conquer the world) and Mission Risk (each player receives an individual secret mission).
The person whose mission gets solved wins. 

0) In the very beginning of the game, each player receives random territories, each territory holds one unit.

Each player's turn contains the following steps:

1) Receive and distribute units: 
Depending on the number of territories and continents a player possesses he or she automatically receives a certain amount of units. The player has to distribute all of these units to own territories.

2) Fight: 
The player may choose to attack a neighbouring territory of an opponent as often as he or she wants or progress with the next step. 
1-3 units of the attacking country can be used for the attack, at least one unit has to remain in the attacking country. The attacker rolls as many dices as units used for the attack.
The defender can use up to 2 units of the attacked country for the defence and also rolls as many dices as units used for the defence. 
The dice numbers are compared, each dice separately, starting with the highest number. The total number of dice being compared is determined by the smallest number of dice rolled by either the attacker or the defender.. 
Example: 
If the attacker threw three dices (e.g. 6,3,2) and the defender two (e.g. 5,4), the smallest total number of dice rolled is 2, therefore two numbers will be compared: The highest ones (6 from the attacker, 5 from the defender) and the second-highest ones (3 from the attacker and 4 from the defender).
Each time one of the players has a higher dice number the opponent loses one unit from the corresponding territory. In case of a draw, the attacker loses a unit. A territory is conquered if there are no units left in it, then the opponent moves the remaining units in the country. Additionally, the attacker can move once more units from the country he or she attacked from to the newly conquered territory. Note that each territory must always have at least one unit.

3) Move units: 
The player may choose to move units from one owned territory to another owned neighbouring territory or finish his or her turn. When moving units it is important to know that only countries which were not involved in a previous fight in the respective turn can be selected.

--- 
- Territory cards have not been implemented. 

- The game can be saved and reloaded persistently. 

- I must claim that I might have misunderstood the rules, I've actually never played this game in my entire life and only chose to implement it because it was an optional choice (which I did not take at the time) for more advanced people in my programming classes from one year ago and which I now wanted to try to implement. I do not possess any particular fascination for war games. 
