#version 120
// glsl version. We're way behind the recommended but blame mojang

// varying variables are given by the vertex shader. See VertexBase.vsh
varying vec2 texcoord;        // the texture coordinate of the current pixel
varying vec4 vPosition;       // the screen position of the current pixel

// uniform variables are given in the java code
uniform sampler2D texture;    // represents what's currently displayed on the screen
uniform float iTime;

float rand(vec2 co) {
   return fract(sin(dot(co.xy, vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    vec4 rgba = texture2D(texture, texcoord);
    float gs = rgba.r + rgba.g + rgba.b;
    gl_FragColor = vec4(1-rgba.rgb, gs);
    // gl_FragColor = vec4(rgba.r * mod(iTime, 1000)/1000, rgba.g * mod(iTime, 100)/100, rgba.b * mod(iTime, 10)/10, rgba.a);
}
