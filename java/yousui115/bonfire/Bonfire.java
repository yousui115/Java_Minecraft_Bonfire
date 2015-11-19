package yousui115.bonfire;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yousui115.bonfire.block.BlockLight;
import yousui115.bonfire.entity.EntityBonfire;
import yousui115.bonfire.event.EventHooks;
import yousui115.bonfire.item.ItemBonfire;
import yousui115.bonfire.item.ItemLightBlock;
import yousui115.bonfire.item.crafting.RecipesStandard;

@Mod(modid = Bonfire.MOD_ID, version = Bonfire.VERSION, useMetadata = true)
public class Bonfire
{
    //■固定文字列
    public static final String MOD_DOMAIN = "yousui115.bonfire";
    public static final String MOD_ID = "Bonfire";
    public static final String VERSION = "1.0";

    //■インスタント
    @Mod.Instance(MOD_ID)
    public static Bonfire INSTANCE;

    //■クライアント側とサーバー側で異なるインスタンスを生成
    @SidedProxy(clientSide = MOD_DOMAIN + ".client.ClientProxy", serverSide = MOD_DOMAIN + ".CommonProxy")
    public static CommonProxy proxy;

    //■追加アイテム
    public static Item itemBonfire;

    //■追加ブロック
    public static Block blockLight;

    /**
     * ■初期化処理（前処理）
     * @param event
     */
    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        //■アイテム系の処理
        // ▼アイテムインスタンスの作成。
        itemBonfire = new ItemBonfire().setUnlocalizedName("ItemBonfire");

        // ▼アイテムの登録。登録文字列はMOD内で被らなければ何でも良い。
        GameRegistry.registerItem(itemBonfire, "ItemBonfire");

        //■ブロック系の処理
        // ▼ブロックインスタンスの作成
        blockLight = new BlockLight(Material.circuits)
                            .setHardness(0.3F)
                            .setLightLevel(1.0F)
                            .setUnlocalizedName("bf_blocklight");

        // ▼ブロックの登録。(最後の文字列がblockstateのjsonファイル名)
        GameRegistry.registerBlock(blockLight, ItemLightBlock.class, "bf_blocklight");

        //■モデル登録
        proxy.registerModels();

        //■Entityの登録
        EntityRegistry.registerModEntity(EntityBonfire.class, "EntityBonfire", 0, this, 250, 5, false);
    }

    /**
     * ■初期化処理（本処理）
     * @param event
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        //■レシピ登録
        RecipesStandard.addRecipes();

        //■レンダラ登録
        proxy.registerRenderers();

        //■イベントの追加
        MinecraftForge.EVENT_BUS.register(new EventHooks());
    }

    /**
     * ■初期化処理（後処理）
     * @param event
     */
    @EventHandler
    public void postinit(FMLPostInitializationEvent event)
    {
    }
}
