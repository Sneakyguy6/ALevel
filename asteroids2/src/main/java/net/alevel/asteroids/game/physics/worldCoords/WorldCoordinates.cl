/*
This file uses a modified version of the C language optimised for parallel.

The functions here are known as kernels. They are designed in a way that allows multiple of them to run physically in parallel.
Each instance of a kernel has several unique properties (e.g. global id, local id). I use these IDs to select the input data from the
input arrays. For example, lets say inputData = {1, 2, 3, 4, 5, 6} and outputData = {0, 0, 0, 0, 0, 0}. You want to add 10 to each value in the inputData array.
The kernel would look like this:
kernel void add10(
    global int inputData*
    global int outputData*
)
{
    int id = get_global_id(0)
    outputData[id] = inputData[id] + 10;
}
There is no for loop required here. In the java code, I would just need to enqueue this kernel and tell it to do 10 instances for it. Each kernel instance
will have a unique global ID (between 0 and 9) which I can use to specify the input data and the location where to store the data.
*/
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