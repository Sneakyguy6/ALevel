kernel void getSurfaceNormals(
    global const float *vertices, //vector3f array (These are the transformed vertices (world coords, not model))
                                    //i.e. the rotation and position vectors have been applied
    global const int *indices, //vector3i array where vertices x, y and z are corners of a triangle
    global float *surfaceNormals //vector3f array
)
{
    int globalId = get_global_id(0) * 3;
    float3 a = (float3)(
        vertices[indices[globalId + 1] * 3] - vertices[indices[globalId] * 3],
        vertices[indices[globalId + 1] * 3 + 1] - vertices[indices[globalId] * 3 + 1],
        vertices[indices[globalId + 1] * 3 + 2] - vertices[indices[globalId] * 3 + 2]
    );
    float3 b = (float3)(
        vertices[indices[globalId + 2] * 3] - vertices[indices[globalId] * 3],
        vertices[indices[globalId + 2] * 3 + 1] - vertices[indices[globalId] * 3 + 1],
        vertices[indices[globalId + 2] * 3 + 2] - vertices[indices[globalId] * 3 + 2]
    );
    float3 c = normalize(cross(a, b));
    surfaceNormals[globalId] = c.x;
    surfaceNormals[globalId + 1] = c.y;
    surfaceNormals[globalId + 2] = c.z;
}

kernel void getProjectedBoundaries(
    global const float *surfaceNormals,
    global const float *vertices,
    global float *boundaries,
    local float *projectedVerticesMin,
    local float *projectedVerticesMax
)
{
    int globalId = get_global_id(0) * 3;
    int localId = get_local_id(0) * 3;
    float3 a = (float3)(vertices[globalId], vertices[globalId + 1], vertices[globalId + 2]);
    float3 n = (float3)(surfaceNormals[localId], surfaceNormals[localId + 1], surfaceNormals[localId + 2]);
    
    float lambda = dot(a, n) / (n.x^2 + n.y^2 + n.z^2);
    int projectedVerticesIndex = globalId + localId;
    projectedVerticesMin[projectedVerticesIndex] = lambda * n.x;
    projectedVerticesMin[projectedVerticesIndex + 1] = lambda * n.y;
    projectedVerticesMin[projectedVerticesIndex + 2] = lambda * n.z;
    projectedVerticesMax[projectedVerticesIndex] = lambda * n.x;
    projectedVerticesMax[projectedVerticesIndex + 1] = lambda * n.y;
    projectedVerticesMax[projectedVerticesIndex + 2] = lambda * n.z;

    int stride = get_global_size(0);
    int boundaryIndex = (get_global_id(0) + get_local_id(0)) * 2;
    int trueLocalId = get_local_id(0);

    //get minimum (uses parallel reduction algorithm (https://dournac.org/info/gpu_sum_reduction))
    barrier(CLK_LOCAL_MEM_FENCE);
    int groupSize = get_local_size(0);
    for(int stride = groupSize / 2; stride > 0; stride /= 2) {
        if(trueLocalId < stride) {
            int a = projectedVerticesMin[trueLocalId];
            int b = projectedVerticesMin[trueLocalId + stride];
            if(b < a){
                projectedVerticesMin[trueLocalId] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(trueLocalId == 0){
         boundaries[boundaryIndex] = projectedVerticesMin[0];
    }

    //same algorithm but for max value
    barrier(CLK_LOCAL_MEM_FENCE);
    for(int stride = groupSize / 2; stride > 0; stride /= 2) {
        if(trueLocalId < stride) {
            int a = projectedVerticesMax[trueLocalId];
            int b = projectedVerticesMax[trueLocalId + stride];
            if(b > a){
                projectedVerticesMax[trueLocalId] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(trueLocalId == 0){
        boundaries[boundaryIndex + 1] = projectedVerticesMax[0];
    }
}