#version 300 es
precision highp float;
precision highp int;
precision highp uint;
out vec4 outColor;
uniform float time;
uniform vec2 resolution;
uniform vec2 touch;
uniform sampler2D backbuffer;
uniform sampler2D font;
uniform int frame;
#define rep(i,n) for(int i=0;i<n;i++)
#define sat(x) clamp(x,.0,1.)
#define norm(x) normalize(x)
#define sc(x) hash(vec3(bt,-96.342,x))
#define nsc(x) hash(vec3(bt+1.,-96.342,x))
#define uvq(uv,q) vec2 ruv=(uv)*(q),iuv=floor(ruv),fuv=ruv-iuv,quv=(iuv+.5)/(q)
vec2 fc,res,asp,asp2,it;
float alt,lt,atr,tr;int bt;
const float pi=acos(-1.);
const float tau=2.*pi;
vec3 hash(vec3 h){uvec3 v=floatBitsToUint(h+vec3(.179,-.241,.731));v = v * 1664525u + 1013904223u;v.x += v.y*v.z;v.y += v.z*v.x;v.z += v.x*v.y;v ^= v >> 16u;v.x += v.y*v.z;v.y += v.z*v.x;v.z += v.x*v.y;return vec3(v)/vec3(-1u);}
mat2 rot(float a){float s=sin(a),c=cos(a);return mat2(c,s,-s,c);}
mat3 bnt(vec3 T){T=norm(T);vec3 N=vec3(0,1,0);vec3 B=norm(cross(N,T));N=norm(cross(T,B));return mat3(B,N,T);}
vec3 erot(vec3 p, vec3 ax, float ro) { return mix(dot(ax, p)*ax, p, cos(ro)) + cross(ax,p)*sin(ro); }
float seg(vec2 p,vec2 a,vec2 b ) { vec2 pa = p-a, ba = b-a; float h = clamp( dot(pa,ba)/dot(ba,ba), 0.0, 1.0 ); return length( pa - ba*h ); }
vec3 cyc(vec3 x,float q)
{
    mat3 m=bnt(norm(vec3(1,8,3)));vec4 v;
    rep(i,5){x+=sin(x.yzx);v=q*v+vec4(cross(cos(x),sin(x.zxy)),1);x*=q*m;}
    return v.xyz/v.w;
}
void set(float t){alt=lt=t;atr=tr=fract(lt);bt=int(lt);lt=float(bt)+tr;}
    
void main() {
	set(time*140./60.);
	fc=gl_FragCoord.xy,res=resolution,asp=res/min(res.x,res.y),asp2=res/max(res.x,res.y),it=touch/res;
	vec2 uv=fc/res,suv=(uv*2.-1.)*asp;
	vec3 c=.5+.5*cos(vec3(1,2,3)+pi*cyc(vec3(suv,time),1.2));
	c=mix(c,texture(backbuffer,uv).rgb,it.x);
	outColor = vec4(c,1);
}