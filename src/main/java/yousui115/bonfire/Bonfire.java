package yousui115.bonfire;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
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
import yousui115.bonfire.entity.EntityFood;
import yousui115.bonfire.entity.EntityPot;
import yousui115.bonfire.item.ItemBonfire;
import yousui115.bonfire.item.ItemFoodB;
import yousui115.bonfire.item.ItemPot;
import yousui115.bonfire.item.ItemWBottle;
import yousui115.bonfire.item.RecipesWBottle;

@Mod(modid = Bonfire.MOD_ID, version = Bonfire.VERSION, useMetadata = true)
public class Bonfire
{
    //■固定文字列
    public static final String MOD_ID = "bonfire";
    public static final String MOD_DOMAIN = "yousui115." + MOD_ID;
    public static final String VERSION = "M1102_F2099_v3";

    //■インスタント
    @Mod.Instance(MOD_ID)
    public static Bonfire INSTANCE;

    //■クライアント側とサーバー側で異なるインスタンスを生成
    @SidedProxy(clientSide = MOD_DOMAIN + ".client.ClientProxy", serverSide = MOD_DOMAIN + ".CommonProxy")
    public static CommonProxy proxy;

    //■追加アイテム
    // ▼焚き火
    public static Item itemBonfire;
    public static String nameBonfire = "bf_bonfire";
    public static ResourceLocation rlBonfire;

    // ▼食料（おいしそう）
    public static Item itemCookedFood;
    public static String nameCookedFood = "bf_cooked_food";
    public static ResourceLocation rlCookedFood;

    // ▼食料（まずそう）
    public static Item itemBurntFood;
    public static String nameBurntFood = "bf_burnt_food";
    public static ResourceLocation rlBurntFood;

    // ▼ポット
    public static Item itemPot;
    public static String namePot = "bf_pot";
    public static ResourceLocation rlPot;

    // ▼水筒
    public static Item itemWBottle;
    public static String nameWBottle = "bf_water_bottle";
    public static ResourceLocation rlWBottle;

    //■追加ブロック
    public static Block blockLight;
    public static String nameLight = "bf_blocklight";
//    public static ResourceLocation rlLight;
    public static Item itemBlockLight;

    //TODO:ここで持つべきか否か
    //■効果音
    public static SoundEvent Gutsu;
    public static SoundEvent Kata;

    /**
     * ■初期化処理（前処理）
     * @param event
     */
    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        //■アイテムの生成と登録
        // ▼焚き火
        itemBonfire = new ItemBonfire()
                          .setUnlocalizedName(nameBonfire)
                          .setCreativeTab(CreativeTabs.MATERIALS);
        rlBonfire = new ResourceLocation(MOD_ID, nameBonfire);
        GameRegistry.register(itemBonfire, rlBonfire);

        // ▼調理食材(おいしそう)
        itemCookedFood = new ItemFoodB(4, 0.6f, false)
                               .setPotionEffect(new PotionEffect(MobEffects.SATURATION, 600, 0), 0.8F)
                               .setUnlocalizedName(nameCookedFood)
                               .setHasSubtypes(true);
        rlCookedFood = new ResourceLocation(MOD_ID, nameCookedFood);
        GameRegistry.register(itemCookedFood, rlCookedFood);

        // ▼調理食材(まずそう)
        itemBurntFood = new ItemFoodB(2, 0.1f, false)
                                .setPotionEffect(new PotionEffect(MobEffects.HUNGER, 300, 0), 0.8f)
                                .setUnlocalizedName(nameBurntFood)
                                .setHasSubtypes(true);
        rlBurntFood = new ResourceLocation(MOD_ID, nameBurntFood);
        GameRegistry.register(itemBurntFood, rlBurntFood);

        // ▼ポット
        itemPot = new ItemPot()
                        .setUnlocalizedName(namePot)
                        .setMaxStackSize(1)
                        .setCreativeTab(CreativeTabs.MATERIALS)
                        .setHasSubtypes(true).setContainerItem(itemPot);
        rlPot = new ResourceLocation(MOD_ID, namePot);
        GameRegistry.register(itemPot, rlPot);

        // ▼水筒
        itemWBottle = new ItemWBottle()
                            .setUnlocalizedName(nameWBottle)
                            .setMaxStackSize(1)
                            .setMaxDamage(100)
                            .setCreativeTab(CreativeTabs.FOOD);
        rlWBottle = new ResourceLocation(MOD_ID, nameWBottle);
        GameRegistry.register(itemWBottle, rlWBottle);

        //■ブロックの生成と登録
        blockLight = new BlockLight(Material.CIRCUITS)
                         .setHardness(0.3F)
                         .setLightLevel(1.0F)
                         .setUnlocalizedName(nameLight)
                         .setTickRandomly(true);
        itemBlockLight = new ItemBlock(blockLight);
        GameRegistry.register(blockLight,     new ResourceLocation(MOD_ID, nameLight));
        GameRegistry.register(itemBlockLight, blockLight.getRegistryName());


