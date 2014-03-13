// object space to camera space transformation
uniform mat4 modelview_matrix;
 
// camera space to clip coordinates
uniform mat4 projection_matrix;

// texture space to camera space transformation
uniform mat4 texture_matrix;


// global color to multiply with
uniform vec4 global_color;

uniform int color_count;
uniform int normal_count;
uniform int texcoord_count;
 
// incoming vertex position
in vec3 vertex_position;
// incoming vertex color
in vec4 vertex_color;
// incoming vertex normal
in vec3 vertex_normal;
// incoming vertex texcoord
in vec3 vertex_texcoord;

// transformed vertex color
out vec4 color;
// transformed vertex normal
out vec3 normal;
// transformed texture coordinate
out vec3 texcoord;
 
void main(void) {
	//not a proper transformation if modelview_matrix involves non-uniform scaling(TODO)
	normal = vec3(0.0,0.0,0.0);
	if(normal_count > 0)
		normal = ( modelview_matrix * vec4( vertex_normal, 0 ) ).xyz;
	texcoord = vec3(0.0,0.0,0.0);
	//all these conditionals are probably really bad
	if(texcoord_count > 0)
		texcoord = ( texture_matrix * vec4( vertex_texcoord, 0 ) ).xyz;
	color = global_color;
	if(color_count > 0)
		color *= vertex_color;
	// transforming the incoming vertex position
	gl_Position = projection_matrix*modelview_matrix*vec4(vertex_position, 1 );
}