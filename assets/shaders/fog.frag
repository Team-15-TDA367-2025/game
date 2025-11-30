#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;

// Camera/world uniforms
uniform vec2 u_cameraPos;      // Camera position in world
uniform float u_cameraZoom;    // Camera zoom level
uniform vec2 u_viewportSize;   // Viewport size in world units (before zoom)

//--------------------------------------------------
// Perlin Noise
//--------------------------------------------------
float rand3(vec3 p) {
    return fract(sin(dot(p, vec3(127.1, 311.7, 74.7))) * 43758.5453123);
}

vec3 randomGradient(vec3 p) {
    float x = rand3(p);
    float y = rand3(p + 13.37);
    float z = rand3(p + 42.69);
    return normalize(vec3(x, y, z) * 2.0 - 1.0);
}

vec3 fade(vec3 t) {
    return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
}

float perlin3(vec3 p) {
    vec3 pi = floor(p);
    vec3 pf = fract(p);
    vec3 w = fade(pf);

    float n000 = dot(randomGradient(pi + vec3(0,0,0)), pf - vec3(0,0,0));
    float n100 = dot(randomGradient(pi + vec3(1,0,0)), pf - vec3(1,0,0));
    float n010 = dot(randomGradient(pi + vec3(0,1,0)), pf - vec3(0,1,0));
    float n110 = dot(randomGradient(pi + vec3(1,1,0)), pf - vec3(1,1,0));
    float n001 = dot(randomGradient(pi + vec3(0,0,1)), pf - vec3(0,0,1));
    float n101 = dot(randomGradient(pi + vec3(1,0,1)), pf - vec3(1,0,1));
    float n011 = dot(randomGradient(pi + vec3(0,1,1)), pf - vec3(0,1,1));
    float n111 = dot(randomGradient(pi + vec3(1,1,1)), pf - vec3(1,1,1));

    float nx00 = mix(n000, n100, w.x);
    float nx10 = mix(n010, n110, w.x);
    float nx01 = mix(n001, n101, w.x);
    float nx11 = mix(n011, n111, w.x);
    float nxy0 = mix(nx00, nx10, w.y);
    float nxy1 = mix(nx01, nx11, w.y);
    return mix(nxy0, nxy1, w.z);
}

float fbm(vec3 p) {
    float sum = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 5; i++) {
        sum += perlin3(p) * amp;
        p *= 2.0;
        amp *= 0.5;
    }
    return sum;
}

void main() {
    // Calculate world coordinates from screen UV
    // Screen UV (0,0) to (1,1) maps to camera view
    vec2 screenOffset = v_texCoords - 0.5;  // -0.5 to 0.5
    
    // Effective viewport size (what we see after zoom)
    vec2 effectiveViewport = u_viewportSize / u_cameraZoom;
    
    // World position = camera position + offset scaled by viewport
    vec2 worldPos = u_cameraPos + screenOffset * effectiveViewport;
    
    // Noise parameters - now in WORLD units
    float noiseScale = 1.5;  // Noise frequency in world space
    float animTime = u_time * 0.1;
    
    // Generate 2D noise offset using world coordinates
    float noiseX = fbm(vec3(worldPos * noiseScale, animTime));
    float noiseY = fbm(vec3(worldPos * noiseScale + 100.0, animTime + 50.0));
    
    // Distortion amount in UV space
    // Convert world-space distortion to UV distortion
    float distortWorld = 2.0;  // Distort by up to 2 world units
    vec2 distortUV = vec2(noiseX, noiseY) * (distortWorld / effectiveViewport);
    
    // Sample fog mask with distorted coordinates
    vec2 distortedUV = v_texCoords + distortUV;
    float fogMask = texture2D(u_texture, distortedUV).r;
    
    // Also sample original for blending
    float originalMask = texture2D(u_texture, v_texCoords).r;
    
    // Blend between distorted and original
    float blendFactor = 0.99;
    float finalMask = mix(originalMask, fogMask, blendFactor);
    
    // Apply some edge softening
    float softMask = smoothstep(0.3, 0.7, finalMask);
    
    // Fog color - dark forest green
    vec3 fogColor = vec3(0.09, 0.188, 0.11);
    float fogAlpha = 0.9;
    
    // Final output
    gl_FragColor = vec4(fogColor, softMask * fogAlpha) * v_color;
}
