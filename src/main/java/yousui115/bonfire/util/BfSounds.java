package yousui115.bonfire.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import yousui115.bonfire.Bonfire;

public class BfSounds
{
    //■ぐつぐつ
    public static SoundEvent Gutsu;

    //■かたかた（未使用）
    public static SoundEvent Kata;

    /**
     * ■生成
     */
    public static void create()
    {
        Gutsu = new SoundEvent(new ResourceLocation(Bonfire.MOD_ID, "gutsugutsu"));
        Kata  = new SoundEvent(new ResourceLocation(Bonfire.MOD_ID, "katakata"));
    }
}
