package yousui115.bonfire.entity;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yousui115.bonfire.Bonfire;
import yousui115.bonfire.util.BfBlocks;

public class EntityBonfire extends Entity
{
    public EntityBonfire.State state;

    protected static final DataParameter<Integer> TICK_FIRE = EntityDataManager.<Integer>createKey(EntityBonfire.class, DataSerializers.VARINT);
    protected static final DataParameter<String> STATE_NAME = EntityDataManager.<String>createKey(EntityBonfire.class, DataSerializers.STRING);

    //==============================================================

    /**
     * ■コンストラクタ
     */
    public EntityBonfire(World worldIn) { super(worldIn); }
    public EntityBonfire(World worldIn, double xIn, double yIn, double zIn)
    {
        this(worldIn);

        xIn += 0.5;
        zIn += 0.5;

        //■位置、回転角度の調整
        setLocationAndAngles(xIn, yIn, zIn, 0.0F, 0.0F);
    }

    /**
     * ■初期化処理
     */
    @Override
    protected void entityInit()
    {
        //■サイズ設定
        setSize(0.9F, 0.25F);

        //■データ (ﾉ∀`)ｳｫｯﾁｬｰ
        initDataManager();

        state = new EntityBonfire.White(0);
    }

    /**
     * ■メイン処理
     */
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        //■ジャーマネから取得
        getDataManagerLocal();

        //■存在できるかチェック
        if (canStay() == false)
        {
            this.setDead();
            return;
        }

        //■燃焼進行
        state.addTick();

        //■燃焼できる環境か否か
        if (canBurnEnv() == false)
        {
            state.isBurning = false;
        }

        //debug
        EntityBonfire.State temp = state;

        //■状態をチェックし、遷移する。
        state = state.nextState();

        //■光源の設置・撤去
        if (state.settingLight() == true)
        {
            setBlock(BfBlocks.blockLight);
        }
        else
        {
            setBlock(Blocks.AIR);
        }

