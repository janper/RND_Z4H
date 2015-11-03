#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D flakes;

uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
   vec4 aboveFlake = texture2D (flakes,vertTexCoord.st +vec2 (0.0,texOffset.t));
   vec4 outColor;

   if (aboveFlake.a!=0.0){
    vec4 belowObstacle = texture2D (texture,vertTexCoord.st +vec2 (0.0,-texOffset.t));
//    vec4 leftObstacle = vertTexCoord.st + texture2D (obstacles,vec2 (-texOffset.s,0.0));
//    vec4 rightObstacle = vertTexCoord.st + texture2D (obstacles,vec2 (+texOffset.s,0.0));

    if (belowObstacle.a==0){
        outColor = vec4(0.0);
    } else {
        outColor = texture2D(flakes, vertTexCoord.st);
    }
   }

  gl_FragColor = mix (texture2D(texture, vertTexCoord.st),outColor,0.0);
}
