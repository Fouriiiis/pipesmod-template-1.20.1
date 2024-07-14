package com.pipesmod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class TransportManager {
    private static final Map<PlayerEntity, TransportData> transports = new HashMap<>();

    public static void startTransport(PlayerEntity player, BlockPos startPos, Direction initialDirection) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            GameMode originalGameMode = serverPlayer.interactionManager.getGameMode();
            serverPlayer.changeGameMode(GameMode.SPECTATOR);
            transports.put(player, new TransportData(startPos, initialDirection, 2, originalGameMode));
        }
    }

    public static void initialize() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            transports.forEach((player, data) -> {
                if (data.ticksLeft > 0) {
                    data.ticksLeft--;
                } else {
                    movePlayer(player, data);
                    data.ticksLeft = 2; // Reset the tick counter
                }
                // Override the player's position to prevent them from moving
                player.teleport(data.currentPos.getX() + 0.5, data.currentPos.getY(), data.currentPos.getZ() + 0.5);
            });
            transports.entrySet().removeIf(entry -> entry.getValue().isCompleted);
        });
    }

    private static void movePlayer(PlayerEntity player, TransportData data) {
        World world = player.getWorld();
        BlockPos nextPos = data.currentPos.offset(data.direction);

        if (world.getBlockState(nextPos).getBlock() instanceof PipeBlock) {
            data.currentPos = nextPos;
            data.direction = getNextDirection(world, nextPos, data.direction);
        } else if (world.getBlockState(nextPos).getBlock() instanceof PipeEntrance) {
            data.currentPos = nextPos;
            data.isCompleted = true; // End the transport
        } else {
            data.isCompleted = true; // End the transport if there's no valid pipe
        }

        if (data.isCompleted && player instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) player).changeGameMode(data.originalGameMode);
        }
    }

    private static Direction getNextDirection(World world, BlockPos pos, Direction currentDirection) {
        int connections = 0;
        Direction nextDirection = currentDirection;
        Direction[] directions = Direction.values();

        for (Direction direction : directions) {
            if (direction != currentDirection.getOpposite()) {
                BlockState neighborState = world.getBlockState(pos.offset(direction));
                Block neighborBlock = neighborState.getBlock();
                if (neighborBlock instanceof PipeBlock || neighborBlock instanceof PipeEntrance) {
                    connections++;
                    if (connections == 1 || connections == 2) {
                        nextDirection = direction;
                    }
                }
            }
        }

        // If more than 2 connections, prioritize straight movement
        if (connections > 2) {
            nextDirection = currentDirection;
        }

        return nextDirection;
    }

    private static class TransportData {
        BlockPos currentPos;
        Direction direction;
        int ticksLeft;
        boolean isCompleted;
        GameMode originalGameMode;

        TransportData(BlockPos startPos, Direction direction, int ticksLeft, GameMode originalGameMode) {
            this.currentPos = startPos;
            this.direction = direction;
            this.ticksLeft = ticksLeft;
            this.isCompleted = false;
            this.originalGameMode = originalGameMode;
        }
    }
}
