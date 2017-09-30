package pacman.controllers.Joy_Machado;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Pacman_NeutralController extends Controller<MOVE>{

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// TODO Auto-generated method stub
		return MOVE.RIGHT;
	}

}
