#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertTexCoord;

varying vec4 vertColor;

uniform float dim;

void main() {
	gl_FragColor = mix (texture2D(texture, vertTexCoord.st), vec4(0.0), dim);;
}
