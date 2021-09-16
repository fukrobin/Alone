package pers.crobin.game.client;

import pers.crobin.game.GameType;
import pers.crobin.game.entity.player.PlayerCapabilities;

/**
 * Created by Administrator
 *
 * @author fkrobin
 * @date 2020/4/6 13:23
 **/
public class GameSetting {

    private final boolean generateStructure;

    private final boolean allowCheats;

    private final boolean bonusChest;

    private final long worldSeed;

    private final String worldName;

    private final GameType gameType;

    private final PlayerCapabilities capabilities;

    public GameSetting(boolean generateStructure, boolean allowCheats, boolean bonusChest, long worldSeed,
                       String worldName, GameType gameType) {
        this.generateStructure = generateStructure;
        this.allowCheats       = allowCheats;
        this.bonusChest        = bonusChest;
        this.worldSeed         = worldSeed;
        this.worldName         = worldName;
        this.gameType          = gameType;

        capabilities = new PlayerCapabilities();
        this.gameType.configurePlayerCapabilities(capabilities);
    }

    public boolean isGenerateStructure() {
        return generateStructure;
    }

    public boolean isAllowCheats() {
        return allowCheats;
    }

    public boolean isBonusChest() {
        return bonusChest;
    }

    public long getWorldSeed() {
        return worldSeed;
    }

    public String getWorldName() {
        return worldName;
    }

    public PlayerCapabilities getCapabilities() {
        return capabilities;
    }
}
