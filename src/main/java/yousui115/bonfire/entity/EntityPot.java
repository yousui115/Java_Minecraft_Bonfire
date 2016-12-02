package yousui115.bonfire.entity;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import yousui115.bonfire.Bonfire;

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
        //TODO EntityBonfireをメンバに持った方がいいかな
        EntityBonfire entityBF = getBonfire();
        if (entityBF == null)
        {
            //■依存先のBonfireが居ないので消滅
            setEntityDead(this.posX, this.posY, this.posZ);
        }
        // ▼MAXまで沸騰しておらず、火が付いている
        else if (this.getBroilTime() < BROIL_TIME_BOIL_MAX && entityBF.getNowWoodState().isFire())
        {
            //■調理時間の加算
            this.setBroilTime(this.getBroilTime() + 1);
        }
        // ▼火が消えている
        else if (!entityBF.getNowWoodState().isFire())
        {
            //■調理時間の減算
            this.setBroilTime(this.getBroilTime() - 1);
        }

        //■MAXまで沸騰したら煮沸済みに入れ替える。
        if (this.getBroilItem() != null &&
            this.getBroilItem().getMetadata() != 2 &&
            this.getBroilTime() == BROIL_TIME_BOIL_MAX)
        {
            this.setBroilItem(new ItemStack(Bonfire.itemPot, 1, 2));
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
        worldObj.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ,
                Bonfire.Gutsu, SoundCategory.NEUTRAL, vol, 1.0f);
    }

    /**
     * ■効果音（カタカタ）
     */
    public void soundCover()
    {
        worldObj.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ,
                Bonfire.Kata, SoundCategory.NEUTRAL, 0.5F, 1.0f);
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
