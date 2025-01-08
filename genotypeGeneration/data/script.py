# -*- coding: utf-8 -*-
import os
import shutil
import subprocess

# Path to the configuration file
config_prefix = "config"
data_dir = "data"

# Current working directory and data directory
data_path = "/beegfs/HPCscratch/braitinger/data"  # Specify the path to the 'data' directory here
print("Path to data directory:", data_path)
print("Current working directory:", os.getcwd())

# Ensure that the "data" directory exists
if not os.path.exists(data_dir):
    try:
        os.makedirs(data_dir)
        print("The 'data' directory has been created.")
    except OSError as e:
        if not os.path.isdir(data_dir):
            raise  # Only raise again if the directory does not exist

# Loop for chromosomes 1 to 22
for chromosome in range(1, 23):
    config_new = "%s%d.yaml" % (config_prefix, chromosome)
    original_config = "%s.yaml" % config_prefix

    # Ensure that the original file exists
    if not os.path.exists(original_config):
        raise IOError("The file %s does not exist." % original_config)

    # If the new configuration file already exists, delete it
    if os.path.exists(config_new):
        print("The file %s already exists and will be deleted." % config_new)
        os.remove(config_new)

    # Copy the original file to the new configuration file
    print("Copying %s to %s" % (original_config, config_new))
    shutil.copy(original_config, config_new)

    # Replace ${chr} with the current chromosome
    with open(config_new, 'r') as file:
        config_content = file.read()

    print("Replacing ${chr} with chromosome %d" % chromosome)
    config_content = config_content.replace('${chr}', str(chromosome))

    # Write the modified configuration file
    with open(config_new, 'w') as file:
        file.write(config_content)

    print("New configuration file %s created successfully." % config_new)

    # Prepare the Docker command
    docker_command = [
        "docker", "run", "--rm",
        "-v", "%s:/data" % data_path,  # Mounting data directory
        "sophiewharrie/intervene-synthetic-data",
        "generate_geno", "32", "/data/%s" % config_new
    ]

    # Debugging: Display the full Docker command
    print("Preparing Docker command:", " ".join(docker_command))

    # Execute the Docker command
    try:
        print("Executing:", " ".join(docker_command))
        subprocess.check_call(docker_command)
    except subprocess.CalledProcessError as e:
        print("Error running Docker for chromosome %d: %s" % (chromosome, e))
    except Exception as e:
        print("Unknown error while running Docker command:", e)

# Function to modify VCF files with RSIDs
def modify_vcf_with_rsid(vcf_dir, rsid_map_dir, output_vcf_dir, chromosome):
    """
    Modifies a VCF file to include the correct RSIDs based on a mapping file.
    """
    # Input file (VCF file)
    vcf_file = os.path.join(vcf_dir, "test10_chr-{}.vcf".format(chromosome))
    # RSID mapping file
    rsid_map_file = os.path.join(rsid_map_dir, "rsid_map_list_chr{}.txt".format(chromosome))
    # Output file (modified VCF file)
    output_vcf_file = os.path.join(output_vcf_dir, "modified_test_chr-{}.vcf".format(chromosome))

    # Ensure the output directory exists
    if not os.path.exists(output_vcf_dir):
        os.makedirs(output_vcf_dir)
        print("Directory {} has been created.".format(output_vcf_dir))

    # Load RSID map
    rsid_map = {}
    try:
        with open(rsid_map_file, 'r') as f:
            f.readline()  # Skip the header
            for line in f:
                line = line.strip()
                if not line:
                    continue
                parts = line.split('\t')
                if len(parts) != 2:
                    continue
                snp, rsid = parts
                snp_parts = snp.split(':')
                if len(snp_parts) == 4:
                    pos = snp_parts[1]
                    rsid_map[pos] = rsid
        print("RSID map for chromosome {} loaded. {} entries.".format(chromosome, len(rsid_map)))
    except IOError as e:
        print("Error loading the RSID map file for chromosome {}: {}".format(chromosome, e))
        return

    # Edit the VCF file
    try:
        with open(vcf_file, 'r') as vcf, open(output_vcf_file, 'w') as out_vcf:
            for line in vcf:
                if line.startswith("#"):
                    out_vcf.write(line)
                else:
                    columns = line.strip().split('\t')
                    pos = columns[1]  # The second column is POS
                    if pos in rsid_map:
                        columns[2] = rsid_map[pos]  # Replace ID with RSID
                    else:
                        columns[2] = '.'  # No match: keep ID as "."
                    out_vcf.write('\t'.join(columns) + '\n')
        print("Modified VCF file for chromosome {} saved: {}".format(chromosome, output_vcf_file))
    except IOError as e:
        print("Error editing the VCF file for chromosome {}: {}".format(chromosome, e))

# Function to create VCF files with PLINK
def run_plink_to_vcf(docker_image, input_dir, output_dir, chromosome):
    """
    Creates a VCF file from PLINK data for a specific chromosome.
    """
    # Paths for input files
    bed_file = os.path.join(input_dir, "test10_chr-{}.bed".format(chromosome))
    bim_file = os.path.join(input_dir, "test10_chr-{}.bim".format(chromosome))
    fam_file = os.path.join(input_dir, "test10_chr-{}.fam".format(chromosome))
    vcf_file = os.path.join(output_dir, "test10_chr-{}.vcf".format(chromosome))

    # Docker command
    command = [
        "docker", "run", "--rm",
        "-v", "{}:/data".format(input_dir),
        "-v", "{}:/output".format(output_dir),
        docker_image,
        "plink",
        "--bfile", "/data/test10_chr-{}".format(chromosome),  # Use the correct bfile
        "--recode", "vcf",
        "--out", "/output/test10_chr-{}".format(chromosome)  # Output in the correct directory
    ]

    try:
        # Execute and verify
        subprocess.call(command)
        print("VCF file for chromosome {} created: {}".format(chromosome, vcf_file))
    except OSError as e:
        print("Error creating VCF file for chromosome {}: {}".format(chromosome, e))


# Main part of the script
input_dir = "/beegfs/HPCscratch/braitinger/data/outputs/test"  # Directory with PLINK data
output_vcf_dir = "/beegfs/HPCscratch/braitinger/data/outputs/vcf"  # Directory for VCF files
rsid_map_dir = "/beegfs/HPCscratch/braitinger/data/inputs/processed/1KG+HGDP"  # Directory for RSID mapping files
docker_image = "plink1.9"  # Docker image for PLINK

# Target directory for modified VCF files
modified_vcf_dir = "/beegfs/HPCscratch/braitinger/data/outputs/modified_vcfs"

# Specify the chromosomes you want to process
for chromosome in range(1, 23):  # Example for chromosomes 1 to 22
    # First, use PLINK to create the VCF file
    run_plink_to_vcf(docker_image, input_dir, output_vcf_dir, chromosome)

    # Then modify the VCF file with RSID
    modify_vcf_with_rsid(output_vcf_dir, rsid_map_dir, modified_vcf_dir, chromosome)
