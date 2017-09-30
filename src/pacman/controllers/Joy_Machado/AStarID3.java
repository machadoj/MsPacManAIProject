package pacman.controllers.Joy_Machado;

import java.util.ArrayList;
import java.util.Collections;
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

/***
 * 
 * @author Joy
 *
 * My own Algorithm, a combination of A* and ID3
 */
public class AStarID3 extends Controller<MOVE>
{
	public static StarterGhosts ghosts = new StarterGhosts();

	@Override
	public MOVE getMove(Game game, long timeDue)
	{
		// Initializing all Moves
        MOVE[] allMoves;
        MOVE pacmanLastMove = game.getPacmanLastMoveMade();
        int currIndex = game.getPacmanCurrentNodeIndex();

		if (pacmanLastMove != null)
		{
		     allMoves = game.getPossibleMoves(currIndex,pacmanLastMove);
		}
		else
		{
		     allMoves = game.getPossibleMoves(currIndex);
		}
		
        int highScore = -1;
        MOVE highMove = null;
        
        for(MOVE m: allMoves)
        {
            //System.out.println("Trying Move: " + m);
            Game gameCopy = game.copy();
            Game gameAtM = gameCopy;
            gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
            
            // Setting depth, g, h to zero for the gamestate
            // Max depth is set to 7
            int tempHighScore = this.aStar(new AStarID3PacManNode(gameAtM, 0,0,0), 7);
            
            if(highScore < tempHighScore)
            {
                highScore = tempHighScore;
                highMove = m;
            }
            
            System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
           
        }
        
        // return the best possible move
        System.out.println("High Score: " + highScore + ", High Move:" + highMove);
          return highMove;        
	}
	
