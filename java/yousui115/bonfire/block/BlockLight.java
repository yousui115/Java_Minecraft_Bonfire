package yousui115.bonfire.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockLight extends Block
{
    /**
     * ■コンストラクタ
     */
    public BlockLight(Material materialIn)
    {
        super(materialIn);
        this.setTickRandomly(true);
        this.setBlockBounds(0.499F, 0.499F, 0.499F, 0.5F, 0.5F, 0.5F);
    }

    /**
     * ■接触したEntityに当たり判定を返す
     *  null = すり抜ける
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }

    //■描画タイプ（Item化した時の描画に関連もありそう？）
    //  RenderBlocks.renderBlockByRenderType()内のとてつもない三項演算子で使用
    @Override
    public int getRenderType()
    {
        return 2;
    }

    /**
     * ■不透明な立方体ですか？
     *  Opaque : 不透明
     */
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * ■アイテムドロップ数
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }


}
