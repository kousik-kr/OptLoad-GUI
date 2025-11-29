#!/bin/bash

# Migration script to move existing Java files to new package structure
# This script helps reorganize the codebase following software engineering principles

echo "Starting code reorganization..."

# Source directory
SRC_DIR="src"
NEW_SRC_DIR="src/main/java/com/vrplu"

# Create a mapping of files to their new packages
declare -A file_mapping

# Domain models (core business entities)
file_mapping["Node.java"]="domain"
file_mapping["Edge.java"]="domain"
file_mapping["Graph.java"]="domain"
file_mapping["Rider.java"]="domain"
file_mapping["Query.java"]="domain"
file_mapping["RoutePlan.java"]="domain"
file_mapping["TimeWindow.java"]="domain"
file_mapping["Service.java"]="domain"
file_mapping["Cluster.java"]="domain"
file_mapping["Path.java"]="domain"

# Exact algorithm implementations
file_mapping["ExactAlgorithmSolver.java"]="algorithm/exact"
file_mapping["ExactSolution.java"]="algorithm/exact"

# Heuristic algorithm implementations
file_mapping["InsertionHeuristicSolver.java"]="algorithm/heuristic"
file_mapping["LifoStackSolver.java"]="algorithm/heuristic"
file_mapping["BazelmansBaselineSolver.java"]="algorithm/heuristic"
file_mapping["FoodMatchSolver.java"]="algorithm/heuristic"

# Solver infrastructure
file_mapping["Solver.java"]="solver"
file_mapping["SolverFactory.java"]="solver"
file_mapping["SolverType.java"]="solver"

# Utility classes
file_mapping["Function.java"]="util"
file_mapping["Point.java"]="util"
file_mapping["BreakPoint.java"]="util"
file_mapping["Priority.java"]="util"
file_mapping["Event.java"]="util"
file_mapping["Ordering.java"]="util"
file_mapping["Properties.java"]="util"

# I/O operations
file_mapping["GenerateTDGraph.java"]="io"

# Main application
file_mapping["VRPLoadingUnloadingMain.java"]=""

# Function to add package declaration to Java file
add_package_declaration() {
    local file=$1
    local package=$2
    local temp_file="${file}.tmp"
    
    if [ ! -z "$package" ]; then
        echo "package com.vrplu.${package};" > "$temp_file"
        echo "" >> "$temp_file"
        cat "$file" >> "$temp_file"
        mv "$temp_file" "$file"
    else
        echo "package com.vrplu;" > "$temp_file"
        echo "" >> "$temp_file"
        cat "$file" >> "$temp_file"
        mv "$temp_file" "$file"
    fi
}

# Move and update files
for file in "${!file_mapping[@]}"; do
    package="${file_mapping[$file]}"
    
    if [ -f "$SRC_DIR/$file" ]; then
        if [ -z "$package" ]; then
            dest_dir="$NEW_SRC_DIR"
        else
            dest_dir="$NEW_SRC_DIR/$package"
        fi
        
        echo "Moving $file to $dest_dir/"
        cp "$SRC_DIR/$file" "$dest_dir/"
        
        # Add package declaration
        add_package_declaration "$dest_dir/$file" "$package"
        
        echo "  ✓ Moved and updated $file"
    else
        echo "  ⚠ Warning: $file not found in $SRC_DIR"
    fi
done

echo ""
echo "Migration complete!"
echo ""
echo "Next steps:"
echo "1. Review the moved files in src/main/java/com/vrplu/"
echo "2. Update import statements in all Java files"
echo "3. Run 'mvn clean compile' to check for compilation errors"
echo "4. Update and fix any import issues"
echo "5. Run 'mvn test' to ensure tests pass"
echo ""
echo "Note: The original files in 'src/' directory are preserved."
echo "After verifying everything works, you can remove the old 'src/' directory."
