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

kernel void getProjectedVertices(
    global const float *surfaceNormals,
    global const float *vertices,
    global float *projectedVerticesMin,
    global float *projectedVerticesMax
)
{
    int localId = get_local_id(0) * 3;
    int groupId = get_group_id(0) * 3;
    int groupSize = get_group_size(0);

    float3 a = (float3)(vertices[localId], vertices[localId + 1], vertices[localId + 2]);
    float3 n = (float3)(surfaceNormals[groupId], surfaceNormals[groupId + 1], surfaceNormals[groupId + 2]);
    
    float lambda = dot(a, n) / (n.x^2 + n.y^2 + n.z^2);
    int projectedVerticesIndex = get_group_id(0) * groupSize + get_local_id(0);
    projectedVerticesMin[projectedVerticesIndex] = lambda;
    projectedVerticesMax[projectedVerticesIndex] = lambda;
    /*projectedVerticesMin[projectedVerticesIndex] = lambda * n.x;
    projectedVerticesMin[projectedVerticesIndex + 1] = lambda * n.y;
    projectedVerticesMin[projectedVerticesIndex + 2] = lambda * n.z;
    projectedVerticesMax[projectedVerticesIndex] = lambda * n.x;
    projectedVerticesMax[projectedVerticesIndex + 1] = lambda * n.y;
    projectedVerticesMax[projectedVerticesIndex + 2] = lambda * n.z;*/
}

kernel void getBoundaries(
    global const float maxProjectedBoundaries,
    global const float minProjectedBoundaries,
    global const float subBufferPointers,
    global float boundaries
)
{
    int globalId = get_global_id(0);
    int groupId = get_group_id(0);
    int localId = get_local_id(0);
    int groupSize = get_group_size(0);

    //get minimum (uses parallel reduction algorithm (https://dournac.org/info/gpu_sum_reduction))
    barrier(CLK_LOCAL_MEM_FENCE);
    //int trueGlobalId = localId; // + get_global_offset(0); //globalId - get_global_offset(0);
    int index = groupId * groupSize + localId;

    for(int stride = groupSize >> 1; stride != 0; stride >>= 1) { //int stride = groupSize / 2; stride > 0; stride /= 2
        if(localId < stride) {
            int a = projectedVerticesMin[index];
            int b = projectedVerticesMin[index + stride];
            if(b < a){
                projectedVerticesMin[index] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(localId == 0){
        int offset = get_global_offset(0);
        int subBufferIndex = 0;
        for(; subBufferPointers[subBufferIndex] != offset; subBufferIndex++);
        int boundaryIndex = (groupId * groupSize + subBufferIndex) * 2;

        

        boundaries[boundaryIndex] = projectedVerticesMin[0];
    }

    //same algorithm but for max value
    //barrier(CLK_LOCAL_MEM_FENCE);
    for(int stride = groupSize >> 1; stride != 0; stride >>= 1) {
        if(trueGlobalId < stride) {
            int a = projectedVerticesMax[globalId];
            int b = projectedVerticesMax[globalId + stride];
            if(b > a){
                projectedVerticesMax[globalId] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(trueGlobalId == 0){
        int boundaryIndex = (localId + groupId) * 2;
        boundaries[boundaryIndex + 1] = projectedVerticesMax[0];
    }
}

kernel void testIntersections(
    global const float boundaries,
    global float collisions
)
{
    
}

/*int stride = get_global_size(0);
    int boundaryIndex = (get_global_id(0) + get_local_id(0)) * 2;
    int globalId = get_local_id(0);

    //get minimum (uses parallel reduction algorithm (https://dournac.org/info/gpu_sum_reduction))
    barrier(CLK_LOCAL_MEM_FENCE);
    int groupSize = get_local_size(0);
    for(int stride = groupSize / 2; stride > 0; stride /= 2) {
        if(globalId < stride) {
            int a = projectedVerticesMin[globalId];
            int b = projectedVerticesMin[globalId + stride];
            if(b < a){
                projectedVerticesMin[globalId] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(globalId == 0){
         boundaries[boundaryIndex] = projectedVerticesMin[0];
    }

    //same algorithm but for max value
    //barrier(CLK_LOCAL_MEM_FENCE);
    for(int stride = groupSize / 2; stride > 0; stride /= 2) {
        if(globalId < stride) {
            int a = projectedVerticesMax[globalId];
            int b = projectedVerticesMax[globalId + stride];
            if(b > a){
                projectedVerticesMax[globalId] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(globalId == 0){
        boundaries[boundaryIndex + 1] = projectedVerticesMax[0];
    }*/