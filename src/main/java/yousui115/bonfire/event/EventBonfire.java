package yousui115.bonfire.event;

import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.bonfire.util.BfLootTable;

public class EventBonfire
{
    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event)
    {
        if (event.getName() == LootTableList.CHESTS_SPAWN_BONUS_CHEST)
        {
            LootTable bonuschest = event.getLootTableManager().getLootTableFromLocation(BfLootTable.BONUS_CHEST);

            event.getTable().addPool(bonuschest.getPool("water_bottle"));

            event.getTable().addPool(bonuschest.getPool("flint_and_steel"));

            event.getTable().addPool(bonuschest.getPool("pot"));
        }
    }
}
