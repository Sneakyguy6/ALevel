__kernel void matrixMultiply(
    __global const float *A,
    //const int AWidth,
    //const int AHeight,
    __global const float *B,
    //const int BWidth,
    //const int BHeight, //AWidth must equal BHeight otherwise the multiplication is invalid
    __global float *C,
	__global const int *matSizes //{AWidth, AHeight, BWidth, BHeight}
)
{
    int x = get_global_id(0);
    int y = get_global_id(1); //coords of element to calculate
    float acc = .0f;
    for(int i = 0; i < matSizes[0]; i++) {
        acc += A[(x * matSizes[0]) + i] * B[(matSizes[2] * i) + y];
    }
    C[(x * matSizes[2]) + y] = acc;
}

//multiply a list of vectors by a matrix
__kernel void matrixVectorMultiply (
    __global const float *matrix,
    __global const int *matrixDimensions, //{matrixWidth, matrixHeight}
    __global const float *vectors, //vector height must equal matrix width
    __global float *transformedVectors
)
{
    int vector = get_group_id(0) * matrixDimensions[0];
    int component = get_local_id(0);
    //printf("%d %d\n", vector, component);
    float acc = .0f;
    for(int i = component, j = 0; i < matrixDimensions[0] * matrixDimensions[1]; i += matrixDimensions[1], j++){
        acc += matrix[i] * vectors[vector + j];
    }
    transformedVectors[vector + component] = acc;
}

//printf("%d %d %d %d\n", A[0], A[1], A[2], A[3]);
    /*int x = get_global_id(0);
    int y = get_global_id(1); //coords of element to calculate
    float acc = .0f;
    for(int i = 0; i < AWidth; i++) {
        acc += A[(x * y) + i] * B[(x * i) + y];
    }
    C[(x * BWidth) + y];*/