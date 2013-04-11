#Simseer Hashes

##Jenkins Hash

This is a pure Java implementation of Jenkins' Hash function [1] and is able to produce both 32-bit and 64-bit hashes. The implementation is based on a python implementation of the hash [2], which in turn is based on the original C implementation released by Jenkins. The code makes use of BigInteger to represent the hashes since a Long is not always big enough (the original code uses a long long). 

###Usage
To get a 32-bit hash use the hashlittle() method:

`BigInteger hash32 = hashlittle("text", 0);`

To get a 64-bit hash use the hashlittle2() method and the combine the two BigIntegers in the array:

`BigInteger [] hashes = new Jenkins().hashlittle2("text", 0, 0);` 
`BigInteger hash64 = hashes[0].add(hashes[1].shiftLeft(32));`

###Test Cases
I haven't written any test cases (yet) but in an experiment where I hashed each word in 100 documents, this implementation and the original C implementation produced exactly the same hash for each word.

##References
[1] B. Jenkins. A hash function for hash table lookup. Dr. Dobb’s Journal, 1997.

[2] http://stackoverflow.com/questions/3279615/python-implementation-of-jenkins-hash2