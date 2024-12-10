package barbosatk.minigames.Games.DeathSwap.Enums;

public enum SwapType {
    NORMAL("Normal"),
    RANDOM("Aleatório"),
    SEQUENTIAL("Sequencial");

    private final String name;

    SwapType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
