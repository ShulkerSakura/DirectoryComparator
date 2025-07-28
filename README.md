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
目录比较结果 (版本: 1.0.0):
目录1: /path/to/dir1
目录2: /path/to/dir2
============================================================
[修改] src/Main.java
[新增] src/Utils.java
[删除] config.txt
[相同] README.md
============================================================
统计结果:
新增文件: 1
删除文件: 1
修改文件: 1
相同文件: 1
```
