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

public class BFS_Controller extends Controller<MOVE>
{
	public static StarterGhosts ghosts = new StarterGhosts();

	@Override
	public MOVE getMove(Game game, long timeDue)
	{
		Random rnd=new Random();
        MOVE[] allMoves=MOVE.values();
    
        int highScore = -1;
        MOVE highMove = null;
        
       
        for(MOVE m: allMoves)
        {
            //System.out.println("Trying Move: " + m);
            Game gameCopy = game.copy();
            Game gameAtM = gameCopy;
            gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
            int tempHighScore = this.bfs_amy(new PacManNode(gameAtM, 0), 7);
            
            if(highScore < tempHighScore)
            {
                highScore = tempHighScore;
                highMove = m;
            }
            
            System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
           
        }
        
        System.out.println("High Score: " + highScore + ", High Move:" + highMove);
          return highMove;        
	}
	
    	
	public int bfs_amy(PacManNode rootGameState, int maxdepth)
	{
		MOVE[] allMoves=Constants.MOVE.values();
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
                    queue.add(node);
                }
            }
        }
        
        return highScore;
	}
	
	
	
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