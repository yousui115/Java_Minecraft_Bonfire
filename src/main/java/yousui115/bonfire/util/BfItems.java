package yousui115.bonfire.util;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.bonfire.Bonfire;
import yousui115.bonfire.item.ItemBonfire;

public class BfItems
{
    //■Item:たき火
    public static Item BONFIRE;


    /**
     * ■生成
     */
    public static void create()
    {
        //■たき火
        BONFIRE = (new ItemBonfire())
                .setRegistryName(Bonfire.MOD_ID, "bf_bonfire")
                .setUnlocalizedName("bf_bonfire")
                .setCreativeTab(CreativeTabs.MISC)
                .setHasSubtypes(false);
    }


    /**
     * ■登録
     * @param event
     */
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        //■たき火
        event.getRegistry().registerAll(BfItems.BONFIRE);
    }

    @SideOnly(Side.CLIENT)
    public static void registerModel()
    {
        //■たき火
        ModelLoader.setCustomModelResourceLocation(BfItems.BONFIRE, 0, new ModelResourceLocation(BfItems.BONFIRE.getRegistryName(), "inventory"));
    }
}
