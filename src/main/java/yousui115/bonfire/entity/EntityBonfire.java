package yousui115.bonfire.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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

public class EntityBonfire extends Entity
{
    protected int tickFire = 0;
    protected boolean isReuse = false;

    protected static final DataParameter<Integer> TICK_FIRE = EntityDataManager.<Integer>createKey(EntityBonfire.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> REUSE = EntityDataManager.<Boolean>createKey(EntityBonfire.class, DataSerializers.BOOLEAN);

    /**
     * ■コンストラクタ（クライアント）
     * @param worldIn
     */
    public EntityBonfire(World worldIn)
    {
        super(worldIn);
    }

    /**
     * ■コンストラクタ（サーバ）
     */
    public EntityBonfire(World world, double dX, double dY, double dZ)
    {
        this(world);

        dX += 0.5;
        dZ += 0.5;

        //■位置、回転角度の調整
        setLocationAndAngles(dX, dY, dZ, 0.0F, 0.0F);
    }

    /**
     * ■■初期化処理
     */
    @Override
    protected void entityInit()
    {
        //■燃え始めてからの時間
        tickFire = 0;

        //■サイズ設定
        setSize(0.9F, 0.25F);

        //■データ (ﾉ∀`)ｳｫｯﾁｬｰ
        initDataWatcher();
    }

    /**
     * ■■更新処理
     */
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        //■でーたうぉっちゃー 取得
        this.getDataWatcherLocal();

        //■存在できるかチェック
        if (canStay() == false)
        {
            this.setDead();
            return;
        }

        //■薪々は燃えているか？
        if (0 < tickFire && tickFire < EnumWoodState.CHARCOAL.getTickFireMax(isReuse))
        {
            //■tickFireの「加算」はここだけ（減算・代入はしてる）
            tickFire++;

            //■燃焼時間から、今の状態を取得
            EnumWoodState stateFire = getNowWoodState();

            //■燃え続けられるか否か
            if (stateFire.isFire() && !this.canBurnEnv(stateFire))
            {
                //■現在燃えている かつ 環境が悪い -> 消火
                stateFire = doFireFighting(stateFire);
            }

            //■光源ブロックの設置
            if (stateFire.isLighting(tickFire))
            {
                setBlock(Bonfire.blockLight);
            }
            else
            {
                setBlock(Blocks.air);
            }

            //■ロールバック処理
            if (stateFire.canRollback())
            {
                //■特定のアイテムが投げ込まれると、ロールバックした状態が帰ってくる
                // (内部でtickFire も更新している)
                stateFire = checkRollback(stateFire);
            }

            //■SE（ぱちぱち）
            if (tickFire % 20 == 0)
            {
                soundFireAmbient();
            }

            //■パーティクル（煙）
            if (rand.nextInt(10) < 5)
            {
                for(int l = 0; l < 3; l++)
                {
                    float fX = (float)posX + (0.5F - rand.nextFloat()) * 0.5F;
                    float fY = (float)posY +  0.7F + rand.nextFloat()  * 0.5F;
                    float fZ = (float)posZ + (0.5F - rand.nextFloat()) * 0.5F;
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL , fX, fY, fZ, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        //■でーたうぉっちゃー 更新
        this.setDataWatcherLocal();
    }

    /**
     * ■■EntityItemによる、延焼時間の巻き戻りがあるか確認
     * (内部で tickFire を更新)
     */
    protected EnumWoodState checkRollback(EnumWoodState state)
    {
        int nBackTime = 0;

        //■当り判定処理
        List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
        for(Entity entity : list)
        {
            //■EntityItem以外なら用はない
            if (!(entity instanceof EntityItem)) { continue; }

            ItemStack itemstack = ((EntityItem)entity).getEntityItem();
            int nTime = 0;

            //■棒, 紙, 本 は燃える
            if (itemstack.getItem().equals(Items.stick)) { nTime = 1000; }
            else if (itemstack.getItem().equals(Items.paper)) { nTime = 100; }
            else if (itemstack.getItem().equals(Items.book)) { nTime = 1000; }
            else { continue; }

            //■投入された数だけロールバック時間を加算
            nBackTime += nTime * itemstack.stackSize;

            if (!worldObj.isRemote)
            {
                //■アイテムは燃える
                entity.setDead();
            }

            //■ボッ！
            soundThrow();
//            worldObj.playSoundEffect(posX, posY, posZ,
//                    "mob.ghast.fireball",
//                    1.0F, rand.nextFloat() * 0.4F + 0.8F);
        }

        tickFire -= nBackTime;
        if (tickFire < state.getRollbackTimeMin(isReuse))
        {
            tickFire = state.getRollbackTimeMin(isReuse);
        }

        //■燃焼時間が変更されたので、今の状態を再取得
        state = getNowWoodState();

        return state;
    }

    /**
     * ■■薪々が燃える環境にあるか否か
     * @return
     */
    protected boolean canBurnEnv(EnumWoodState stateFire)
    {
        int nX = MathHelper.floor_double(posX);
        int nY = MathHelper.floor_double(posY);
        int nZ = MathHelper.floor_double(posZ);
        BlockPos pos = new BlockPos(nX, nY, nZ);

        // (空の下じゃない 又は 雨が降ってない)
//        return (!worldObj.canLightningStrike(pos) || !worldObj.isRaining());
        return !worldObj.isRainingAt(pos);
    }

    /**
     * ■■消火します
     * (内部で tickFire, isReuse を更新)
     */
    protected EnumWoodState doFireFighting(EnumWoodState stateFire)
    {
        //■自作光源ブロックは消失
        setBlock(Blocks.air);

        //■消火時の状態遷移先を取得
        stateFire = stateFire.getFireFightState();

        //■薪々（再利用）になってたらフラグ立てる。
        if (stateFire == EnumWoodState.WOOD_RE) { isReuse = true; }

        //■燃焼時間の変更
        tickFire = stateFire.getTickFireMax(isReuse);

        //■火が消える音
//        worldObj.playSoundEffect(posX, posY, posZ, "random.fizz", 0.5F, 3.0F);
        soundFizz();

        return stateFire;
    }


    /**
     * ■■燃焼時間から薪々の状態を調べる
     */
    public EnumWoodState getNowWoodState()
    {
        int nMaxTime = 0;
        for(EnumWoodState e : EnumWoodState.values())
        {
            nMaxTime += e.getInterval(isReuse);

            if (e == EnumWoodState.WOOD && isReuse) { continue; }
            if (nMaxTime >= tickFire) { return e; }
        }
        return EnumWoodState.CHARCOAL;
    }


    /**
     * ■■生存可能条件の確認
     * @return
     */
    public boolean canStay()
    {
        int nX = MathHelper.floor_double(posX);
        int nY = MathHelper.floor_double(posY);
        int nZ = MathHelper.floor_double(posZ);

        //■接触してるEntityをリストアップしてチェック
        List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
        for (Entity entity : list)
        {
            //■EntityBonfireが既に置いてあるとfalse
            if (entity instanceof EntityBonfire) { return false; }
        }

        //■設置場所にBonfireBlock以外のブロックがあったらfalse
        BlockPos pos = new BlockPos(nX, nY, nZ);
        Block block = worldObj.getBlockState(pos).getBlock();
        if (Block.isEqualTo(block, Blocks.air) == false &&
            Block.isEqualTo(block, Bonfire.blockLight) == false)
        { return false; }

        //■設置場所の下のブロックが空 or 普通の立方体じゃない とfalse
        pos = new BlockPos(nX, nY - 1, nZ);
        block = worldObj.getBlockState(pos).getBlock();
//        if (Block.isEqualTo(block, Blocks.air) ||
//            block.isSolidFullCube() == false)
        if (Block.isEqualTo(block, Blocks.air))
        { return false; }

        //■そのブロックに当り判定がない とfalse
//        AxisAlignedBB aabb = block.getCollisionBoundingBox(worldObj, pos, worldObj.getBlockState(pos));
        AxisAlignedBB aabb = block.getCollisionBoundingBox(worldObj.getBlockState(pos), worldObj, pos);
        if(aabb == null) { return false; }

        return true;
    }


    /**
     * ■■消滅処理
     */
    @Override
    public void setDead()
    {
        //■消滅フラグを立てる
        super.setDead();

        //■現在の薪々の状態を取得
        EnumWoodState state = getNowWoodState();

        //■火がついてるなら、音が鳴る
        if (state.isFire())
        {
//            worldObj.playSoundEffect(posX, posY, posZ, "random.fizz", 0.5F, 3.0F);
            soundFizz();
        }

        if (!worldObj.isRemote)
        {
            //■ブロックがあれば消滅させる
            this.setBlock(Blocks.air);

            //■状態に応じたドロップアイテムを顕現させる
            worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, state.getItemStack()));
        }
    }


    /**
     * ■■ブロックの再設置
     * @param blockIn 設置したいブロック
     */
    protected void setBlock(Block blockIn)
    {
        //■クライアント側では処理しない
        if (worldObj.isRemote) { return; }

        //■現在地にあるブロック情報を取得
        int nX = MathHelper.floor_double(posX);
        int nY = MathHelper.floor_double(posY);
        int nZ = MathHelper.floor_double(posZ);
        BlockPos pos = new BlockPos(nX, nY, nZ);
        Block block = worldObj.getBlockState(pos).getBlock();

        //■等しいブロックなので、何もしない
        if (Block.isEqualTo(block, blockIn)) { return; }

        //■空、もしくは自作光源ブロックなら、置き換え
        if (Block.isEqualTo(block, Blocks.air) || Block.isEqualTo(block, Bonfire.blockLight))
        {
            //worldObj.notifyBlockOfStateChange(pos, blockIn);
            worldObj.setBlockState(pos, blockIn.getDefaultState());
        }
    }


    /**
     * ■■プレイヤーが右クリックすると呼ばれる
     */
    @Override
//    public boolean interactFirst(EntityPlayer playerIn)
    public boolean processInitialInteract(EntityPlayer playerIn, ItemStack stackIn, EnumHand handIn)
    {
        boolean isInteract = false;

        //■何も持ってない(もしくは何らかの理由で0個のアイテムを持つ)プレイヤーなどフヨウラ！
        if (stackIn == null || stackIn.stackSize <= 0) { return isInteract; }

        //■でーたうぉっちゃー 取得
        this.getDataWatcherLocal();

        EnumWoodState stateFire = this.getNowWoodState();

        //■アイテムによる処理分岐
        // ▼点火(火打ち石持ってる かつ 燃焼間隔が0
        if (stackIn.getItem().equals(Items.flint_and_steel) &&
            stateFire.getInterval(isReuse) == 0)
        {
            //■イグニッション！
            this.startBurning();

            //■カチッ！
            soundIgnition();

            playerIn.swingArm(handIn);

            //■アイテムへの使用ダメージ
            stackIn.damageItem(1, playerIn);

            //■false で返す。
            //isInteract = true;
        }
        // ▼消化
        else if (stackIn.getItem().equals(Items.water_bucket)  &&
                 stateFire.isFire())
        {
            //■消火
            doFireFighting(stateFire);

//            playerIn.swingItem();
            playerIn.swingArm(handIn);

            isInteract = true;
        }
        // ▼食べ物
        else if (EntityFood.canBroilFood(stackIn))
        {
            double dDiffX = this.posX - playerIn.posX;
            double dDiffZ = this.posZ - playerIn.posZ;
            double dPosX = this.posX;
            double dPosZ = this.posZ;
            int nPlayerDir = 0; //Bonfireから見てのPlayerの方向
            double dTheta = Math.atan2(dDiffX, dDiffZ) + Math.PI; //0 ~ 6.28...

            double dTmp = Math.PI/4.0;
            if (dTmp <= dTheta && dTheta < dTmp*3) { nPlayerDir = 0; }
            else if (dTmp*3 <= dTheta && dTheta < dTmp*5) { nPlayerDir = 1; }
            else if (dTmp*5 <= dTheta && dTheta < dTmp*7) { nPlayerDir = 2; }
            else { nPlayerDir = 3; }

            List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
            int nDir = 0;
            int idx;
            for (idx = 0; idx < entities.size(); idx++)
            {
                if (entities.get(idx) instanceof EntityFood)
                {
                    EntityFood food = (EntityFood)entities.get(idx);
                    nDir |= 0x1 << food.getDirection();
                }
            }

            for (idx = 0; idx < 4; idx++)
            {
                int dir = (nPlayerDir + idx) % 4;
                int nBit = nDir & (0x1 << dir);
                if (nBit == 0)
                {
                    //TODO:配置可能
                    if (!this.worldObj.isRemote)
                    {
                        this.worldObj.spawnEntityInWorld(new EntityFood(this.worldObj, this.posX, this.posY, this.posZ, dir, stackIn));
                    }
                    if (--stackIn.stackSize <= 0)
                    {
                        //playerIn.destroyCurrentEquippedItem();
                    }
                    isInteract = true;
                    break;
                }
            }
        }

        //■でーたうぉっちゃー 更新
        this.setDataWatcherLocal();

        return isInteract;
    }


    /**
     * ■■イグニッション！
     */
    public void startBurning()
    {
        this.tickFire = 1;
    }

    /**
     * ■■ブロック外への押し出しは有効か否か
     */
    @Override
    protected boolean pushOutOfBlocks(double x, double y, double z)
    {
        return false;
    }


    /**
     * ■■右クリックを有効にするか否か
     */
    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * ■薪々はかよわいので、ダメージを貰ったら死にます
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if(!isDead && !worldObj.isRemote)
        {
            setBeenAttacked();
            setDead();
        }
        return true;
    }

