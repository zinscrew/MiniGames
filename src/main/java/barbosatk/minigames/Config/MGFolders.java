package barbosatk.minigames.Config;

import barbosatk.minigames.Config.Contants.MGConstants;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;

public class MGFolders {

    public void create() {
        this.folder(MGConstants.PATH_TO_MINIGAMES_FOLDER);
        this.folder(MGConstants.PATH_TO_WORLDS_FOLDERS);
        this.folder(MGConstants.PATH_TO_PLAYER_DATA);
        this.folder(MGConstants.PATH_TO_MG_WORKING_WORLDS);
    }

    private void folder(String pathToFolder) {
        try {
            File file = new File(pathToFolder);
            if (!file.exists())
                file.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteWorlds() {
        this.resetData(MGConstants.PATH_TO_WORLDS_FOLDERS);
    }

    private void resetData(String pathToFolder) {
        File file = new File(pathToFolder);
        try {
            FileUtils.cleanDirectory(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
