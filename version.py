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

        update(build_gradle, r"(project.version = ').*('.*)", version)
        update(mcmod_info, r"(.*\"version\": \").*(\",.*)", version)
        update(earthhack, r"(.*VERSION = \").*(\";.*)", version)

        if '-nogit' not in argv:
            subprocess.run(['git', 'reset'], stdout=sys.stdout, stderr=sys.stderr)
            subprocess.run(['git', 'add', build_gradle], stdout=sys.stdout, stderr=sys.stderr)
            subprocess.run(['git', 'add', mcmod_info], stdout=sys.stdout, stderr=sys.stderr)
            subprocess.run(['git', 'add', earthhack], stdout=sys.stdout, stderr=sys.stderr)
            subprocess.run(['git', 'commit', '-m', f"[{version}] Bump version"], stdout=sys.stdout, stderr=sys.stderr)
    else:
        print("Cancelled version update!")
