package yousui115.bonfire.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import yousui115.bonfire.Bonfire;

public class BfLootTable
{
    public static ResourceLocation BONUS_CHEST;

    /**
     * ■
     */
    public static void create()
    {
        BONUS_CHEST = new ResourceLocation(Bonfire.MOD_ID, "chests/bounus_chest");
    }

    /**
     * ■
     */
    public static void register()
    {
        LootTableList.register(BONUS_CHEST);
    }
}
