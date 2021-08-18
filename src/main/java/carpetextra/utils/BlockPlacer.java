package carpetextra.utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockPlacer
{
    public static Boolean HasDirectionProperty(BlockState state)
    {
        //malilib code
        for (Property<?> prop : state.getProperties())
        {
            if (prop instanceof DirectionProperty)
            {
                return true;
            }
        }
        return false;
    }
    public static DirectionProperty getFirstDirectionProperty(BlockState state)
    {
        //malilib code
        for (Property<?> prop : state.getProperties())
        {
            if (prop instanceof DirectionProperty)
            {
                return (DirectionProperty) prop;
            }
        }
        return DirectionProperty.of("north",Direction.NORTH);
    }
    public static BlockState alternativeBlockPlacement(Block block, ItemPlacementContext context)//World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        //actual alternative block placement code

        Direction facing;
        Vec3d vec3d = context.getHitPos();
        BlockPos pos = context.getBlockPos();
        double hitX = vec3d.x - pos.getX();
        BlockState state = block.getDefaultState();
        if (hitX<2 || !HasDirectionProperty(state) ) // vanilla
            return null;
        DirectionProperty property = getFirstDirectionProperty(state);
        int code = (int)(hitX-2)/2;
        int FacingId = code % 16;
        //
        // now it would be great if hitX was adjusted in context to original range from 0.0 to 1.0
        // since its actually using it. Its private - maybe with Reflections?
        //
        PlayerEntity placer = context.getPlayer();
        World world = context.getWorld();
        
        if (FacingId == 6) 
        {
            facing = placer.getHorizontalFacing().getOpposite();
        }
        else if (FacingId >= 0 && FacingId <= 5)
        {
            facing = Direction.byId(FacingId);
        }
        else
        {
            facing = Direction.UP;
        }
        
        if (property.getValues().contains(facing) == false) 
        {
            facing = placer.getHorizontalFacing().getOpposite();
            state = state.with(HorizontalFacingBlock.FACING, facing);
        }
        else
        {
            state = state.with(FacingBlock.FACING, facing);
        }
       
        //check blocks with additional states first
        if (block instanceof RepeaterBlock)
        {
            return state
                    .with(RepeaterBlock.DELAY, MathHelper.clamp(code / 16, 1, 4))
                    .with(RepeaterBlock.LOCKED, Boolean.FALSE);
        }
        else if (block instanceof TrapdoorBlock)
        {
            return state
                    .with(TrapdoorBlock.OPEN, Boolean.FALSE)
                    .with(TrapdoorBlock.HALF, (code >= 16) ? BlockHalf.TOP : BlockHalf.BOTTOM)
                    .with(TrapdoorBlock.OPEN, world.isReceivingRedstonePower(pos));
        }
        else if (block instanceof ComparatorBlock)
        {
            ComparatorMode m = (hitX >= 16)?ComparatorMode.SUBTRACT: ComparatorMode.COMPARE;
            return block.getDefaultState()
                    .with(ComparatorBlock.POWERED, Boolean.FALSE)
                    .with(ComparatorBlock.MODE, m);
        }
        else if (block instanceof StairsBlock)
        {
            return block.getPlacementState(context)//worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
                    .with(StairsBlock.FACING, facing)
                    .with(StairsBlock.HALF, ( hitX >= 16)?BlockHalf.TOP : BlockHalf.BOTTOM);
        }
        
        return state;
    }
}

