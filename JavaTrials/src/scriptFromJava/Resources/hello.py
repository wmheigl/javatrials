import argparse
import csv
import sys

parser = argparse.ArgumentParser(description='pass a file path')
parser.add_argument('--file', help='Shared memory file', type=str, nargs='+')
parser.add_argument('--type', help='Data type in file', type=str, nargs='+')
args = parser.parse_args()

print('Hello from Python!')
print(args)

print('reading from ' + args.file[0])
with open(args.file[0], mode='r') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',', quoting=csv.QUOTE_NONNUMERIC)
    for row in csv_reader:
        print(row)
