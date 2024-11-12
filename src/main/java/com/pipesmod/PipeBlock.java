package com.pipesmod;

import org.jetbrains.annotations.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PipeBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty LIT = BooleanProperty.of("lit");

    public PipeBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(NORTH, false)
            .with(SOUTH, false)
            .with(EAST, false)
            .with(WEST, false)
            .with(UP, false)
            .with(DOWN, false)
            .with(LIT, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, LIT);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        // This method can be used for additional logic on placement if needed
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();

        for (Direction direction : Direction.values()) {
            if (shouldConnect(ctx, direction)) {
                state = state.with(getProperty(direction), true);
            }
        }

        return state;
    }

    private boolean shouldConnect(ItemPlacementContext ctx, Direction direction) {
        BlockState neighborState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction));
        Block neighborBlock = neighborState.getBlock();
        return neighborBlock instanceof PipeBlock || neighborBlock instanceof PipeEntrance;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Block neighborBlock = neighborState.getBlock();
        if (!(neighborBlock instanceof PipeBlock || neighborBlock instanceof PipeEntrance)) {
            return state.with(getProperty(direction), false);
        }

        return state.with(getProperty(direction), true);
    }

    public static BooleanProperty getProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            // Output the block's connections to the console
            System.out.println("North: " + state.get(NORTH));
            System.out.println("South: " + state.get(SOUTH));
            System.out.println("East: " + state.get(EAST));
            System.out.println("West: " + state.get(WEST));
            System.out.println("Up: " + state.get(UP));
            System.out.println("Down: " + state.get(DOWN));

            // Print the block's base block
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PipeBlockEntity) {
                BlockState baseBlock = ((PipeBlockEntity) blockEntity).getBaseBlock();
                System.out.println("Base Block: " + baseBlock.getBlock().toString());
            }
        }
        return ActionResult.SUCCESS;
    }

    //@Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            System.out.println("setting lit to false");
            world.setBlockState(pos, state.with(LIT, false));
        }
    }

    // Method to switch to lit state for a set amount of time
    public void switchLitState(World world, BlockPos pos, BlockState state, int ticks) {
        System.out.println("setting lit to true");
        world.setBlockState(pos, state.with(LIT, true));
        world.scheduleBlockTick(pos, this, ticks);
    }
}
