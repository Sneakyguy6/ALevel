//each work group will handle a mesh
//each work item will handle a triangle
__kernel void getSurfaceNormals(
    __global const float3 *vertices, //vector3f array (These are the transformed vertices (world coords, not model))
                                     //i.e. the rotation and position vectors have been applied
    //__global const int numOfVertices, //int
    __global const int3 *indices, //vector3i array where vertices x, y and z are corners of a triangle
    __global float3 *surfaceNormals, //vector3f array
    __global float3 *aTemp,
    __global float3 *bTemp,
    __global int3 *inTemp,
    __global float3 *vertexTemp
)
{
    int id = get_global_id(0);
    inTemp[0] = indices[id];
    
    vertexTemp[0] = vertices[indices[id].x];
    vertexTemp[1] = vertices[indices[id].y];
    vertexTemp[2] = vertices[indices[id].z];
    float3 a = (float3)(
        vertices[indices[id].y].x - vertices[indices[id].x].x,
        vertices[indices[id].y].y - vertices[indices[id].x].y,
        vertices[indices[id].y].z - vertices[indices[id].x].z
    );
    float3 b = (float3)(
        vertices[indices[id].z].x - vertices[indices[id].x].x,
        vertices[indices[id].z].y - vertices[indices[id].x].y,
        vertices[indices[id].z].z - vertices[indices[id].x].z
    );
    //float3 a = vertices[indices[id].y] - vertices[indices[id].x];
    aTemp[0] = a;
    //float3 b = vertices[indices[id].z] - vertices[indices[id].x];
    bTemp[0] = b;
    surfaceNormals[id] = normalize(cross(a, b));
    
}