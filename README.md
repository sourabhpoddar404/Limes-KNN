# Limes-KNN
Search for exact KNN for high dimensional dataset using Limes algorithm based on https://www.ijcai.org/Proceedings/11/Papers/385.pdf

### Summary of features
* Support Euclidean distance, Manhattan distance and cosine distance.
* Searching is seperate from the index creation process.
* Builds index on the main memory.
* Gurantees 100% recall.

## API description

* **LIMESKNN.create(Dataset, IDistance, Number of exemplar, isDistance)** - Returns a LIMESKNN object with indexed dataset.
  * Dataset: N x d float matrix, where N is the number of records in the data and d is the dimention of each record.
  * IDistance: Accepts an implementaion of IDistance interface (Euclidean, Manhattan or Cosine).
  * Number of exemplar: default to sqrt(N), increasing number of exemplar require higher memory usage.
  * isDistance: True for Euclidean and Manhattan, false for Cosine.
  
* **LIMESKNN.search(query, k)** - Returns K nearest neighbor of the "query" using LIMES-KNN algorithm
  * query: d-dimensional float vector
  * k: Number of nearest neighbor


