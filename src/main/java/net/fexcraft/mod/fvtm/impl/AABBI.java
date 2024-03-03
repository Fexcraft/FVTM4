package net.fexcraft.mod.fvtm.impl;


import net.fexcraft.lib.common.math.V3D;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AABBI extends net.fexcraft.mod.fvtm.data.block.AABB {

    private AABB aabb;

    @Override
    public net.fexcraft.mod.fvtm.data.block.AABB set(float sx, float sy, float sz, float ex, float ey, float ez){
        aabb = new AABB(sx, sy, sz, ex, ey, ez);
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

    @Override
    public boolean contains(V3D vec){
        return aabb.contains(vec.x, vec.y, vec.z);
    }

    @Override
    public boolean contains(Object vec){
        return aabb.contains((Vec3)vec);
    }

}
