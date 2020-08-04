#version 330

in vec2 outTexCoord;
//flat in int shouldLight; //light test
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec3 colour;
uniform int useColour;

void main()
{
    if ( useColour == 1 )
    {
        fragColor = vec4(colour, 0);// * vec4(0, 0, 0, 0);
    }
    else
    {
        fragColor = texture(texture_sampler, outTexCoord);// * shouldLight;
    }
}