    // The A* algorithm which uses the attribute of the ID3 algorithm
	// to decide the best possible heuristics
	public int aStar(AStarID3PacManNode rootGameState, int maxdepth)
	{
		// Finding all possible moves
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
		
		// Initializing the high score to minimum
        int highScore = -1;
        
        // Paths to explore
        Queue<AStarID3PacManNode> queue = new LinkedList<AStarID3PacManNode>();
        
        // Visited List
        ArrayList closed = new ArrayList<AStarID3PacManNode>();
        
        // Add the first node to the queue
        queue.add(rootGameState);
        double costfunction = 0;
    	MOVE nextMove = null;
    	
    	// Iterate until the queue is empty
        while(!queue.isEmpty())
        {
        	AStarID3PacManNode pmnode = queue.remove();
        	
        	if(pmnode.depth >= maxdepth)
            {
                int score = pmnode.gameState.getScore();
                 if (highScore < score)
                          highScore = score;
            }
        	else
            {   //initialize the cost function and heuristic to zero
        		Double h = 0.0;
        		// Used to keep track of the high score of each attribute
        		int interScore = 0;
        		
        		for(MOVE m: allMoves)
                {
        			// For each move, make a game copy and advance it in 
        			// the direction of the move
	        		Game gamecopy = pmnode.gameState.copy();
	        		gamecopy.advanceGame(m, ghosts.getMove(gamecopy, 0));
	        		
	        		// Get the score of the move
	        		interScore = gamecopy.getScore();
	        		
	        		// Use the attributes from ID3 algorithm
	        		MOVE powerPillMove = powerPillAttributeMOVE(gamecopy);
	        		MOVE pillMove = activePillAttributeMOVE(gamecopy);
	        		MOVE ghostMove = ghostsAttributeMOVE(gamecopy);
	        		
	        		// Make game copies for each of the attributes
	        		Game gameCopy1 = gamecopy;
	        		Game gameCopy2 = gamecopy;
	        		Game gameCopy3 = gamecopy;
	        		
	        		// MOVE the game copies by the respective moves
	        		gameCopy1.advanceGame(powerPillMove, ghosts.getMove(gameCopy1, 0));
	        		gameCopy2.advanceGame(pillMove, ghosts.getMove(gameCopy2, 0));
	        		gameCopy3.advanceGame(ghostMove, ghosts.getMove(gameCopy3, 0));
	        		
	        		// Get the scores from each of the attributes
	        		int powerPillHighScore = gameCopy1.getScore();
	        		int activePillHighScore = gameCopy2.getScore();
	        		int ghostsHighScore = gameCopy3.getScore();
	        		
	        		// Prepare a PacMan node
	        		AStarID3PacManNode node1;
	        		int highScore1;
	        		
	        		// Find the highest gain for each attribute and initialize the 
	        		// PacMan node and find the highscore among them
	        		int currentIndex = pmnode.gameState.getPacmanCurrentNodeIndex();
	        		if(powerPillHighScore >= activePillHighScore && powerPillHighScore >= ghostsHighScore)
	    			{
	        			int t = gameCopy1.getPacmanCurrentNodeIndex();
	    				h = pmnode.gameState.getEuclideanDistance(currentIndex, t);
	    				node1 =  new AStarID3PacManNode(gameCopy1,pmnode.g,h, pmnode.depth+1);
	    				highScore1 = powerPillHighScore;
	    			}
	    		else if(activePillHighScore >= powerPillHighScore && activePillHighScore >= ghostsHighScore)
	    			{
	    				int t = gameCopy2.getPacmanCurrentNodeIndex();
	    				h = pmnode.gameState.getEuclideanDistance(currentIndex, t);
	    				node1 =  new AStarID3PacManNode(gameCopy2,pmnode.g,h, pmnode.depth+1);
	    				highScore1 = activePillHighScore;
	    			}
	    		else
	    			{
		    			int t = gameCopy3.getPacmanCurrentNodeIndex();
						h = pmnode.gameState.getEuclideanDistance(currentIndex, t);
						node1 =  new AStarID3PacManNode(gameCopy3,pmnode.g,h, pmnode.depth+1);
						highScore1 = ghostsHighScore;
	    			}
	        		
	        		// Use the A* algorithm to find if the cost function is
	        		// least expensive and if the high score gained benefits
	        		// by the move
	        		if((double) (pmnode.g+node1.g+node1.h) < costfunction || interScore < highScore1)
	            	{
	        			// Update the cost function, heuristics and the score
	            		costfunction = pmnode.g + node1.g;
	            		h = node1.h;
	            		nextMove = m;
	            		interScore = highScore1;
	            	}
        		
                }
        		
        		// Advance the game by the next move
        		Game gameCopy = pmnode.gameState.copy();
                gameCopy.advanceGame(nextMove, ghosts.getMove(gameCopy, 0));
                // Make a node of the next path which is the path with the
                // minimum cost and add it to the list of nodes to be visited
                AStarID3PacManNode node = new AStarID3PacManNode(gameCopy,pmnode.g,h, pmnode.depth+1);
                // The current node goes to the list of visited nodes
                closed.add(pmnode);
                
                // If the closed node already contains the node to be explored, do not explore it further
                if(!closed.contains(node))
                {	
                	queue.add(node);
                }
            }
        
        }
        return highScore;
	}
	
	
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
	
	
	public static MOVE ghostsAttributeMOVE(Game game) {
		
		int current=game.getPacmanCurrentNodeIndex();
		int MIN_DISTANCE=20;
		//Strategy 1: if any non-edible ghost is too close (less than MIN_DISTANCE), run away
		for(GHOST ghost : GHOST.values())
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
				if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
		
		//Strategy 2: find the nearest edible ghost and go after them 
		int minDistance=Integer.MAX_VALUE;
		GHOST minGhost=null;		
		MOVE ghost_move = null;
		for(GHOST ghost : GHOST.values())
			if(game.getGhostEdibleTime(ghost)>0)
			{
				int distance=game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));
				
				if(distance<minDistance)
				{
					minDistance=distance;
					minGhost=ghost;
				}
			}
		
		if(minGhost!=null)	//we found an edible ghost
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(minGhost),DM.PATH);
		else 
			return ghost_move;
			
		
	}
}

// Added g, h for the PacMan to note down the
// path taken as well as the hueristics with 
// with the copy of the game state
class AStarID3PacManNode
{
    Game gameState;
    int g;
    double h;
    int depth;
    
    public AStarID3PacManNode(Game game, int g, double h, int depth)
    {
        this.gameState = game;
        this.g = g;
        this.h = h;
        this.depth = depth;
    }
}