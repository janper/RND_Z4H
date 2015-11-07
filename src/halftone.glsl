#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertTexCoord;

varying vec4 vertColor;

uniform float minThreshold;
uniform float maxThreshold;

void main() {

	float midValue = (minThreshold+maxThreshold)/2.0;
	vec4 color = texture2D(texture, vertTexCoord.st);

	float value = (color.r+color.g+color.b)/3.0;

	vec4 outColor;

	if (value<midValue){
	    outColor = vec4(vec3(1.0),smoothstep(minThreshold,midValue, value));
	} else {
	    outColor = vec4(vec3(1.0),1.0-smoothstep(midValue,maxThreshold, value));
	}

//if (value>minThreshold && value<maxThreshold){
//outColor = vec4 (1.0);
//} else {
//outColor = vec4 (1.0,0.0,0.0,1.0);
//}

	gl_FragColor = outColor;
}
