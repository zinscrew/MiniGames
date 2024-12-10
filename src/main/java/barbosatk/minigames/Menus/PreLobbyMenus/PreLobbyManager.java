package barbosatk.minigames.Menus.PreLobbyMenus;

import barbosatk.minigames.Enums.GameType;

import java.util.HashMap;

public class PreLobbyManager {

    private final HashMap<GameType, PreLobbyMenu> preLobbiesMenus = new HashMap<>();

    public PreLobbyManager() {
        this.loadPreLobbyForMGs();
    }

    public HashMap<GameType, PreLobbyMenu> getPreLobbiesMenus() {
        return preLobbiesMenus;
    }

    public PreLobbyMenu findPreLobbyMenu(GameType gameType) {
        return preLobbiesMenus.get(gameType);
    }

    public GameType findGameType(PreLobbyMenu preLobbyMenu) {
        for (GameType gameType : preLobbiesMenus.keySet()) {
            if (preLobbiesMenus.get(gameType) == preLobbyMenu) {
                return gameType;
            }
        }
        return null;
    }

    public void loadPreLobbyForMGs() {
        for (GameType gameType : GameType.values()) {
            if (gameType.isActive())
                preLobbiesMenus.put(gameType, new PreLobbyMenu(gameType));
        }
        System.out.println("[Task] Were loaded " + preLobbiesMenus.size() + " PreLobbiesMenus");
    }
}
