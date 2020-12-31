__kernel void matrixMultiply(
    __global const float *A,
    //const int AWidth,
    //const int AHeight,
    __global const float *B,
    //const int BWidth,
    //const int BHeight, //AWidth must equal BHeight otherwise the multiplication is invalid
    __global float *C,
	__global const int *matSizes
)
{
    //printf("%d %d %d %d\n", A[0], A[1], A[2], A[3]);
    /*int x = get_global_id(0);
    int y = get_global_id(1); //coords of element to calculate
    float acc = .0f;
    for(int i = 0; i < AWidth; i++) {
        acc += A[(x * y) + i] * B[(x * i) + y];
    }
    C[(x * BWidth) + y];*/
    int x = get_global_id(0);
    int y = get_global_id(1); //coords of element to calculate
    float acc = .0f;
    for(int i = 0; i < matSizes[0]; i++) {
        acc += A[(x * matSizes[0]) + i] * B[(matSizes[2] * i) + y];
    }
    C[(x * matSizes[2]) + y] = acc;
    printf("%d %d %d \n", x, y, acc);
}

__kernel void matrixVectorMultiply (

)
{

}