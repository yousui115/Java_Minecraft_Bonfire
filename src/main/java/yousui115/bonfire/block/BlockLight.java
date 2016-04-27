package yousui115.bonfire.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLight extends Block
{
    /**
     * ■コンストラクタ
     */
    public BlockLight(Material materialIn)
    {
        super(materialIn);
        this.setTickRandomly(true);
        //this.setBlockBounds(0.499F, 0.499F, 0.499F, 0.5F, 0.5F, 0.5F);
    }

    /**
     * ■接触したEntityに当たり判定を返す
     *  null = すり抜ける
     */
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState worldIn, World pos, BlockPos state)
    {
        //return worldIn.getBoundingBox(pos, state).offset(state);
        return FULL_BLOCK_AABB;
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

    /**
     * ■アイテムドロップ数
     */
    @Override
    public int quantityDropped(Random random)
    {
        return 0;
    }


}
