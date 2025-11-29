#!/bin/bash

# Script to display the new project structure
echo "========================================="
echo "  VRPLU-OptLoad Project Structure"
echo "========================================="
echo ""

tree -L 4 -I 'target|*.class' /home/gunturi/VRPLU-OptLoad 2>/dev/null || find /home/gunturi/VRPLU-OptLoad -type d -not -path '*/target/*' -not -path '*/.git/*' | sed 's|/home/gunturi/VRPLU-OptLoad||' | sort

echo ""
echo "========================================="
echo "  Package Organization"
echo "========================================="
echo ""
echo "com.vrplu/"
echo "├── domain/          Domain models (Node, Edge, Graph, etc.)"
echo "├── algorithm/"
echo "│   ├── exact/       Exact algorithms"
echo "│   └── heuristic/   Heuristic algorithms"
echo "├── solver/          Solver infrastructure"
echo "├── util/            Utility classes"
echo "└── io/              Input/Output operations"
echo ""