        //■Entityの登録
        EntityRegistry.registerModEntity(EntityBonfire.class, "EntityBonfire", 0, this, 250, 5, false);
        EntityRegistry.registerModEntity(EntityFood.class,    "EntityFood",    1, this, 250, 5, false);
        EntityRegistry.registerModEntity(EntityPot.class,     "EntityPot",     2, this, 250, 5, false);

        //■モデル登録
        proxy.registerModels();

        //■レンダラ登録
        proxy.registerRenderers();

    }

    /**
     * ■初期化処理（本処理）
     * @param event
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        //■レシピ登録
        // ▼焚き火
        GameRegistry.addRecipe(new ItemStack(Bonfire.itemBonfire),
                               "##",
                               "##",
                               '#', Items.STICK
                              );

        // ▼ポット
        GameRegistry.addRecipe(new ItemStack(Bonfire.itemPot, 1, 0),
                " # ",
                " #w",
                "## ",
                '#', Items.IRON_INGOT,
                'w', Items.STICK
               );

        // ▼ポット（水入り）
        GameRegistry.addShapelessRecipe(new ItemStack(Bonfire.itemPot, 1, 1),
                                        new ItemStack(Bonfire.itemPot, 1, 0),
                                        new ItemStack(Items.WATER_BUCKET));

        // ▼水筒
        ItemStack wbottle = new ItemStack(Bonfire.itemWBottle);
        wbottle.setItemDamage(99);
        GameRegistry.addRecipe(wbottle ,
                "w#",
                "##",
                'w', Blocks.PLANKS,
                '#', Items.LEATHER
               );

        // ▼水筒への水補充
        GameRegistry.addRecipe(new RecipesWBottle());

//        //■レンダラ登録
//        proxy.registerRenderers();

        //■アイテム色の登録
        proxy.registerItemColor();

        //■効果音の登録
        Gutsu = new SoundEvent(new ResourceLocation(this.MOD_ID, "gutsugutsu"));
        Kata  = new SoundEvent(new ResourceLocation(this.MOD_ID, "katakata"));
    }

    @EventHandler
    public void post(FMLPostInitializationEvent event)
    {
        //■焚き火での調理可能食材情報登録(順番厳守)
//        EntityFood.registItemState(Items.BEEF,     0, Bonfire.itemCookedFood, 0, Bonfire.itemBurntFood, 0);
//        EntityFood.registItemState(Items.PORKCHOP, 0, Bonfire.itemCookedFood, 1, Bonfire.itemBurntFood, 1);
//        EntityFood.registItemState(Items.CHICKEN,  0, Bonfire.itemCookedFood, 2, Bonfire.itemBurntFood, 2);
//        EntityFood.registItemState(Items.MUTTON,   0, Bonfire.itemCookedFood, 3, Bonfire.itemBurntFood, 3);
//        EntityFood.registItemState(Items.RABBIT,   0, Bonfire.itemCookedFood, 4, Bonfire.itemBurntFood, 4);
//        EntityFood.registItemState(Items.FISH, ItemFishFood.FishType.COD.getMetadata(),    Bonfire.itemCookedFood, 5, Bonfire.itemBurntFood, 5);
//        EntityFood.registItemState(Items.FISH, ItemFishFood.FishType.SALMON.getMetadata(), Bonfire.itemCookedFood, 6, Bonfire.itemBurntFood, 6);
        EntityFood.registItemState(Items.BEEF,     0, Items.COOKED_BEEF,     0, Bonfire.itemBurntFood, 0);
        EntityFood.registItemState(Items.PORKCHOP, 0, Items.COOKED_PORKCHOP, 0, Bonfire.itemBurntFood, 1);
        EntityFood.registItemState(Items.CHICKEN,  0, Items.COOKED_CHICKEN,  0, Bonfire.itemBurntFood, 2);
        EntityFood.registItemState(Items.MUTTON,   0, Items.COOKED_MUTTON,   0, Bonfire.itemBurntFood, 3);
        EntityFood.registItemState(Items.RABBIT,   0, Items.COOKED_RABBIT,   0, Bonfire.itemBurntFood, 4);
        EntityFood.registItemState(Items.FISH, ItemFishFood.FishType.COD.getMetadata(),    Items.COOKED_FISH, ItemFishFood.FishType.COD.getMetadata(),    Bonfire.itemBurntFood, 5);
        EntityFood.registItemState(Items.FISH, ItemFishFood.FishType.SALMON.getMetadata(), Items.COOKED_FISH, ItemFishFood.FishType.SALMON.getMetadata(), Bonfire.itemBurntFood, 6);

    }
}
