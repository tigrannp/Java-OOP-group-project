package Utils;

import Core.Team;

public class GameUtils {
    public static String getTeamName(Team team) {
        if (team == Team.PLAYER) {
            return "Defenders";
        } else {
            return "Invaders";
        }
    }
}