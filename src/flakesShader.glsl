#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D obstacles;

uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
   vec4 aboveFlake = texture2D (texture,vertTexCoord.st + vec2 (0.0,texOffset.t));
   vec4 outColor;
   vec4 blank = vec4(0.0);

//   if ((aboveFlake.r!=0.0)||(aboveFlake.g!=0.0)||(aboveFlake.b!=0.0)||(aboveFlake.a!=0.0)){
   if (aboveFlake!=blank){
    vec4 belowObstacle =  texture2D (obstacles,vertTexCoord.st + vec2 (0.0,-1*texOffset.t));
//    vec4 leftObstacle = vertTexCoord.st + texture2D (obstacles,vec2 (-texOffset.s,0.0));
//    vec4 rightObstacle = vertTexCoord.st + texture2D (obstacles,vec2 (+texOffset.s,0.0));

    if (belowObstacle.a==0.0){
            outColor = aboveFlake;
    } else {
        outColor = vec4(1.0,1.0,0.0,1.0);
    }
   } else {
        outColor = vec4(0.0);
   }

  gl_FragColor = outColor;
}
