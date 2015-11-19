package yousui115.bonfire.client;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.bonfire.Bonfire;
import yousui115.bonfire.CommonProxy;
import yousui115.bonfire.client.render.RenderBonfire;
import yousui115.bonfire.entity.EntityBonfire;

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
        String strName = Bonfire.MOD_ID + ":bf_bonfire";
        ModelResourceLocation model = new ModelResourceLocation(strName, "inventory");

        ModelBakery.addVariantName(Bonfire.itemBonfire , strName);
        ModelLoader.setCustomModelResourceLocation(Bonfire.itemBonfire, 0, model);

        //■ブロック
        strName = Bonfire.MOD_ID + ":" + "bf_blocklight";
        model = new ModelResourceLocation(strName, "inventory");

        ModelBakery.addVariantName(Item.getItemFromBlock(Bonfire.blockLight), strName);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Bonfire.blockLight), 0, model);
    }

    /**
     * ■レンダラの登録
     */
    @Override
    public void registerRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityBonfire.class, new RenderBonfire(FMLClientHandler.instance().getClient().getRenderManager()));
    }

    /**
     * ■テクスチャの登録
     */
    @Override
    public void registerTextures()
    {
    }
}
