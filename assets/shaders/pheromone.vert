attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_localPos;

void main() {
    v_color = a_color;
    v_texCoords = a_texCoord0;
    
    // Pass the texture coords - for SpriteBatch these are 0-1 within the sprite
    // But for a 1x1 texture they're all the same point, so we need another approach
    // We'll use a_texCoord0 directly since SpriteBatch generates proper quad UVs
    v_localPos = a_texCoord0;
    
    gl_Position = u_projTrans * a_position;
}
