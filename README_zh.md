## README.md (中文版)

# DirectoryComparator - 目录比较工具

[English Version](README_en.md) | 中文版

一个用于比较两个目录文件差异的Java工具。

## 功能特性

- 递归比较两个目录及其子目录
- 检测新增、删除、修改的文件
- 基于文件内容的精确比较（MD5校验）
- 支持命令行参数操作
- 可重定向输出到文件便于查看

## 使用方法 (Usage)

### 基本用法
```bash
java -jar DirectoryComparator.jar <目录1> <目录2>
```

### 显示版本号
```bash
java -jar DirectoryComparator.jar --version
```

### 隐藏相同文件（只显示变动）
```bash
java -jar DirectoryComparator.jar --hide-same <目录1> <目录2>
```

### 显示帮助信息
```bash
java -jar DirectoryComparator.jar --help
```

## 特殊用法提示

**日志重定向输出：**
```bash
java -jar DirectoryComparator.jar <Directory1> <Directory2> > change.log
```
将比较结果重定向到 `change.log` 文件中，方便后续查看和分析。

## 参数说明

| 参数 | 说明 |
|------|------|
| `-v`, `--version` | 显示版本号 |
| `-h`, `--help` | 显示帮助信息 |
| `-s`, `--hide-same` | 隐藏相同文件，只显示有变动的文件 |

## 输出示例

```
目录比较结果 (版本: 1.2.0):
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
