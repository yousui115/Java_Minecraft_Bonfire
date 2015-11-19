package yousui115.bonfire.event;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.bonfire.item.ItemBonfire;

public class EventHooks
{
    @SubscribeEvent
    public void onPlayerTossEvent(ItemTooltipEvent event)
    {
        if (event.itemStack.getItem() instanceof ItemBonfire)
        {
/*            String strName = event.toolTip.get(0);
            strName = EnumChatFormatting.DARK_RED + strName;
            event.toolTip.set(0, strName);
*/
            String strName1 = EnumChatFormatting.AQUA + StatCollector.translateToLocal("bonfire").trim();
            //event.toolTip.add(strName1);
            event.toolTip.set(0, strName1);
        }
    }
}
