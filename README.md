# Sort
Sort numbers in a huge file with the contraint of 100mb heap size
------------------------------------------------------------------------

## Algorithm:
`````````````
1. Split large files into several small sorted temporary files.
2. Perform a k-way merge on the resulting temporary files and save it into an output file.

-------------------------------------------------------------------------------------------
```````````````````````````````````````````````````````````````````````````````````````````

### Instructions:
`````````````````
Set the heap size to 100mb

Can be run using:
java -Xmx100m -cp bin SortNumbersInAFile [<path-to-folder> <input-filename> <output-filename>]
