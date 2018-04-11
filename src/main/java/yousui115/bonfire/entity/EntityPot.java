package yousui115.bonfire.entity;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import yousui115.bonfire.util.BfSounds;

public class EntityPot extends EntityBroilItem
{
    //■最大調理時間
    public static final int BROIL_TIME_BOIL = 400;
    public static final int BROIL_TIME_BOIL_MAX = BROIL_TIME_BOIL + 100;

    public int tick = 0;
    public float fNext = 0f;

    public EntityPot(World worldIn)
    {
        super(worldIn);
    }

    public EntityPot(World world, double dX, double dY, double dZ, int nDir, ItemStack stackIn)
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
        setSize(0.3F, 0.5F);

        super.entityInit();
    }

    @Override
    public double dist() { return 0.5; }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (isDead) { return; }

        // ▼MAXまで沸騰しておらず、火が付いている
        if (this.getBroilTime() < BROIL_TIME_BOIL_MAX
            && bonfire.state.isBurning == true)
        {
            //■調理時間の加算
            this.setBroilTime(this.getBroilTime() + 1);
        }
        // ▼火が消えている
        else if (bonfire.state.isBurning == false)
        {
            //■調理時間の減算
            this.setBroilTime(this.getBroilTime() - 1);
        }

        //■MAXまで沸騰したら煮沸済みに入れ替える。
        if (this.getBroilItem() != null &&
            this.getBroilItem().getMetadata() != 2 &&
            this.getBroilTime() == BROIL_TIME_BOIL_MAX)
        {
            ItemStack copy = getBroilItem().copy();
            copy.setItemDamage(2);
            this.setBroilItem(copy);
        }

        //■効果音
        if (this.getBroilTime() > BROIL_TIME_BOIL)
        {
            if (tick++ % 35 == 0)
            {
                int volu = this.getBroilTime();
                float vol = (float)volu / (float)BROIL_TIME_BOIL_MAX;
                soundBoil(0.1f * vol);
                tick = 1;
            }
        }

        //■フタの傾き
        fNext = this.rand.nextFloat();

        //TODO フタがカタカタ鳴る音も入れてみたが、思いのほかうるさかったのでヤメ。残念
//        if (getLidRot() > 0f) { soundCover(); }
    }

    @Override
    public ItemStack getNowItemStack()
    {
        return getBroilItem();
    }

    /**
     * ■効果音（ぐつぐつ）
     */
    public void soundBoil(float vol)
    {
        world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ,
                BfSounds.Gutsu, SoundCategory.NEUTRAL, vol, 1.0f);
    }

    /**
     * ■効果音（カタカタ）
     */
    public void soundCover()
    {
        world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ,
                BfSounds.Kata, SoundCategory.NEUTRAL, 0.5F, 1.0f);
    }

    public float getLidRot()
    {
        if (this.getBroilTime() > BROIL_TIME_BOIL_MAX - 1)
        {
            float fx = fNext / 10f;
            float fBorder = 0.09f;
            return fx < fBorder ? 0f : fx;
        }

        return 0f;
    }

    public Random getRnd() { return this.rand; }

}