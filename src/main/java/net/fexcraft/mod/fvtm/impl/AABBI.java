package net.fexcraft.mod.fvtm.impl;


import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AABBI extends net.fexcraft.mod.fvtm.data.block.AABB {

    private AABB aabb;

    @Override
    public net.fexcraft.mod.fvtm.data.block.AABB set(float sx, float sy, float sz, float ex, float ey, float ez){
        float gx = ex - sx, gy = ey - sy, gz = ez - sz;
        aabb = AABB.ofSize(new Vec3((sx + ex) * 0.5, (sy + ey) * 0.5, (sz + ez) * 0.5), gx, gy, gz);
        return this;
    }

    @Override
    public <AB> AB local(){
        return (AB)aabb;
    }

    @Override
    public Object direct(){
        return aabb;
    }

    @Override
    public <AB> AB offset(int x, int y, int z){
        return (AB)aabb;//TODO.offset(x, y, z);
    }

}
