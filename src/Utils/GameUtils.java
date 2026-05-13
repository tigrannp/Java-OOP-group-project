package Utils;

import Core.Team;

/**
 * Utility methods for the game.
 */
public class GameUtils {

    /**
     * Returns the display name for a given team.
     *
     * @param team the team to look up
     * @return "Defenders" for {@link Team#PLAYER}, "Invaders" for {@link Team#ENEMY}
     */
    public static String getTeamName(Team team) {
        if (team == Team.PLAYER) {
            return "Defenders";
        } else {
            return "Invaders";
        }
    }
}
