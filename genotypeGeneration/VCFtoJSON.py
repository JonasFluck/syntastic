import json
import os
from glob import glob  # For listing files with specific patterns

# Input and output paths
input_folder = "modified_test_vcfs"  # Folder containing all the modified_test_chr-X.vcf files
output_json = "all_patients.json"  # Large JSON file for all patients across all VCF files

# Function to process a single VCF file into a JSON structure
def process_vcf_to_json(input_vcf):
    patient_data = {}

    with open(input_vcf, "r") as file:
        lines = file.readlines()

    # Extract header and data
    header_line = None
    for line in lines:
        if line.startswith("#CHROM"):  # Find the header line
            header_line = line.strip()
            break

    if not header_line:
        raise ValueError(f"VCF header (#CHROM) not found in the file: {input_vcf}")

    # Parse header to extract patient IDs
    headers = header_line.split("\t")
    patient_ids = headers[9:]  # Patient data starts at the 10th column

    print(f"Processing SNP data for {input_vcf}...")

    # Process SNP data
    for line in lines:
        if line.startswith("#"):
            continue  # Skip metadata lines
        fields = line.strip().split("\t")
        
        chromosome = fields[0]  # Chromosome is in the first column
        position = fields[1]    # Position is in the second column
        id = fields[2]          # rsID is in the third column
        reference = fields[3]   # Reference allele is in the fourth column
        alternative = fields[4]  # Alternative allele is in the fifth column
        genotype_data = fields[9:]  # Patient-specific genotype data starts at the 10th column

        # Add genotype data to each patient
        for patient_idx, genotype in enumerate(genotype_data):
            patient_id = patient_ids[patient_idx]

            # Initialize patient data structure if not already created
            if patient_id not in patient_data:
                patient_data[patient_id] = []

            # Parse genotype expression
            genotype_expression = genotype.split(":")[0]  # Extract the genotype (e.g., "0/1")

            # Append SNP data for the patient
            patient_data[patient_id].append({
                "id": id,
                "chromosome": chromosome,
                "position": position,
                "reference": reference,
                "alternative": alternative,
                "expression": genotype_expression
            })

    print(f"Finished processing {input_vcf}.")
    return patient_data

# Function to write all patient data to one large JSON file
def write_large_json(all_patient_data, output_large_json):
    # Convert dictionary to a list of patient data
    all_patients = [
        {"patient_id": patient_id, "snps": snps}
        for patient_id, snps in all_patient_data.items()
    ]

    # Write all patient data to one large JSON file
    with open(output_large_json, "w") as large_file:
        json.dump(all_patients, large_file, indent=4)
    
    print(f"All patient data successfully written to {output_large_json}")

# Main execution for multiple VCF files
if __name__ == "__main__":
    print("Starting VCF processing...")
    
    # Collect all VCF files in the input folder
    vcf_files = glob(os.path.join(input_folder, "*.vcf"))
    if not vcf_files:
        print("No VCF files found in the input folder!")
        exit()

    combined_patient_data = {}  # Dictionary to combine data across all VCF files

    # Process each VCF file
    for vcf_file in vcf_files:
        try:
            patient_data = process_vcf_to_json(vcf_file)
            
            # Merge patient data from this file into the combined data
            for patient_id, snps in patient_data.items():
                if patient_id not in combined_patient_data:
                    combined_patient_data[patient_id] = []
                combined_patient_data[patient_id].extend(snps)

        except Exception as e:
            print(f"Error processing file {vcf_file}: {e}")
    
    # Write the combined patient data to a single JSON file
    write_large_json(combined_patient_data, output_json)

    print("All VCF files processed successfully.")
