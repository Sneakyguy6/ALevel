//each work item will handle a face normal
__kernel void getMinMaxPoints(
    __global const float3 *vertices, //vector3f array
    __global const int *numOfVertices, //int
    __global const float3 *faceNormals, //vector3f array
    __global float2 *minMaxPoints
)
{
    int id = get_global_id(0);
    int min = 0;
    int max = 0;
    for(int i = 0; i < numOfVertices; i++){
        float3 value = (float3)(vertices[i].x, vertices[i].y, vertices[i].z).dot((float3)(faceNormals[id].x, faceNormals[id].y, faceNormals[id].z));
        if(value > max){
            max = value;
        } else if(value < min){
            min = value;
        }
    }
    minMaxPoints[id] = (float2)(min, max);
}