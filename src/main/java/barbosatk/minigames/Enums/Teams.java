package barbosatk.minigames.Enums;

public enum Teams {

    RED("Equipa Vermelha"),
    BLUE("Equipa Azul"),
    YELLOW("Equipa Amarela"),
    GREEN("Equipa Verde");

    private final String teamName;

    Teams(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }
}
