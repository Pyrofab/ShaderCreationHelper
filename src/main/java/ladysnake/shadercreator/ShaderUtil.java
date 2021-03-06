package ladysnake.shadercreator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("WeakerAccess")
@SideOnly(Side.CLIENT)
public class ShaderUtil {

    public static int test = 0;

    private static int prevProgram = 0, currentProgram = 0;
    static final String RUNTIME_LOCATION_PREFIX = "shaders/";
    private static final String JAR_LOCATION_PREFIX = "/assets/shader_creator/shaders/";
    static String vertex = "vertex_base.vsh";
    static String fragment = "fragment_test.fsh";

    static {
        initShaders();
    }

    private static boolean shouldNotUseShaders() {
        return !OpenGlHelper.shadersSupported;
    }

    /**
     * Initializes all known shaders
     */
    public static void initShaders() {
        if (shouldNotUseShaders())
            return;
        test = initShader(vertex, fragment);
    }

    /**
     * Initializes a program with two shaders having the same name
     *
     * @param shaderName the common name or relative location of both shaders, minus the file extension
     * @return the reference to the initialized program
     */
    public static int initShader(String shaderName) {
        return initShader(shaderName + ".vsh", shaderName + ".fsh");
    }

    /**
     * Initializes a program with one or two shaders
     *
     * @param vertexLocation   the name or relative location of the vertex shader
     * @param fragmentLocation the name or relative location of the fragment shader
     * @return the reference to the initialized program
     */
    private static int initShader(String vertexLocation, String fragmentLocation) {

        // program creation
        int program = OpenGlHelper.glCreateProgram();

        // vertex shader creation
        if (vertexLocation != null && !vertexLocation.trim().isEmpty()) {
            int vertexShader = OpenGlHelper.glCreateShader(OpenGlHelper.GL_VERTEX_SHADER);
            ARBShaderObjects.glShaderSourceARB(vertexShader, fromFile(vertexLocation, false));
            OpenGlHelper.glCompileShader(vertexShader);
            OpenGlHelper.glAttachShader(program, vertexShader);
        }

        // fragment shader creation
        if (fragmentLocation != null && !fragmentLocation.trim().isEmpty()) {
            int fragmentShader = OpenGlHelper.glCreateShader(OpenGlHelper.GL_FRAGMENT_SHADER);
            ARBShaderObjects.glShaderSourceARB(fragmentShader, fromFile(fragmentLocation, false));
            OpenGlHelper.glCompileShader(fragmentShader);
            OpenGlHelper.glAttachShader(program, fragmentShader);
        }

        OpenGlHelper.glLinkProgram(program);

        return program;
    }

    /**
     * Sets the currently used program
     *
     * @param program the reference to the desired shader (0 to remove any current shader)
     */
    public static void useShader(int program) {
        if (shouldNotUseShaders())
            return;

        prevProgram = GlStateManager.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        OpenGlHelper.glUseProgram(program);

        currentProgram = program;
    }

    /**
     * Sets the value of a uniform from the current program
     *
     * @param uniformName the uniform's name
     * @param value       an int value for this uniform
     */
    public static void setUniform(String uniformName, int value) {
        if (shouldNotUseShaders() || currentProgram == 0)
            return;

        int uniform = GL20.glGetUniformLocation(currentProgram, uniformName);
        if (uniform != -1)
            GL20.glUniform1i(uniform, value);
    }

    /**
     * Sets the value of a uniform from the current program
     *
     * @param uniformName the name of the uniform field to retrieve
     * @param value       a float value for this uniform
     */
    public static void setUniform(String uniformName, float value) {
        if (shouldNotUseShaders())
            return;

        int uniform = GL20.glGetUniformLocation(currentProgram, uniformName);
        if (uniform != -1)
            GL20.glUniform1f(uniform, value);
    }

