# -*- coding: utf-8 -*-
import os
import shutil
import subprocess


def create_directory(directory_name):
    """Create a directory if it doesn't exist."""
    if not os.path.exists(directory_name):
        os.makedirs(directory_name)
        print("Directory '{}' created.".format(directory_name))
    else:
        print("Directory '{}' already exists.".format(directory_name))


def write_dockerfile():
    """Write the Dockerfile to the current directory."""
    dockerfile_content = """FROM ubuntu:20.04

# Install dependencies
RUN apt-get update && apt-get install -y \\
    wget \\
    unzip \\
    && apt-get clean

# Download and install PLINK 1.9
RUN wget https://s3.amazonaws.com/plink1-assets/plink_linux_x86_64_20230116.zip -O plink.zip && \\
    unzip plink.zip -d /usr/local/bin && \\
    rm plink.zip

# Set PATH
ENV PATH="/usr/local/bin:$PATH"

# Test PLINK installation
RUN plink --version
"""

    with open("Dockerfile", "w") as file:
        file.write(dockerfile_content)
    print("Dockerfile written.")


def build_docker_image(image_name):
    """Build the Docker image using the Dockerfile."""
    try:
        subprocess.check_call(["docker", "build", "-t", image_name, "."])
        print("Docker image '{}' built successfully.".format(image_name))
    except subprocess.CalledProcessError as e:
        print("Error building Docker image: {}".format(e))


def run_docker_commands():
    """Run the Docker commands."""
    try:
        # Pull the container
        subprocess.check_call(["docker", "pull", "sophiewharrie/intervene-synthetic-data"])
        print("Container 'sophiewharrie/intervene-synthetic-data' pulled successfully.")

        # Set up directory structure
        create_directory("data")

        # Run the init command
        subprocess.check_call([
            "docker", "run", "--rm", "-v", "$(pwd)/data:/data",
            "sophiewharrie/intervene-synthetic-data", "init"
        ])
        print("Init command executed successfully.")

        # Run the fetch command
        subprocess.check_call([
            "docker", "run", "--rm", "-v", "$(pwd)/data:/data",
            "sophiewharrie/intervene-synthetic-data", "fetch"
        ])
        print("Fetch command executed successfully.")

    except subprocess.CalledProcessError as e:
        print("Error executing command: {}".format(e))


def copy_rsid_file_and_cleanup():
    """
    Copy rsid_variant_map_list_chr9.txt to the target directory,
    rename it, and remove the setup directory.
    """
    source_dir = "./setup"
    target_dir = "./inputs/processed/1KG+HGDP"
    source_file_name = "rsid_variant_map_list_chr9.txt"
    target_file_name = "rsid_map_list_chr9.txt"

    # Ensure the target directory exists
    create_directory(target_dir)

    # Check if the source file exists
    source_file = os.path.join(source_dir, source_file_name)
    if not os.path.exists(source_file):
        print("Source file '{}' does not exist.".format(source_file))
        return

    # Copy the file to the target directory with the new name
    target_file = os.path.join(target_dir, target_file_name)
    shutil.copy(source_file, target_file)
    print("Copied '{}' to '{}'.".format(source_file, target_file))

    # Remove the setup directory
    if os.path.exists(source_dir):
        shutil.rmtree(source_dir)
        print("Removed setup directory '{}'.".format(source_dir))
    else:
        print("Setup directory '{}' does not exist.".format(source_dir))


def main():
    # Step 1: Create the data directory
    create_directory("data")

    # Step 2: Write the Dockerfile
    write_dockerfile()

    # Step 3: Build the Docker container from the Dockerfile
    build_docker_image("plink_container")

    # Step 4: Execute the Singularity commands as Docker commands
    run_docker_commands()

    # Step 5: Copy rsid_variant_map_list_chr9.txt to the target directory and clean up
    copy_rsid_file_and_cleanup()


if __name__ == "__main__":
    main()
