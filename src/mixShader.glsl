#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;

uniform sampler2D b;
uniform sampler2D c;
uniform float bOpacity = 0.0;

uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  vec4 aColor = texture2D(texture, vertTexCoord.st);
  vec4 bColor = texture2D(b, vertTexCoord.st);
  vec4 cColor = texture2D(c, vertTexCoord.st);
  vec4 firstBlend = mix (aColor, bColor, bColor.a*bOpacity);
  vec4 secondBlend = mix (firstBlend, cColor, cColor.a);

  gl_FragColor = secondBlend;
}
