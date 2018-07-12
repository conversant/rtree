<img src="https://github.com/conversant/rtree/blob/master/src/main/resources/RTree.png?raw=true">

# Conversant RTree

RTree is an index that supports building a tree of bounding rectangles for arbitrary range searches.   RTrees are efficient for geospatial data but can be extended to support any data that is amenable to range searching.

Conversant RTree is a hyper-dimensional (2D, 3D, 4D, nD) implementation of RTrees in Java.  Conversant RTree supports data with large numbers of orthogonal relations or high dimensionality in the same way that traditional RTrees support 2 or 3 dimensional spatial data.

### Conversant R-Tree is on Maven Central

Maven users can incorporate Conversant R-Tree the usual way.

```
<dependency>
  <groupId>com.conversantmedia</groupId>
  <artifactId>rtree</artifactId>
  <version>1.0.5</version>
</dependency>
```

Optionally specify a classifier for your version of Java
| classifier |
|------------|
| jdk8 |
| jdk10|
