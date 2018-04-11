package yousui115.bonfire.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.bonfire.Bonfire;
import yousui115.bonfire.entity.EntityPot;

public class RenderPot extends Render<EntityPot>
{
    protected static ResourceLocation tex = new ResourceLocation(Bonfire.MOD_ID + ":textures/models/teapot.png");

    protected ModelPot modelPot;

    protected RenderPot(RenderManager renderManager)
    {
        super(renderManager);

        modelPot = new ModelPot();
    }

    /**
     * ■描画処理（呼び出し部分）
     */
    @Override
//    public void doRender(EntityBonfire entity, double x, double y, double z, float p_76986_8_, float partialTicks)
    public void doRender(EntityPot entity, double dX, double dY, double dZ, float ff, float ff1)
    {
        //memo:  Techneで出力したModelPotを呼ぶ際、
        //      Modelのソースを弄ると、モデルを手直しした時に修正が大変なので、
        //      このRenderクラスで回転やら縮小やらを行い、手間を省いてる。
        //       後、GlStateManagerを用いて回転縮小を設定する際に
        //      登録とは逆の順番で処理される為（FILO）、注意が必要。
        //       実はもっと楽な方法があるかもしれない。誰かおせーておせーて
        //■
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
//        ItemStack food = entityFood.getItemStack();
        ItemStack food = entity.getNowItemStack();
        int nDir = entity.getDirection();

        //■画像をバインド
        this.bindEntityTexture(entity);

        //■行列のコピー
        GlStateManager.pushMatrix();

        //■？
        GlStateManager.enableRescaleNormal();

        //■FILOで処理される
        // ▼4. 適正位置
        GlStateManager.translate((float)dX, (float)(dY + 0.35d), (float)dZ);

        // ▼3. Y軸回転
        GlStateManager.rotate(nDir*90f, 0.0F, 1.0F, 0.0F);

        // ▼2. X軸回転
        GlStateManager.rotate(180f, 1.0F, 0.0F, 0.0F);

        // ▼1. サイズ調整
        GlStateManager.scale(0.015, 0.015, 0.015);

        //★このメソッドだけは追加した。publicメンバで出力してよ
        modelPot.setLidRotation(entity.getLidRot(), 0);

        //★Techneで出力したrenderをそのまま呼んでる。
        modelPot.render(entity, 0, 0, 0, 0, 0, 1);

        //■行列の削除
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPot entity)
    {
        return tex;
    }

    /* ======================================== Factory =====================================*/

    @SideOnly(Side.CLIENT)
    public static class Factory implements IRenderFactory<EntityPot>
    {
        @Override
        public Render<? super EntityPot> createRenderFor(RenderManager manager)
        {
            return new RenderPot(manager);
        }
    }
}