FROM openjdk:21-jdk-slim
LABEL authors="jonasfluck"
# Set working directory inside the container
WORKDIR /app

# Copy compiled JAR file from target/ (after `mvn package`)
COPY target/app.jar /app/app.jar

# Copy the Python script that generates command-line args
COPY init.py /app/init.py

# Install Python inside the container (needed for the script)
RUN apt-get update && apt-get install -y python3 && rm -rf /var/lib/apt/lists/*

# Default command (will be replaced by generate_args.py)
# Ensure the Python script is executable
RUN chmod +x /app/init.py

# Set the entrypoint to execute init.py, and then run the generated Java command
ENTRYPOINT ["python3", "/app/init.py"]

