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
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PipeBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty LIGHT_ON = BooleanProperty.of("light_on");

    public PipeBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(NORTH, false)
            .with(SOUTH, false)
            .with(EAST, false)
            .with(WEST, false)
            .with(UP, false)
            .with(DOWN, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PipeBlockEntity) {
                // check if the block below is a pipe
                BlockState belowBlockState = world.getBlockState(pos.down());
                if (belowBlockState.getBlock() instanceof PipeBlock) {
                    //get the block entity of the block below
                    BlockEntity belowBlockEntity = world.getBlockEntity(pos.down());
                    //call the get base block method of the block entity
                    belowBlockState = ((PipeBlockEntity) belowBlockEntity).getBaseBlock();
                }
                //set the base block of the block entity to the block below
                ((PipeBlockEntity) blockEntity).setBaseBlock(belowBlockState);
            }
        }
    
    @Override
public BlockState getPlacementState(ItemPlacementContext ctx) {
    BlockState state = getDefaultState();
    int connections = 0;

    for (Direction direction : Direction.values()) {
        if (shouldConnect(ctx, direction)) {
            if (connections < 2) { // Only allow new connections if there are less than 2
                state = state.with(getProperty(direction), true);
                connections++;
            }
        }
    }

    return state;
}

    

    private boolean shouldConnect(ItemPlacementContext ctx, Direction direction) {
        BlockState neighborState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction));
        return neighborState.getBlock() instanceof PipeBlock;
    }

    @Override
public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (!(neighborState.getBlock() instanceof PipeBlock)) {
        return state.with(getProperty(direction), false);
    }

    // Count current connections before adding a new one
    int connections = 0;
    for (Direction dir : Direction.values()) {
        if (state.get(getProperty(dir)) && dir != direction) { // exclude the current direction from count
            connections++;
        }
    }

    if (connections < 2) {
        return state.with(getProperty(direction), true);
    } else {
        return state.with(getProperty(direction), false);
    }
}


    private BooleanProperty getProperty(Direction direction) {
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
            //output the block's connections to the console
            System.out.println("North: " + state.get(NORTH));
            System.out.println("South: " + state.get(SOUTH));
            System.out.println("East: " + state.get(EAST));
            System.out.println("West: " + state.get(WEST));
            System.out.println("Up: " + state.get(UP));
            System.out.println("Down: " + state.get(DOWN));

            //print the block's base block
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PipeBlockEntity) {
                BlockState baseBlock = ((PipeBlockEntity) blockEntity).getBaseBlock();
                System.out.println("Base Block: " + baseBlock.getBlock().toString());
            }
        }
        return ActionResult.SUCCESS;
    }
}
