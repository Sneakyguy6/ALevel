__kernel void getSurfaceNormals(
    __global const float *vertices, //vector3f array (These are the transformed vertices (world coords, not model))
                                    //i.e. the rotation and position vectors have been applied
    __global const int *indices, //vector3i array where vertices x, y and z are corners of a triangle
    __global float *surfaceNormals //vector3f array
)
{
    int id = get_global_id(0) * 3;
    float3 a = (float3)(
        vertices[indices[id + 1] * 3] - vertices[indices[id] * 3],
        vertices[indices[id + 1] * 3 + 1] - vertices[indices[id] * 3 + 1],
        vertices[indices[id + 1] * 3 + 2] - vertices[indices[id] * 3 + 2]
    );
    float3 b = (float3)(
        vertices[indices[id + 2] * 3] - vertices[indices[id] * 3],
        vertices[indices[id + 2] * 3 + 1] - vertices[indices[id] * 3 + 1],
        vertices[indices[id + 2] * 3 + 2] - vertices[indices[id] * 3 + 2]
    );
    float3 c = normalize(cross(a, b));
    surfaceNormals[id] = c.x;
    surfaceNormals[id + 1] = c.y;
    surfaceNormals[id + 2] = c.z;
}

__kernel void getProjectedBoundaries(

)
{
    
}