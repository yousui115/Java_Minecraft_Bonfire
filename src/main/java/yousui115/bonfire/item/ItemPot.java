package yousui115.bonfire.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPot extends Item
{
    public static final EnumPotState enumPots[] = EnumPotState.values();

    /**
     * ■クリエイティブモードの取り出し
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (EnumPotState pot : enumPots)
        {
            subItems.add(new ItemStack(itemIn, 1, pot.ordinal()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = MathHelper.clamp_int(stack.getMetadata(), 0, enumPots.length - 1);
        return super.getUnlocalizedName(stack) + "." + enumPots[meta].itemName;
    }


    public enum EnumPotState
    {
        EMPTY(  "empty",  "bf_pot_empty"),
        WATER(  "water",  "bf_pot_water"),
        BOILED( "boiled", "bf_pot_boiled");

        public final String itemName;
        public final String modelName;

        private EnumPotState(String itemNameIn, String modelNameIn)
        {
            itemName  = itemNameIn;
            modelName = modelNameIn;
        }
    }
}