    /**
     * ■NBT読込
     * @param tagCompund
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound)
    {
        tickFire = tagCompound.getInteger("tickFire");
        setTickFire();
        isReuse = tagCompound.getBoolean("isReuse");
        setIsReuse();
    }

    /**
     * ■NBT書込
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setInteger("tickFire", getTickFire());
        tagCompound.setBoolean("isReuse", getIsReuse());
    }

    /**
     * ■火の燃える音
     */
    protected void soundFireAmbient()
    {
        this.worldObj.playSound((float)posX + 0.5F, (float)posY + 0.5F, (float)posZ + 0.5F,
                SoundEvents.block_fire_ambient,
                SoundCategory.BLOCKS,
                1.0F + rand.nextFloat(),
                rand.nextFloat() * 0.7F + 0.3F, false);
    }

    /**
     * ■着火音
     */
    protected void soundIgnition()
    {
        this.worldObj.playSound(posX, posY, posZ,
                SoundEvents.item_flintandsteel_use,
                SoundCategory.BLOCKS,
                1.0F,
                rand.nextFloat() * 0.4F + 0.8F, false);
    }
    /**
     * ■燃料をくべた音
     */
    protected void soundThrow()
    {
        this.worldObj.playSound(posX, posY, posZ,
                SoundEvents.entity_ghast_shoot,
                SoundCategory.BLOCKS,
                1.0F,
                rand.nextFloat() * 0.4F + 0.8F, false);
    }

