package com.pipesmod;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

public class TelePipeBlockEntity extends BlockEntity {
    public TelePipeBlockEntity(BlockPos pos, BlockState state) {
        super(PipesMod.TELEPIPE_BLOCK_ENTITY, pos, state);
    }
}
