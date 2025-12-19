precision highp float;

varying vec4 v_color;
varying vec2 v_texCoords;

uniform float u_time;

// Decode pheromone type to actual color
vec3 getTypeColor(float typeIndex) {
    if (typeIndex < 0.2) {
        return vec3(0.1, 0.3, 1.0);  // GATHER - blue
    } else if (typeIndex < 0.5) {
        return vec3(1.0, 0.3, 0.3);  // ATTACK - red
    } else {
        return vec3(0.9, 0.9, 0.1);  // EXPLORE - yellow
    }
}

// Deterministic hash for consistent randomness [0, 1)
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

void main() {
    float trailStrength = v_color.g;  // 1.0 at start, 0.0 at end
    float seedVal = v_color.b;        // Random seed per pheromone
    vec3 baseColor = getTypeColor(v_color.r);
    
    vec2 uv = v_texCoords; // 0..1
    
    float alpha = 0.0;
    
    // Max spread radius relative to texture size
    float spread = 0.5; 
    
    // Size of each square (in UV units)
    float size = 0.04; 

    for (int i = 0; i < 10; i++) {
        vec2 p = vec2(seedVal * 100.0 + float(i) * 13.0, seedVal * 50.0 + float(i) * 7.0);

        // Calculate a random distance and angle + offset by time that's smoothly changing
        float dist = hash(p) * spread;
        float angle = hash(p + 23.0) * 6.28318530718;
        vec2 offset = vec2(cos(angle), sin(angle)) * dist * cos(u_time * hash(p));

        // Center of this square, offset by half the size of one square
        vec2 center = vec2(0.5) + offset + vec2(size / 2.0);

        // Check if current pixel is inside this square
        vec2 d = abs(uv - center);
        if (d.x < size && d.y < size) {
            alpha = 1.0;
            break;
        }
    }
    
    if (alpha < 0.1) discard;
    
    gl_FragColor = vec4(baseColor, 0.6);
}
