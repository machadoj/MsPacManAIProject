package pacman.controllers.Joy_Machado;

import java.util.ArrayList;
import java.util.List;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GA_Controller extends Controller<MOVE>{

	MOVE highMove;
	int highScore = -1;
	public static StarterGhosts ghosts = new StarterGhosts();
	
	@Override
	public MOVE getMove(Game game, long timeDue)
	{
		MOVE highMove = MOVE.LEFT;
		int highScore = -1;
		Game gameCopy = game.copy();
		System.out.println("Hello GA Controller");
		
		GA_FitnessChecking ga = new GA_FitnessChecking();
		List<ArrayList<MOVE>> parents = ga.prepareInitialPopulation();
		ArrayList<MOVE> best = ga.runExperiment(new GA_Controller(), new StarterGhosts(), 10, parents);
		
		for(MOVE m : best)
		{
			gameCopy.advanceGame(m, ghosts.getMove(gameCopy, timeDue));
			int score = gameCopy.getScore();
			if(highScore < score)
			{
				highScore = score;
				highMove = m;
			}
		}
		
		return highMove;
	}
	
	public MOVE getMove(Game game, long timeDue, ArrayList<MOVE> ip)
	{
		MOVE tempMove = null;
		int tempHighScore = -1;
		Game gameCopy = game.copy();
		Game gameAtM = gameCopy;
		
			for(MOVE m : ip)
			{
				gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
				int tempScore = gameAtM.getScore();
				if(tempHighScore < tempScore)
	            {
					tempHighScore = tempScore;
	                tempMove = m;
	            }
				
			}
		
		return tempMove;
	}

}
