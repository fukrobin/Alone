package pers.crobin.game.entity.player;

/**
 * Created by Administrator
 *
 * @author fkrobin
 * @date 2020/4/6 13:37
 **/
public class PlayerCapabilities {
    private final float flySpeed = 0.05F;

    private final float walkSpeed = 0.1F;

    /**
     * Disables player damage.
     */
    public boolean disableDamage;

    /**
     * Sets/indicates whether the player is flying.
     */
    public boolean isFlying;

    /**
     * whether or not to allow the player to fly when they double jump.
     */
    public boolean allowFlying;

    /**
     * Used to determine if creative mode is enabled, and therefore if items should be depleted on usage
     */
    public boolean isCreativeMode;

    /**
     * Indicates whether the player is allowed to modify the surroundings
     */
    public boolean allowEdit = true;
}
