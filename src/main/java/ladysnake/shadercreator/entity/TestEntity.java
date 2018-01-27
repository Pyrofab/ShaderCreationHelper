package ladysnake.shadercreator.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TestEntity extends Entity {

    public TestEntity(World worldIn) {
        super(worldIn);
        setSize(4, 4);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {

    }
}
