package barbosatk.minigames.Enums;

public enum GameStatus {

    AWAITING_FOR_PLAYERS("§eEm lobby...", true, false, true, true, true),
    COUNTDOWN_TO_START("§bEm contagem decrescente...", true, false, true, true, true),
    GAME_INITIALIZATION("§3A iniciar o jogo...", false, false, true, false, false),
    GAME_RUNNING("§aA decorrer...", false, true, true, false, true),
    ENDING_GAME("§cA encerrar jogo...", false, false, false, false, true);

    private final String statusName;
    private final boolean usersCanJoin;
    private final boolean allowSpectators;
    private final boolean canOpenLobby;
    private final boolean allowToClickLobbyItems;
    private final boolean allowPlayerToMove;

    GameStatus(String statusName, boolean usersCanJoin, boolean allowSpectators, boolean canOpenLobby, boolean allowToClickLobbyItems, boolean allowPlayerToMove) {
        this.statusName = statusName;
        this.usersCanJoin = usersCanJoin;
        this.allowSpectators = allowSpectators;
        this.canOpenLobby = canOpenLobby;
        this.allowToClickLobbyItems = allowToClickLobbyItems;
        this.allowPlayerToMove = allowPlayerToMove;
    }

    public String getStatusName() {
        return statusName;
    }

    public boolean usersCanJoin() {
        return usersCanJoin;
    }

    public boolean spectatorsAllowed() {
        return allowSpectators;
    }

    public boolean canOpenLobby() {
        return canOpenLobby;
    }

    public boolean isAllowToClickLobbyItems() {
        return allowToClickLobbyItems;
    }

    public boolean isAllowPlayerToMove() {
        return allowPlayerToMove;
    }
}