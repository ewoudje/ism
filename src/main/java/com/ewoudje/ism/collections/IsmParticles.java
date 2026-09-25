package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class IsmParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, Ism.ID);

    public static final Supplier<SimpleParticleType> TRISTITIA = REGISTER.register("tristitia", () -> new SimpleParticleType(true));
}
