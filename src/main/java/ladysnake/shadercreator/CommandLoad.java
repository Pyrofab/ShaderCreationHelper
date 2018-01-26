package ladysnake.shadercreator;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CommandLoad extends CommandBase {
    private static final Pattern FRAGMENT = Pattern.compile(".*\\.fsh");
    private static final Pattern VERTEX = Pattern.compile(".*\\.vsh");

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Nonnull
    @Override
    public String getName() {
        return "load";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/load <fragment|vertex>";
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> ret = new ArrayList<>();
        File[] allShaders = new File(ShaderUtil.RUNTIME_LOCATION_PREFIX).listFiles();
        assert allShaders != null;
        for(File f : allShaders)
            if(FRAGMENT.matcher(f.getName()).matches() || VERTEX.matcher(f.getName()).matches())
                ret.add(f.getName());
        return getListOfStringsMatchingLastWord(args, ret);
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        if(args.length >= 1) {
            File[] allShaders = new File(ShaderUtil.RUNTIME_LOCATION_PREFIX).listFiles();
            for(File f : allShaders != null ? allShaders : new File[0]) {
                if(f.getName().equals(args[0])) {
                    if(VERTEX.matcher(args[0]).matches())
                        ShaderUtil.vertex = args[0];
                    else if(FRAGMENT.matcher(args[0]).matches())
                        ShaderUtil.fragment = args[0];
                }
            }
        }
//        ShaderUtil.initShaders();
    }
}