    /**
     * ■消火音
     */
    protected void soundFizz()
    {
        this.worldObj.playSound(posX, posY, posZ,
                SoundEvents.block_fire_extinguish,
                SoundCategory.BLOCKS,
                0.5F,
                2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F, false);
    }
    //--------------------------------------------- ↓Render から呼ばれるメソッド ------------------------------------------
    public float getFireScale(EnumWoodState state)
    {
        float fScale = 0;
        switch(state)
        {
            //■徐々に大きくなる
            case IGNITION:
            case IGNITION_RE:
                fScale = 1.0f - (float)(state.getTickFireMax(isReuse) - tickFire) / (float)state.getInterval(isReuse);
                break;

            //■最大
            case BURST_WR:
            case BURST_WR_RE:
            case BURST_RB:
                fScale = 1.0f;
                break;

            //■徐々に小さくなる
            case BURST_B:
                fScale = (float)(state.getTickFireMax(isReuse) - tickFire) / (float)state.getInterval(isReuse);
                break;

            //■消えている
            case WOOD:
            case WOOD_RE:
            case CHARCOAL:
            default:
                break;
        }

        return fScale;
    }

    public float[] getWoodColor_0(EnumWoodState state)
    {
        float fColor[] = {1.0f, 1.0f, 1.0f, 1.0f};  //R G B A

        //■進捗(1.0f -> 0.0f)
        float fProgr = (float)(state.getTickFireMax(isReuse) - tickFire) / (float)state.getInterval(isReuse);

        switch(state)
        {
            // ▼白 -> 赤
            case BURST_WR:
                fColor[1] = fColor[2] = fProgr;
                break;

            // ▼黒 -> 赤
            case BURST_WR_RE:
                fColor[0] = 1.0f - fProgr;
                fColor[1] = fColor[2] = 0.0f;
                break;

            // ▼赤 -> 赤黒
            case BURST_RB:
                fColor[0] = 0.1f + fProgr * 0.9f;
                fColor[1] = fColor[2] = 0.0f;
                break;

            // ▼赤黒 -> 黒
            case BURST_B:
                fColor[0] = fProgr * 0.1f;
                fColor[1] = fColor[2] = 0.0f;
                break;

            // ▼黒
            case WOOD_RE:
            case IGNITION_RE:
            case CHARCOAL:
                fColor[0] = fColor[1] = fColor[2] = 0.0f;
                break;

            // ▼白
            case WOOD:
            case IGNITION:
            default:
                break;
        }

        for (int idx = 1; idx < fColor.length - 1; idx++)
        {
            if (fColor[idx] < 0.05f) { fColor[idx] = 0.05f; }
        }

        return fColor;
    }

