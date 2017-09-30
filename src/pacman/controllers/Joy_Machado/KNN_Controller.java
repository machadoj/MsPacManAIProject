package pacman.controllers.Joy_Machado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class KNN_Controller  extends Controller<MOVE>{

	public static StarterGhosts ghosts = new StarterGhosts();

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// MOVE[] allMoves = MOVE.values();

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
			// System.out.println("Trying Move: " + m);
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			int tempHighScore = this.knn(new PacManNode(gameAtM, 0), 7, 50);
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

	// KNN function
	public int knn(PacManNode rootGameState, int maxdepth , int k) {
		
		MOVE[] allMoves;
		MOVE pacmanLastMove = rootGameState.gameState.getPacmanLastMoveMade();
        int currIndex =  rootGameState.gameState.getPacmanCurrentNodeIndex();

		if (pacmanLastMove != null)
		{
		     allMoves =  rootGameState.gameState.getPossibleMoves(currIndex,pacmanLastMove);
		}
		else
		{
		     allMoves =  rootGameState.gameState.getPossibleMoves(currIndex);
		}
		
		
		int highScore = -1;
		
		// ArrayList to store moves
		ArrayList<MOVE> kMoves = new ArrayList<MOVE>();
		
		// Tree map to store the distance and the corresponding move
		TreeMap<Double,MOVE> map = new TreeMap<Double,MOVE>();
		
		// Iterating the game state space to find distances
		while(maxdepth > 0){
			
			for(MOVE m: allMoves){
				
			Game gamecopy = rootGameState.gameState.copy();
        	gamecopy.advanceGame(m, ghosts.getMove(gamecopy, 0));
        	
        	// Get a move based on the active power pills
			MOVE power_pill_move = powerPillAttributeMOVE(gamecopy);
			// Get a move based on the active pills
			MOVE pill_move = activePillAttributeMOVE(gamecopy);
			// Get a move based on the ghosts present
			MOVE ghost_move = ghostsAttributeMOVE(gamecopy);
			
			// Make game copies for each of the attributes
			Game gameCopy1 = gamecopy;
			Game gameCopy2 = gamecopy;
			Game gameCopy3 = gamecopy;
			
			// MOVE the game copies by the respective moves
			gameCopy1.advanceGame(power_pill_move, ghosts.getMove(gameCopy1, 0));
			gameCopy2.advanceGame(pill_move, ghosts.getMove(gameCopy2, 0));
			gameCopy3.advanceGame(ghost_move, ghosts.getMove(gameCopy3, 0));

			double distance = 0;
					
			int currentNode = rootGameState.gameState.getPacmanCurrentNodeIndex();
			int powerPillScore = gameCopy1.getPacmanCurrentNodeIndex();
			int activePillScore = gameCopy2.getPacmanCurrentNodeIndex();
			int ghostsScore = gameCopy3.getPacmanCurrentNodeIndex();
			
			// distance from current pacman to pacman with move towards power pill
			distance += rootGameState.gameState.getEuclideanDistance(currentNode, powerPillScore);
			
			// distance from current pacman to pacman with move towards pill
			distance += rootGameState.gameState.getEuclideanDistance(currentNode, activePillScore);
			
			// distance from current pacman to pacman with move towards edible ghost or move away from the ghost
			distance += rootGameState.gameState.getEuclideanDistance(currentNode, ghostsScore);
			
			// Add the distance and its label to a map for sorting purposes
			map.put(distance, m);
		}
			maxdepth--;			
		}

					
		int count = 0;
		// Find the K nearest neighbors
		for (Map.Entry<Double, MOVE> entry : map.entrySet())
		{
		    if (count == k)
		    {
		        break;
		    }
		    count++;
		    MOVE m = entry.getValue();
	        kMoves.add(m);
		}
		
		// Get the majority label of moves
		MOVE majorityMove = findMajorityMoves(kMoves);
		Game finalGameCopy1 = rootGameState.gameState.copy();
		finalGameCopy1.advanceGame(majorityMove, ghosts.getMove(finalGameCopy1, 0));
		
		// Return the high score
		return finalGameCopy1.getScore();
	}
	
	// Get the majority label represented
	public MOVE findMajorityMoves(ArrayList<MOVE> kMoves)
	{
		HashMap<MOVE,Integer> map = new HashMap<MOVE, Integer>();
		MOVE move = null;
		int maxCount = -1;
		
		for(MOVE m : kMoves)
		{
			if(map.containsKey(m))
			{
				map.put(m, map.get(m)+1);
			}
			else
			{
				map.put(m, 1);
			}
		}
		
		Iterator it = map.entrySet().iterator();
	    while (it.hasNext())
	    {
	        Map.Entry pair = (Map.Entry)it.next();
	        if(maxCount < (int)pair.getValue())
	        {
	        	maxCount = (int)pair.getValue();
	        	move = (MOVE)pair.getKey();
	        }
	        it.remove(); 
	    }
		return move;
	}
	
	
	// Get the index of the all the active target power pills
	// Used the strategy from Starter Pac Man to move the
    // Pac Man in the direction of the closest power pill
	public static MOVE powerPillAttributeMOVE(Game game) {
		int targetsPowerPills[] = game.getActivePowerPillsIndices();
		// If all the power pills are consumed
		// Do not move the pac man and return a 
		// Neutral move
		// Used the strategy from starter pacman
		if(targetsPowerPills.length > 0)
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				game.getClosestNodeIndexFromNodeIndex(
						game.getPacmanCurrentNodeIndex(), targetsPowerPills,
						DM.PATH), DM.PATH);
		else 
			return MOVE.RIGHT;
	}

	public static MOVE activePillAttributeMOVE(Game game) {
		// Get the index of the active pills in the game
		int targetsPills[] = game.getPillIndices();
		
		// Get the index of the all the active target pills
		// Used the strategy from Starter Pac Man to move the
		// Pac Man in the direction of the closest active pill
		return game
				.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
						game.getClosestNodeIndexFromNodeIndex(
								game.getPacmanCurrentNodeIndex(), targetsPills,
								DM.PATH), DM.PATH);
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

	
	class PacManNode {
		Game gameState;
		int depth;

		PacManNode(Game game, int m) {
			gameState = game;
			depth = m;
		}
	}
}
