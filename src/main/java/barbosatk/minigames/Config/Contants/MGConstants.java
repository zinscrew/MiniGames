package barbosatk.minigames.Config.Contants;

import barbosatk.minigames.Enums.GameType;

public class MGConstants {

    public static final int MINIMUM_REQUIRED_PLAYERS = 1; //TODO: Set to 2

    public static final int LOBBY_QUANTITY_LIMIT = 18;
    public static final int MG_MENU_INVENTORY_SIZE = 27;
    public static final int PRE_LOBBY_MENU_INVENTORY_SIZE = 36; // multiples of 9
    public static final int LOBBY_MENU_INVENTORY_SIZE = 36;
    public static final int SPECTATOR_MENU_INVENTORY_SIZE = 36;
    public static final int LOBBY_MG_SETTINGS_INVENTORY_SIZE = 18;

    public static final String worldSideBarSB = "worldSideBarSB";
    public static final String playerListSB = "playerListSB";

    // Game Messages
    public static String creatingWorldsMessage(String gameTypeName) {
        return MGConstants.chatTagSuccess(gameTypeName) + "A criar os mundos para o jogo. (Aprox. 20 seg)";
    }

    public static String worldsCreatedMessage(String gameTypeName) {
        return MGConstants.chatTagSuccess(gameTypeName) + "Mundos criados com sucesso!";
    }

    // Path to MiniGame's folders
    public static final String PATH_TO_MINIGAMES_FOLDER = "plugins/MiniGames";
    public static final String PATH_TO_PLAYER_DATA = PATH_TO_MINIGAMES_FOLDER + "/PlayerData/";
    public static final String PATH_TO_WORLDS_FOLDERS = PATH_TO_MINIGAMES_FOLDER + "/MGWorlds/";
    public static final String PATH_TO_MG_WORKING_WORLDS = PATH_TO_MINIGAMES_FOLDER + "/WorkingWorlds/";


    // yml Files PlayerData
    public static final String ITEM_MG_SLOT = "items.mg_slot";

    // Settings Inventory Title
    public static String mgInventoryTitle(GameType gameType) {
        return "Configurações - §2" + gameType.getGameName();
    }

    // Lobby and Game related messages
    public static String gameCounterInitializedMessage(String ownerName, String gameTypeName) {
        return MGConstants.chatTagSuccess(gameTypeName) + "O Jogo foi inicializado pelo owner '§a" + ownerName + "§6' e começará dentro de §a" + 10 + "§6 segundos.";
    }

    public static String gameCounterCanceledMessage(String ownerName, String gameTypeName) {
        return MGConstants.chatTagUnsuccessful(gameTypeName) + "O Jogo foi§c cancelado §6pelo owner '§c" + ownerName + "§6'";
    }

    public static String gameLobbyDeletedMessage(String ownerName, String gameTypeName) {
        return MGConstants.chatTagUnsuccessful(gameTypeName) + "A lobby de §c§l" + ownerName + " §6foi eliminada.";
    }

    public static String lobbyQttReachedLimit(String gameTypeName) {
        return MGConstants.chatTagUnsuccessful(gameTypeName) + "Não existe espaço para lobbies adicionais. Espera por um jogo terminar!";
    }

    public static String userHasNoOwnerPermission(String gameTypeName) {
        return MGConstants.chatTagUnsuccessful(gameTypeName) + "Não tens permissão para efetuar a ação.";
    }

    public static String lobbyGameAlreadyStarted(String gameTypeName) {
        return MGConstants.chatTagUnsuccessful(gameTypeName) + "Não é possível entrar na lobby. O jogo já começou.";
    }

    public static String lobbyIsCurrentlyClosing(String gameTypeName) {
        return MGConstants.chatTagUnsuccessful(gameTypeName) + "A lobby está atualmente a ser encerrada.";
    }

    public static String playerRequestedEndGame(String gameTypeName, String playerName) {
        return MGConstants.chatTagSuccess(gameTypeName) + "§6O Jogador §a" + playerName + " §6pediu para terminar o jogo.\n§b O pedido expira dentro de 20 segundos.";
    }

    public static String playersNeededToEndGame(String gameTypeName, int playersNeeded) {
        return MGConstants.chatTagSuccess(gameTypeName) + "§6São necessários mais §a" + playersNeeded + "§6 jogadores.";
    }

    public static String playerRequestEndGameExpired(String gameTypeName) {
        return MGConstants.chatTagUnsuccessful(gameTypeName) + "§6O teu pedido para terminar o jogo expirou";
    }

    // Add Chat Prefix Channel
    public static String chatTagSuccess(String gameTypeName) {
        return "§6[§a" + gameTypeName + "§6] ";
    }

    public static String chatTagUnsuccessful(String gameTypeName) {
        return "§6[§c" + gameTypeName + "§6] ";
    }
}
