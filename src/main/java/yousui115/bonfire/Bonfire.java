package yousui115.bonfire;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import yousui115.bonfire.entity.EntityBonfire;
import yousui115.bonfire.entity.EntityFood;
import yousui115.bonfire.entity.EntityPot;
import yousui115.bonfire.event.EventBonfire;
import yousui115.bonfire.item.ItemFoodB.EnumBroilFood;
import yousui115.bonfire.item.ItemPot.EnumPotState;
import yousui115.bonfire.recipe.RecipesPotWater;
import yousui115.bonfire.recipe.RecipesWBottle;
import yousui115.bonfire.util.BfBlocks;
import yousui115.bonfire.util.BfItems;
import yousui115.bonfire.util.BfLootTable;
import yousui115.bonfire.util.BfSounds;

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
     * ■初期化処理（前処理）
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //■ろがー
        logger = event.getModLog();

        //■Renderの生成 と Entity <-> Render の関連性登録
        proxy.registerRenderer();

        //■ルーティングテーブルの登録
        BfLootTable.create();
        BfLootTable.register();
    }

    /**
     * ■初期化処理（後処理）
     * @param event
     */
    @EventHandler
    public void post(FMLPostInitializationEvent event)
    {
        //■焚き火での調理可能食材情報登録(順番厳守)
        EntityFood.registItemState(Items.BEEF,     0, Items.COOKED_BEEF,     0, BfItems.BURNT_FOOD, 0);
        EntityFood.registItemState(Items.PORKCHOP, 0, Items.COOKED_PORKCHOP, 0, BfItems.BURNT_FOOD, 1);
        EntityFood.registItemState(Items.CHICKEN,  0, Items.COOKED_CHICKEN,  0, BfItems.BURNT_FOOD, 2);
        EntityFood.registItemState(Items.MUTTON,   0, Items.COOKED_MUTTON,   0, BfItems.BURNT_FOOD, 3);
        EntityFood.registItemState(Items.RABBIT,   0, Items.COOKED_RABBIT,   0, BfItems.BURNT_FOOD, 4);
        //■魚は特別扱い。だって好きなんだもの。
        EntityFood.registItemState(Items.FISH, ItemFishFood.FishType.COD.getMetadata(),
                                   BfItems.COOK_FOOD, EnumBroilFood.COD.ordinal(),
                                   BfItems.BURNT_FOOD, EnumBroilFood.COD.ordinal());
        EntityFood.registItemState(Items.FISH, ItemFishFood.FishType.SALMON.getMetadata(),
                                   BfItems.COOK_FOOD,  EnumBroilFood.SALMON.ordinal(),
                                   BfItems.BURNT_FOOD, EnumBroilFood.SALMON.ordinal());

        //■アイテム色の登録
        proxy.registerItemColor();

        //■さうんどの生成
        BfSounds.create();

        MinecraftForge.EVENT_BUS.register(new EventBonfire());

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
     * ■ブロックの登録
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

    /**
     * ■エンティティの登録
     * @param event
     */
    @SubscribeEvent
    public static void entityRegistration(final RegistryEvent.Register<EntityEntry> event)
    {
        //■たき火
        event.getRegistry().register(
                EntityEntryBuilder.create()
                    .entity(EntityBonfire.class)
                    .id(new ResourceLocation(Bonfire.MOD_ID, "bf_bonfire"), 1)
                    .name("bf_bonfire")
                    .tracker(160, 5, false)
//                    .egg(0xffffff, 0xffffff)
//                    .spawn(EnumCreatureType.MONSTER, 10, 1, 1, ForgeRegistries.BIOMES.getValues())
                    .build()
            );

        event.getRegistry().register(
                EntityEntryBuilder.create()
                .entity(EntityFood.class)
                .id(new ResourceLocation(Bonfire.MOD_ID, "bf_food"), 2)
                .name("bf_food")
                .tracker(160, 5, false)
                .build()
        );

        event.getRegistry().register(
                EntityEntryBuilder.create()
                .entity(EntityPot.class)
                .id(new ResourceLocation(Bonfire.MOD_ID, "bf_pot"), 3)
                .name("bf_pot")
                .tracker(160, 5, false)
                .build()
        );
    }

    /**
     * ■レシピの登録
     * @param event
     */
    @SubscribeEvent
    protected static void registerRecipe(RegistryEvent.Register<IRecipe> event)
    {
        //■水筒（補充）
        IRecipe recipe = new RecipesWBottle(new ResourceLocation(MOD_ID, "bf_recipe_wate_bottle"),
                                            new ItemStack(BfItems.WATER_BOTTLE),
                                            new ItemStack(BfItems.POT, 1, EnumPotState.BOILED.ordinal()), BfItems.WATER_BOTTLE);
        recipe.setRegistryName(new ResourceLocation(MOD_ID, "bf_recipe_wate_bottle_supplement"));
        event.getRegistry().register(recipe);

        //■ポット（補充）
        IRecipe recipe2 = new RecipesPotWater(new ResourceLocation(MOD_ID, "bf_recipe_pot"),
                                        new ItemStack(BfItems.POT, 1, EnumPotState.WATER.ordinal()),
                                        new ItemStack(BfItems.POT, 1, EnumPotState.EMPTY.ordinal()), Items.WATER_BUCKET);
        recipe2.setRegistryName(new ResourceLocation(MOD_ID, "bf_recipe_pot_supplement"));
        event.getRegistry().register(recipe2);

    }



    public static void logout(String str)
    {
//        logger.error(str);
    }
}