    public float[] getWoodColor_3(EnumWoodState state)
    {
        float fColor[] = {1.0f, 1.0f, 1.0f, 1.0f};  //R G B A

        //■進捗(1.0f -> 0.0f)
        float fProgr = (float)(state.getTickFireMax(isReuse) - tickFire) / (float)state.getInterval(isReuse);

        switch(state)
        {
            // ▼白 -> 赤
            case BURST_RB:
                fColor[1] = fColor[2] = fProgr;
                break;

            // ▼赤 -> 黒
            case BURST_B:
                fColor[0] = fProgr;
                fColor[1] = fColor[2] = 0.0f;
                break;

            // ▼黒
            case CHARCOAL:
                fColor[0] = fColor[1] = fColor[2] = 0.0f;
                break;

            // ▼白
            case BURST_WR_RE:
            case BURST_WR:
            case WOOD_RE:
            case IGNITION_RE:
            case WOOD:
            case IGNITION:
            default:
                break;
        }

        for (int idx = 1; idx < fColor.length - 1; idx++)
        {
            if (fColor[idx] < 0.05f) { fColor[idx] = 0.05f; }
        }

        return fColor;
    }
    //--------------------------------------------- ↑Render から呼ばれるメソッド ------------------------------------------

    public void initDataWatcher()
    {
//        this.dataWatcher.addObject(10, new Integer(0));     // tickFire
//        this.dataWatcher.addObject(11, new Integer(0));     // isReuse
        this.dataWatcher.register(TICK_FIRE, Integer.valueOf(0));
        this.dataWatcher.register(REUSE,     Boolean.FALSE);
    }

