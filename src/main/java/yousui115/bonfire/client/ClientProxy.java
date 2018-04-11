package yousui115.bonfire.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import yousui115.bonfire.CommonProxy;
import yousui115.bonfire.client.render.RenderBonfire;
import yousui115.bonfire.client.render.RenderFood;
import yousui115.bonfire.client.render.RenderPot;
import yousui115.bonfire.entity.EntityBonfire;
import yousui115.bonfire.entity.EntityFood;
import yousui115.bonfire.entity.EntityPot;
import yousui115.bonfire.util.BfItems;

public class ClientProxy extends CommonProxy
{
    /**
     * ■モデルの登録
     */
    @Override
    public void registerItemModel()
    {
        //■アイテムのモデル　登録
        BfItems.registerModel();
    }

    @Override
    public void registerRenderer()
    {
        //■レンダラーの登録
        RenderingRegistry.registerEntityRenderingHandler(EntityBonfire.class, new RenderBonfire.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityFood.class,    new RenderFood.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityPot.class,     new RenderPot.Factory());
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
            @Override
            public int colorMultiplier(ItemStack stack, int layer)
            {
                return 0x444444;
            }
        }, new Item[] { BfItems.BURNT_FOOD });
    }

    //■RenderManager
    public static RenderManager getRenderMgr() { return FMLClientHandler.instance().getClient().getRenderManager(); }
}
