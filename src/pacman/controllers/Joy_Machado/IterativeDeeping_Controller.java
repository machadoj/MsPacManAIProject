package pacman.controllers.Joy_Machado;

import java.util.Stack;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.controllers.Joy_Machado.DFS_Controller.PacManNode;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class IterativeDeeping_Controller extends Controller<MOVE>
{
	
	 public static StarterGhosts ghosts = new StarterGhosts();
		public MOVE getMove(Game game,long timeDue)
		{
	            MOVE[] allMoves=MOVE.values();	
	            
	            int highScore = -1;
	            MOVE highMove = null;  
	           
	            
	            for(MOVE m: allMoves)
	            {
	            	// Make a copy of the game state
	                Game gameCopy = game.copy();
	                Game gameAtM = gameCopy;
	                
	                // Make the pacman move in the copy of the game state in one direction
	                gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
	                
	                // Get the highScore on passing the current move to the DFS function with the current 
	                // depth
	                for(int max_depth = 0; max_depth <= 7; max_depth++)
	                {
	                int tempHighScore = this.DFS_Controller(new PacManNode(gameAtM, 0), max_depth);
	                
	                // Based on the HighScore assign the current move as the HighMove
	                if(highScore < tempHighScore)
	                {
	                    highScore = tempHighScore;
	                    highMove = m;
	                }	 
	                
	                System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
	                }
	            }    
	               
	                
	           
	            
	            
	            System.out.println("High Score: " + highScore + ", High Move:" + highMove);
	            
	            // Move the pac man in the direction of the highest gain returned by the DFS function
	            return highMove;	                
		}
	        
		
		// DFS Function
	        public int DFS_Controller(PacManNode rootGameState, int maxdepth)
		{
	        	 MOVE[] allMoves=Constants.MOVE.values();
		            int depth = 0;
		            int highScore = -1;		
		            
		            // Enter moves for a move into the Stack until max depth is reached
		            Stack<PacManNode> stack = new Stack<PacManNode>();
		            stack.push(rootGameState);
		            
		            // Iterate over the stack until all the moves within the Stack is checked
		            while(!stack.isEmpty())
		                {
		            		// Pop the top most node and calcualte the weight of all the children
		                    PacManNode pmnode = stack.pop();
		                    if(pmnode.depth >= maxdepth)
		                    {
		                        int active = pmnode.gameState.getNumberOfActivePills();
		                        int power = pmnode.gameState.getNumberOfActivePowerPills();
		                        int score = pmnode.gameState.getScore();
		                        
		                        // Changed the logic here slightly to make the PacMan move in a better direction
		                         if (active + power - score > 0)
		                                  highScore = score;
		                    }
		                    else
		                    {
		                        for(MOVE m: allMoves)
		                        {
		                        	// Add all the Children for a move into the Stack if the ideal results 
		                        	// were not reached.
		                            Game gameCopy = pmnode.gameState.copy();
		                            gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
		                            PacManNode node = new PacManNode(gameCopy, pmnode.depth+1);
		                            stack.push(node);
		                            
		                        }
		                    }

		                } 
		            
		               // Return the highScore so the parent move can be set as the highMove
		                return highScore;

		}
	        
	    class PacManNode 
	    {
	    	Game gameState;
	    	int depth;
	    	PacManNode(Game game, int m)
	    	{
	    		gameState = game;
	   			depth = m;
	   		}
	  	}
	    	
}
