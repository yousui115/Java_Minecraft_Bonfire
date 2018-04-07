package yousui115.bonfire.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yousui115.bonfire.Bonfire;

    public class BlockLight extends Block
    {
        /**
     * ■コンストラクタ
     */
    public BlockLight(Material materialIn)
    {
        super(materialIn);

        setRegistryName(Bonfire.MOD_ID, "bf_blocklight");

        setHardness(0.3F);

        setLightLevel(1.0F);

        setUnlocalizedName("bf_blocklight");

        setTickRandomly(true);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }

    /**
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
