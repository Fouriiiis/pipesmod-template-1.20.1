package com.pipesmod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public class PipeEntrance extends Block {
    
    //enum for orientation
    public enum Orientation implements StringIdentifiable {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        UP,
        DOWN;

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }

    public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.of("orientation", Orientation.class);

    public PipeEntrance(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(ORIENTATION, Orientation.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getPlayerLookDirection().getOpposite(); // Get the direction the player is looking
        Orientation orientation;

        switch (facing) {
            case NORTH:
                orientation = Orientation.NORTH;
                break;
            case SOUTH:
                orientation = Orientation.SOUTH;
                break;
            case EAST:
                orientation = Orientation.EAST;
                break;
            case WEST:
                orientation = Orientation.WEST;
                break;
            case UP:
                orientation = Orientation.UP;
                break;
            case DOWN:
                orientation = Orientation.DOWN;
                break;
            default:
                orientation = Orientation.NORTH;
                break;
        }

        return this.getDefaultState().with(ORIENTATION, orientation);
    }
}
