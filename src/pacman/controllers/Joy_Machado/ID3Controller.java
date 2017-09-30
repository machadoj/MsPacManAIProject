package pacman.controllers.Joy_Machado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

public class ID3Controller extends Controller<MOVE> {

	public static StarterGhosts ghosts = new StarterGhosts();

	@Override
	public MOVE getMove(Game game, long timeDue) {

		MOVE[] allMoves;
		MOVE pacmanLastMove = game.getPacmanLastMoveMade();
		int currIndex = game.getPacmanCurrentNodeIndex();
		if (pacmanLastMove != null) {
			allMoves = game.getPossibleMoves(currIndex, pacmanLastMove);

		} else {
			allMoves = game.getPossibleMoves(currIndex);
		}

		int highScore = -1;
		MOVE highMove = null;

		for (MOVE m : allMoves) {
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			int tempHighScore = this.id3Tree(new ID3PacManNode(gameAtM, 0), 7);
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

	/**The decision tree has 3 features/attributes. 
	 1. The active power pills
	 2. The active pills
	 3. The distance from ghosts
	 Based on the values of the outcomes after considering the highest
	 gain (highscore) the leaf move will be returned.
	 */		
	
	public int id3Tree(ID3PacManNode rootGameState, int maxdepth) {
		int highScore = -1;

		
		// Get a move based on the active power pills
		MOVE powerPillMove = powerPillAttributeMOVE(rootGameState.gameState);
		// Get a move based on the active pills
		MOVE pillMove = activePillAttributeMOVE(rootGameState.gameState);
		// Get a move based on the ghosts present
		MOVE ghostMove = ghostsAttributeMOVE(rootGameState.gameState);
		
		// Make game copies for each of the attributes
		Game gameCopy1 = rootGameState.gameState.copy();
		Game gameCopy2 = rootGameState.gameState.copy();
		Game gameCopy3 = rootGameState.gameState.copy();
		
		// MOVE the game copies by the respective moves
		gameCopy1.advanceGame(powerPillMove, ghosts.getMove(gameCopy1, 0));
		gameCopy2.advanceGame(pillMove, ghosts.getMove(gameCopy2, 0));
		gameCopy3.advanceGame(ghostMove, ghosts.getMove(gameCopy3, 0));

		
		// Get respective highscores from each game copies
		int powerPillHighScore = gameCopy1.getScore();
		int activePillHighScore = gameCopy2.getScore();
		int ghostsHighScore = gameCopy3.getScore();
		
		// Pick the best feature based on the highest gain
		// Used highscore as information gain 
		// The move which gets the highest score among the 3 attributes
		// gets to be the root of the tree and the next move is based
		// accordingly
		Game highestGainGameCopy;
		
		String flag = "";
		if(powerPillHighScore >= activePillHighScore && powerPillHighScore >= ghostsHighScore)
			{
				flag = "PowerPill";
				highestGainGameCopy = gameCopy1;
			}
		else if(activePillHighScore >= powerPillHighScore && activePillHighScore >= ghostsHighScore)
			{
				flag = "ActivePill";
				highestGainGameCopy = gameCopy2;
			}
		else
			{
				flag = "Ghost";
				highestGainGameCopy = gameCopy3;
			}
		
		// Make the next game copies based on the highest gain
		// The new game copies are to find the next highest
		// gain based on the attribute for the previous highest gain
		Game gainGameCopy1 = highestGainGameCopy.copy();
		Game gainGameCopy2 = highestGainGameCopy.copy();
		
		// Based on the gain from the previous gain, the last 
		// copy of the gain which decides the final move from
		// the enumeration
		Game finalGainGameCopy;
		String newFlag = "";
		if (flag.equals("PowerPill")) {
			// Find the gain from th eother two Attributes
			// Get the highestscore again as the gain
			// Based on the highes score, return the enumeration
			// of the last attribute
			pillMove = activePillAttributeMOVE(gainGameCopy1);
			gainGameCopy1.advanceGame(pillMove,
					ghosts.getMove(gainGameCopy1, 0));

			ghostMove = ghostsAttributeMOVE(gainGameCopy2);
			gainGameCopy2.advanceGame(ghostMove,
					ghosts.getMove(gainGameCopy2, 0));

			if (gainGameCopy1.getScore() > gainGameCopy2.getScore()) {
				newFlag = "ActivePill";
				finalGainGameCopy = gainGameCopy1;
			} else {
				newFlag = "Ghost";
				finalGainGameCopy = gainGameCopy2;
			}

			// Return an enumeration value on the last 
			// decision taken by the decision tree
			if (newFlag.equals("ActivePill")) {
				ghostMove = ghostsAttributeMOVE(finalGainGameCopy);
				finalGainGameCopy.advanceGame(ghostMove,
						ghosts.getMove(finalGainGameCopy, 0));
				return finalGainGameCopy.getScore();
			} else {
				pillMove = activePillAttributeMOVE(finalGainGameCopy);
				finalGainGameCopy.advanceGame(pillMove,
						ghosts.getMove(finalGainGameCopy, 0));
				return finalGainGameCopy.getScore();
			}
		}
		// If the highest attribute is the active pills
		if (flag.equals("ActivePill")) {
			powerPillMove = powerPillAttributeMOVE(gainGameCopy1);
			gainGameCopy1.advanceGame(powerPillMove,
					ghosts.getMove(gainGameCopy1, 0));

			ghostMove = ghostsAttributeMOVE(gainGameCopy2);
			gainGameCopy2.advanceGame(ghostMove,
					ghosts.getMove(gainGameCopy2, 0));

			if (gainGameCopy1.getScore() > gainGameCopy2.getScore()) {
				newFlag = "PowerPill";
				finalGainGameCopy = gainGameCopy1;
			} else {
				newFlag = "Ghost";
				finalGainGameCopy = gainGameCopy2;
			}

			if (newFlag.equals("PowerPill")) {
				ghostMove = ghostsAttributeMOVE(finalGainGameCopy);
				finalGainGameCopy.advanceGame(ghostMove,
						ghosts.getMove(finalGainGameCopy, 0));
				return finalGainGameCopy.getScore();
			} else {
				powerPillMove = powerPillAttributeMOVE(finalGainGameCopy);
				finalGainGameCopy.advanceGame(powerPillMove,
						ghosts.getMove(finalGainGameCopy, 0));
				return finalGainGameCopy.getScore();
			}
		}
		// If the hihgest gain is the MOVE returned based on GHOSTS
		else {
			powerPillMove = powerPillAttributeMOVE(gainGameCopy1);
			gainGameCopy1.advanceGame(powerPillMove,
					ghosts.getMove(gainGameCopy1, 0));

			pillMove = activePillAttributeMOVE(gainGameCopy2);
			gainGameCopy2.advanceGame(pillMove,
					ghosts.getMove(gainGameCopy2, 0));

			if (gainGameCopy1.getScore() > gainGameCopy2.getScore()) {
				newFlag = "PowerPill";
				finalGainGameCopy = gainGameCopy1;
			} else {
				newFlag  = "ActivePill";
				finalGainGameCopy = gainGameCopy2;
			}

			if (flag.equals("Ghost")) {
				pillMove = activePillAttributeMOVE(finalGainGameCopy);
				finalGainGameCopy.advanceGame(pillMove,
						ghosts.getMove(finalGainGameCopy, 0));
				return finalGainGameCopy.getScore();
			} else {
				powerPillMove = powerPillAttributeMOVE(finalGainGameCopy);
				finalGainGameCopy.advanceGame(powerPillMove,
						ghosts.getMove(finalGainGameCopy, 0));
				return finalGainGameCopy.getScore();
			}
		}

	}

	// Get the index of the all the active target power pills
	// Used the strategy from Starter Pac Man to move the
	// Pac Man in the direction of the closest power pill
	public static MOVE powerPillAttributeMOVE(Game game) {
		int targetsPowerPills[] = game.getActivePowerPillsIndices();
		// If all the power pills areconsumed
		// Do not move the pac man and return a 
		// Neutral move
		if (targetsPowerPills.length > 0){
			// Used the strategy from starter pacman
			return game.getNextMoveTowardsTarget(game
					.getPacmanCurrentNodeIndex(), game
					.getClosestNodeIndexFromNodeIndex(
							game.getPacmanCurrentNodeIndex(),
							targetsPowerPills, DM.PATH), DM.PATH);
		}
		else{
			// MOVE the pac man to the right if all the power pills are over
			return MOVE.RIGHT;
		}
	}

	public static MOVE activePillAttributeMOVE(Game game) {
		// Get the index of the active pills in the game
		int targetsPills[] = game.getPillIndices();
		// Get the index of the all the active target pills
		// Used the strategy from Starter Pac Man to move the
		// Pac Man in the direction of the closest active pill
		if (targetsPills.length > 0){
		// Used the strategy from starter pac man to steer the pac man
		// towards the next available pill
		return game
				.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
						game.getClosestNodeIndexFromNodeIndex(
								game.getPacmanCurrentNodeIndex(), targetsPills,
								DM.PATH), DM.PATH);
		}
		else
		{
			// MOVE the pac man to the right if all the pills are over
			return MOVE.RIGHT;
		}
	}

	// Used the strategy from starter pacman to 
	// figure the influence of ghosts on pacman moves
	public static MOVE ghostsAttributeMOVE(Game game) {
		int MIN_DISTANCE = 20;
		int minDistance = Integer.MAX_VALUE;
		GHOST minGhost = null;
		
		//if any non-edible ghost is too close (less than MIN_DISTANCE), run away
		for (GHOST ghost : GHOST.values())
			if (game.getGhostEdibleTime(ghost) > 0) {
				int distance = game.getShortestPathDistance(
						game.getPacmanCurrentNodeIndex(),
						game.getGhostCurrentNodeIndex(ghost));

				if (distance < minDistance) {
					minDistance = distance;
					minGhost = ghost;
				}
			}
		// find the nearest edible ghost and go after them
		MOVE ghost_move = null;
		if (minGhost != null) // we found an edible ghost
			ghost_move = game.getNextMoveTowardsTarget(
					game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(minGhost), DM.PATH);
		else {
			for (GHOST ghost : GHOST.values())
				if (game.getGhostEdibleTime(ghost) == 0
						&& game.getGhostLairTime(ghost) == 0)
					if (game.getShortestPathDistance(
							game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(ghost)) < MIN_DISTANCE)
						ghost_move = game.getNextMoveAwayFromTarget(
								game.getPacmanCurrentNodeIndex(),
								game.getGhostCurrentNodeIndex(ghost), DM.PATH);
		}
		return ghost_move;
	}

	class ID3PacManNode {
		Game gameState;
		int depth;

		ID3PacManNode(Game game, int m) {
			gameState = game;
			depth = m;
		}
	}

}
