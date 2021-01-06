//each work item works on an axis
__kernel void SAT(
    //__global int *numberOfNormals, //used to split the globalMinMaxs array per work group
    //__local float2 *startEndIndices, //specifies which range of elements from globalMinMaxs this group will use
    __global const float2 *minMaxs, //x = min, y = max
    __global const int numOfAxis,
    __global bool *isColliding //final results array
    //__local bool *isCollidingLocal //Array of results where each element is the result of a test on one axis.
                                   //It will go through parallel reduction to lead to final result
    
)
{
    int id = get_global_id(0);
    float2 o1 = minMaxs[id];
    float2 o2 = minMaxs[id + numOfAxis];
    if(o1.x > o2.y) {
        isColliding[id] = true;
    } else if(o2.x > o1.y) {
        isColliding[id] = true;
    } else {
        isColliding[id] = false;
    }
    barrier(CLK_GLOBAL_MEM_FENCE);
    
    int size = get_global_size(0);
    for(int stride = size / 2; stride > 0; stride /= 2) {
        if(id < stride) {
            //int a = isColliding[id];
            int b = isColliding[id + stride];
            if(!b) { //parallel reduce to false (that means at least on 1 axis, there was no intersection)
                isColliding[id] = b;
            }
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
}