    public void getDataWatcherLocal()
    {
        // ▼燃焼時間
        tickFire = this.getTickFire();
        // ▼再燃焼か否か
        isReuse = this.getIsReuse();
    }

    public void setDataWatcherLocal()
    {
        // ▼燃焼時間
        this.setTickFire();
        // ▼再燃焼か否か
        this.setIsReuse();
    }

    public int getTickFire()
    {
//        return this.dataWatcher.getWatchableObjectInt(10);
        return dataWatcher.get(TICK_FIRE);
    }

    public void setTickFire()
    {
//        this.dataWatcher.updateObject(10, this.tickFire);
        dataWatcher.set(TICK_FIRE, tickFire);
//        dataWatcher.setDirty(TICK_FIRE);
    }

    public boolean getIsReuse()
    {
//        return this.dataWatcher.getWatchableObjectInt(11) == 1;
        return dataWatcher.get(REUSE);
    }

    public void setIsReuse()
    {
//        this.dataWatcher.updateObject(11, isReuse ? 1 : 0);
        dataWatcher.set(REUSE, isReuse);
//        dataWatcher.setDirty(REUSE);
    }

    /**
     * ■薪々の状態
     */
    public enum EnumWoodState
    {
        WOOD        (   0, false, false, new ItemStack(Bonfire.itemBonfire)),  //白い薪々
        WOOD_RE     (   0, false, false, new ItemStack(Items.stick, 2)),
        IGNITION    ( 100,  true, false, new ItemStack(Bonfire.itemBonfire)), //点火
        IGNITION_RE ( 100,  true, false, new ItemStack(Items.stick, 2)),
        BURST_WR    ( 700,  true, false, new ItemStack(Items.stick, 2)),
        BURST_WR_RE ( 700,  true, false, new ItemStack(Items.stick, 2)),
        BURST_RB    (2000,  true,  true, new ItemStack(Items.coal, 1, 1)),
        BURST_B     ( 500,  true,  true, new ItemStack(Items.coal, 1, 1)),
        CHARCOAL    (   1, false, false, new ItemStack(Items.coal, 1, 1));     //薪々、完全なる黒へ

