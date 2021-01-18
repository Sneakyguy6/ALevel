//NOTE: all kernels assume that inputs are 3 float vectors and 3x3 float matrices
__kernel void tranformVectors(
    __global const float *modelVertices,
    __global const int *objectIndices,
    __global const float *posVectors,
    __global const float *rotMatrices, //column major format
    __global float *worldVertices
)
{
    int id = get_global_id(0);
    int objectIndex = objectIndices[id];
    int rotateIndex = objectIndices[id] * 9;
    int vertexIndex = id * 3;
    int posIndex = objectIndices[id] * 3;
    worldVertices[vertexIndex] = ((modelVertices[vertexIndex] * rotMatrices[rotateIndex]) + (modelVertices[vertexIndex + 1] * rotMatrices[rotateIndex + 3]) + (modelVertices[vertexIndex + 2] * rotMatrices[rotateIndex + 6])) + posVectors[posIndex];
    worldVertices[vertexIndex + 1] = ((modelVertices[vertexIndex] * rotMatrices[rotateIndex + 1]) + (modelVertices[vertexIndex + 1] * rotMatrices[rotateIndex + 4]) + (modelVertices[vertexIndex + 2] * rotMatrices[rotateIndex + 7])) + posVectors[posIndex + 1];
    worldVertices[vertexIndex + 2] = ((modelVertices[vertexIndex] * rotMatrices[rotateIndex + 2]) + (modelVertices[vertexIndex + 1] * rotMatrices[rotateIndex + 5]) + (modelVertices[vertexIndex + 2] * rotMatrices[rotateIndex + 8])) + posVectors[posIndex + 2];
}


__kernel void getRotationMatrix ( //get rotation in matrix format
    __global const float *rotationVectors,
    __global float *matrices //This is in column major format (so i can dump it into a matrix3f if i needed to)
)
{
    int vecId = get_global_id(0) * 3;
    int matId = get_global_id(0) * 9; //each matrix is 9 floats. VecId is 3 floats
    float x = rotationVectors[vecId];
    float y = rotationVectors[vecId + 1];
    float z = rotationVectors[vecId + 2];

    matrices[matId] = cos(z) * cos(y); //from https://en.wikipedia.org/wiki/Rotation_matrix
    matrices[matId + 1] = sin(z) * cos(y);
    matrices[matId + 2] = sin(y);
    matrices[matId + 3] = (cos(z) * sin(y) * sin(x)) - (sin(z) * cos(x));
    matrices[matId + 4] = (sin(z) * sin(y) * sin(x)) + (cos(z) * cos(x));
    matrices[matId + 5] = cos(y) * sin(x);
    matrices[matId + 6] = (cos(z) * sin(y) * cos(x)) + (sin(z) * sin(x));
    matrices[matId + 7] = (sin(z) * sin(y) * cos(x)) - (cos(z) * sin(x));
    matrices[matId + 8] = cos(y) * cos(x);
}

__kernel void rotateVectors( //apply rotation matrix calculated from rotation vector
    __global const float *vectors,
    __global const float *matrix,
    __global float *transformedVectors
)
{
    int vector = get_group_id(0) * 3;
    int component = get_global_id(1);
    //printf("%d %d\n", vector, component);
    float acc = .0f;
    for(int i = component * 3, j = 0; j < 3; i++, j++){
        acc += matrix[i] * vectors[vector + j];
    }
    transformedVectors[vector + component] = acc;
}

__kernel void translateVectors( //apply position vector
    __global const float *vecIn,
    __global const float *translation,
    __global float *vecOut
)
{
    int id0 = get_global_id(0) * 3;
    int id1 = get_global_id(1);
    vecOut[id0 + id1] = vecIn[id0 + id1] + translation[id1];
}

/*__kernel void getRotationMatrixOld(
    const float *A,
    const float *B,
    const float *C,
    float *D,
    float *E
)
{
    int x = get_global_id(0);
    int y = get_global_id(1); //coords of element to calculate
    float acc = .0f;
    for(int i = 0; i < matSizes[0]; i++) {
        acc += A[(x * matSizes[0]) + i] * B[(matSizes[2] * i) + y];
    }
    D[(x * matSizes[2]) + y] = acc;

    //int x = get_global_id(0);
    //int y = get_global_id(1); //coords of element to calculate
    acc = .0f;
    for(int i = 0; i < matSizes[0]; i++) {
        acc += D[(x * matSizes[0]) + i] * C[(matSizes[2] * i) + y];
    }
    E[(x * matSizes[2]) + y] = acc;
}*/

/*float3 XMat[3] = {
        (float3)(1, 0, 0),
        (float3)(0, cos(x), -sin(x)),
        (float3)(0, sin(x), cos(x))
    };

    float3 YMat[3] = {
        (float3)(cos(y), 0, sin(y)),
        (float3)(0, 1, 0),
        (float3)(-sin(y), 0, cos(y))
    };

    float3 ZMat[3] = {
        (float3)(cos(z), -sin(z), 0),
        (float3)(sin(z), cos(z), 0),
        (float3)(0, 0, 1)
    };*/

    /*float3 XYMat[3] = {
        (float3)(
            XMat[0].x * YMat[0].x + XMat[0].y * YMat[1].x + XMat[0].z * YMat[2].x,
            XMat[0].x * YMat[0].y + XMat[0].y * YMat[1].y + XMat[0].z * YMat[2].y,
            XMat[0].x * YMat[0].z + XMat[0].y * YMat[1].z + XMat[0].z * YMat[2].z),
        (float3)(
            XMat[1].x * YMat[0].x + XMat[1].y * YMat[1].x + XMat[1].z * YMat[2].x,
            XMat[1].x * YMat[0].y + XMat[1].y * YMat[1].y + XMat[1].z * YMat[2].y,
            XMat[1].x * YMat[0].z + XMat[1].y * YMat[1].z + XMat[1].z * YMat[2].z),
        (float3)(
            XMat[2].x * YMat[0].x + XMat[2].y * YMat[1].x + XMat[2].z * YMat[2].x,
            XMat[2].x * YMat[0].y + XMat[2].y * YMat[1].y + XMat[2].z * YMat[2].y,
            XMat[2].x * YMat[0].z + XMat[2].y * YMat[1].z + XMat[2].z * YMat[2].z)
    }

    float3 XYZMat[3] = {
        (float3)(
            XYMat[0].x * ZMat[0].x + XYMat[0].y * ZMat[1].x + XYMat[0].z * ZMat[2].x,
            XYMat[0].x * ZMat[0].y + XYMat[0].y * ZMat[1].y + XYMat[0].z * ZMat[2].y,
            XYMat[0].x * ZMat[0].z + XYMat[0].y * ZMat[1].z + XYMat[0].z * ZMat[2].z),
        (float3)(
            XYMat[1].x * ZMat[0].x + XYMat[1].y * ZMat[1].x + XYMat[1].z * ZMat[2].x,
            XYMat[1].x * ZMat[0].y + XYMat[1].y * ZMat[1].y + XYMat[1].z * ZMat[2].y,
            XYMat[1].x * ZMat[0].z + XYMat[1].y * ZMat[1].z + XYMat[1].z * ZMat[2].z),
        (float3)(
            XYMat[2].x * ZMat[0].x + XYMat[2].y * ZMat[1].x + XYMat[2].z * ZMat[2].x,
            XYMat[2].x * ZMat[0].y + XYMat[2].y * ZMat[1].y + XYMat[2].z * ZMat[2].y,
            XYMat[2].x * ZMat[0].z + XYMat[2].y * ZMat[1].z + XYMat[2].z * ZMat[2].z)
    }*/