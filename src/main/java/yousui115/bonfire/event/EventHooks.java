package yousui115.bonfire.event;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.bonfire.item.ItemBonfire;

public class EventHooks
{
    @SubscribeEvent
    public void onPlayerTossEvent(ItemTooltipEvent event)
    {
        if (event.getItemStack().getItem() instanceof ItemBonfire)
        {
/*            String strName = event.toolTip.get(0);
            strName = EnumChatFormatting.DARK_RED + strName;
            event.toolTip.set(0, strName);
*/
            String strName1 = TextFormatting.AQUA + I18n.translateToLocal("bonfire").trim();
            //event.toolTip.add(strName1);
            event.getToolTip().set(0, strName1);
        }
    }
}
