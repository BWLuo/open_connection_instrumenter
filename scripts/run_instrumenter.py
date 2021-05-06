import os
import subprocess
import sys
import time

# Pass in the folder containing the apks and the output folder
dir_path = os.path.dirname(os.path.realpath(__file__))
platform_dir = os.path.abspath(sys.argv[1])  # sdk/platforms directory
api_level = sys.argv[2]                      # api level of android jar
apk_dir = os.path.abspath(sys.argv[3])       # apk file
out_dir = os.path.abspath(sys.argv[4])       # output dir

# run the instrumenter
cmd = ['java', '-jar', f"{dir_path}/open_connection_instrumenter-1.0-SNAPSHOT-jar-with-dependencies.jar", platform_dir, api_level, apk_dir, out_dir]
start = time.time()
p = subprocess.Popen(cmd, stdout=subprocess.PIPE)
for line in p.stdout:
    print(line)
p.wait()
end = time.time()
total = end - start
print(f"TOTAL OF {total} (s)")
