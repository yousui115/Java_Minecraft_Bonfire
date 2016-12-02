package yousui115.bonfire.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import com.google.common.base.Optional;

public class EntityBroilItem extends Entity
{
    //■データウォッチャー用パラメータ
    public static final DataParameter<Integer> DIRECTION = EntityDataManager.<Integer>createKey(EntityBroilItem.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> BROIL_TIME = EntityDataManager.<Integer>createKey(EntityBroilItem.class, DataSerializers.VARINT);
    public static final DataParameter<Optional<ItemStack>> BROIL_ITEM = EntityDataManager.<Optional<ItemStack>>createKey(EntityBroilItem.class, DataSerializers.OPTIONAL_ITEM_STACK);

    /**
     * ■コンストラクタ
     */
    public EntityBroilItem(World worldIn) { super(worldIn); }

    /**
     * ■コンストラクタ
     */
    public EntityBroilItem(World worldIn, double dX, double dY, double dZ, int nDir, ItemStack stackIn)
    {
        this(worldIn);

        ItemStack copyStack = stackIn.copy();
        copyStack.stackSize = 1;

        init(true, dX, dY, dZ, nDir, 0, copyStack);
    }

    /**
     * ■初期化処理
     */
    public void init(boolean isFirst, double dX, double dY, double dZ, int nDir, int nTime, ItemStack stackIn)
    {
        //■DataWatcher
        this.setDirection(nDir);
        this.setBroilTime(nTime);
        this.setBroilItem(stackIn);


        //■初期位置・初期角度等の設定
        double ddX = dX;
        double ddZ = dZ;
        if (isFirst)
        {
            double d = dist();
            if (nDir == 0) { ddX += d; }
            else if (nDir == 1) { ddZ -= d; }
            else if (nDir == 2) { ddX -= d; }
            else {ddZ += d; }
        }
        setLocationAndAngles(ddX, dY, ddZ, rotationYaw, rotationPitch);
    }

    public double dist() { return 0.4; }

    /**
     * ■
     */
    @Override
    protected void entityInit()
    {
        //■DataWatcher領域の確保
        this.getDataManager().register(DIRECTION, 0);
        this.getDataManager().register(BROIL_TIME, 0);
        this.getDataManager().register(BROIL_ITEM, Optional.<ItemStack>absent());
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
//        else if (this.getBroilTime() < BROIL_TIME_BURNT && entityBF.getNowWoodState().isFire())
//        {
//            //■調理時間の加算
//            this.setBroilTime(this.getBroilTime() + 1);
//        }
    }

    /**
     * ■当り判定が仕事するか否か
     */
    @Override
    public boolean canBeCollidedWith() { return true; }

    //■NBTタグ名
    protected static final String NBTTAG_DIRECTION = "Direction";
    protected static final String NBTTAG_BROIL_TIME = "BroilTime";
    protected static final String NBTTAG_BROIL_ITEM = "BroilItem";

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
        NBTTagCompound tagItem = nbt.getCompoundTag(NBTTAG_BROIL_ITEM);

        //■設定
        init(false, posX, posY, posZ, nDir, nTime, ItemStack.loadItemStackFromNBT(tagItem));

        //■確認
        ItemStack item = this.getDataManager().get(BROIL_ITEM).orNull();
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
        if (getBroilItem() != null)
        {
            nbt.setTag(NBTTAG_BROIL_ITEM, getBroilItem().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer playerIn, ItemStack stackIn, EnumHand handIn)
    {
        setEntityDead(playerIn.posX, playerIn.posY, playerIn.posZ);
        return true;
    }

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
            if (entityBF.getNowWoodState().isFire()) { break; }
        }
        return entityBF;
    }

    /**
     * ■消滅処理
     */
    public void setEntityDead(double dX, double dY, double dZ)
    {
        this.setDead();

        ItemStack itemstack = getNowItemStack().copy();
        if (itemstack != null && !this.worldObj.isRemote)
        {
            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, dX, dY, dZ, itemstack));
        }
    }

    /**
     *
     * @return
     */
    public ItemStack getNowItemStack()
    {
        return getBroilItem();
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
        nDir = MathHelper.clamp_int(nDir, 0, 3);
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
        nTime = MathHelper.clamp_int(nTime, 0, Integer.MAX_VALUE - 1);
        this.getDataManager().set(BROIL_TIME, nTime);
        this.getDataManager().setDirty(BROIL_TIME);
    }

    /**
     * ■設置されているItemStack
     */
    public ItemStack getBroilItem()
    {
        return this.getDataManager().get(BROIL_ITEM).orNull();
    }
    public void setBroilItem(ItemStack stack)
    {
        this.getDataManager().set(BROIL_ITEM, Optional.fromNullable(stack));
        this.getDataManager().setDirty(BROIL_ITEM);
    }

}
