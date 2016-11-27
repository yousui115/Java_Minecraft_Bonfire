package yousui115.bonfire.entity;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class EntityFood extends Entity
{
    //■最大調理時間
    public static final int BROIL_TIME_COOKED = 400;
    public static final int BROIL_TIME_BURNT  = 600;

    //■データウォッチャー用パラメータ
    public static final DataParameter<Integer> DIRECTION = EntityDataManager.<Integer>createKey(EntityFood.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> BROIL_TIME = EntityDataManager.<Integer>createKey(EntityFood.class, DataSerializers.VARINT);
    public static final DataParameter<Optional<ItemStack>> FOOD_ITEMSTACK = EntityDataManager.<Optional<ItemStack>>createKey(EntityFood.class, DataSerializers.OPTIONAL_ITEM_STACK);

    //■食材の状態変異Map
//    public static HashMap<String, List<ItemStack>> mapFoodState = Maps.newHashMap();
    public static Map<Pair<RegistryDelegate<Item>, Integer>, List<ItemStack>> mapFoodState = com.google.common.collect.Maps.newHashMap();

    /**
     * ■コンストラクタ
     */
    public EntityFood(World worldIn)
    {
        super(worldIn);
    }

    /**
     * ■コンストラクタ
     */
    public EntityFood(World world, double dX, double dY, double dZ, int nDir, ItemStack foodIn)
    {
        this(world);

        ItemStack copyStack = foodIn.copy();
        copyStack.stackSize = 1;

        initStickFood(true, dX, dY, dZ, nDir, 0, copyStack);
    }

    public void initStickFood(boolean isFirst, double dX, double dY, double dZ, int nDir, int nTime, ItemStack foodIn)
    {
        //■DataWatcher
        this.setDirection(nDir);
        this.setBroilTime(nTime);
        this.setItemStack(foodIn);

        //■初期位置・初期角度等の設定
        double ddX = dX;
        double ddZ = dZ;
        if (isFirst)
        {
            double d = 0.4;
            if (nDir == 0) { ddX += d; }
            else if (nDir == 1) { ddZ -= d; }
            else if (nDir == 2) { ddX -= d; }
            else {ddZ += d; }
        }
        setLocationAndAngles(ddX, dY, ddZ, rotationYaw, rotationPitch);
    }

    /**
     * ■
     */
    @Override
    protected void entityInit()
    {
        //■サイズ設定
        setSize(0.25F, 0.5F);

        //■ItemStack保持用DataWatcher領域の確保
        this.getDataManager().register(DIRECTION, 0);
        this.getDataManager().register(BROIL_TIME, 0);
        this.getDataManager().register(FOOD_ITEMSTACK, Optional.<ItemStack>absent());
    }

    @Override
    public void onUpdate()
    {
        //TODO EntityBonfireをメンバに持った方がいいかな
        EntityBonfire entityBF = getBonfire();
        if (entityBF == null)
        {
            //■依存先のBonfireが居ないので消滅
            setFoodDead(this.posX, this.posY, this.posZ);
        }
        else if (this.getBroilTime() < BROIL_TIME_BURNT && entityBF.getNowWoodState().isFire())
        {
            //■調理時間の加算
            this.setBroilTime(this.getBroilTime() + 1);
        }
    }

    //■当り判定が仕事するか否か
    @Override
    public boolean canBeCollidedWith() { return true; }


    //■NBTタグ名
    protected static final String NBTTAG_DIRECTION = "Direction";
    protected static final String NBTTAG_BROIL_TIME = "BroilTime";
    protected static final String NBTTAG_ITEM = "ItemFood";
    /**
     * ■ロード
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        //■位置
        posX = nbt.getDouble("PosX");
        posY = nbt.getDouble("PosY");
        posZ = nbt.getDouble("PosZ");

        //■調理時間
        int nTime = nbt.getInteger(NBTTAG_BROIL_TIME);

        //■方角
        int nDir = nbt.getInteger(NBTTAG_DIRECTION);

        //■調理アイテム
        NBTTagCompound tagItem = nbt.getCompoundTag(NBTTAG_ITEM);

        //■設定
        initStickFood(false, posX, posY, posZ, nDir, nTime, ItemStack.loadItemStackFromNBT(tagItem));

        //■確認
        ItemStack item = this.getDataManager().get(this.FOOD_ITEMSTACK).orNull();
        if (item == null || item.stackSize <= 0) this.setDead();
    }

    /**
     * ■セーブ
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        //■位置
        nbt.setDouble("PosX", posX);
        nbt.setDouble("PosY", posY);
        nbt.setDouble("PosZ", posZ);

        //■調理時間
        nbt.setInteger(NBTTAG_BROIL_TIME, getBroilTime());

        //■方角
        nbt.setInteger(NBTTAG_DIRECTION, getDirection());

        //■ItemStack
        if (this.getItemStack() != null)
        {
            nbt.setTag(NBTTAG_ITEM, this.getItemStack().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer playerIn, ItemStack stackIn, EnumHand handIn)
    {
        setFoodDead(playerIn.posX, playerIn.posY, playerIn.posZ);
        return true;
    }

    /* ======================================== イカ、自作 =====================================*/


    /**
     * ■その場に留まる事が出来るかどうか
     */
    public EntityBonfire getBonfire()
    {
        EntityBonfire entityBF = null;
        List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
        for(int idx = 0; idx < entities.size(); idx++)
        {
            if (!(entities.get(idx) instanceof EntityBonfire)) { continue; }
            entityBF = (EntityBonfire)entities.get(idx);
            break;
        }
        return entityBF;
    }

    /**
     * ■消滅処理
     */
    public void setFoodDead(double dX, double dY, double dZ)
    {
        this.setDead();

        ItemStack itemstack = getNowItemStack().copy();
        if (itemstack != null && !this.worldObj.isRemote)
        {
            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, dX, dY, dZ, itemstack));
        }
    }

    public ItemStack getNowItemStack()
    {
        List<ItemStack> stacks = getFoodStateList(getItemStack());

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
                return getItemStack();
            }
        }

        //■予防線
        return new ItemStack(Items.STICK);
    }

    public static boolean canBroilFood(ItemStack stackIn)
    {
        return getFoodStateList(stackIn) != null;
    }

    public static List<ItemStack> getFoodStateList(ItemStack key)
    {
        boolean is = mapFoodState.containsKey(Pair.of(key.getItem().delegate, key.getMetadata()));

        if (is)
        {
            return mapFoodState.get(Pair.of(key.getItem().delegate, key.getMetadata()));
        }

        return null;
    }

    public static void registItemState(Item itemRaw, int sub_Raw, Item itemCooked, int sub_Cooked, Item itemBurnt, int sub_Burnt)
    {
        registItemState(new ItemStack(itemRaw,    1, sub_Raw),
                        new ItemStack(itemCooked, 1, sub_Cooked),
                        new ItemStack(itemBurnt,  1, sub_Burnt));
    }

    public static void registItemState(ItemStack stackRaw, ItemStack stackCooked, ItemStack stackBurnt)
    {
        if (stackRaw == null || stackCooked == null || stackBurnt == null) { return; }

        List<ItemStack> listStacks = Lists.newArrayList(stackRaw, stackCooked, stackBurnt);

        mapFoodState.put(Pair.of(stackRaw.getItem().delegate, stackRaw.getMetadata()), listStacks);
    }
    /* ======================================== setter, getter =====================================*/

    /**
     * ■設置されている方角
     */
    public int getDirection()
    {
        return this.getDataManager().get(DIRECTION);
    }
    public void setDirection(int nDir)
    {
        this.getDataManager().set(DIRECTION, nDir);
        this.getDataManager().setDirty(DIRECTION);
    }

    /**
     * ■設置されている食べ物の調理時間
     */
    public int getBroilTime()
    {
        return this.getDataManager().get(BROIL_TIME);
    }
    public void setBroilTime(int nTime)
    {
        this.getDataManager().set(BROIL_TIME, nTime);
        this.getDataManager().setDirty(BROIL_TIME);
    }

    /**
     * ■設置されているItemStack
     */
    public ItemStack getItemStack()
    {
        return this.getDataManager().get(FOOD_ITEMSTACK).orNull();
    }
    public void setItemStack(ItemStack stack)
    {
        this.getDataManager().set(FOOD_ITEMSTACK, Optional.fromNullable(stack));
        this.getDataManager().setDirty(FOOD_ITEMSTACK);
    }

}
