//each work group will handle an object pair
//each work item will handle 1 axis
__kernel void getMinMaxPoints(
    __global const float *rot, //vector3f array
    __global const float *vertices, //vector3f array
    __global const int *numOfVertices, //int
    __global const int *triangles, //vector3i array
    __global float *faceNormals //vector3f array
    __global bool *isColliding //boolean array containing
)
{

}