#shader vert
#version 120

varying vec3 pos;
varying vec3 normal;
varying vec4 tex_coord;

void main(void) {
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	pos = gl_Vertex.xyz;
	normal = gl_Normal;
	tex_coord = gl_MultiTexCoord0;
}

#shader frag
#version 120

uniform vec3 viewPos;
uniform sampler2D tex;
uniform bool depth;

varying vec3 pos;
varying vec3 normal;
varying vec4 tex_coord;

void main(void) {
	vec3 actualPos = pos - viewPos;

	/*vec3 lightDir = vec3(0, 1, 0); // Upwards
	float specularStrength = 0.5;
	vec3 viewDir = normalize(viewPos - actualPos);
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
	vec3 specular = specularStrength * spec * vec3(1, 1, 1);
	gl_FragColor = vec4(specular * vec3(0.72, 0.72, 0.0), 1.0) * gl_Color;*/
	float distanceBrightness = abs(sin(actualPos.x)) * abs(sin(actualPos.z));
	float normalBrightness = normal.y * -0.2;
	vec4 texColor = texture2D(tex, tex_coord.st) * 0.04;
	texColor.a = 0.0;

	vec4 espColor;
	if (depth) {
		espColor = vec4(0.72 + normalBrightness + (distanceBrightness * 0.3), 0.72 + normalBrightness + (distanceBrightness * 0.3), 0.0, 1.0) * 0.5;
	} else {
		espColor = vec4(0.0, 0.72 + normalBrightness + (distanceBrightness * 0.3), 0.72 + normalBrightness + (distanceBrightness * 0.3), 1.0) * 0.5;
	}
	espColor.a = 1.0;
	gl_FragColor = espColor + texColor;

	// Apply brightness based on height
	float yBrightness = min(abs(pos.y) + 0.75, 1.0);
	gl_FragColor.r *= yBrightness;
	gl_FragColor.g *= yBrightness;
	gl_FragColor.b *= yBrightness;
}