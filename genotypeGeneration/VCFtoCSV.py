import os
import csv
from glob import glob  # For listing files with specific patterns

# Input and output paths
input_folder = "modified_test_vcfs"  # Folder containing all the modified_test_chr-X.vcf files
output_csv = "input.csv"  # Output CSV file

def process_vcf_to_csv(vcf_files, output_csv):
    """Processes multiple VCF files and writes patient genotype data to CSV."""
    all_rows = []
    header_written = False

    for vcf_file in vcf_files:
        with open(vcf_file, "r") as file:
            lines = file.readlines()

        header_line = None
        for line in lines:
            if line.startswith("#CHROM"):  # Find the header line
                header_line = line.strip()
                break

        if not header_line:
            print("VCF header (#CHROM) not found in file: {}".format(vcf_file))
            continue

        headers = header_line.split("\t")
        patient_ids = headers[9:]  # Extract patient IDs from header

        for line in lines:
            if line.startswith("#"):
                continue  # Skip metadata lines
            
            fields = line.strip().split("\t")
            chromosome = fields[0]  # Chromosome
            position = fields[1]    # Position
            rsid = fields[2]         # rsID
            reference = fields[3]    # Reference allele
            alternative = fields[4]  # Alternative allele
            genotype_data = fields[9:]  # Patient-specific genotype data
            
            for patient_idx, genotype in enumerate(genotype_data):
                patient_id = patient_ids[patient_idx]
                genotype_expression = genotype.split(":")[0]  # Extract the genotype (e.g., "0/1")
                all_rows.append([patient_id, chromosome, position, rsid, reference, alternative, genotype_expression])

    # Write to CSV
    with open(output_csv, "w") as csv_file:
        writer = csv.writer(csv_file)
        # Write the header
        writer.writerow(["Patient ID", "Chromosome", "Position", "rsID", "Reference", "Alternative", "Genotype"])
        # Write data rows
        writer.writerows(all_rows)

    print("CSV file successfully written to {}".format(output_csv))

# Main execution
if __name__ == "__main__":
    print("Starting VCF processing...")
    vcf_files = glob(os.path.join(input_folder, "*.vcf"))
    
    if not vcf_files:
        print("No VCF files found in the input folder!")
    else:
        process_vcf_to_csv(vcf_files, output_csv)
        print("All VCF files processed successfully.")