#!/usr/bin/env python3
import argparse
import fnmatch
import os


def main():
    parser = argparse.ArgumentParser(description='Remove footer in files having extension with ".platuml"')
    parser.add_argument('-p', '--path', help='Path to the Directory', required=True)
    parser.add_argument('-s', '--search', help='Searched String', required=True)
    parser.add_argument('-r', '--replace', help='Replace String', required=True)
    parser.add_argument('-e', '--extension', help='Optional value for file extensions like *.*',
                        nargs='?', const='*.*', type=str)

    args = vars(parser.parse_args())

    args_search_path = args['path']
    args_search_string = args['search']
    args_test_to_be_replace = args['replace']
    args_file_extension = args['extension']

    find_replace(args_search_path, args_search_string, args_test_to_be_replace, args_file_extension)


def find_replace(args_search_path, args_search_string, args_test_to_be_replace, args_file_extension):
    for path, dirs, files in os.walk(os.path.abspath(args_search_path)):
        for filename in fnmatch.filter(files, args_file_extension):
            filepath = os.path.join(path, filename)
            with open(filepath) as f:
                s = f.read()
            s = s.replace(args_search_string, args_test_to_be_replace)
            with open(filepath, "w") as f:
                f.write(s)


if __name__ == "__main__":
    main()
