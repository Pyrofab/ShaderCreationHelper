package ladysnake.shadercreator;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MatUtilTest {
    @org.junit.Test
    public void invertMat4Identity() {
        float[] regular = new float[] {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
        float[] inverted = new float[16];
        MatUtil.invertMat4(inverted, regular);
        assertArrayEquals(regular, inverted, 0);
    }

    @org.junit.Test
    public void invertMat4() {
        float[] regular = new float[] {1,2,0,0,0,0,1,2,1,4,0,0,0,0,5,6};
        float[] inverted = new float[16];
        MatUtil.invertMat4(inverted, regular);
        float[] oracle = new float[] {
                2,      0,    -1,     0,
                -1f/2f,      0, 1f/2f,     0,
                0, -3f/2f,     0,  1/2f,
                0,  5f/4f,     0, -1/4f
        };
        assertArrayEquals(oracle, inverted, 0);
    }


}