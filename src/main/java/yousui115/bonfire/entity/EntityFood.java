package yousui115.bonfire.entity;

import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class EntityFood extends EntityBroilItem
{
    //■最大調理時間
    public static final int BROIL_TIME_COOKED = 400;
    public static final int BROIL_TIME_BURNT  = 600;

//    //■データウォッチャー用パラメータ
//    public static final DataParameter<Integer> DIRECTION = EntityDataManager.<Integer>createKey(EntityFood.class, DataSerializers.VARINT);
//    public static final DataParameter<Integer> BROIL_TIME = EntityDataManager.<Integer>createKey(EntityFood.class, DataSerializers.VARINT);
//    public static final DataParameter<Optional<ItemStack>> FOOD_ITEMSTACK = EntityDataManager.<Optional<ItemStack>>createKey(EntityFood.class, DataSerializers.OPTIONAL_ITEM_STACK);

    //■食材の状態変異Map
    public static Map<Pair<RegistryDelegate<Item>, Integer>, List<ItemStack>> mapFoodState = com.google.common.collect.Maps.newHashMap();

    /**
     * ■コンストラクタ
     */
    public EntityFood(World worldIn) { super(worldIn); }

    /**
     * ■コンストラクタ
     */
    public EntityFood(World world, double dX, double dY, double dZ, int nDir, ItemStack stackIn)
    {
        super(world, dX, dY, dZ, nDir, stackIn);
    }

    /**
     * ■
     */
    @Override
    protected void entityInit()
    {
        //■サイズ設定
        setSize(0.25F, 0.5F);

        super.entityInit();
    }

    @Override
    public void onUpdate()
    {
        //TODO EntityBonfireをメンバに持った方がいいかな
        EntityBonfire entityBF = getBonfire();
        if (entityBF == null)
        {
            //■依存先のBonfireが居ないので消滅
            setEntityDead(this.posX, this.posY, this.posZ);
        }
        else if (this.getBroilTime() < BROIL_TIME_BURNT && entityBF.getNowWoodState().isFire())
        {
            //■調理時間の加算
            this.setBroilTime(this.getBroilTime() + 1);
        }
    }

    /**
     * ■調理時間による、状態変化した食べ物の取得
     */
    @Override
    public ItemStack getNowItemStack()
    {
        List<ItemStack> stacks = getFoodStateList(getBroilItem());

        if (stacks != null)
        {
            if (getBroilTime() >= BROIL_TIME_BURNT)
            {
                return stacks.get(2);
            }
            else if (getBroilTime() >= BROIL_TIME_COOKED)
            {
                return stacks.get(1);
            }
            else
            {
                return getBroilItem();
            }
        }

        //■予防線
        return new ItemStack(Items.STICK);
    }

    /**
     * ■焚き火に設置できる食べ物か否か
     */
    public static boolean canBroilFood(ItemStack stackIn)
    {
        return getFoodStateList(stackIn) != null;
    }

    /**
     * ■設置した食べ物の状態リスト(0:生 1:おいしそう 2:焦げ)
     */
    public static List<ItemStack> getFoodStateList(ItemStack key)
    {
        boolean is = mapFoodState.containsKey(Pair.of(key.getItem().delegate, key.getMetadata()));

        if (is)
        {
            return mapFoodState.get(Pair.of(key.getItem().delegate, key.getMetadata()));
        }

        return null;
    }

    /**
     * ■調理する食べ物の状態リストへの登録
     */
    public static void registItemState(Item itemRaw, int sub_Raw, Item itemCooked, int sub_Cooked, Item itemBurnt, int sub_Burnt)
    {
        registItemState(new ItemStack(itemRaw,    1, sub_Raw),
                        new ItemStack(itemCooked, 1, sub_Cooked),
                        new ItemStack(itemBurnt,  1, sub_Burnt));
    }

    /**
     * ■調理する食べ物の状態リストへの登録
     */
    public static void registItemState(ItemStack stackRaw, ItemStack stackCooked, ItemStack stackBurnt)
    {
        if (stackRaw == null || stackCooked == null || stackBurnt == null) { return; }

        List<ItemStack> listStacks = Lists.newArrayList(stackRaw, stackCooked, stackBurnt);

        mapFoodState.put(Pair.of(stackRaw.getItem().delegate, stackRaw.getMetadata()), listStacks);
    }
}