        private final int interval;
        private final boolean isFire;
        private final boolean canRollback;
        private final ItemStack itemstack;

        /**
         * ■コンストラクタ
         */
        EnumWoodState(int inter, boolean fire, boolean rollback, ItemStack stack)
        {
            interval = inter;
            isFire = fire;
            canRollback = rollback;
            itemstack = stack;
        }

        /**
         * ■■今の状態が続く、最大の燃焼時間を取得
         */
        public int getTickFireMax(boolean isReuse)
        {
            int nMaxTime = 0;
            for(EnumWoodState e : EnumWoodState.values())
            {
                if (e.ordinal() > this.ordinal()) { break; }
                nMaxTime += e.getInterval(isReuse);
            }
            return nMaxTime;
        }

        /**
         * ■■一つ前の状態 + 1Tick の時間を取得
         */
        public int getTickFireMin(boolean isReuse)
        {
            return this.getTickFireMax(isReuse) - this.interval + 1;
        }

        /**
         * ■■巻き戻し出来る時間の下限
         */
        public int getRollbackTimeMin(boolean isReuse)
        {
            return BURST_RB.getTickFireMin(isReuse);
        }

        /**
         * ■■消火時の状態遷移
         */
        public EnumWoodState getFireFightState()
        {
            EnumWoodState e;
            switch(this)
            {
                case IGNITION:
                    e = WOOD;
                    break;

                case BURST_WR:
                case IGNITION_RE:
                case BURST_WR_RE:
                    e = WOOD_RE;
                    break;

                case BURST_RB:
                case BURST_B:
                    e = CHARCOAL;
                    break;

                case WOOD:
                case WOOD_RE:
                case CHARCOAL:
                default:
                    e = this;
                    break;
            }
            return e;
        }

        public boolean isLighting(int tick)
        {
            boolean is = false;

            switch(this)
            {
            case IGNITION:
            case IGNITION_RE:
                if (tick > this.interval / 2)
                {
                    is = true;
                }
                else
                {
                    is = false;
                }
                break;

            case BURST_B:
                if (tick > this.interval / 2 + this.getTickFireMin(true))
                {
                    is = false;
                }
                else
                {
                    is = true;
                }
            case BURST_WR:
            case BURST_WR_RE:
            case BURST_RB:
                is = true;
                break;

            case WOOD:
            case WOOD_RE:
            case CHARCOAL:
            default:
                is = false;
                break;
            }
            return is;
        }
        //------ getter ----------------------------------------------------------------------
        public int getInterval(boolean isReuse)
        {
            int nTime = 0;
            switch(this)
            {
                case WOOD:
                case IGNITION:
                case BURST_WR:
                    if (!isReuse) { nTime = this.interval; }
                    break;

                case WOOD_RE:
                case IGNITION_RE:
                case BURST_WR_RE:
                    if (isReuse) { nTime = this.interval; }
                    break;

                default:
                    nTime = this.interval;
                    break;
            }

            return nTime;
        }
        public boolean isFire() { return isFire; }
        public boolean canRollback() { return canRollback; }
        public ItemStack getItemStack() { return itemstack.copy(); }
    }

    public void debugText(String strText)
    {
        if (!worldObj.isRemote)
        {
            System.out.println("[Server] : " + strText);
        }
        else
        {
            System.out.println("[Client] : " + strText);
        }
    }
}
