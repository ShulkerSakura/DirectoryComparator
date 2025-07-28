## README_en.md (English Version)

# DirectoryComparator - Directory Comparison Tool

English Version | [中文版](README_zh.md)

A Java tool for comparing file differences between two directories.

## Features

- Recursively compare two directories and their subdirectories
- Detect added, deleted, and modified files
- Accurate comparison based on file content (MD5 checksum)
- Command-line parameter support
- Output redirection support for easy viewing

## Usage

### Basic Usage
```bash
java -jar DirectoryComparator.jar <Directory1> <Directory2>
```

### Show Version
```bash
java -jar DirectoryComparator.jar --version
```

### Hide Same Files (Show Changes Only)
```bash
java -jar DirectoryComparator.jar --hide-same <Directory1> <Directory2>
```

### Show Help
```bash
java -jar DirectoryComparator.jar --help
```

## Special Usage Tip

**Log Redirection Output:**
```bash
java -jar DirectoryComparator.jar <Directory1> <Directory2> > change.log
```
Redirect the comparison results to `change.log` file for convenient viewing and analysis later.

## Parameters

| Parameter | Description |
|-----------|-------------|
| `-v`, `--version` | Show version number |
| `-h`, `--help` | Show help information |
| `-s`, `--hide-same` | Hide same files, only show changed files |

## Output Example

```
Directory Comparison Results (Version: 1.2.0):
Directory 1: /path/to/dir1
Directory 2: /path/to/dir2
============================================================
[Modified] src/Main.java
[Added] src/Utils.java
[Deleted] config.txt
[Same] README.md
============================================================
Statistics:
Added files: 1
Deleted files: 1
Modified files: 1
Same files: 1
```
