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
    public static Block PIPE_ENTRANCE;

    @Override
    public void onInitialize() {
        PIPE_BLOCK = Registry.register(Registries.BLOCK, new Identifier("pipesmod", "pipe_block"), new PipeBlock(getSetting()));
        PIPE_ENTRANCE = Registry.register(Registries.BLOCK, new Identifier("pipesmod", "pipe_entrance"), new PipeEntrance(getSetting()));

        // Register the custom item class for PIPE_BLOCK
        Registry.register(Registries.ITEM, new Identifier("pipesmod", "pipe_block"), new PipeBlockItem(PIPE_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier("pipesmod", "pipe_entrance"), new BlockItem(PIPE_ENTRANCE, new FabricItemSettings()));

        PIPE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("pipesmod", "pipe_block_entity"), FabricBlockEntityTypeBuilder.create(PipeBlockEntity::new, PIPE_BLOCK).build(null));
    }

    private static AbstractBlock.Settings getSetting() {
        AbstractBlock.ContextPredicate never = (state, world, pos) -> false;
        return FabricBlockSettings.copyOf(Blocks.GLASS).suffocates(never).blockVision(never);
    }
}
