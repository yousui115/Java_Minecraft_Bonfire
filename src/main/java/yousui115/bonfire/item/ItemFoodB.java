package yousui115.bonfire.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFoodB extends ItemFood
{
    //■調理可能食材情報
    public static final EnumBroilFood enumFoods[] = EnumBroilFood.values();

    /**
     * ■コンストラクタ
     */
    public ItemFoodB(int amount, float saturation, boolean isWolfFood)
    {
        super(amount, saturation, isWolfFood);
    }

    /**
     * ■クリエイティブモードの取り出し
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (EnumBroilFood food : enumFoods)
        {
            subItems.add(new ItemStack(itemIn, 1, food.ordinal()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = MathHelper.clamp_int(stack.getMetadata(), 0, enumFoods.length - 1);
        return super.getUnlocalizedName(stack) + "." + enumFoods[meta].itemName;
    }


    /**
     * ■焚き火での調理可能食材情報
     *   (Cooked,Burnt 共に同じモデルを使用する)
     */
    public enum EnumBroilFood
    {
        BEEF(    "beef",     "cooked_beef"),
        PORK(    "porkchop", "cooked_porkchop"),
        CHICKEN( "chicken",  "cooked_chicken"),
        MUTTON(  "mutton",   "cooked_mutton"),
        RABBIT(  "rabbit",   "cooked_rabbit"),
        COD(     "cod",      "cooked_cod"),
        SALMON(  "salmon",   "cooked_salmon");

        public final String itemName;
        public final String modelName;

        private EnumBroilFood(String itemNameIn, String modelNameIn)
        {
            itemName  = itemNameIn;
            modelName = modelNameIn;
        }
    }
}
