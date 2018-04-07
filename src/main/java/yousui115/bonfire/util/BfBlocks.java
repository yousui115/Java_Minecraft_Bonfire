package yousui115.bonfire.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import yousui115.bonfire.block.BlockLight;

public class BfBlocks
{
    public static Block blockLight;

    /**
     * ■生成
     */
    public static void create()
    {
        //■たき火の光源
        blockLight = new BlockLight(Material.CIRCUITS);
    }

    /**
     * ■登録
     * @param event
     */
    public static void register(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockLight);
    }
}
