import os
import subprocess
import sys

# Pass in the folder containing the apks and the output folder
dir_path = os.path.dirname(os.path.realpath(__file__))
apk_file = os.path.abspath(sys.argv[1])       # apk file
tmp_file = f"{dir_path}/tmp/tmp.apk"

subprocess.check_output(f"zipalign -f 4 {apk_file} {tmp_file}", shell=True)
subprocess.check_output(f"echo password | apksigner sign --ks my.keystore {tmp_file}", shell=True)
subprocess.check_output(f"rm {apk_file}", shell=True)
subprocess.check_output(f"mv {tmp_file} {apk_file}", shell=True)
