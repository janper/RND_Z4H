#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D textureAlfa;
uniform float textureOpacity = 1.0;

uniform float opacity = 1.0;

uniform sampler2D layer1;
uniform sampler2D layer2;
uniform sampler2D layer3;
uniform sampler2D layer4;
uniform sampler2D layer5;
uniform sampler2D layer1Alfa;
uniform sampler2D layer2Alfa;
uniform sampler2D layer3Alfa;
uniform sampler2D layer4Alfa;
uniform sampler2D layer5Alfa;
uniform float layer1Opacity = 1.0;
uniform float layer2Opacity = 1.0;
uniform float layer3Opacity = 1.0;
uniform float layer4Opacity = 1.0;
uniform float layer5Opacity = 1.0;

uniform int no = 0;

uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  vec4 texColor = texture2D(texture, vertTexCoord.st).rgba;
  vec4 maskColor = texture2D(textureAlfa, vertTexCoord.st).rgba;
  vec4 baseColor= mix(texColor, vec4(0, 0, 0, 0), 1.0 - maskColor.r*textureOpacity);

  vec4 texColor2Temp;
  vec4 currentColor;

  if (no>0) {
    texColor2Temp = texture2D(layer1, vertTexCoord.st).rgba;
    maskColor = texture2D(layer1Alfa, vertTexCoord.st).rgba;
    currentColor = mix(texColor2Temp, vec4(0, 0, 0, 0), 1.0 - maskColor.r);
    baseColor = mix(baseColor, currentColor, (currentColor.a*layer1Opacity));
  }


  if (no>1) {
      texColor2Temp = texture2D(layer2, vertTexCoord.st).rgba;
      maskColor = texture2D(layer2Alfa, vertTexCoord.st).rgba;
      currentColor = mix(texColor2Temp, vec4(0, 0, 0, 0), 1.0 - maskColor.r);
      baseColor = mix(baseColor, currentColor, (currentColor.a*layer2Opacity));
  }

  if (no>2) {
        texColor2Temp = texture2D(layer3, vertTexCoord.st).rgba;
        maskColor = texture2D(layer3Alfa, vertTexCoord.st).rgba;
        currentColor = mix(texColor2Temp, vec4(0, 0, 0, 0), 1.0 - maskColor.r);
        baseColor = mix(baseColor, currentColor, (currentColor.a*layer3Opacity));
  }

  if (no>3) {
          texColor2Temp = texture2D(layer4, vertTexCoord.st).rgba;
          maskColor = texture2D(layer4Alfa, vertTexCoord.st).rgba;
          currentColor = mix(texColor2Temp, vec4(0, 0, 0, 0), 1.0 - maskColor.r);
          baseColor = mix(baseColor, currentColor, (currentColor.a*layer4Opacity));
  }

  if (no>4) {
          texColor2Temp = texture2D(layer5, vertTexCoord.st).rgba;
          maskColor = texture2D(layer5Alfa, vertTexCoord.st).rgba;
          currentColor = mix(texColor2Temp, vec4(0, 0, 0, 0), 1.0 - maskColor.r);
          baseColor = mix(baseColor, currentColor, (currentColor.a*layer5Opacity));
  }

  gl_FragColor = mix (vec4(0.0,0.0,0.0,0.0), baseColor, opacity);
}