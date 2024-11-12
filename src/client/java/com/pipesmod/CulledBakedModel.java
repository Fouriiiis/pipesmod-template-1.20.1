package com.pipesmod;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.block.BlockState;

import java.util.List;
import java.util.stream.Collectors;

public class CulledBakedModel implements BakedModel {
    private final BakedModel originalModel;
    private final List<Boolean> connections;

    public CulledBakedModel(BakedModel originalModel, List<Boolean> connections) {
        this.originalModel = originalModel;
        this.connections = connections;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        if (face != null) {
            int directionIndex = face.getId();
            
            // Cull the face if there's a connection in this direction
            if (connections.get(directionIndex)) {
                return List.of();
            }
        }

        // Return the original quads for unconnected faces
        return originalModel.getQuads(state, face, random)
                            .stream()
                            .filter(quad -> !connections.get(quad.getFace().getId()))
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
