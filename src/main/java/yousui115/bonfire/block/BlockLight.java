package yousui115.bonfire.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLight extends Block
{
    /**
     * ■コンストラクタ
     */
    public BlockLight(Material materialIn)
    {
        super(materialIn);
//        this.setDefaultState(getBlockState().getBaseState());
//        super(Material.rock);
//        setCreativeTab(CreativeTabs.tabBlock);/*クリエイティブタブの選択*/
//        setUnlocalizedName("blockSample");/*システム名の設定*/
//        /*以下のものは消しても結構です*/
//        setHardness(1.5F);/*硬さ*/
//        setResistance(1.0F);/*爆破耐性*/
//        setStepSound(SoundType.STONE);/*ブロックの上を歩いた時の音*/
//        /*setBlockUnbreakable();*//*ブロックを破壊不可に設定*/
//        /*setTickRandomly(true);*//*ブロックのtick処理をランダムに。デフォルトfalse*/
//        /*disableStats();*//*ブロックの統計情報を保存しない*/
//        setLightOpacity(1);/*ブロックの透過係数。デフォルト0(不透過)*/
//        setLightLevel(1.0F);/*明るさ 1.0F = 15*/
//        /*this.setDefaultState(getBlockState().getBaseState());*//*初期BlockStateの設定*/
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    //■描画タイプ（Item化した時の描画に関連もありそう？）
    //  RenderBlocks.renderBlockByRenderType()内のとてつもない三項演算子で使用
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }

    /**
     * ■不透明な立方体ですか？
     *  Opaque : 不透明
     */
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isCollidable()
    {
        return false;
    }
    /**
     * ■アイテムドロップ数
     */
    @Override
    public int quantityDropped(Random random)
    {
        return 0;
    }


}
