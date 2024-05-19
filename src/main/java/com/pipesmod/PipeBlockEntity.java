package com.pipesmod;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class PipeBlockEntity extends BlockEntity {

    private BlockState baseBlockState;

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(PipesMod.PIPE_BLOCK_ENTITY, pos, state);
    }

    public BlockState getBaseBlock() {
        return baseBlockState;
    }

    public void setBaseBlock(BlockState blockState) {
        this.baseBlockState = blockState;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (baseBlockState != null) {  // Ensure that there is a state to save
            nbt.put("BaseBlockState", NbtHelper.fromBlockState(baseBlockState));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("BaseBlockState", 10)) {
            NbtCompound stateCompound = nbt.getCompound("BaseBlockState");
            DataResult<BlockState> blockStateResult = BlockState.CODEC.parse(NbtOps.INSTANCE, stateCompound);
            blockStateResult.result().ifPresent(this::setBaseBlock);
        }
    }
}
