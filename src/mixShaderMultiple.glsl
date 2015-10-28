#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D textureAlfa;
uniform float textureOpacity = 1.0;

uniform float opacity = 1.0;

uniform sampler2D layer0;
uniform sampler2D layer1;
uniform sampler2D layer2;
uniform sampler2D layer3;
uniform sampler2D layer4;
uniform sampler2D layer0Alfa;
uniform sampler2D layer1Alfa;
uniform sampler2D layer2Alfa;
uniform sampler2D layer3Alfa;
uniform sampler2D layer4Alfa;
uniform float layer0Opacity = 0.0;
uniform float layer1Opacity = 0.0;
uniform float layer2Opacity = 0.0;
uniform float layer3Opacity = 0.0;
uniform float layer4Opacity = 0.0;

uniform int no = 0;

uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  vec4 texColor = texture2D(texture, vertTexCoord.st).rgba;
  vec4 baseColor= mix(texColor, vec4(0.0, 0.0, 0.0, 0.0), 1.0-texture2D(textureAlfa, vertTexCoord.st).r*textureOpacity);

  if (no>0) {
    texColor = vec4(texture2D(layer0, vertTexCoord.st).rgb, 1.0);
    baseColor = mix ( baseColor, texColor , (texture2D(layer0Alfa,vertTexCoord.st).r*layer0Opacity) );
  }

  if (no>1) {
      texColor = vec4(texture2D(layer1, vertTexCoord.st).rgb, 1.0);
      baseColor = mix ( baseColor, texColor , (texture2D(layer1Alfa,vertTexCoord.st).r*layer1Opacity) );
  }

  if (no>2) {
       texColor = vec4(texture2D(layer2, vertTexCoord.st).rgb, 1.0);
       baseColor = mix ( baseColor, texColor , (texture2D(layer2Alfa,vertTexCoord.st).r*layer2Opacity) );
  }

  if (no>3) {
      texColor = vec4(texture2D(layer3, vertTexCoord.st).rgb, 1.0);
      baseColor = mix ( baseColor, texColor , (texture2D(layer3Alfa,vertTexCoord.st).r*layer3Opacity) );
  }

  if (no>4) {
      texColor = vec4(texture2D(layer4, vertTexCoord.st).rgb, 1.0);
      baseColor = mix ( baseColor, texColor , (texture2D(layer4Alfa,vertTexCoord.st).r*layer4Opacity) );
  }

  gl_FragColor = mix (vec4(0.0,0.0,0.0,0.0), baseColor, opacity);
}
