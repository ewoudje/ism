package com.ewoudje.ism.features.tristitia.client;

import com.ewoudje.ism.collections.IsmParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber
public class TristitiaParticles {

    public static class Biome implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Biome(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }


        @Override
        public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new Particle(level, x, y, z, spriteSet);
        }

        private static class Particle extends SimpleAnimatedParticle {

            public Particle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
                super(level, x, y, z, sprites, 0);
                this.quadSize *= 0.75F;
                this.lifetime = 40 + this.random.nextInt(12);
                this.setFadeColor(15916745);
                this.setSpriteFromAge(sprites);
            }

            @Override
            public void move(double xa, double ya, double za) {
                this.setBoundingBox(this.getBoundingBox().move(xa, ya, za));
                this.setLocationFromBoundingbox();
            }

            @Override
            public void tick() {
                move(random.nextDouble() * 0.02 - 0.01, random.nextDouble() * 0.02 - 0.01, random.nextDouble() * 0.02 - 0.01);
                super.tick();
            }
        }

    }


    @SubscribeEvent
    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(IsmParticles.TRISTITIA.get(), Biome::new);
    }
}
