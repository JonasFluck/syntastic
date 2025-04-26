import os
import sys
import json
import subprocess

# Define input paths (Users only modify external data folder)
config_file = "/app/data/config.json"

# Ensure required input files exist
for file in [config_file]:
    if not os.path.exists(file):
        print(f"Error: {file} not found!")
        sys.exit(1)

# Ensure output directory exists
os.makedirs("/app/data/output", exist_ok=True)

# Read JSON config and extract parameters
try:
    with open(config_file, "r") as f:
        config = json.load(f)
except json.JSONDecodeError:
    print("Error: Invalid JSON format in config file.")
    sys.exit(1)

# Base Java command
java_command = ["java", "-jar", "/app/app.jar"]
print("Wir sind hier.")
# Append parameters from JSON
for key, value in config.items():
    # Ensure the key is in the format --key=value
    if isinstance(value, list):
        # Join list items with commas (this might be useful for things like 'countries' or similar)
        java_command.append(f"--{key}={','.join(map(str, value))}")
    else:
        # Otherwise, just append the key-value pair in --key=value format
        java_command.append(f"--{key}={str(value)}")

# Print the final command for debugging purposes
print("Running command:", " ".join(java_command))

# Execute the command (this runs the Java application)
try:
    subprocess.run(java_command, check=True)  # Will raise an error if the command fails
    print("Java application executed successfully.")
except subprocess.CalledProcessError as e:
    print(f"Error: The Java application failed with error: {e}")
    sys.exit(1)