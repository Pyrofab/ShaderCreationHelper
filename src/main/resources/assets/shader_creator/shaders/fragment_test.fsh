#version 120
// glsl version. We're way behind the recommended but blame mojang

// varying variables are given by the vertex shader. See VertexBase.vsh
varying vec2 texcoord;        // the texture coordinate of the current pixel
varying vec4 vPosition;       // the screen position of the current pixel

// uniform variables are given in the java code
uniform sampler2D texture;    // represents what's currently displayed on the screen

void main() {
    gl_FragColor = texture2D(texture, texcoord);
}
