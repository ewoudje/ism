package com.ewoudje.ism.features.tristitia.frog;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.OptionalInt;

public class TristitiaFrogEntity extends Monster implements RangedAttackMob {
    private static final EntityDataAccessor<OptionalInt> DATA_TONGUE_TARGET_ID = SynchedEntityData.defineId(
            TristitiaFrogEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT
    );
    private static final EntityDimensions DEFAULT_DIMENSIONS = EntityDimensions.fixed(1.05f, 1.05f);

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    public final AnimationState jumpAnimationState = new AnimationState();
    public final AnimationState croakAnimationState = new AnimationState();
    public final AnimationState tongueAnimationState = new AnimationState();
    public final AnimationState swimIdleAnimationState = new AnimationState();

    private int timeSinceAttack = -1;

    public TristitiaFrogEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        //TODO this.lookControl = new Frog.FrogLookControl(this);
        //this.setPathfindingMalus(PathType.WATER, 4.0F);
        //this.setPathfindingMalus(PathType.TRAPDOOR, -1.0F);
        //this.moveControl = new SmoothSwimmingMoveControl<>(this, 85, 10, 0.02F, 0.1F, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
    }

    @Override
    protected void registerGoals() {
        //TODO croaking, swimIdle?, longJump
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.0f, 60, 4));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cat.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true));
    }


    @Override
    protected void updateWalkAnimation(float distance) {
        super.updateWalkAnimation(distance);


        //float targetSpeed;
        //if (this.jumpAnimationState.isStarted()) {
            //targetSpeed = 0.0F;
        //} else {
            //targetSpeed = Math.min(distance * 25.0F, 1.0F);
        //}

        //this.walkAnimation.update(targetSpeed, 0.4F, 1.0F);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected int calculateFallDamage(double fallDistance, float damageModifier) {
        return super.calculateFallDamage(fallDistance, damageModifier) - 5;
    }

    @Override
    protected void travelInWater(Vec3 input, double baseGravity, boolean isFalling, double oldY) {
        this.moveRelative(this.getSpeed(), input);
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FROG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FROG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.FROG_STEP, 0.15F, 1.0F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_POSE.equals(accessor)) {
            Pose pose = this.getPose();
            if (pose == Pose.LONG_JUMPING) {
                this.jumpAnimationState.start(this.tickCount);
            } else {
                this.jumpAnimationState.stop();
            }

            if (pose == Pose.CROAKING) {
                this.croakAnimationState.start(this.tickCount);
            } else {
                this.croakAnimationState.stop();
            }

            if (pose == Pose.USING_TONGUE) {
                this.tongueAnimationState.start(this.tickCount);
            } else {
                this.tongueAnimationState.stop();
            }
        }

        super.onSyncedDataUpdated(accessor);
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        return super.getTarget();
    }

    @Override
    public void tick() {
        super.tick();

        //TODO should prob be part of the goal
        if (!level().isClientSide() && getPose() == Pose.USING_TONGUE && tickCount > timeSinceAttack) {
            setPose(Pose.STANDING);
            timeSinceAttack = tickCount;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.of(target.getId()));

        setPose(Pose.USING_TONGUE);
        Vec3 target2self = target.position().vectorTo(position()).normalize();
        target.setDeltaMovement(target2self.scale(2));

        //TODO is this the right way?
        target.hurtOrSimulate(createDamageSource(), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
        timeSinceAttack = tickCount + 20;
    }
}
