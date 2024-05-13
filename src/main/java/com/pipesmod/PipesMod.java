package com.pipesmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipesMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pipesmod");

    public static Block PIPE_BLOCK;
    public static BlockEntityType<PipeBlockEntity> PIPE_BLOCK_ENTITY;

    @Override
    public void onInitialize() {
        // Initialize PIPE_BLOCK before registering it and using it to create PIPE_BLOCK_ENTITY.
        PIPE_BLOCK = Registry.register(Registries.BLOCK, new Identifier("pipesmod", "pipe_block"), new PipeBlock(getSetting()));

        // Now register the BlockItem with the PIPE_BLOCK we just registered.
        Registry.register(Registries.ITEM, new Identifier("pipesmod", "pipe_block"), new BlockItem(PIPE_BLOCK, new FabricItemSettings()));

        // Finally, register the BlockEntityType using the PIPE_BLOCK.
        PIPE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("pipesmod", "pipe_block_entity"), FabricBlockEntityTypeBuilder.create(PipeBlockEntity::new, PIPE_BLOCK).build(null));
    }

    private static AbstractBlock.Settings getSetting() {
        AbstractBlock.ContextPredicate never = (state, world, pos) -> false;
        return FabricBlockSettings.copyOf(Blocks.GLASS).suffocates(never).blockVision(never);
    }
}
