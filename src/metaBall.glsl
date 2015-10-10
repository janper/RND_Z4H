#version 120

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_COLOR_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertColor;

uniform vec3 pt00;
uniform vec3 pt01;
uniform vec3 pt02;
uniform vec3 pt03;
uniform vec3 pt04;
uniform vec3 pt05;
uniform vec3 pt06;
uniform vec3 pt07;
uniform vec3 pt08;
uniform vec3 pt09;

uniform float minThreshold;
uniform float maxThreshold;

void main() {

	float dst = 1.0-smoothstep( 0.0, pt00.z,distance (gl_FragCoord.xy , pt00.xy));
	dst += 1.0-smoothstep( 0.0, pt01.z,distance (gl_FragCoord.xy , pt01.xy));
	dst += 1.0-smoothstep( 0.0, pt02.z,distance (gl_FragCoord.xy , pt02.xy));
	dst += 1.0-smoothstep( 0.0, pt03.z,distance (gl_FragCoord.xy , pt03.xy));
	dst += 1.0-smoothstep( 0.0, pt04.z,distance (gl_FragCoord.xy , pt04.xy));
	dst += 1.0-smoothstep( 0.0, pt05.z,distance (gl_FragCoord.xy , pt05.xy));
	dst += 1.0-smoothstep( 0.0, pt06.z,distance (gl_FragCoord.xy , pt06.xy));
	dst += 1.0-smoothstep( 0.0, pt07.z,distance (gl_FragCoord.xy , pt07.xy));
	dst += 1.0-smoothstep( 0.0, pt08.z,distance (gl_FragCoord.xy , pt08.xy));
	dst += 1.0-smoothstep( 0.0, pt09.z,distance (gl_FragCoord.xy , pt09.xy));

	dst = smoothstep(0.0,1.0, dst);

    float thresh = 0.2;
    float a = 0.0;

	if ((dst>minThreshold)&&(dst<maxThreshold)){
	    a = 1.0;

	    float position = (dst-minThreshold)/(maxThreshold-minThreshold);
	    if (position>(0.5+thresh)){
            a = smoothstep(1.0,0.5+thresh, position);
        } else {
            if (position<(0.5-thresh)){
                a = smoothstep(0.0,0.5-thresh, position);
            }
	    }
	}

	gl_FragColor = vertColor*vec4(vec3(1.0), a);
}
