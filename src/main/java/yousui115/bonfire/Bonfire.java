package yousui115.bonfire;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import yousui115.bonfire.entity.EntityBonfire;
import yousui115.bonfire.util.BfBlocks;
import yousui115.bonfire.util.BfItems;

@Mod(modid = Bonfire.MOD_ID, name = Bonfire.MOD_NAME, version = Bonfire.VERSION)
@EventBusSubscriber
public class Bonfire
{
    //■
    public static final String MOD_ID = "bonfire";
    public static final String MOD_DOMAIN = "yousui115." + MOD_ID;

    public static final String MOD_NAME = "Bonfire";

    public static final String VERSION = "M1122_F2611_v1";

    //■インスタント
    @Mod.Instance(MOD_ID)
    public static Bonfire INSTANCE;

    //■ロガー
    private static Logger logger;

    //■クライアント側とサーバー側で異なるインスタンスを生成
    @SidedProxy(clientSide = MOD_DOMAIN + ".client.ClientProxy", serverSide = MOD_DOMAIN + ".CommonProxy")
    public static CommonProxy proxy;



    /**
     * ■初期化処理 前処理
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //■ろがー
        logger = event.getModLog();

        //■Renderの生成 と Entity <-> Render の関連性登録
        proxy.registerRenderer();
    }

    /**
     * ■アイテムの登録
     * @param event
     */
    @SubscribeEvent
    protected static void registerItem(RegistryEvent.Register<Item> event)
    {
        //■アイテムの生成と登録
        BfItems.create();
        BfItems.registerItem(event);
    }

    /**
     * ■
     * @param event
     */
    @SubscribeEvent
    protected static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        BfBlocks.create();
        BfBlocks.register(event);
    }

    /**
     * ■モデルの登録
     * @param event
     */
    @SubscribeEvent
    public static void registerItemModel(ModelRegistryEvent event)
    {
        proxy.registerItemModel();
    }

    @SubscribeEvent
    public static void entityRegistration(final RegistryEvent.Register<EntityEntry> event)
    {
        event.getRegistry().register(
                EntityEntryBuilder.create()
                    .entity(EntityBonfire.class)
                    .id(new ResourceLocation(Bonfire.MOD_ID, "bf_bonfire"), 1)
                    .name("bf_bonfire")
                    .tracker(160, 2, false)
//                    .egg(0xffffff, 0xffffff)
//                    .spawn(EnumCreatureType.MONSTER, 10, 1, 1, ForgeRegistries.BIOMES.getValues())
                    .build()
            );
    }

    public static void logout(String str)
    {
        logger.error(str);
    }
}