    public static void setUniform(String uniformName, float[] values) {
        if (shouldNotUseShaders())
            return;

        int uniform = GL20.glGetUniformLocation(currentProgram, uniformName);
        if (uniform != -1) {
            switch (values.length) {
                case 2:
                    GL20.glUniform2f(uniform, values[0], values[1]);
                    break;
                case 3:
                    GL20.glUniform3f(uniform, values[0], values[1], values[2]);
                    break;
                case 4:
                    GL20.glUniform4f(uniform, values[0], values[1], values[2], values[3]);
                    break;
            }
        }
    }

    public static void setUniform(String uniformName, FloatBuffer mat4) {
        if (shouldNotUseShaders())
            return;

        int uniform = GL20.glGetUniformLocation(currentProgram, uniformName);
        if (uniform != -1) {
            GL20.glUniformMatrix4(uniform, true, mat4);
        }
    }

    /**
     * Binds any number of additional textures to be used by the current shader
     */
    public static void bindAdditionalTextures(ResourceLocation... textures) {
        for (int i = 0; i < textures.length; i++) {
            ResourceLocation texture = textures[i];
            // don't mess with the lightmap (1) nor the default texture (0)
            GlStateManager.setActiveTexture(i + OpenGlHelper.defaultTexUnit + 2);
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            // start texture uniforms at 1, as 0 would be the default texture which doesn't require any special operation
            setUniform("texture" + (i + 1), i + 2);
        }
    }

    public static FloatBuffer getProjectionMatrix() {
        ByteBuffer projection = ByteBuffer.allocateDirect(16 * Float.BYTES);
        projection.order(ByteOrder.nativeOrder());
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, (FloatBuffer) projection.asFloatBuffer().position(0));
        return projection.asFloatBuffer();
    }

    public static FloatBuffer getProjectionMatrixInverse() {
        FloatBuffer projection = ShaderUtil.getProjectionMatrix();
        FloatBuffer projectionInverse = ByteBuffer.allocateDirect(16 * Float.BYTES).asFloatBuffer();
        MatUtil.invertMat4FBFA((FloatBuffer) projectionInverse.position(0), (FloatBuffer) projection.position(0));
        projection.position(0);
        projectionInverse.position(0);
        return projectionInverse;
    }

    public static FloatBuffer getModelViewMatrix() {
        ByteBuffer modelView = ByteBuffer.allocateDirect(16 * Float.BYTES);
        modelView.order(ByteOrder.nativeOrder());
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, (FloatBuffer) modelView.asFloatBuffer().position(0));
        return modelView.asFloatBuffer();
    }

    public static FloatBuffer getModelViewMatrixInverse() {
        FloatBuffer modelView = ShaderUtil.getModelViewMatrix();
        FloatBuffer modelViewInverse = ByteBuffer.allocateDirect(16 * Float.BYTES).asFloatBuffer();
        MatUtil.invertMat4FBFA((FloatBuffer) modelViewInverse.position(0), (FloatBuffer) modelView.position(0));
        modelView.position(0);
        modelViewInverse.position(0);
        return modelViewInverse;
    }

    /**
     * Reverts to the previous shader used
     */
    public static void revert() {
        useShader(prevProgram);
    }

    /**
     * Reads a text file into a single String
     *
     * @param filename the path to the file to read
     * @return a string with the content of the file
     */
    private static String fromFile(String filename, boolean fromJar) {
        StringBuilder source = new StringBuilder();

        try (InputStream in = fromJar
                ? ShaderUtil.class.getResourceAsStream(JAR_LOCATION_PREFIX + filename)
                : Files.newInputStream(Paths.get(RUNTIME_LOCATION_PREFIX + filename));
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null)
                source.append(line).append('\n');
        } catch (IOException exc) {
            ShaderCreator.LOGGER.error(exc);
        } catch (NullPointerException e) {
            if (!fromJar) {
                ShaderCreator.LOGGER.warn(filename + "could not be found in run directory. Searching in packaged files...");
                return fromFile(filename, true);
            }
            ShaderCreator.LOGGER.error(e + " : " + filename + " does not exist");
        }

        return source.toString();
    }

}
