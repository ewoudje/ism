package com.ewoudje.ism.features.tristitia;

import com.ewoudje.ism.collections.IsmBlockEntities;
import com.ewoudje.ism.collections.IsmCapabilities;
import com.ewoudje.ism.features.seal.SealStructurePiece;
import com.ewoudje.ism.features.tristitia.poi.AbstractTristitiaChildPOI;
import com.ewoudje.ism.features.tristitia.poi.TristitiaPOI;
import com.ewoudje.ism.features.tristitia.poi.TristitiaRootPOI;
import com.ewoudje.ism.util.block.IsmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

import java.util.function.Supplier;

public class TristitiaGrowthHoleBlockEntity extends IsmBlockEntity implements Supplier<TristitiaPOI> {
    private BlockPos corePos;

    public TristitiaGrowthHoleBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(IsmBlockEntities.TRISTITIA_SEAL_HOLE.get(), worldPosition, blockState);
        var cPos = ChunkPos.containing(worldPosition);
        corePos = new BlockPos(
                cPos.getBlockX(SealStructurePiece.CORE_REL_X),
                SealStructurePiece.CORE_Y,
                cPos.getBlockZ(SealStructurePiece.CORE_REL_Z) - 1
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        trackCapability(IsmCapabilities.TRISTITIA_POI);
    }

    @Override
    public TristitiaPOI get() {
        return new POI(getCorePOI(), new Vector3d(
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5
        ));
    }

    private TristitiaRootPOI getCorePOI() {
        return (TristitiaRootPOI) level.getCapability(IsmCapabilities.TRISTITIA_POI, corePos);
    }

    public static TristitiaPOI poiCapability(TristitiaGrowthHoleBlockEntity self, Void unused) {
        return self.getCorePOI().createChild(self);
    }

    private class POI extends AbstractTristitiaChildPOI {

        protected POI(TristitiaPOI parent, Vector3d position) {
            super(parent, position);
        }

        @Override
        public double reach() {
            return 100;
        }
    }
}
