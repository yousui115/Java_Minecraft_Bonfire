package yousui115.bonfire.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.bonfire.entity.EntityBonfire;

@SideOnly(Side.CLIENT)
public class RenderBonfire extends Render<EntityBonfire>
{
    private static final ResourceLocation resource = new ResourceLocation("textures/atlas/blocks.png");
    private float fColor[][] = new float[4][4];

    protected TextureAtlasSprite iconIndex[] = new TextureAtlasSprite[5];

    //■薪々パラメータ
    private
    static double[][] dVec = {  { 0.1,  0.1,  0.3}, //0
                                {-0.1,  0.1,  0.3}, //1
                                {-0.1,  0.1, -0.3}, //2
                                { 0.1,  0.1, -0.3}, //3
                                { 0.1, -0.1,  0.3}, //4
                                {-0.1, -0.1,  0.3}, //5
                                {-0.1, -0.1, -0.3}, //6
                                { 0.1, -0.1, -0.3}};//7

    private
    static int[][] nTex    = {  {4, 0, 0},   //0
                                {4, 0, 2},   //1
                                {4, 1, 0},   //2
                                {4, 1, 2},   //3
                                {3, 0, 1},   //4
                                {3, 0, 2},   //5
                                {3, 2, 1},   //6
                                {3, 2, 2},   //7
                                {2, 0, 0},   //8
                                {2, 0, 1},   //9
                                {2, 1, 0},   //10
                                {2, 1, 1}};  //11


/*    private
    static float[][] fTex  = {  {     64F,    224F},   //0 木    左上
                                {     64F, 239.99F},   //1 木    左下
                                {  71.99F,    224F},   //2 木    右上（U側半分）
                                {  71.99F, 239.99F},   //3 木    右下（U側半分）
                                {    256F,     80F},   //4 木材  左上（V側半分）
                                {    256F,  87.99F},   //5 木材  左下
                                { 269.99F,     80F},   //6 木材  右上（V側+14）
                                { 269.99F,  87.99F},   //7 木材  右下
                                {     80F,    224F},   //8 年輪  左上
                                {     80F, 231.99F},   //9 年輪  左下（V側半分）
                                {  87.99F,    224F},   //10年輪  右上（U側半分）
                                {  87.99F, 231.99F}};  //11年輪  右下（UV半分）
*/
    private
    static int[][] nVecTexPos = {{3, 2, 1, 0,  3,  1,  0,  2},
                                 {7, 3, 0, 4,  3,  1,  0,  2},
                                 {4, 0, 1, 5,  9,  8, 10, 11},
                                 {5, 1, 2, 6,  7,  6,  4,  5},
                                 {6, 2, 3, 7, 11, 10,  8,  9},
                                 {5, 6, 7, 4,  6,  4,  5,  7}};

    private
    static int[] nVertexColor = {2, 2, 0, 0, 3, 3, 1, 1};

    /**
     * ■コンストラクタ
     * @param renderManager
     */
    public RenderBonfire(RenderManager renderManager)
    {
        super(renderManager);

//        TextureMap texture = Minecraft.getMinecraft().getTextureMapBlocks();
//        iconIndex = new TextureAtlasSprite[] { texture.getAtlasSprite("minecraft:blocks/fire_layer_0"),
//                                               texture.getAtlasSprite("minecraft:blocks/fire_layer_1"),
//                                               texture.getAtlasSprite("minecraft:blocks/log_oak_top"),
//                                               texture.getAtlasSprite("minecraft:blocks/planks_oak"),
//                                               texture.getAtlasSprite("minecraft:blocks/log_oak")
//                                             };
//        iconIndex = new TextureAtlasSprite[] {  texture.getTextureExtry("minecraft:blocks/fire_layer_0"),
//                                                texture.getTextureExtry("minecraft:blocks/fire_layer_1"),
//                                                texture.getTextureExtry("minecraft:blocks/log_oak_top"),
//                                                texture.getTextureExtry("minecraft:blocks/planks_oak"),
//                                                texture.getTextureExtry("minecraft:blocks/log_oak")
//                                             };

    }

