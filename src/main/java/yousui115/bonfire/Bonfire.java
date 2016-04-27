package yousui115.bonfire;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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
import yousui115.bonfire.item.crafting.RecipesStandard;

@Mod(modid = Bonfire.MOD_ID, version = Bonfire.VERSION, useMetadata = true)
public class Bonfire
{
    //■固定文字列
    public static final String MOD_ID = "bonfire";
    public static final String MOD_DOMAIN = "yousui115." + MOD_ID;
    public static final String VERSION = "1.0";

    //■インスタント
    @Mod.Instance(MOD_ID)
    public static Bonfire INSTANCE;

    //■クライアント側とサーバー側で異なるインスタンスを生成
    @SidedProxy(clientSide = MOD_DOMAIN + ".client.ClientProxy", serverSide = MOD_DOMAIN + ".CommonProxy")
    public static CommonProxy proxy;

    //■追加アイテム
    public static Item itemBonfire;
    public static String nameBonfire = "bf_bonfire";
    public static ResourceLocation rlBonfire;

    //■追加ブロック
    public static Block blockLight;
    public static String nameLight = "bf_blocklight";
    public static ResourceLocation rlLight;

    /**
     * ■初期化処理（前処理）
     * @param event
     */
    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        //■アイテムの生成と登録
        itemBonfire = new ItemBonfire().setUnlocalizedName(nameBonfire);
        rlBonfire = new ResourceLocation(MOD_ID, nameBonfire);
        GameRegistry.register(itemBonfire, rlBonfire);

        //■ブロックの生成と登録
        blockLight = new BlockLight(Material.circuits)
                            .setHardness(0.3F)
                            .setLightLevel(1.0F)
                            .setUnlocalizedName(nameLight);
        rlLight = new ResourceLocation(MOD_ID, nameLight);
        GameRegistry.register(blockLight, rlLight);

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

    @EventHandler
    public void post(FMLPostInitializationEvent event)
    {
    }
}
