import subprocess
import time

# Define the path to your Java JAR or classpath
classpath = "target/syntastic-1.0-SNAPSHOT.jar"  # Adjust if needed
java_program = "org.example.Main"

# Define the arguments
args = [
    "--minAge=18",
    "--maxAge=75",
    "--gender=Male,Female",
    "--countryList=Germany including former GDR,Italy,France,Spain,Switzerland,Denmark,Croatia",
    "--maxDrugs=6",
    "--snpsPerDrugType=50",
    "--percentageOfSnpsForDrugPerDrugType=80",
    "--baseDrugEffectiveness=0.5",
    "--negativePriorDrugEvent=-0.1",
    "--positivePriorDrugEvent=0.1"
]

# Build the full Java command
command = ["java", "-Xrs", "-cp", classpath, java_program] + args

# Start measuring execution time
start_time = time.time()

try:
    # Run the Java program with output captured
    result = subprocess.run(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, timeout=30)

    # Print Java output
    print(result.stdout)

    # Check for errors
    if result.stderr:
        print("Java Error Output:")
        print(result.stderr)

except subprocess.TimeoutExpired:
    print("⏳ Java execution took too long and was forcefully stopped!")

except Exception as e:
    print(f"An error occurred: {e}")

# Print execution time
execution_time = time.time() - start_time
print(f"Java execution finished in {execution_time:.2f} seconds.")
