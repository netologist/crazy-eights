import java.io.File

object Main extends App {
  val parser = new scopt.OptionParser[Config]("crazy-eights") {
    head("crazy-eights", "0.0.1")
    help("help") text("prints this usage text")
    opt[Int]('n', "new-game") action { (x, c) =>
      c.copy(newGame = true, playerCount = x)
    } validate { x =>
      if (x >= 2 && x <= 5) success else failure("Value <player-count> must be between 2 to 5")
    } keyValueName("<libname>", "<player-count>") text("maximum count for <player-count> should be 5")
    opt[Unit]('j', "join-game") action { (x, c) =>
      c.copy(joinGame = true)
    } text ("join game")
  }

  // parser.parse returns Option[C]
  parser.parse(args, Config()) map { config =>
    if (config.newGame) {
      CrazyEights.startGame(config.playerCount)
    }
    else if (config.joinGame) {
      CrazyEights.joinGame
    }
    else {
      parser.showUsage
    }
  } getOrElse {
  }
}
