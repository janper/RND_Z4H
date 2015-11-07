#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertTexCoord;

varying vec4 vertColor;

void main() {
    float alpha;
    float range = 0.1;

    if (vertTexCoord.t<range){
        alpha = smoothstep(0.0, texture2D(texture, vertTexCoord.st).a, 1.0-vertTexCoord.t/range);
    }
    if (vertTexCoord.t>1-range){
            alpha = smoothstep(texture2D(texture, vertTexCoord.st).a,0.0, 1.0- (vertTexCoord.t-(1-range))/range);
        }

	gl_FragColor = mix (texture2D(texture, vertTexCoord.st), vec4(0.0), alpha);
}
