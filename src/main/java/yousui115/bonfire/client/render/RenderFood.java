package yousui115.bonfire.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.bonfire.entity.EntityFood;

@SideOnly(Side.CLIENT)
public class RenderFood extends Render<EntityFood>
{
    private static ItemStack stick;

    /**
     * ■コンストラクタ
     * @param renderManager
     */
    protected RenderFood(RenderManager renderManager)
    {
        super(renderManager);

        stick = new ItemStack(Items.stick);
    }

    /**
     * ■描画処理
     */
    @Override
    public void doRender(EntityFood entityFood, double x, double y, double z, float entityYaw, float partialTicks)
    {
        //■
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
//        ItemStack food = entityFood.getItemStack();
        ItemStack food = entityFood.getNowItemStack();
        int nDir = entityFood.getDirection() + 1;

        //■画像をバインド
        this.bindEntityTexture(entityFood);

        /************************/

        //■
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        //■
        GlStateManager.pushMatrix();
        GlStateManager.enableNormalize();
        GlStateManager.translate(x, y + 0.4d, z);
        GlStateManager.rotate(nDir*90f, 0, 1, 0);
        GlStateManager.rotate(-15f, 1, 0, 0);
        GlStateManager.rotate( 45f, 0, 0, 1);
        GlStateManager.scale( 0.5f, 0.5f, 0.5f);

        //■
        IBakedModel ibakedmodel = renderItem.getItemModelMesher().getItemModel(food);
        renderItem.renderItem(food, ibakedmodel);

        //■
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();


        /************************/

        //■
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        //■
        GlStateManager.pushMatrix();
        GlStateManager.enableNormalize();
        GlStateManager.translate(x, y + 0.2d, z);
        GlStateManager.rotate(nDir*90f, 0, 1, 0);
        GlStateManager.rotate(-15f, 1, 0, 0);
        GlStateManager.rotate( 45f, 0, 0, 1);
        GlStateManager.translate(0d, 0d, 0.05d);
        GlStateManager.scale( 0.7f, 0.7f, 0.3f);

        //■
        ibakedmodel = renderItem.getItemModelMesher().getItemModel(stick);
        renderItem.renderItem(stick, ibakedmodel);

        //■
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();

    }

    /**
     * ■描画したいResourceLocationを返す
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityFood foodIn)
    {
        return TextureMap.locationBlocksTexture;
    }

    /* ======================================== Factory =====================================*/

    @SideOnly(Side.CLIENT)
    public static class Factory implements IRenderFactory<EntityFood>
    {
        @Override
        public Render<? super EntityFood> createRenderFor(RenderManager manager)
        {
            return new RenderFood(manager);
        }
    }
}
