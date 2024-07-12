package com.pipesmod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PipeBlockItem extends BlockItem {

    public PipeBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos targetPos = context.getBlockPos();
        BlockState baseBlockState = world.getBlockState(targetPos);

        // Check if the target block is a PipeBlock and use its stored base block state
        BlockEntity targetBlockEntity = world.getBlockEntity(targetPos);
        if (targetBlockEntity instanceof PipeBlockEntity) {
            BlockState storedBaseBlockState = ((PipeBlockEntity) targetBlockEntity).getBaseBlock();
            if (storedBaseBlockState != null) {
                baseBlockState = storedBaseBlockState;
            }
        }

        ActionResult result = super.useOnBlock(context);

        if (result.isAccepted()) {
            BlockPos placedPos = context.getBlockPos().offset(context.getSide());
            BlockEntity placedBlockEntity = world.getBlockEntity(placedPos);
            if (placedBlockEntity instanceof PipeBlockEntity) {
                ((PipeBlockEntity) placedBlockEntity).setBaseBlock(baseBlockState);
            }
        }

        return result;
    }
}
