//each work group will handle a face normal
__kernel void getMinMaxPoints(
    __global const float *vertices, //vector3f array
    __global const float *faceNormals, //vector3f array (should already be normalized)
    __global float2 *minMaxPoints,
    __local float *projectedVertexDistancesMax, //specific to each face normal
    __local float *projectedVertexDistancesMin
)
{
    int groupId = get_group_id(0);
    int localId = get_local_id(0);
    int min;
    int max;
    //printf("%d %d -> vertices %d %d %d %d %d %d %d %d %d %d\n", groupId, localId, vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], vertices[6], vertices[7], vertices[8]);
    //printf("%p", vertices);
    printf("%d %d test -> %d\n", groupId, localId, vertices[1]);
    printf("%d %d vert -> %d %d %d\n", groupId, localId, vertices[localId * 3], vertices[localId * 3 + 1], vertices[localId * 3 + 2]);
    printf("%d %d face -> %d %d %d\n", groupId, localId, faceNormals[groupId * 3], faceNormals[groupId * 3 + 1], faceNormals[groupId * 3 + 2]);
    float value = dot (
        (float3)(vertices[localId * 3], vertices[localId * 3 + 1], vertices[localId * 3 + 2]),
        (float3)(faceNormals[groupId * 3], faceNormals[groupId * 3 + 1], faceNormals[groupId * 3 + 2])
    );
    //printf("%d %d\n", groupId, localId);
    projectedVertexDistancesMax[localId] = value;
    projectedVertexDistancesMin[localId] = value;
    //barrier(CLK_LOCAL_MEM_FENCE);
    //printf("%d %d %d %d %d %d %d %d %d %d \n", projectedVertexDistancesMax[0], projectedVertexDistancesMax[1], projectedVertexDistancesMax[2], projectedVertexDistancesMax[3], projectedVertexDistancesMax[4], projectedVertexDistancesMax[5], projectedVertexDistancesMax[6], projectedVertexDistancesMax[7], projectedVertexDistancesMax[8]);
    //barrier(CLK_LOCAL_MEM_FENCE);
    //printf("%d %d %d %d %d %d %d %d %d %d \n\n", projectedVertexDistancesMin[0], projectedVertexDistancesMin[1], projectedVertexDistancesMin[2], projectedVertexDistancesMin[3], projectedVertexDistancesMin[4], projectedVertexDistancesMin[5], projectedVertexDistancesMin[6], projectedVertexDistancesMin[7], projectedVertexDistancesMin[8]);

    //get minimum (uses parallel reduction algorithm (https://dournac.org/info/gpu_sum_reduction))
    barrier(CLK_LOCAL_MEM_FENCE);
    int groupSize = get_local_size(0);
    for(int stride = groupSize / 2; stride > 0; stride /= 2) {
        if(localId < stride) {
            int a = projectedVertexDistancesMin[localId];
            int b = projectedVertexDistancesMin[localId + stride];
            if(b < a){
                projectedVertexDistancesMin[localId] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(localId == 0){
        min = projectedVertexDistancesMin[0];
    }

    //same algorithm but for max value
    //barrier(CLK_LOCAL_MEM_FENCE);
    for(int stride = groupSize / 2; stride > 0; stride /= 2) {
        if(localId < stride) {
            int a = projectedVertexDistancesMax[localId];
            int b = projectedVertexDistancesMax[localId + stride];
            if(b > a){
                projectedVertexDistancesMax[localId] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if(localId == 0){
        max = projectedVertexDistancesMax[0];
    }

    minMaxPoints[groupId] = (float2)(min, max);
}