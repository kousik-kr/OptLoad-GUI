# Test Data Samples

This directory contains sample test data for unit and integration tests.

## File Structure

- `sample_graph.txt` - Small graph for basic testing
- `sample_queries.txt` - Sample customer queries
- `expected_output.txt` - Expected results for validation

## Usage

Test files can reference these samples using:

```java
InputStream input = getClass().getResourceAsStream("/sample_graph.txt");
```
