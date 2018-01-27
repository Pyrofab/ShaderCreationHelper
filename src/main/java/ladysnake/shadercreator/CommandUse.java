package ladysnake.shadercreator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class CommandUse extends CommandBase {
    private static Field _listShaders;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().entityRenderer.isShaderActive()) {
            ShaderGroup sg = Minecraft.getMinecraft().entityRenderer.getShaderGroup();
            try {
                @SuppressWarnings("unchecked")
                List<Shader> shaders = (List<Shader>) _listShaders.get(sg);
                for (Shader s : shaders) {
                    ShaderUniform su = s.getShaderManager().getShaderUniform("iTime");
                    if (su != null) {
                        su.set((float) System.currentTimeMillis());
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "useshader";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/useshader <shadername>";
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.singletonList("fade_in_blur.json");
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (_listShaders == null) {
                _listShaders = ReflectionHelper.findField(ShaderGroup.class, "field_148031_d", "listShaders");
            }
            if (Minecraft.getMinecraft().world != null) {
                EntityRenderer er = Minecraft.getMinecraft().entityRenderer;
                if (args.length >= 1) {
                    MinecraftForge.EVENT_BUS.register(this);
                    er.loadShader(new ResourceLocation("shaders/post/" + args[0]));
                } else {
                    er.stopUseShader();
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        });
    }
}
