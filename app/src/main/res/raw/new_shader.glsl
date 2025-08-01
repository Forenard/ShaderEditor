#version 300 es
precision highp float;
precision highp int;
precision highp uint;
out vec4 outColor;
uniform float time;
uniform vec2 resolution;

void main() {
	vec2 fc=gl_FragCoord.xy,res=resolution,asp=res/min(res.x,res.y);
	vec2 uv=fc/res,suv=(uv*2.-1.)*asp;
	vec3 c=vec3(step(length(suv),.5));
	outColor = vec4(c,1);
}