import subprocess

# Define the path to your Java classes or JAR file
# If using Maven or Gradle, the JAR is often in `target/` or `build/libs/`
classpath = "target/syntastic-1.0-SNAPSHOT.jar" # Adjust this if your project structure is different
java_program = "org.example.Main"

# Define the arguments
args = [
    "--minAge=1",
    "--maxAge=2",
    "--gender=Male,Female",
    "--countryList=Germany,Italy,France",
    "--maxDrugs=6",
    "--snpsPerDrugType=100",
    "--percentageOfSnpsForDrugPerDrugType=80",
    "--baseDrugEffectiveness=0.5",
    "--negativePriorDrugEvent=-0.1",
    "--positivePriorDrugEvent=0.1"
]

# Build the full command
command = ["java", "-cp", classpath, java_program] + args

# Run the Java program
subprocess.run(command)
