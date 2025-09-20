#!/bin/bash

# Script to update javax imports to jakarta imports in Java files

echo "Updating javax imports to jakarta imports..."

# Find all Java files in the project
find src -name "*.java" -type f | while read -r file; do
  echo "Processing $file"
  
  # Update javax.persistence to jakarta.persistence
  sed -i '' 's/import javax\.persistence\./import jakarta.persistence./g' "$file"
  
  # Update javax.validation to jakarta.validation
  sed -i '' 's/import javax\.validation\./import jakarta.validation./g' "$file"
  
  # Update javax.servlet to jakarta.servlet
  sed -i '' 's/import javax\.servlet\./import jakarta.servlet./g' "$file"
done

echo "Update complete!"