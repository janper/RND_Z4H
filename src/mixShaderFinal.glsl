#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform float textureOpacity = 1.0;

uniform float opacity = 1.0;

uniform int bCount = 0;

uniform sampler2D b0;
uniform sampler2D b1;
uniform sampler2D b2;
uniform sampler2D b3;
uniform sampler2D b4;

uniform float b0Opacity = 0.0;
uniform float b1Opacity = 0.0;
uniform float b2Opacity = 0.0;
uniform float b3Opacity = 0.0;
uniform float b4Opacity = 0.0;

uniform int fCount = 0;

uniform sampler2D f0;
uniform sampler2D f1;
uniform sampler2D f2;
uniform sampler2D f3;
uniform sampler2D f4;

uniform float f0Opacity = 0.0;
uniform float f1Opacity = 0.0;
uniform float f2Opacity = 0.0;
uniform float f3Opacity = 0.0;
uniform float f4Opacity = 0.0;

uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

vec4 bendCurrent (vec4 c, sampler2D s, float o){
    vec4 a = texture2D(s, vertTexCoord.st);
    return mix (a, c, a.a*o);
}

void main() {

  vec4 c = vec4(0.0);

  if (bCount>0){
    c = bendCurrent (c, b0, b0Opacity);
  }

  if (bCount>1){
    c = bendCurrent (c, b1, b1Opacity);
  }

  if (bCount>2){
    c = bendCurrent (c, b2, b2Opacity);
  }

  if (bCount>3){
    c = bendCurrent (c, b3, b3Opacity);
  }

  if (bCount>4){
    c = bendCurrent (c, b4, b4Opacity);
  }

  c = bendCurrent (c, texture, textureOpacity);

   if (fCount>0){
      c = bendCurrent (c, f0, f0Opacity);
   }

   if (fCount>1){
     c = bendCurrent (c, f1, f1Opacity);
   }

   if (fCount>2){
     c = bendCurrent (c, f2, f2Opacity);
   }

   if (fCount>3){
     c = bendCurrent (c, f3, f3Opacity);
   }

   if (fCount>4){
    c = bendCurrent (c, f4, f4Opacity);
   }

  gl_FragColor = vec4(c.rgb, c.a*opacity);
}
