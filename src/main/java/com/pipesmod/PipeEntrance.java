package com.pipesmod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PipeEntrance extends Block {
    
    // Enum for orientation
    public enum Orientation implements StringIdentifiable {
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        EAST(Direction.EAST),
        WEST(Direction.WEST),
        UP(Direction.UP),
        DOWN(Direction.DOWN);

        private final Direction direction;

        Orientation(Direction direction) {
            this.direction = direction;
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }

        public Direction getDirection() {
            return direction;
        }
    }

    // Property for pipe connection direction
    public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.of("orientation", Orientation.class);
    public static final EnumProperty<Orientation> CONNECTION = EnumProperty.of("connection", Orientation.class);

    public PipeEntrance(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
            .with(ORIENTATION, Orientation.NORTH)
            .with(CONNECTION, Orientation.NORTH)); // Default to NORTH, but will be updated upon placement
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION, CONNECTION);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getSide(); // Get the face of the block that was clicked
        Orientation orientation = Orientation.NORTH; // Default orientation

        for (Orientation o : Orientation.values()) {
            if (o.getDirection() == facing) {
                orientation = o;
                break;
            }
        }

        BlockState state = this.getDefaultState().with(ORIENTATION, orientation);
        Direction connectionDirection = getConnectionDirection(ctx, orientation.getDirection());
        Orientation connectionOrientation = Orientation.NORTH; // Default to NORTH

        for (Orientation o : Orientation.values()) {
            if (o.getDirection() == connectionDirection) {
                connectionOrientation = o;
                break;
            }
        }

        return state.with(CONNECTION, connectionOrientation);
    }

    private Direction getConnectionDirection(ItemPlacementContext ctx, Direction excludeDirection) {
        for (Direction direction : Direction.values()) {
            if (direction == excludeDirection) continue;
            BlockState neighborState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction));
            Block neighborBlock = neighborState.getBlock();
            if (neighborBlock instanceof PipeBlock || neighborBlock instanceof PipeEntrance) {
                return direction;
            }
        }
        return excludeDirection; // Default to the same direction if no connection found
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Block neighborBlock = neighborState.getBlock();
        if (neighborBlock instanceof PipeBlock || neighborBlock instanceof PipeEntrance) {
            Orientation connectionOrientation = Orientation.NORTH; // Default to NORTH

            for (Orientation o : Orientation.values()) {
                if (o.getDirection() == direction) {
                    connectionOrientation = o;
                    break;
                }
            }

            return state.with(CONNECTION, connectionOrientation);
        }
        return state;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            TransportManager.startTransport(player, pos, state.get(CONNECTION).getDirection());
        }
        return ActionResult.SUCCESS;
    }
}
