#version 330

layout (location = 0) in vec3 position; //using glEnableVertexAttribArray(0) we are able to tell the shader where the attribute is in memory. In this case it is in location 0
layout (location = 1) in vec2 textCoord; //we specified that the attribute containing the colours is in location 1 (using glEnableVertexAttribArray(1))
layout (location = 2) in vec3 vertexNormal;

out vec2 outTextCoord; //'out' means that once this shader is finished, whatever is stored in that variable will be passed to the next shader in the pipeline (that next shader will use 'in' to 'catch' the data)
                   //NOTE: to recieve the same data using 'in' and 'out', the variable name must be exactly the same
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix; //'uniform' is a global GLSL variable that shaders can use. This is our projectionMatrix
                               //NOTE: if this variable is unused, the shader will essentially garbage collect it. This means that using glGetUniformLocation() will not work

void main()
{
    //translate the coordinates using our matrices
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0); //we have added an extra dimension to position. gl_Position contains the position of the current vertex and is used to write the 'homogeneous' vertex position and can only be used in the 'vertex shader'
    outTextCoord = textCoord; //here we are simply passing the data down the graphics pipeline
}