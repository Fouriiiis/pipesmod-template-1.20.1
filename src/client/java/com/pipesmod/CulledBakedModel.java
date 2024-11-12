package com.pipesmod;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.block.BlockState;

import java.util.List;
import java.util.stream.Collectors;

public class CulledBakedModel implements BakedModel {
    private final BakedModel originalModel;
    private final BlockState pipeState;

    public CulledBakedModel(BakedModel originalModel, BlockState pipeState) {
        this.originalModel = originalModel;
        this.pipeState = pipeState;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        if (face != null) {
            // Check if the current face has a connection using the pipeState
            BooleanProperty connectionProperty = PipeBlock.getProperty(face);
            if (pipeState.get(connectionProperty)) {
                // Cull the face if there’s a connection in that direction
                return List.of();
            }
        }

        // Allow rendering for unconnected faces
        return originalModel.getQuads(state, face, random)
                            .stream()
                            .filter(quad -> !pipeState.get(PipeBlock.getProperty(quad.getFace())))
                            .collect(Collectors.toList());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return originalModel.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return originalModel.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return originalModel.isBuiltin();
    }

    @Override
    public Sprite getParticleSprite() {
        return originalModel.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return originalModel.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return originalModel.getOverrides();
    }
}
