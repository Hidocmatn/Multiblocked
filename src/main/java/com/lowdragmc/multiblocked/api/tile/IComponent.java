package com.lowdragmc.multiblocked.api.tile;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.multiblocked.Multiblocked;
import com.lowdragmc.multiblocked.api.definition.ComponentDefinition;
import com.lowdragmc.multiblocked.api.kubejs.events.UpdateRendererEvent;
import com.lowdragmc.multiblocked.client.renderer.IMultiblockedRenderer;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * @author KilaBash
 * @date 2022/06/01
 * @implNote IComponent
 */
public interface IComponent {
    default TileEntity self() {
        return (TileEntity) this;
    }

    ComponentDefinition getDefinition();

    default ActionResultType use(PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return ActionResultType.PASS;
    }

    default void onNeighborChange() {}

    default void setOwner(UUID uuid) {}

    default boolean isValidFrontFacing(Direction up) {
        return getDefinition().properties.rotationState.test(up);
    }

    default void setFrontFacing(Direction facing) {
        World level = self().getLevel();
        if (level != null && !level.isClientSide) {
            if (!isValidFrontFacing(facing)) return;
            if (self().getBlockState().getValue(BlockStateProperties.FACING) == facing) return;
            level.setBlock(self().getBlockPos(), self().getBlockState().setValue(BlockStateProperties.FACING, facing), 3);
        }
    }

    default String getSubID() {
        return getDefinition().getID();
    }

    default IMultiblockedRenderer updateCurrentRenderer() {
        IMultiblockedRenderer renderer = getDefinition().getStatus(getStatus()).getRenderer();
        if (Multiblocked.isKubeJSLoaded() && self().getLevel() != null) {
            UpdateRendererEvent event = new UpdateRendererEvent(this, renderer);
            event.post(ScriptType.of(self().getLevel()), UpdateRendererEvent.ID, getSubID());
            renderer = event.getRenderer();
        }
        return renderer;
    }

    default Direction getFrontFacing() {
        return self().getBlockState().getValue(BlockStateProperties.FACING);
    }

    default void rotateTo(Rotation direction) {
        setFrontFacing(direction.rotate(getFrontFacing()));
    }

    default void onDrops(NonNullList<ItemStack> drops, PlayerEntity entity) {
        drops.add(getDefinition().getStackForm());
    }

    default boolean canConnectRedstone(Direction direction) {
        return false;
    }

    IRenderer getRenderer();

    boolean isFormed();

    void setRendererObject(Object o);

    Object getRendererObject();

    String getStatus();

    void setStatus(String status);

    default VoxelShape getDynamicShape() {
        return getDefinition().getStatus(getStatus()).getShape(getFrontFacing());
    }
}