    /**
     * ■描画処理（呼び出し部分）
     */
    @Override
//    public void doRender(EntityBonfire entity, double x, double y, double z, float p_76986_8_, float partialTicks)
    public void doRender(EntityBonfire bonfire, double dX, double dY, double dZ, float ff, float ff1)
    {
        TextureMap texture = Minecraft.getMinecraft().getTextureMapBlocks();
        iconIndex[0] = texture.getAtlasSprite("minecraft:blocks/fire_layer_0");
        iconIndex[1] = texture.getAtlasSprite("minecraft:blocks/fire_layer_1");
        iconIndex[2] = texture.getAtlasSprite("minecraft:blocks/log_oak_top");
        iconIndex[3] = texture.getAtlasSprite("minecraft:blocks/planks_oak");
        iconIndex[4] = texture.getAtlasSprite("minecraft:blocks/log_oak");

        //■描画に必要なデータを作成
        // ▼データウォッチャー 取得
//        entity.getDataWatcherLocal();
        // ▼薪々の状態
//        EnumWoodState state = entity.getNowWoodState();

        // ▼燃焼中か否か
        boolean isBurning = bonfire.state.isBurning;

        // ▼火のサイズ
//        float fScaleFire = entity.getFireScale(state) / 2.0f;
        float fScaleFire = bonfire.state.getRenderFireScale() / 2.0f;

        // ▼薪々の色
//        float fColor0[] = entity.getWoodColor_0(state);
//        float fColor3[] = entity.getWoodColor_3(state);
        float fColor0[] = bonfire.state.getRenderWoodColor_0();
        float fColor3[] = bonfire.state.getRenderWoodColor_3();

        for (int i = 0; i < fColor[0].length; i++)
        {
            fColor[0][i] = fColor0[i];
            fColor[3][i] = fColor3[i];
            fColor[1][i] = fColor[2][i] = (fColor[0][i] + fColor[3][i]) / 2.0f;
        }

        // ▼てせれいたー
        Tessellator tessellator = Tessellator.getInstance();
        // ▼わーるどれんだらー
//        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
//        VertexBuffer worldrenderer = tessellator.getBuffer();
        BufferBuilder buffer = tessellator.getBuffer();

        //■薪々の描画
        for (int idy = 0; idy < 4; idy++)
        {
            //■行列のコピー
            GlStateManager.pushMatrix();

            //■？
            GlStateManager.enableRescaleNormal();

            //■画像をバインド
            this.bindEntityTexture(bonfire);

            //▼ブレンド設定On
            //GlStateManager.enableBlend();     //<- 影Modだと透過しちゃう
            //▼アルファ値On
            //GlStateManager.enableAlpha();
            //▼ライティング処理On
//            GlStateManager.enableLighting();
            //▼グラデーション設定On
            GlStateManager.shadeModel(GL11.GL_SMOOTH);

            //■FILOで処理される
            // ▼4. 適正位置
            //GL11.glTranslatef((float)dX, (float)dY+0.1F, (float)dZ);
            GlStateManager.translate((float)dX, (float)dY+0.2F, (float)dZ);
            // ▼3. Y軸回転
            //GL11.glRotatef(45.0F + 90F * (float)(idy), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(45.0F + 90F * (float)(idy), 0.0F, 1.0F, 0.0F);
            // ▼2. Z軸移動
            //GL11.glTranslatef(0.0F, 0.0F, 0.3F);
            GlStateManager.translate(0.0F, 0.0F, 0.3F);
            // ▼1. X軸回転
            //GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);

//            worldrenderer.startDrawingQuads();
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

//            worldrenderer.setNormal(0.0F, 1.0F, 0.0F);

            final float fXMax = 512f;
            final float fYMax = 512f;

            //■描画開始（薪１本の描画）
            for(int idx = 0; idx < nVecTexPos.length; idx++)
            {
                TextureAtlasSprite iconT = iconIndex[ nTex[ nVecTexPos[idx][4] ][0] ];
                float fHalf = (iconT.getMaxU() - iconT.getMinU()) / 2.0F;
                float fUV[][] = {   {iconT.getMinU(), iconT.getMinU() + fHalf, iconT.getMaxU()},
                                    {iconT.getMinV(), iconT.getMinV() + fHalf, iconT.getMaxV()}};

                int col = nVertexColor[nVecTexPos[idx][0]];
                buffer.pos(  dVec[nVecTexPos[idx][0]][0],
                                    dVec[nVecTexPos[idx][0]][1],
                                    dVec[nVecTexPos[idx][0]][2])
                             .tex(  fUV[0][ nTex[ nVecTexPos[idx][4] ][1] ],
                                    fUV[1][ nTex[ nVecTexPos[idx][4] ][2] ])
                             .color(fColor[col][0], fColor[col][1], fColor[col][2], fColor[col][3])
                             .normal(0.0f, 1.0f, 0.0f)
                             .endVertex();

                col = nVertexColor[nVecTexPos[idx][1]];
                buffer.pos(  dVec[nVecTexPos[idx][1]][0],
                                    dVec[nVecTexPos[idx][1]][1],
                                    dVec[nVecTexPos[idx][1]][2])
                             .tex(  fUV[0][ nTex[ nVecTexPos[idx][5] ][1] ],
                                    fUV[1][ nTex[ nVecTexPos[idx][5] ][2] ])
                             .color(fColor[col][0], fColor[col][1], fColor[col][2], fColor[col][3])
                             .normal(0.0f, 1.0f, 0.0f)
                             .endVertex();

                col = nVertexColor[nVecTexPos[idx][2]];
                buffer.pos(  dVec[nVecTexPos[idx][2]][0],
                                    dVec[nVecTexPos[idx][2]][1],
                                    dVec[nVecTexPos[idx][2]][2])
                             .tex(  fUV[0][ nTex[ nVecTexPos[idx][6] ][1] ],
                                    fUV[1][ nTex[ nVecTexPos[idx][6] ][2] ])
                             .color(fColor[col][0], fColor[col][1], fColor[col][2], fColor[col][3])
                             .normal(0.0f, 1.0f, 0.0f)
                             .endVertex();

                col = nVertexColor[nVecTexPos[idx][3]];
                buffer.pos(  dVec[nVecTexPos[idx][3]][0],
                                    dVec[nVecTexPos[idx][3]][1],
                                    dVec[nVecTexPos[idx][3]][2])
                             .tex(  fUV[0][ nTex[ nVecTexPos[idx][7] ][1] ],
                                    fUV[1][ nTex[ nVecTexPos[idx][7] ][2] ])
                             .color(fColor[col][0], fColor[col][1], fColor[col][2], fColor[col][3])
                             .normal(0.0f, 1.0f, 0.0f)
                             .endVertex();

            }
            tessellator.draw();

            GlStateManager.shadeModel(GL11.GL_FLAT);
            //GlStateManager.disableBlend();
            //GlStateManager.disableAlpha();
//            GlStateManager.disableLighting();

            //GlStateManager.disableRescaleNormal();

            //■行列の削除
            GlStateManager.popMatrix();
        }

        //■炎の描画
//        if (state.isFire())
        if (isBurning == true)
        {
            //▼ライティング処理On
            //GlStateManager.enableLighting();

//            worldrenderer.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);

            for (int idx = 0; idx < 4; idx++)
            {
                float fX = 0.5F;
                float fY = 1.0F;
                float fZ = 0.0F;

                int nNo = idx % 2;
                float fU1 = iconIndex[nNo].getMinU();
                float fU2 = iconIndex[nNo].getMaxU();
                float fV1 = iconIndex[nNo].getMinV();
                float fV2 = iconIndex[nNo].getMaxV();

                GlStateManager.pushMatrix();

//                worldrenderer.startDrawingQuads();
                buffer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

                //5.適正位置
                GlStateManager.translate((float)dX, (float)dY + 0.4F, (float)dZ);
                //4.回転
                GlStateManager.rotate(90F * (float)(idx), 0.0F, 1.0F, 0.0F);
                //3.移動
                GlStateManager.translate(0.0F, 0.0F, 0.25F);
                //2.回転
                GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0F);
                //1.縮小
                GlStateManager.scale(fScaleFire, fScaleFire, 0.0F);

                buffer.pos(-fX,        fY, fZ).tex(fU1, fV1).normal(0f, 1f, 0f).endVertex();
                buffer.pos(-fX, 1.0F - fY, fZ).tex(fU1, fV2).normal(0f, 1f, 0f).endVertex();
                buffer.pos( fX, 1.0F - fY, fZ).tex(fU2, fV2).normal(0f, 1f, 0f).endVertex();
                buffer.pos( fX,        fY, fZ).tex(fU2, fV1).normal(0f, 1f, 0f).endVertex();

                tessellator.draw();
                GlStateManager.popMatrix();
            }

            //■炎（ななめ）
            for (int idx = 0; idx < 4; idx++)
            {
                float fX = 0.5F;
                float fY = 1.0F;
                float fZ = 0.0F;

                int nNo = idx % 2;
                float fU1 = iconIndex[nNo].getMinU();
                float fU2 = iconIndex[nNo].getMaxU();
                float fV1 = iconIndex[nNo].getMinV();
                float fV2 = iconIndex[nNo].getMaxV();

                GlStateManager.pushMatrix();
//                worldrenderer.startDrawingQuads();
                buffer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

                //4.適正位置
                GlStateManager.translate((float)dX, (float)dY + 0.4F, (float)dZ);
                //3.回転
                GlStateManager.rotate(90F * (float)(idx), 0.0F, 1.0F, 0.0F);
                //2.回転
                GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
                //1.縮小
                GlStateManager.scale(fScaleFire, fScaleFire, 0.0F);

                buffer.pos(-fX,        fY, fZ).tex(fU1, fV1).normal(0f, 1f, 0f).endVertex();
                buffer.pos(-fX, 1.0F - fY, fZ).tex(fU1, fV2).normal(0f, 1f, 0f).endVertex();
                buffer.pos( fX, 1.0F - fY, fZ).tex(fU2, fV2).normal(0f, 1f, 0f).endVertex();
                buffer.pos( fX,        fY, fZ).tex(fU2, fV1).normal(0f, 1f, 0f).endVertex();

                tessellator.draw();
                GlStateManager.popMatrix();
            }

            //▼ライティング処理On
            //GlStateManager.disableLighting();
        }
    }

    /**
     * ■バインドしたい画像リソース
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityBonfire entity)
    {
        return this.resource;
    }

    /**
     * ■RenderBonfireFactory
     * @author yousui
     *
     */
    @SideOnly(Side.CLIENT)
    public static class Factory implements IRenderFactory<EntityBonfire>
    {
        @Override
        public Render<? super EntityBonfire> createRenderFor(RenderManager manager)
        {
            return new RenderBonfire(manager);
        }
    }
}
