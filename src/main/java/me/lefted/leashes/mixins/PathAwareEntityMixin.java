package me.lefted.leashes.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(PathAwareEntity.class)
@Environment(EnvType.SERVER)
public abstract class PathAwareEntityMixin extends MobEntity {

    protected PathAwareEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Overwrite
    public void updateLeash() {
        sandbox();
    }

    private void sandbox() {
        super.updateLeash();
        Entity entity = this.getHoldingEntity();
        if (entity != null && entity.world == this.world) {
            this.setPositionTarget(entity.getBlockPos(), 5);
            float f = this.distanceTo(entity);
            if (((Object) this) instanceof TameableEntity && ((TameableEntity) (Object) this).isInSittingPose()) {
                if (f > 50.0f) {
                    this.detachLeash(true, true);
                }
                return;
            }
            this.updateForLeashLength(f);
            if (f > 50.0f) {
                this.detachLeash(true, true);
                this.goalSelector.disableControl(Goal.Control.MOVE);
            } else if (f > 6.0f) {
                double d = (entity.getX() - this.getX()) / (double) f;
                double e = (entity.getY() - this.getY()) / (double) f;
                double g = (entity.getZ() - this.getZ()) / (double) f;
                this.setVelocity(this.getVelocity().add(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(g * g * 0.4, g)));
            } else {
                this.goalSelector.enableControl(Goal.Control.MOVE);
                float d = 2.0f;
                Vec3d vec3d = new Vec3d(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ()).normalize()
                    .multiply(Math.max(f - 2.0f, 0.0f));
                this.getNavigation().startMovingTo(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z, this.getRunFromLeashSpeed());
            }
        }
    }

    @Shadow
    protected abstract void updateForLeashLength(float leashLength);

    @Shadow
    protected abstract double getRunFromLeashSpeed();
}
