# -*- coding: utf-8 -*-
import json
import os
from glob import glob
from multiprocessing import Pool

# Input and output paths
input_folder = "/beegfs/HPCscratch/braitinger/data/outputs/modified_vcfs"
output_folder = "/beegfs/HPCscratch/braitinger/data/outputs/json_per_chromosome"
final_output_json = "all_patients_combined.json"

# Ensure output folder exists
if not os.path.exists(output_folder):
    os.makedirs(output_folder)

# Function to process a single VCF file and save per chromosome JSON
def process_vcf_to_json(input_vcf):
    chromosome_data = {}

    with open(input_vcf, "r") as file:
        lines = file.readlines()

    # Extract header and data
    header_line = None
    for line in lines:
        if line.startswith("#CHROM"):
            header_line = line.strip()
            break

    if not header_line:
        raise ValueError("VCF header (#CHROM) not found in the file: {}".format(input_vcf))

    # Parse header to extract patient IDs
    headers = header_line.split("\t")
    patient_ids = headers[9:]

    print "Processing SNP data for {}...".format(input_vcf)

    # Process SNP data
    for line in lines:
        if line.startswith("#"):
            continue
        fields = line.strip().split("\t")

        chromosome = fields[0]
        position = fields[1]
        snp_id = fields[2]
        reference = fields[3]
        alternative = fields[4]
        genotype_data = fields[9:]

        # Initialize chromosome data structure if not already created
        if chromosome not in chromosome_data:
            chromosome_data[chromosome] = {}

        # Add genotype data to each patient for the chromosome
        for patient_idx, genotype in enumerate(genotype_data):
            patient_id = patient_ids[patient_idx]

            # Initialize patient data structure if not already created
            if patient_id not in chromosome_data[chromosome]:
                chromosome_data[chromosome][patient_id] = []

            genotype_expression = genotype.split(":")[0]

            # Append SNP data for the patient
            chromosome_data[chromosome][patient_id].append({
                "id": snp_id,
                "position": position,
                "reference": reference,
                "alternative": alternative,
                "expression": genotype_expression
            })

    # Save each chromosome's data to a separate JSON file
    for chromosome, data in chromosome_data.items():
        output_file = os.path.join(output_folder, "chromosome_{}.json".format(chromosome))
        with open(output_file, "w") as chrom_file:
            json.dump(data, chrom_file, indent=4)
        print "Chromosome {} data written to {}".format(chromosome, output_file)

    print "Finished processing {}.".format(input_vcf)

# Function to combine all JSON files into one
def combine_all_json(output_folder, final_output_json):
    combined_data = {}

    # Load all JSON files in the output folder
    json_files = glob(os.path.join(output_folder, "*.json"))
    for json_file in json_files:
        with open(json_file, "r") as file:
            data = json.load(file)
            for patient_id, snps in data.items():
                if patient_id not in combined_data:
                    combined_data[patient_id] = []
                combined_data[patient_id].extend(snps)

    # Write combined data to the final JSON file
    with open(final_output_json, "w") as final_file:
        json.dump(combined_data, final_file, indent=4)

    print "All chromosome data combined and written to {}".format(final_output_json)

# Main execution for multiple VCF files
if __name__ == "__main__":
    print "Starting VCF processing..."

    # Collect all VCF files in the input folder
    vcf_files = glob(os.path.join(input_folder, "*.vcf"))
    if not vcf_files:
        print "No VCF files found in the input folder!"
        exit()

    # Create a Pool of processes
    pool = Pool()

    # Use multiprocessing Pool to process VCF files in parallel
    pool.map(process_vcf_to_json, vcf_files)

    # Close the pool and wait for the processes to finish
    pool.close()
    pool.join()

    # Combine all chromosome JSON files into one
    combine_all_json(output_folder, final_output_json)

    print "All VCF files processed and combined successfully."

