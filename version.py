import sys
from os import path, linesep
from sys import argv
import subprocess
import fileinput
import re


def update(file, regex, value):
    print(f"Checking file {file} for {regex}, replacing with {value}...")
    with fileinput.input(file, inplace=True) as f:
        for line in f:
            if re.match(regex, line):
                line = re.sub(regex, f"\\g<1>{value}\\g<2>", line)
            print(line, end='')


if __name__ == '__main__':
    if len(argv) < 2:
        version = input(f"Please input the version to update to...{linesep}")
        used_input = True
    else:
        version = argv[1]
        used_input = False

    if used_input or (len(argv) > 2 and argv[2] == '-f') or input(f"Set version to {version} (y/n)?{linesep}") == 'y':
        base = path.dirname(__file__)
        build_gradle = path.join(base, 'build.gradle')
        mcmod_info = path.join(base, 'src', 'main', 'resources', 'mcmod.info')
        earthhack = path.join(base, 'src', 'main', 'java', 'me', 'earth', 'earthhack', 'impl', 'Earthhack.java')

        sha = subprocess.check_output(['git', 'rev-parse', 'HEAD']).decode('ascii')
        sha2short = sha[:10]

        update(build_gradle, r"(project.version = ').*('.*)", version + "-35." + sha2short.strip())
        update(mcmod_info, r"(.*\"version\": \").*(\",.*)", version + "-35." + sha2short.strip())
        update(earthhack, r"(.*VERSION = \").*(\";.*)", version + "-35." + sha2short.strip())
    else:
        print("Cancelled version update!")
