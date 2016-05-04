package yousui115.bonfire;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
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
import yousui115.bonfire.item.ItemBonfire;
import yousui115.bonfire.item.ItemFoodB;

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

    public static Item itemCookedFood;
    public static String nameCookedFood = "bf_cooked_food";
    public static ResourceLocation rlCookedFood;

    public static Item itemBurntFood;
    public static String nameBurntFood = "bf_burnt_food";
    public static ResourceLocation rlBurntFood;

    //■追加ブロック
    public static Block blockLight;
    public static String nameLight = "bf_blocklight";
//    public static ResourceLocation rlLight;
    public static Item itemBlockLight;

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
                          .setUnlocalizedName(nameBonfire);
        rlBonfire = new ResourceLocation(MOD_ID, nameBonfire);
        GameRegistry.register(itemBonfire, rlBonfire);

        // ▼調理食材(おいしそう)
        itemCookedFood = new ItemFoodB(4, 0.6f, false)
                               .setPotionEffect(new PotionEffect(MobEffects.saturation, 600, 0), 0.8F)
                               .setUnlocalizedName(nameCookedFood)
                               .setHasSubtypes(true);
        rlCookedFood = new ResourceLocation(MOD_ID, nameCookedFood);
        GameRegistry.register(itemCookedFood, rlCookedFood);

        // ▼調理食材(まずそう)
        itemBurntFood = new ItemFoodB(2, 0.1f, false)
                                .setPotionEffect(new PotionEffect(MobEffects.hunger, 300, 0), 0.8f)
                                .setUnlocalizedName(nameBurntFood)
                                .setHasSubtypes(true);
        rlBurntFood = new ResourceLocation(MOD_ID, nameBurntFood);
        GameRegistry.register(itemBurntFood, rlBurntFood);

        //■ブロックの生成と登録
        blockLight = new BlockLight(Material.circuits)
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
        GameRegistry.addRecipe(new ItemStack(Bonfire.itemBonfire),
                               "##",
                               "##",
                               '#', Items.stick
                              );

//        //■レンダラ登録
//        proxy.registerRenderers();

        //■アイテム色の登録
        proxy.registerItemColor();

    }

    @EventHandler
    public void post(FMLPostInitializationEvent event)
    {
        //TODO:ItemFoodBのEnumと連携させよう。でないと表示が狂う可能性あり
        //■焚き火での調理可能食材情報登録(順番厳守)
        // ▼ここで食材(生・おいしそう・まずそう)を設定してやれば、なんでも焼ける
        EntityFood.registItemState(Items.beef,     0, Bonfire.itemCookedFood, 0, Bonfire.itemBurntFood, 0);
        EntityFood.registItemState(Items.porkchop, 0, Bonfire.itemCookedFood, 1, Bonfire.itemBurntFood, 1);
        EntityFood.registItemState(Items.chicken,  0, Bonfire.itemCookedFood, 2, Bonfire.itemBurntFood, 2);
        EntityFood.registItemState(Items.mutton,   0, Bonfire.itemCookedFood, 3, Bonfire.itemBurntFood, 3);
        EntityFood.registItemState(Items.rabbit,   0, Bonfire.itemCookedFood, 4, Bonfire.itemBurntFood, 4);
        EntityFood.registItemState(Items.fish, ItemFishFood.FishType.COD.getMetadata(),    Bonfire.itemCookedFood, 5, Bonfire.itemBurntFood, 5);
        EntityFood.registItemState(Items.fish, ItemFishFood.FishType.SALMON.getMetadata(), Bonfire.itemCookedFood, 6, Bonfire.itemBurntFood, 6);
    }
}
