package yousui115.bonfire.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import yousui115.bonfire.CommonProxy;
import yousui115.bonfire.client.render.RenderBonfire;
import yousui115.bonfire.entity.EntityBonfire;
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
    }
}
