package pacman.controllers.Joy_Machado;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.Joy_Machado.DFS_Controller.PacManNode;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class SimulatedAnnealingController extends Controller<MOVE>
{
	public static StarterGhosts ghosts = new StarterGhosts();
	
	@Override
	public MOVE getMove(Game game, long timeDue)
	{
		Random rnd=new Random();
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
        
        // Setting the temperature to a high point
        int temperature = 7;
       while(temperature >= 0)
       {
	        for(MOVE m: allMoves)
	        {
	            //System.out.println("Trying Move: " + m);
	            Game gameCopy = game.copy();
	            Game gameAtM = gameCopy;
	            gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
	            int tempHighScore = this.simulatedAnnealing(new PacManNode(gameAtM, 0), temperature);
	            
	            if(highScore < tempHighScore)
	            {
	                highScore = tempHighScore;
	                highMove = m;
	            }
	            
	            System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
	           
	        }
	        
	        // Eventually decreasing the temperature so that it cools down slowly and eventually
	        temperature--;
       } 
        System.out.println("High Score: " + highScore + ", High Move:" + highMove);
          return highMove;        
	}
	
    
	// Here the temperature is sent as the max depth for calculation purposes
	public int simulatedAnnealing(PacManNode rootGameState, int maxdepth)
	{
		 MOVE[] allMoves;
	        MOVE pacmanLastMove = rootGameState.gameState.getPacmanLastMoveMade();
	        int currIndex = rootGameState.gameState.getPacmanCurrentNodeIndex();

			if (pacmanLastMove != null)
			{
			     allMoves = rootGameState.gameState.getPossibleMoves(currIndex,pacmanLastMove);
			}
			else
			{
			     allMoves = rootGameState.gameState.getPossibleMoves(currIndex);
			}
			
        int depth = 0;
        int highScore = -1;
        
        Queue<PacManNode> queue = new LinkedList<PacManNode>();
        queue.add(rootGameState);
        
        while(!queue.isEmpty())
        {
        	PacManNode pmnode = queue.remove();
        	if(pmnode.depth >= maxdepth)
            {
                int score = pmnode.gameState.getScore();
                 if (highScore < score)
                          highScore = score;
            }
        	else
            {
        		for(MOVE m: allMoves)
                {
                    Game gameCopy = pmnode.gameState.copy();
                    gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
                    PacManNode node = new PacManNode(gameCopy, pmnode.depth+1);
                    
                    int nodeScore = node.gameState.getScore();
                    int pmnodeScore = pmnode.gameState.getScore();
                    
                    // If the score of the game copy for the next move is better, simply add it 
                    // to the queue
                    if(nodeScore > pmnodeScore)
                    {
                    	queue.add(node);
                    }
                    // Do not discard it like in the case of hill climbing
                    // Accept negative solutions but only it the diffrence
                    // in the probability is higher than a certain threshold
                    // and the temperature eventually goes down to zero
                    else
                    {
                    	// Threshold probability
                    	double r = 0.5;
                    	double probability = Math.exp((pmnodeScore - nodeScore)/maxdepth);
                    	// If probabilty is better for a certain point, 
                    	// make sure to explore it, hence add it to the queue
                    	if(probability > r)
                    		queue.add(node);
                    }
                }
            }
        }
        
        return highScore;
	}
	
	class PacManNode 
	{
	    Game gameState;
	    int depth;
	    
	    public PacManNode(Game game, int depth)
	    {
	        this.gameState = game;
	        this.depth = depth;
	        
	    }

	}
}
