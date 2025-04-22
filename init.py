import os
import sys
import subprocess

# Ensure output directory exists
os.makedirs("/app/data/output", exist_ok=True)

# Base Java command to run the JAR file with additional JVM options
java_command = ["java", "-XX:+UseG1GC", "-XX:+PrintGCDetails", "-jar", "/app/app.jar"]

# Print the final command for debugging purposes
print("Running command:", " ".join(java_command))

# Execute the command (this runs the Java application)
try:
    subprocess.run(java_command, check=True)  # Will raise an error if the command fails
    print("Java application executed successfully.")
except subprocess.CalledProcessError as e:
    print(f"Error: The Java application failed with error: {e}")
    sys.exit(1)
