package barbosatk.minigames.Enums;

import barbosatk.minigames.Games.DeathSwap.DeathSwap;
import barbosatk.minigames.Games.HideAndSeek.HideAndSeek;
import barbosatk.minigames.Games.MiniGame;

public enum GameType {

    GLOBAL("Global", "GLB", null, null,false, false),
    /* ---------------------------------------------------------------------------------------------------------------*/
    LAST_MAN_STANDING("Last Man Standing", "LMS", "lastManStanding", null,true, false),
    DEATH_SWAP("Death Swap", "DS", "deathSwap", new DeathSwap(), false,true),
    HIDE_AND_SEEK("Hide and Seek", "HNS", "hideAndSeek", new HideAndSeek(),false,true);

    private final String gameName;
    private final boolean active;
    private final boolean allowTeams;
    private final MiniGame miniGame;
    private final String worldName;
    private final String MGAbbreviation;

    GameType(String gameName, String MGAbbreviation, String worldName, MiniGame miniGame, boolean allowTeams, boolean active) {
        this.gameName = gameName;
        this.MGAbbreviation = MGAbbreviation;
        this.worldName = worldName;
        this.miniGame = miniGame;
        this.allowTeams = allowTeams;
        this.active = active;
    }

    public String getGameName() {
        return gameName;
    }

    public String getWorldName() {
        return worldName;
    }

    public MiniGame getMiniGame() {
        return miniGame;
    }

    public boolean isActive() {
        return active;
    }

    public String getMGAbbreviation() {
        return MGAbbreviation;
    }

    public String getGameNameWAbbrev() {
        return this.gameName + " - " + this.MGAbbreviation;
    }

    public boolean allowTeams() {
        return allowTeams;
    }
}
