#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main(void) {
  // Grouping texcoord variables in order to make it work in the GMA 950. See post #13
  // in this thread:
  // http://www.idevgames.com/forums/thread-3467.html
  vec2 tc0 = vertTexCoord.st + vec2(-texOffset.s, -texOffset.t);
  vec2 tc1 = vertTexCoord.st + vec2(         0.0, -texOffset.t);
  vec2 tc2 = vertTexCoord.st + vec2(+texOffset.s, -texOffset.t);
  vec2 tc3 = vertTexCoord.st + vec2(-texOffset.s,          0.0);
  vec2 tc4 = vertTexCoord.st + vec2(         0.0,          0.0);
  vec2 tc5 = vertTexCoord.st + vec2(+texOffset.s,          0.0);
  vec2 tc6 = vertTexCoord.st + vec2(-texOffset.s, +texOffset.t);
  vec2 tc7 = vertTexCoord.st + vec2(         0.0, +texOffset.t);
  vec2 tc8 = vertTexCoord.st + vec2(+texOffset.s, +texOffset.t);

  vec2 tc00 = vertTexCoord.st + vec2(-2*texOffset.s, -2*texOffset.t);
  vec2 tc01 = vertTexCoord.st + vec2(-2*texOffset.s, -1*texOffset.t);
  vec2 tc02 = vertTexCoord.st + vec2(-2*texOffset.s, 0.0);
  vec2 tc03 = vertTexCoord.st + vec2(-2*texOffset.s, texOffset.t);
  vec2 tc04 = vertTexCoord.st + vec2(-2*texOffset.s, 2*texOffset.t);

  vec2 tc40 = vertTexCoord.st + vec2(2*texOffset.s, -2*texOffset.t);
  vec2 tc41 = vertTexCoord.st + vec2(2*texOffset.s, -1*texOffset.t);
  vec2 tc42 = vertTexCoord.st + vec2(2*texOffset.s, 0.0);
  vec2 tc43 = vertTexCoord.st + vec2(2*texOffset.s, texOffset.t);
  vec2 tc44 = vertTexCoord.st + vec2(2*texOffset.s, 2*texOffset.t);

  vec2 tc10 = vertTexCoord.st + vec2(-1*texOffset.s, -2*texOffset.t);
  vec2 tc20 = vertTexCoord.st + vec2(0.0, -2*texOffset.t);
  vec2 tc30 = vertTexCoord.st + vec2(texOffset.s, -2*texOffset.t);

  vec2 tc14 = vertTexCoord.st + vec2(-1*texOffset.s, 2*texOffset.t);
  vec2 tc24 = vertTexCoord.st + vec2(0.0, 2*texOffset.t);
  vec2 tc34 = vertTexCoord.st + vec2(texOffset.s, 2*texOffset.t);

  vec4 col0 = texture2D(texture, tc0);
  vec4 col1 = texture2D(texture, tc1);
  vec4 col2 = texture2D(texture, tc2);
  vec4 col3 = texture2D(texture, tc3);
  vec4 col4 = texture2D(texture, tc4);
  vec4 col5 = texture2D(texture, tc5);
  vec4 col6 = texture2D(texture, tc6);
  vec4 col7 = texture2D(texture, tc7);
  vec4 col8 = texture2D(texture, tc8);

  vec4 col00 = texture2D(texture, tc00);
  vec4 col01 = texture2D(texture, tc01);
  vec4 col02 = texture2D(texture, tc02);
  vec4 col03 = texture2D(texture, tc03);
  vec4 col04 = texture2D(texture, tc04);

  vec4 col40 = texture2D(texture, tc40);
  vec4 col41 = texture2D(texture, tc41);
  vec4 col42 = texture2D(texture, tc42);
  vec4 col43 = texture2D(texture, tc43);
  vec4 col44 = texture2D(texture, tc44);

  vec4 col10 = texture2D(texture, tc10);
  vec4 col20 = texture2D(texture, tc20);
  vec4 col30 = texture2D(texture, tc30);

  vec4 col14 = texture2D(texture, tc14);
  vec4 col24 = texture2D(texture, tc24);
  vec4 col34 = texture2D(texture, tc34);



  vec4 sum = (1.0 * col0 + 2.0 * col1 + 1.0 * col2 +  
              2.0 * col3 + 4.0 * col4 + 2.0 * col5 +
              1.0 * col6 + 2.0 * col7 + 1.0 * col8 +
              0.5 * col00 + 0.5 * col01 + 0.5 * col02 + 0.5 * col03 + 0.5 * col04 +
              0.5 * col40 + 0.5 * col41 + 0.5 * col42 + 0.5 * col43 + 0.5 * col44 +
              0.5 * col10 + 0.5 * col20 + 0.5 * col30 +
              0.5 * col14 + 0.5 * col24 + 0.5 * col34 ) / 32.0;
  gl_FragColor = vec4(sum.rgb, 1.0) * vertColor;
}
