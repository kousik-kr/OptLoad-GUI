# Data Directory

This directory contains all data files for the VRPLU-OptLoad project.

## Structure

### `raw/`
Original, unprocessed input data files:
- Node files (coordinates, capacities)
- Edge files (connections, distances)
- Query files (customer requests)

### `processed/`
Processed and transformed data:
- Optimized graphs
- Preprocessed queries
- Intermediate results

### `sample/`
Small sample datasets for:
- Testing
- Documentation
- Demos

## File Formats

### Nodes File
```
nodeId,x,y,capacity
1,0.0,0.0,100
2,10.5,20.3,50
```

### Edges File
```
fromNode,toNode,distance,travelTime
1,2,15.2,10
2,3,8.5,5
```

### Queries File
```
queryId,pickupNode,deliveryNode,demand,timeWindow
1,2,5,20,0-100
2,3,7,30,50-150
```
