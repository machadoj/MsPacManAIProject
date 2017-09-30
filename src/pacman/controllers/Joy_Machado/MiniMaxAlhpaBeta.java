package pacman.controllers.Joy_Machado;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;

import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MiniMaxAlhpaBeta extends Controller<MOVE>{
	
	public static StarterGhosts ghosts = new StarterGhosts();

	public MOVE getMove(Game game, long timeDue)
	{
		MOVE[] allMoves;

		MOVE pacmanLastMove = game.getPacmanLastMoveMade();

		int currIndex = game.getPacmanCurrentNodeIndex();

		if (pacmanLastMove != null){

		     allMoves = game.getPossibleMoves(currIndex,pacmanLastMove);

		}else{

		     allMoves = game.getPossibleMoves(currIndex);

		}

		int highScore = -1;
		MOVE highMove = null;

		for (MOVE m : allMoves) {
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			int tempHighScore = this.minimax(new PacManNodeAlphaBeta(gameAtM), 7, Integer.MIN_VALUE, Integer.MAX_VALUE,true);
			if (highScore < tempHighScore) {
				highScore = tempHighScore;
				highMove = m;
			}
			
			System.out.println("Trying Move: " + m + ", Score: "
					+ tempHighScore);
		}

		System.out.println("High Score: " + highScore + ", High Move:"
				+ highMove);
		
		return highMove;
	}

	public int minimax(PacManNodeAlphaBeta rootGameState, int maxdepth, int alpha, int beta, boolean maxPlayer)
	{
		
		//MOVE[] allMoves = Constants.MOVE.values();
		MOVE[] allMoves;
		MOVE pacmanLastMove = rootGameState.gameState.getPacmanLastMoveMade();

		int currIndex = rootGameState.gameState.getPacmanCurrentNodeIndex();

		if (pacmanLastMove != null){

		     allMoves = rootGameState.gameState.getPossibleMoves(currIndex,pacmanLastMove);

		}else{

		     allMoves = rootGameState.gameState.getPossibleMoves(currIndex);

		}

		if (maxdepth <= 0)
		{
			return (rootGameState.gameState.getScore());
		}
		if (maxPlayer)
		{
			int v = Integer.MIN_VALUE;
			for (MOVE m : allMoves)
			{
				Game gameCopy = rootGameState.gameState.copy();
				gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
				PacManNodeAlphaBeta node = new PacManNodeAlphaBeta(gameCopy);
				v = Math.max(v, minimax(node, maxdepth - 1, alpha, beta, false));
				alpha = Math.max(alpha, v);
				if(beta <= alpha)
					break;	
			}
			return v;
		} else
		{
			int v = Integer.MAX_VALUE;
			for (MOVE m : allMoves)
			{
				Game gameCopy = rootGameState.gameState.copy();
				gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));				
				PacManNodeAlphaBeta node = new PacManNodeAlphaBeta(gameCopy);
				v = Math.min(v, minimax(node, maxdepth - 1, alpha, beta, true));
				beta = Math.min(beta, v);
				if(beta <= alpha)
					break;
			}
			return v;
		}
	}

}

class PacManNodeAlphaBeta
{
	Game gameState;
	int depth;

	PacManNodeAlphaBeta(Game game) {
		gameState = game;
	}
}
