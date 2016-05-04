package yousui115.bonfire.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.bonfire.Bonfire;
import yousui115.bonfire.CommonProxy;
import yousui115.bonfire.client.render.RenderBonfire;
import yousui115.bonfire.client.render.RenderFood;
import yousui115.bonfire.entity.EntityBonfire;
import yousui115.bonfire.entity.EntityFood;
import yousui115.bonfire.item.ItemFoodB;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    /**
     * ■モデルの登録
     */
    @Override
    public void registerModels()
    {
        //■アイテム
        // ▼焚き火
        ModelResourceLocation mrlBonfire = new ModelResourceLocation(Bonfire.rlBonfire, "inventory");
        ModelLoader.setCustomModelResourceLocation(Bonfire.itemBonfire, 0, mrlBonfire);

        // ▼食べ物
        ResourceLocation[] rlFoods = new ResourceLocation[ItemFoodB.enumFoods.length];
        for (int idx = 0; idx < ItemFoodB.enumFoods.length; idx++)
        {
            rlFoods[idx] = new ResourceLocation(ItemFoodB.enumFoods[idx].modelName);
        }

        ModelBakery.registerItemVariants(Bonfire.itemCookedFood, rlFoods);
        ModelBakery.registerItemVariants(Bonfire.itemBurntFood,  rlFoods);

        for (int idx = 0; idx < rlFoods.length; idx++)
        {
            ModelResourceLocation mrlCooked = new ModelResourceLocation(rlFoods[idx], "inventory");
            ModelLoader.setCustomModelResourceLocation(Bonfire.itemCookedFood, idx, mrlCooked);
            ModelLoader.setCustomModelResourceLocation(Bonfire.itemBurntFood,  idx, mrlCooked);
        }

        //■ブロックアイテム
        ModelLoader.setCustomModelResourceLocation(Bonfire.itemBlockLight, 0, new ModelResourceLocation(new ResourceLocation(Bonfire.MOD_ID, Bonfire.nameLight), "inventory"));
    }

    /**
     * ■レンダラの登録
     */
    @Override
    public void registerRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityBonfire.class, new RenderBonfire.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityFood.class,    new RenderFood.Factory());
    }

    /**
     * ■アイテム色の登録
     */
    @Override
    public void registerItemColor()
    {
        //■まずそうな食材の色は「黒」で
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor()
        {
            public int getColorFromItemstack(ItemStack stack, int layer)
            {
                return 0x444444;
            }
        }, new Item[] { Bonfire.itemBurntFood });
    }

    //■RenderManager
    public static RenderManager getRenderMgr() { return FMLClientHandler.instance().getClient().getRenderManager(); }
}
