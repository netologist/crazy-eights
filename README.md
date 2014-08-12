crazy-eights
============

Crazy Eights Game (Akka Example)


Game Arguments
```bash
$ sbt "run"

Usage: crazy-eights [options]

  --help
        prints this usage text
  -n <player-count> | --new-game <player-count>
        maximum count for <player-count> should be 5
  -j | --join-game
        join game
```


Start New Game
```bash
$ sbt "run --new-game 2"


Notification: Game turn to Player #1

Notification: Player #1 joined
Player #1> 
```

Join Game
```bash
$ sbt "run --join-game"


Notification: Player #2 joined

Notification: All players joined
Player #2> 
```

Game Commands
```bash
Player #1> help

Crazy Eights Commands
---------------------
h, help    help description
1..99      discard card operation (choose task order)
s, status  get game status
d, draw    draw card
x, exit    exit game

```

Game Status
```bash
Player #1> status

Game Stats
----------
DiscardPile: 	 DIAMOND 3
StockPile: 	 39 cards
OtherHandPiles: 
		 Player #1 has 8 cards
YourHandPile: 
		 1 - JOKER
		 2 - JOKER
		 3 - DIAMOND * 8 *
		 4 - SPADE 9
		 5 - HEART QUEEN
		 6 - CLUB 5
		 7 - DIAMOND QUEEN
		 8 - JOKER

```
