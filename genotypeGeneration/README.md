# Genotype Generation Project

This project consists of two main components designed to generate genotype data using Docker and Python. It is built to work on Linux systems and requires Python 2.7.
This is the ReadME for the genotyp generation:
### Important Notes:
- This script will only work on Linux and needs atleast Python 2.7
- Do not modify the folder structure in `genotypeGeneration/`. The scripts depend on the specific organization of directories and files.
- Ensure Docker is installed and properly configured on your Linux machine.

## Setup Script: `setup.py`

The `setup.py` script (located in `genotypeGeneration/`) prepares the environment for genotype generation. It performs the following tasks:

1. **Creates necessary directories**: Ensures that the required directory structure is in place.
2. **Writes the Dockerfile**: The Dockerfile installs dependencies like PLINK 1.9 in a container.
3. **Builds a Docker container**: Using the Dockerfile, this script builds a container named `plink_container`.
4. **Pulls data from a synthetic data tool**: The script uses the Docker container to pull necessary data by running commands from the tool available at [synthetic_data tool](https://github.com/intervene-EU-H2020/synthetic_data).
5. **Handles file preparation**: Copies the file `rsid_variant_map_list_chr9.txt` from the setup folder to the target directory `inputs/processed/1KG+HGDP` with a new name `rsid_map_list_chr9.txt`, and deletes the setup folder afterward.

### Usage Instructions for `setup.py`:

Run the `setup.py` script to prepare the environment:

```bash
python setup.py
```
## Main Script: `script.py`

The `script.py` (located in `genotypeGeneration/`) is the main driver script for generating genotypes. Before running this script, you must update the paths to use absolute paths that match your system setup.

### What the script does:

- **Processes Chromosomes**: Loops over chromosomes 1 to 22 and generates configuration files for each chromosome.
- **Runs Docker Commands**: Uses Docker to run the `intervene-synthetic-data` tool to generate synthetic genotype data for each chromosome.
- **Modifies VCF Files**: After generating VCF files, it modifies them by replacing SNP IDs with RSIDs, based on a provided mapping file.
- **Converts PLINK Data to VCF**: Uses PLINK to convert binary files (BED, BIM, FAM) into VCF format for each chromosome.


**ALL** File Paths in this script Must Be Updated: It's **critical** to update all file paths in the script to reflect the absolute paths on your system, or the script will fail to find the required directories and files. Its also **NOT** possible to use relative paths!
    Only **absolut** Paths will work.
### Usage Instructions for `script.py`:

Once the paths are updated, run `script.py` to generate the genotype data:

```bash
python script.py
```
## VCFtoJson Script: `VCFtoJSON.py`
The script converts the created VCF Files for every
chromosome into on big JSON. This JSON file will be the input for the 
phenotype generation. Before running the script, you
must update the paths.
```bash
python VCFtoJSON.py`
```