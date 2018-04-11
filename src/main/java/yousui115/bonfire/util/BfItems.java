package yousui115.bonfire.util;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.bonfire.Bonfire;
import yousui115.bonfire.item.ItemBonfire;
import yousui115.bonfire.item.ItemFoodB;
import yousui115.bonfire.item.ItemPot;
import yousui115.bonfire.item.ItemWBottle;

public class BfItems
{
    //■Item:たき火
    public static Item BONFIRE;

    //■Item:食料
    // ▼おいしそう
    public static Item COOK_FOOD;

    // ▼こげ
    public static Item BURNT_FOOD;

    //■ポット
    public static Item POT;

    //■水筒
    public static Item WATER_BOTTLE;

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

        //■食料
        // ▼おいしそう
        COOK_FOOD  = (new ItemFoodB(4, 0.6f, false))
                    .setPotionEffect(new PotionEffect(MobEffects.SATURATION, 60, 0), 0.8F)
                    .setRegistryName(Bonfire.MOD_ID, "bf_cooked_food")
                    .setUnlocalizedName("bf_cooked_food")
                    .setCreativeTab(CreativeTabs.FOOD)
                    .setHasSubtypes(true);

        // ▼こげ
        BURNT_FOOD = (new ItemFoodB(2, 0.1f, false))
                    .setPotionEffect(new PotionEffect(MobEffects.HUNGER, 60, 0), 0.8F)
                    .setRegistryName(Bonfire.MOD_ID, "bf_burnt_food")
                    .setUnlocalizedName("bf_burnt_food")
                    .setCreativeTab(CreativeTabs.FOOD)
                    .setHasSubtypes(true);

        //■ポット
        POT = (new ItemPot())
                .setRegistryName(Bonfire.MOD_ID, "bf_pot")
                .setUnlocalizedName("bf_pot")
                .setMaxStackSize(1)
                .setCreativeTab(CreativeTabs.MATERIALS)
                .setHasSubtypes(true);

        //■水筒
        WATER_BOTTLE = (new ItemWBottle())
                        .setRegistryName(Bonfire.MOD_ID, "bf_water_bottle")
                        .setUnlocalizedName("bf_water_bottle")
                        .setMaxStackSize(1)
                        .setMaxDamage(100)
                        .setCreativeTab(CreativeTabs.FOOD);
    }


    /**
     * ■登録
     * @param event
     */
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        //■
        event.getRegistry().registerAll(BfItems.BONFIRE,
                                        BfItems.COOK_FOOD,
                                        BfItems.BURNT_FOOD,
                                        BfItems.POT,
                                        BfItems.WATER_BOTTLE);
    }

    /**
     * ■アイテムのモデルを登録
     */
    @SideOnly(Side.CLIENT)
    public static void registerModel()
    {
        //■たき火
        ModelLoader.setCustomModelResourceLocation(BfItems.BONFIRE, 0, new ModelResourceLocation(BfItems.BONFIRE.getRegistryName(), "inventory"));

        //■食料
        ResourceLocation[] rlFoods = new ResourceLocation[ItemFoodB.enumFoods.length];
        for (int idx = 0; idx < ItemFoodB.enumFoods.length; idx++)
        {
            rlFoods[idx] = new ResourceLocation(ItemFoodB.enumFoods[idx].modelName);
        }

        ModelBakery.registerItemVariants(BfItems.COOK_FOOD, rlFoods);
        ModelBakery.registerItemVariants(BfItems.BURNT_FOOD, rlFoods);

        for (int idx = 0; idx < rlFoods.length; idx++)
        {
            ModelResourceLocation mrlCooked = new ModelResourceLocation(rlFoods[idx], "inventory");
            ModelLoader.setCustomModelResourceLocation(BfItems.COOK_FOOD, idx, mrlCooked);
            ModelLoader.setCustomModelResourceLocation(BfItems.BURNT_FOOD,  idx, mrlCooked);
        }

        //■ポット
        ResourceLocation[] rlPots = new ResourceLocation[ItemPot.enumPots.length];
        for (int idx = 0; idx < ItemPot.enumPots.length; idx++)
        {
            rlPots[idx] = new ResourceLocation(Bonfire.MOD_ID, ItemPot.enumPots[idx].modelName);
        }

        ModelBakery.registerItemVariants(BfItems.POT, rlPots);

        for (int idx = 0; idx < rlPots.length; idx++)
        {
            ModelResourceLocation mrlPot = new ModelResourceLocation(rlPots[idx], "inventory");
            ModelLoader.setCustomModelResourceLocation(BfItems.POT, idx, mrlPot);
        }

        // ▼水筒
        ModelResourceLocation mrlWBottle = new ModelResourceLocation(new ResourceLocation(Bonfire.MOD_ID, "bf_water_bottle"), "inventory");
        ModelLoader.setCustomModelResourceLocation(BfItems.WATER_BOTTLE, 0, mrlWBottle);

    }
}
