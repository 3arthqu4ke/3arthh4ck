import errno
import fileinput
import os
import re
import shutil
from os import path, linesep


def copy(src, dst):
    try:
        shutil.copytree(src, dst)
    except OSError as exc:
        if exc.errno in (errno.ENOTDIR, errno.EINVAL):
            shutil.copy(src, dst)
        else:
            raise


def update(file, regex, value):
    print(f"Checking file {file} for {regex}, replacing with {value}...")
    with fileinput.input(file, inplace=True) as file_to_update:
        for line in file_to_update:
            if re.match(regex, line):
                line = re.sub(regex, f"\\g<1>{value}\\g<2>", line)
            print(line, end='')


def yesNoPrompt(message):
    while True:
        resultInput = input(message)
        if resultInput.casefold() == 'y':
            result = True
            break
        elif resultInput.casefold() == 'n':
            result = False
            break
        else:
            print('Please answer with one of yes/no...')
    return result


if __name__ == '__main__':
    name = input(f"Please input a name for your plugin (properly capitalized!)...{linesep}")
    capName = name
    name = name.casefold()
    if path.isdir(name):
        exit('A directory with that name already exists!')
    package = input(f"Please enter a package name (e.g. me.earth.exampleplugin)...{linesep}")
    mixins = yesNoPrompt(f"Do you want to use Mixins? (y/n){linesep}")

    group = package
    archivesBaseName = name
    earthhackConfig = f"{capName}PluginConfig"
    examplePlugin = 'example' if mixins else 'example-no-mixins'

    os.mkdir(name)
    copy(path.join(examplePlugin, 'gradle'), path.join(name, 'gradle'))
    copy(path.join(examplePlugin, 'gradlew'), path.join(name, 'gradlew'))
    copy(path.join(examplePlugin, 'gradlew.bat'), path.join(name, 'gradlew.bat'))
    copy(path.join(examplePlugin, 'gradle.properties'), path.join(name, 'gradle.properties'))
    copy(path.join(examplePlugin, 'build.gradle'), path.join(name, 'build.gradle'))
    os.makedirs(path.join(name, 'src', 'main', 'java', *package.split('.')))

    update(path.join(name, 'build.gradle'), r"(group = ').*('.*)// TODO", group)
    update(path.join(name, 'build.gradle'), r"(archivesBaseName = ').*('.*)// TODO", archivesBaseName)
    update(path.join(name, 'build.gradle'), r"(.*'3arthh4ckConfig': ').*('.*)// TODO", f"{earthhackConfig}.json")
    if mixins:
        # noinspection PyUnboundLocalVariable
        update(path.join(name, 'build.gradle'), r"(.*add sourceSets.main, ').*('.*)// TODO",
               f"mixins.{name}.refmap.json")
        # noinspection PyUnboundLocalVariable
        update(path.join(name, 'build.gradle'), r"(.*'MixinConfigs': ').*('.*)// TODO",
               f"mixins.{name}.json")

    os.makedirs(path.join(name, 'src', 'main', 'resources'))
    with open(path.join(name, 'src', 'main', 'resources', f"{earthhackConfig}.json"), 'w') as f:
        # TODO: I KNOW THESE F-STRINGS LOOK HIDEOUS BUT IT DOES NOT WORK
        f.write(f"""{{
    "name": "{capName}",
    "mainClass": "{package}.{capName}",
    "mixinConfig": {f'"mixins.{name}.json"' if mixins else 'null'}
}}
""")

    with open(path.join(name, 'src', 'main', 'java', *package.split('.'), f"{capName}.java"), 'w') as f:
        f.write(f"""package {package};
        
import me.earth.earthhack.api.plugin.Plugin;

@SuppressWarnings("unused")
public class {capName} implements Plugin {{
    @Override
    public void load() {{
        System.out.println("Hello from the {capName} plugin!");
    }}
}}
""")

    if mixins:
        os.makedirs(path.join(name, 'src', 'main', 'java', *package.split('.'), 'mixins'))
        with open(path.join(name, 'src', 'main', 'resources', f"mixins.{name}.json"), 'w') as f:
            f.write(f"""{{
    "required": true,
    "compatibilityLevel": "JAVA_8",
    "package": "{package}.mixins",
    "refmap": "mixins.{name}.refmap.json",
    "mixins": [
    ]
}}
""")
