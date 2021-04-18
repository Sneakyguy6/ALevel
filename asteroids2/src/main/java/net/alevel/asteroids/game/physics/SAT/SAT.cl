//this file contains functions for my OpenCL SAT (Separating axis theorem) implementation
//All world coordinates are stored in one huge array so the program could scale well and use the GPU to its full SIMT (same instruction multiple threads) capacity.

//int get_group_size(int x);

//This kernel is used in SAT.SurfaceNormals. It calculates a surface normal for each triangle for all triangles in the world. Each kernel instance is responsible for 1 triangle
kernel void getSurfaceNormals(
    global const float *vertices, //vector3f array (These are the transformed vertices (world coords, not model))
                                    //i.e. the rotation and position vectors have been applied.
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

//These project the vertices onto each axis (surface normal)
kernel void getProjectedVertices(
    global const float *surfaceNormals,
    global const float *vertices,
    global float *projectedVerticesMin, //these arrays store exactly the same data which is needed for how I have done the getBoundaries method
    global float *projectedVerticesMax
)
{
    int localId = get_local_id(0) * 3;
    int groupId = get_group_id(0) * 3;
    int groupSize = get_group_size(0);

    float3 a = (float3)(vertices[localId], vertices[localId + 1], vertices[localId + 2]);
    float3 n = (float3)(surfaceNormals[groupId], surfaceNormals[groupId + 1], surfaceNormals[groupId + 2]);
    
    float lambda = dot(a, n) / (pow(n.x, 2) + pow(n.y, 2) + pow(n.z, 2));
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

//Gets the max and min values for lambda for each axis for each object. To be used when testing collisions as 2 intersecting. It uses a parallel reduction algorithm (https://dournac.org/info/gpu_sum_reduction)
kernel void getBoundaries(
    global float *maxProjectedBoundaries,
    global float *minProjectedBoundaries
)
{
    int globalId = get_global_id(0);
    int groupId = get_group_id(0);
    int localId = get_local_id(0);
    int groupSize = get_group_size(0);

    //get minimum
    barrier(CLK_LOCAL_MEM_FENCE);
    //int trueGlobalId = localId; // + get_global_offset(0); //globalId - get_global_offset(0);
    int index = groupId * groupSize + localId;

    for(int stride = groupSize >> 1; stride != 0; stride >>= 1) { //int stride = groupSize / 2; stride > 0; stride /= 2
        if(localId < stride) {
            int a = minProjectedBoundaries[index];
            int b = minProjectedBoundaries[index + stride];
            if(b < a){
                minProjectedBoundaries[index] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(localId == 0){
        /*int offset = get_global_offset(0);
        int subBufferIndex = 0;
        for(; subBufferPointers[subBufferIndex] != offset; subBufferIndex++);
        int boundaryIndex = (groupId * groupSize + subBufferIndex) * 2;
        boundaries[boundaryIndex] = projectedVerticesMin[0];*/
    }

    //same algorithm but for max value
    //barrier(CLK_LOCAL_MEM_FENCE);
    for(int stride = groupSize >> 1; stride != 0; stride >>= 1) {
        if(localId < stride) {
            int a = maxProjectedBoundaries[globalId];
            int b = maxProjectedBoundaries[globalId + stride];
            if(b > a){
                maxProjectedBoundaries[globalId] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(localId == 0){
        //int boundaryIndex = (localId + groupId) * 2;
        //boundaries[boundaryIndex + 1] = projectedVerticesMax[0];
    }
}

//use the boundaries to check for collisions from different objects
/*kernel void testIntersections(
    global const float *boundaries,
    global float *collisions
)
{
    
}*/