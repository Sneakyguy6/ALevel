//each work group will handle an object
//each work item will handle a triangle
__kernel void getSurfaceNormals(
    __global const float3 *rot, //vector3f array
    __global const float3 *vertices, //vector3f array
    __global const int numOfVertices, //int
    __global const int3 *triangles, //vector3i array
    __global float3 *faceNormals //vector3f array
    __global bool *isColliding //boolean array containing
)
{
    int index = get_global_id(0) * 3;
    int[] a = {
        vertices[triangles[index + 1] * 3] - vertices[triangles[index] * 3],
        vertices[(triangles[index + 1] * 3) + 1] - vertices[(triangles[index] * 3) + 1],
        vertices[(triangles[index + 1] * 3) + 2] - vertices[(triangles[index] * 3) + 2]
    }
    int[] b = {
        vertices[triangles[index + 2] * 3] - vertices[triangles[index] * 3],
        vertices[(triangles[index + 2] * 3) + 1] - vertices[(triangles[index] * 3) + 1],
        vertices[(triangles[index + 2] * 3) + 2] - vertices[(triangles[index] * 3) + 2]
    }
    faceNormals[index] = abs((a[1] * b[2]) - (a[2] * b[1]));
    faceNormals[index] = abs((a[2] * b[0]) - (a[0] * b[2]));
    faceNormals[index] = abs((a[0] * b[1]) - (a[1] * b[0]));
}