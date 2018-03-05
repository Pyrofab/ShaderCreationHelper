package ladysnake.shadercreator;

import ladysnake.shadercreator.entity.RenderTestEntity;
import ladysnake.shadercreator.entity.RenderTestEntityBillboard;
import ladysnake.shadercreator.entity.TestEntity;
import ladysnake.shadercreator.entity.TestEntityBillboard;
import ladysnake.shadercreator.items.ItemDebug;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ShaderCreator.MOD_ID,
        name = ShaderCreator.MOD_NAME,
        version = ShaderCreator.VERSION,
        clientSideOnly = true
)
public class ShaderCreator {

    public static final String MOD_ID = "shader_creator";
    static final String MOD_NAME = "ShaderCreator";
    static final String VERSION = "1.0-SNAPSHOT";

    public static final ResourceLocation TEST_SHADER = new ResourceLocation(MOD_ID, "test");

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static ShaderCreator INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger("Shader Creator");

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(TestEntity.class, RenderTestEntity::new);
        RenderingRegistry.registerEntityRenderingHandler(TestEntityBillboard.class, RenderTestEntityBillboard::new);
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandLoad());
        event.registerServerCommand(new CommandUse());
    }

    /**
     * Forge will automatically look up and bind items to the fields in this class
     * based on their registry name.
     */
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class ModItems {
        public static final Item debug = Items.AIR;
    }

    /**
     * This is a special class that listens to registry events, to allow creation of mod blocks and items at the proper time.
     */
    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        /**
         * Listen for the register event for creating custom items
         */
        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().register(new ItemDebug().setRegistryName("debug_item"));
        }

        @SubscribeEvent
        public static void addEntities(RegistryEvent.Register<EntityEntry> event) {
            event.getRegistry().registerAll(
                    EntityEntryBuilder.create()
                            .entity(TestEntity.class)
                            .factory(TestEntity::new)
                            .id(new ResourceLocation(MOD_ID, "test"), 0)
                            .name("test")
                            .egg(0x100, 0x99)
                            .tracker(64, 1, true).build(),
                    EntityEntryBuilder.create()
                            .entity(TestEntityBillboard.class)
                            .factory(TestEntityBillboard::new)
                            .id(new ResourceLocation(MOD_ID, "test_billboard"), 1)
                            .name("test")
                            .egg(0x100, 0x99)
                            .tracker(64, 1, true).build());
        }

    }
}