        //■燃焼中のエフェクトやら
        if (state.isBurning == true)
        {
            //■パチパチ
            if (state.tick % 20 == 0)
            {
                soundFireAmbient();
            }

            //■モクモク
            if (rand.nextInt(10) < 5)
            {
                for(int l = 0; l < 3; l++)
                {
                    float fX = (float)posX + (0.5F - rand.nextFloat()) * 0.5F;
                    float fY = (float)posY +  0.7F + rand.nextFloat()  * 0.5F;
                    float fZ = (float)posZ + (0.5F - rand.nextFloat()) * 0.5F;
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL , fX, fY, fZ, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        //debug
        if (temp != state) { state.debuglog(); }

        //■ジャーマネに登録
        setDataManagerLocal();
    }

    /**
     * ■データマネージャー 初期化処理
     */
    public void initDataManager()
    {
        this.dataManager.register(TICK_FIRE, Integer.valueOf(0));
        this.dataManager.register(STATE_NAME,  "White");
    }

    /**
     * ■薪々はかよわいので、ダメージを貰ったら死にます
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if(!isDead && !world.isRemote)
        {
//            setBeenAttacked();
            setDead();
        }
        return true;
    }

    /**
     * 消滅処理
     */
    @Override
    public void setDead()
    {
        //■消滅フラグを立てる
        super.setDead();

        //■火がついてるなら、音が鳴る
        if (state.isBurning == true)
        {
            soundFizz();
        }

        if (!world.isRemote)
        {
            //■ブロックがあれば消滅させる
            this.setBlock(Blocks.AIR);

            //■状態に応じたドロップアイテムを顕現させる
            world.spawnEntity(new EntityItem(world, posX, posY, posZ, state.getItemStack()));
        }
    }

    /**
     * ■ブロック外への押し出しは有効か否か
     */
    @Override
    protected boolean pushOutOfBlocks(double x, double y, double z)
    {
        return false;
    }

    /**
     * ■当たり判定が仕事をするか否か
     */
    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * ■
     */
    @Override
    public boolean processInitialInteract(EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        return state.interact(stack);
    }

    /**
     * ■NBT読込
     * @param tagCompund
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound)
    {
        int tick = tagCompound.getInteger("tickFire");
        String name = tagCompound.getString("stateName");
        state = createStateInstance(name, tick);
        setDataManagerLocal();
    }

    /**
     * ■NBT書込
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        getDataManagerLocal();
        tagCompound.setInteger("tickFire", getTickFire());
        tagCompound.setString("stateName", getStateName());
    }

    //=====================================================================

    /**
     * ■生存可能条件の確認
     * @return
     */
    public boolean canStay()
    {
        int nX = MathHelper.floor(posX);
        int nY = MathHelper.floor(posY);
        int nZ = MathHelper.floor(posZ);

        //■接触してるEntityをリストアップしてチェック
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
        for (Entity entity : list)
        {
            //■EntityBonfireが既に置いてあるとfalse
            if (entity instanceof EntityBonfire) { return false; }
        }

        //■設置場所にBonfireBlock以外のブロックがあったらfalse
        BlockPos pos = new BlockPos(nX, nY, nZ);
        Block block = world.getBlockState(pos).getBlock();
        if (Block.isEqualTo(block, Blocks.AIR) == false &&
            Block.isEqualTo(block, BfBlocks.blockLight) == false)
        { return false; }

        //■設置場所の下のブロックが空 or 普通の立方体じゃない とfalse
        pos = new BlockPos(nX, nY - 1, nZ);
        block = world.getBlockState(pos).getBlock();
//        if (Block.isEqualTo(block, Blocks.air) ||
//            block.isSolidFullCube() == false)
        if (Block.isEqualTo(block, Blocks.AIR))
        { return false; }

        //■そのブロックに当り判定がない とfalse
//        AxisAlignedBB aabb = block.getCollisionBoundingBox(worldObj, pos, worldObj.getBlockState(pos));
        AxisAlignedBB aabb = block.getCollisionBoundingBox(world.getBlockState(pos), world, pos);
        if(aabb == null) { return false; }

        return true;
    }

    /**
     * ■■薪々が燃える環境にあるか否か
     * @return
     */
    protected boolean canBurnEnv()
    {
        int nX = MathHelper.floor(posX);
        int nY = MathHelper.floor(posY);
        int nZ = MathHelper.floor(posZ);
        BlockPos pos = new BlockPos(nX, nY, nZ);

        // (空の下じゃない 又は 雨が降ってない)
//        return (!worldObj.canLightningStrike(pos) || !worldObj.isRaining());
        return !world.isRainingAt(pos);
    }

    /**
     * ■ブロックの設置
     * @param blockIn 設置したいブロック
     */
    protected void setBlock(Block blockIn)
    {
        //■クライアント側では処理しない
        if (world.isRemote) { return; }

        //■現在地にあるブロック情報を取得
        int nX = MathHelper.floor(posX);
        int nY = MathHelper.floor(posY);
        int nZ = MathHelper.floor(posZ);
        BlockPos pos = new BlockPos(nX, nY, nZ);
        Block block = world.getBlockState(pos).getBlock();

        //■等しいブロックなので、何もしない
        if (Block.isEqualTo(block, blockIn)) { return; }

        //■空、もしくは自作光源ブロックなら、置き換え
        if (Block.isEqualTo(block, Blocks.AIR) || Block.isEqualTo(block, BfBlocks.blockLight))
        {
            //worldObj.notifyBlockOfStateChange(pos, blockIn);
            world.setBlockState(pos, blockIn.getDefaultState());
        }
    }

    /**
     * ■リフレクションを用いた、状態の生成
     * @return
     */
    @Nullable
    public State createStateInstance(String nameIn, int tickIn)
    {
        EntityBonfire.State state = null;

        try
        {
            //■りふれくしょん！
            Class cls = Class.forName("yousui115.bonfire.entity.EntityBonfire$" + nameIn);
            Constructor cst = cls.getConstructor(EntityBonfire.class, int.class);
            //this渡してええんやろか・・・
            state = (EntityBonfire.State) cst.newInstance(this, tickIn);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return state;
    }

    /**
     * ■
     */
    public void getDataManagerLocal()
    {
        //■燃焼時間
        int tick = this.getTickFire();

        //■状態
        String name = this.getStateName();
        if (name.compareTo(state.getClass().getSimpleName()) != 0)
        {
            State st = createStateInstance(name, tick);

            //■nullなら、よくわがんにゃいのでリセット。
            if (st == null)
            {
                state = new White(0);
            }
            else
            {
                state = st;
            }
        }
        else
        {
            state.tick = tick;
        }
    }

    /**
     * ■
     */
    public void setDataManagerLocal()
    {
        // ▼燃焼時間
        this.setTickFire();
        // ▼
        this.setStateName();
    }

    public int getTickFire()
    {
        return dataManager.get(TICK_FIRE);
    }

    public void setTickFire()
    {
        dataManager.set(TICK_FIRE, state.tick);
        dataManager.setDirty(TICK_FIRE);
    }

    public String getStateName()
    {
        return dataManager.get(STATE_NAME);
    }

    public void setStateName()
    {
        String str = state.getClass().getSimpleName();
        dataManager.set(STATE_NAME, str);
        dataManager.setDirty(STATE_NAME);
    }

    /**
     * ■火の燃える音
     */
    protected void soundFireAmbient()
    {
        this.world.playSound((float)posX + 0.5F, (float)posY + 0.5F, (float)posZ + 0.5F,
                SoundEvents.BLOCK_FIRE_AMBIENT,
                SoundCategory.BLOCKS,
                1.0F + rand.nextFloat(),
                rand.nextFloat() * 0.7F + 0.3F, false);
    }

    /**
     * ■着火音
     */
    protected void soundIgnition()
    {
        this.world.playSound(posX, posY, posZ,
                SoundEvents.ITEM_FLINTANDSTEEL_USE,
                SoundCategory.BLOCKS,
                1.0F,
                rand.nextFloat() * 0.4F + 0.8F, false);
    }
    /**
     * ■燃料をくべた音
     */
    protected void soundThrow()
    {
        this.world.playSound(posX, posY, posZ,
                SoundEvents.ENTITY_GHAST_SHOOT,
                SoundCategory.BLOCKS,
                0.5F,
                rand.nextFloat() * 0.4F + 0.8F, false);
    }

    /**
     * ■消火音
     */
    protected void soundFizz()
    {
        this.world.playSound(posX, posY, posZ,
                SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.BLOCKS,
                0.5F,
                2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F, false);
    }

    //======================= State =================================================

    /**
     * ■
     */
    public abstract class State
    {
        //■経過時間
        public int tick;
        //■燃焼中か否か
        //  (このフラグを変更する事で、自動で状態遷移を行う)
        public boolean isBurning;

        //■コンストラクタ
        public State(int tickIn)
        {
            tick = tickIn;
            isBurning = false;
        }

        //■tick進行(燃焼中のみ)
        public void addTick() { if (isBurning == true) { tick++; } }

        //■最大Tick
        public abstract int getMaxTick();

        //■次の状態
        public abstract State nextState();

        //■光源を設置するか否か
        public boolean settingLight() { return false; }

        //■
        public boolean interact(ItemStack stackIn)
        {
            //TODO 仮実装
            boolean isInteract = false;

            if (isBurning == true)
            {
                isBurning = false;
                isInteract = true;
            }
            else if (isBurning == false)
            {
                isBurning = true;
                isInteract = true;
            }

            return isInteract;
        }

        public ItemStack getItemStack() { return ItemStack.EMPTY; }

        //==========Render=============

        //■描画関連：炎のサイズ(0f ～ 1f)
        public abstract float getRenderFireScale();

        //■薪々の色
        public abstract float[] getRenderWoodColor_0();
        public abstract float[] getRenderWoodColor_3();

        //==========Debug==============
        public void debuglog()
        {
            Bonfire.logout(this.getClass().getName());
        }
    }

    /**
     * ■
     */
    public class White extends State
    {
        public White() { this(0); }
        public White(int tickIn)
        {
            super(tickIn);

            isBurning = false;
        }

        @Override
        public int getMaxTick() { return 0; }

        @Override
        public State nextState()
        {
            //■燃焼開始
            return isBurning == true ? new Ignition(0) : this;
        }

        @Override
        public float getRenderFireScale() { return 0f; }

        @Override
        public float[] getRenderWoodColor_0()
        {
            return new float[] {1f, 1f, 1f, 1f};
        }

        @Override
        public float[] getRenderWoodColor_3()
        {
            return new float[] {1f, 1f, 1f, 1f};
        }

    }

    /**
     * ■
     */
    public class Ignition extends State
    {
        public Ignition() { this(0); }
        public Ignition(int tickIn)
        {
            super(tickIn);

            isBurning = true;
        }

        @Override
        public int getMaxTick() { return 100; }

        @Override
        public State nextState()
        {
            State state = this;

            if (isBurning == false)
            {
                //■消火時
                state = new White_Re(0);
            }
            else if (tick >= getMaxTick())
            {
                //■燃焼時
                state = new BurstWR(0);
            }

            return state;
        }

        @Override
        public boolean settingLight()
        {
            return tick < getMaxTick() / 2 ? false : true;
        }

        @Override
        public float getRenderFireScale() { return (float)tick / (float)getMaxTick(); }

        @Override
        public float[] getRenderWoodColor_0()
        {
            return new float[] {1f, 1f, 1f, 1f};
        }

        @Override
        public float[] getRenderWoodColor_3()
        {
            return new float[] {1f, 1f, 1f, 1f};
        }

    }

    /**
     * ■
     */
    public class BurstWR extends State
    {
        public BurstWR() { this(0); }
        public BurstWR(int tickIn)
        {
            super(tickIn);

            isBurning = true;
        }

        @Override
        public int getMaxTick() { return 100; }

        @Override
        public State nextState()
        {
            State state = this;

            if (isBurning == false)
            {
                //■消火時
                state = new White_Re(0);
            }
            else if (tick >= getMaxTick())
            {
                //■燃焼時
                state = new BurstRB(0);
            }

            return state;
        }

        @Override
        public boolean settingLight() { return true; }

        @Override
        public float getRenderFireScale() { return 1.0f; }

        @Override
        public float[] getRenderWoodColor_0()
        {
            //1.0 -> 0.0
            float fProgr = (float)(getMaxTick() - tick) / (float)getMaxTick();
            return new float[] {1f, fProgr, fProgr, 1f};
        }

        @Override
        public float[] getRenderWoodColor_3()
        {
            return new float[] {1f, 1f, 1f, 1f};
        }

    }

    /**
     * ■
     */
    public class White_Re extends State
    {
        public White_Re() { this(0); }
        public White_Re(int tickIn)
        {
            super(tickIn);

            isBurning = false;
        }

        @Override
        public int getMaxTick() { return 0; }

        @Override
        public State nextState()
        {
            //■燃焼開始
            return isBurning == true ? new Ignition_Re(0) : this;
        }

        @Override
        public float getRenderFireScale() { return 0f; }

        @Override
        public float[] getRenderWoodColor_0()
        {
            return new float[] {0f, 0f, 0f, 1f};
        }

        @Override
        public float[] getRenderWoodColor_3()
        {
            return new float[] {1f, 1f, 1f, 1f};
        }

    }

    /**
     * ■
     */
    public class Ignition_Re extends State
    {
        public Ignition_Re() { this(0); }
        public Ignition_Re(int tickIn)
        {
            super(tickIn);

            isBurning = true;
        }

        @Override
        public int getMaxTick() { return 100; }

        @Override
        public State nextState()
        {
            State state = this;

            if (isBurning == false)
            {
                //■消火時
                state = new White_Re(0);
            }
            else if (tick >= getMaxTick())
            {
                //■燃焼時
                state = new BurstWR_Re(0);
            }

            return state;
        }

        @Override
        public boolean settingLight()
        {
            return tick < getMaxTick() / 2 ? false : true;
        }

        @Override
        public float getRenderFireScale() { return (float)tick / (float)getMaxTick(); }

        @Override
        public float[] getRenderWoodColor_0()
        {
            return new float[] {0f, 0f, 0f, 1f};
        }

        @Override
        public float[] getRenderWoodColor_3()
        {
            return new float[] {1f, 1f, 1f, 1f};
        }

    }

    /**
     * ■
     */
    public class BurstWR_Re extends State
    {
        public BurstWR_Re() { this(0); }
        public BurstWR_Re(int tickIn)
        {
            super(tickIn);

            isBurning = true;
        }

        @Override
        public int getMaxTick() { return 100; }

        @Override
        public State nextState()
        {
            State state = this;

            if (isBurning == false)
            {
                //■消火時
                state = new White_Re(0);
            }
            else if (tick >= getMaxTick())
            {
                //■燃焼時
                state = new BurstRB(0);
            }

            return state;
        }

        @Override
        public boolean settingLight() { return true; }

        @Override
        public float getRenderFireScale() { return 1f; }

        @Override
        public float[] getRenderWoodColor_0()
        {
            //0.0 -> 1.0
            float fProgr = (float)tick / (float)getMaxTick();
            return new float[] {fProgr, 0f, 0f, 1f};
        }

        @Override
        public float[] getRenderWoodColor_3()
        {
            return new float[] {1f, 1f, 1f, 1f};
        }

    }

    /**
     * ■
     */
    public class BurstRB extends State
    {
        public BurstRB() { this(0); }
        public BurstRB(int tickIn)
        {
            super(tickIn);

            isBurning = true;
        }

        @Override
        public int getMaxTick() { return 200; }

        @Override
        public State nextState()
        {
            State state = this;

            if (isBurning == false)
            {
                //■消火時
                state = new Charcoal(0);
            }
            else if (tick >= getMaxTick())
            {
                //■燃焼時
                state = new BurstB(0);
            }

            return state;
        }

        @Override
        public boolean settingLight() { return true; }

        @Override
        public float getRenderFireScale() { return 1f; }

        @Override
        public float[] getRenderWoodColor_0()
        {
            //1.0 -> 0.0
            float fProgr = (float)(getMaxTick() - tick) / (float)getMaxTick();
            return new float[] {0.1f + fProgr * 0.9f, 0f, 0f, 1f};
        }

        @Override
        public float[] getRenderWoodColor_3()
        {
            //1.0 -> 0.0
            float fProgr = (float)(getMaxTick() - tick) / (float)getMaxTick();
            return new float[] {1f, fProgr, fProgr, 1f};
        }

    }

    /**
     * ■
     */
    public class BurstB extends State
    {
        public BurstB() { this(0); }
        public BurstB(int tickIn)
        {
            super(tickIn);

            isBurning = true;
        }

        @Override
        public int getMaxTick() { return 100; }

        @Override
        public State nextState()
        {
            State state = this;

            if (isBurning == false || tick >= getMaxTick())
            {
                //■消火時 or 燃焼時
                state = new Charcoal(0);
            }

            return state;
        }

        @Override
        public boolean settingLight()
        {
            return tick < getMaxTick() / 2 ? true : false;
        }

        @Override
        public float getRenderFireScale() { return (float)(getMaxTick() - tick) / (float)getMaxTick(); }

        @Override
        public float[] getRenderWoodColor_0()
        {
            //1.0 -> 0.0
            float fProgr = (float)(getMaxTick() - tick) / (float)getMaxTick();
            return new float[] {fProgr * 0.1f, 0f, 0f, 1f};
        }

        @Override
        public float[] getRenderWoodColor_3()
        {
            //1.0 -> 0.0
            float fProgr = (float)(getMaxTick() - tick) / (float)getMaxTick();
            return new float[] {fProgr, 0f, 0f, 1f};
        }

    }

    /**
     * ■
     */
    public class Charcoal extends State
    {
        public Charcoal() { this(0); }
        public Charcoal(int tickIn)
        {
            super(tickIn);

            isBurning = false;
        }

        @Override
        public int getMaxTick() { return 0; }

        @Override
        public State nextState()
        {
            return this;
        }

        @Override
        public float getRenderFireScale() { return 0f; }

        @Override
        public float[] getRenderWoodColor_0()
        {
            return new float[] {0f, 0f, 0f, 1f};
        }

        @Override
        public float[] getRenderWoodColor_3()
        {
            return new float[] {0f, 0f, 0f, 1f};
        }

    }
}