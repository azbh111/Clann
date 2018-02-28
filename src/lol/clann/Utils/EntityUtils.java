/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.util.List;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;
import net.minecraft.server.v1_7_R4.Vec3D;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 *
 * @author zyp
 */
public class EntityUtils {

    /**
     * 设置实体的生命值
     *
     * @param le
     * @param expression 表达式
     */
    public static void setHealth(LivingEntity le, double t) {
        double max = le.getMaxHealth();
        le.setHealth(t > max ? max : t);
    }

    /**
     * 获取玩家准星所指的实体,若没有则返回null
     *
     * @param p
     * @param distance
     *
     * @return
     */
    public static Entity getTargetEnttiy(Player p, double distance) {
        EntityPlayer nmsP = ((CraftPlayer) p).getHandle();
        net.minecraft.server.v1_7_R4.Entity pointedEntity = null;
        List<Entity> list = p.getNearbyEntities(distance, distance, distance);//不含自身
        double d2, d0;
        d2 = d0 = distance;
        Vec3D vec3 = Vec3D.a(nmsP.locX, nmsP.locY, nmsP.locZ);//createVectorHelper
        Vec3D vec31 = nmsP.ag();//getLookVec
        Vec3D vec32 = vec3.add(vec31.a * d0, vec31.b * d0, vec31.c * d0);
        for (int j = 0; j < list.size(); j++) {
            net.minecraft.server.v1_7_R4.Entity entity = ((CraftEntity) list.get(j)).getHandle();
            boolean canBeCollidedWith = entity.R();
            if (canBeCollidedWith) {
                float f = entity.af();//getCollisionBorderSize
                AxisAlignedBB axisalignedbb = entity.boundingBox.grow(f, f, f);
                axisalignedbb = axisalignedbb.c(0, -1.5, 0);//向下偏移
                MovingObjectPosition movingobjectposition = axisalignedbb.a(vec3, vec32);
                if (axisalignedbb.a(vec3)) {//isVecInside
                    pointedEntity = entity;
                    d2 = 0.0D;
                } else if (movingobjectposition != null) {
                    double d3 = vec3.d(movingobjectposition.pos);
                    if ((d3 < d2) || (d2 == 0.0D)) {
                        if (entity == nmsP.vehicle) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity;
                            }
                        } else {
                            pointedEntity = entity;
                            d2 = d3;
                        }
                    }
                }
            }
        }
        return pointedEntity != null ? pointedEntity.getBukkitEntity() : null;
    }

    /**
     * 获取实体的MovingObjectPosition
     *
     * @param entity
     *
     * @return
     */
    public static MovingObjectPosition getMovingObjectPosition(Entity entity) {
        WorldServer world = ((CraftWorld) entity.getWorld()).getHandle();
        net.minecraft.server.v1_7_R4.Entity entityhuman = ((CraftEntity) entity).getHandle();
        float f = 1.0F;
        float f1 = entityhuman.lastPitch + (entityhuman.pitch - entityhuman.lastPitch) * f;
        float f2 = entityhuman.lastYaw + (entityhuman.yaw - entityhuman.lastYaw) * f;
        double d0 = entityhuman.lastX + (entityhuman.locX - entityhuman.lastX) * f;
        double d1 = entityhuman.lastY + (entityhuman.locY - entityhuman.lastY) * f + 1.62D - entityhuman.height;
        double d2 = entityhuman.lastZ + (entityhuman.locZ - entityhuman.lastZ) * f;
        Vec3D vec3d = Vec3D.a(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        Vec3D vec3d1 = vec3d.add(f7 * d3, f6 * d3, f8 * d3);
        return world.rayTrace(vec3d, vec3d1, true, !true, false);
    }
}
