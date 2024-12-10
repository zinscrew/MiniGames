package barbosatk.minigames.MGDrivers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public final class MGRecipes {

    private final List<ShapedRecipe> mgRecipes = new ArrayList<>();

    public MGRecipes(){}

    public List<ShapedRecipe> getMgRecipes() {
        return mgRecipes;
    }

    public void loadRecipes(Runnable insertRecipes) {
        insertRecipes.run();
        this.mgRecipes.forEach(shapedRecipe -> getServer().addRecipe(shapedRecipe));
    }


    public boolean hasItemResult(ItemStack mgItemResult) {
        return this.mgRecipes.stream().anyMatch(recipe -> recipe.getResult().isSimilar(mgItemResult));
    }

    public void clearRecipes() {
        this.mgRecipes.forEach(shapedRecipe -> getServer().removeRecipe(shapedRecipe.getKey()));
    }
}
