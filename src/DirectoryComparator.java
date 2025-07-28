import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryComparator {

    // 全局版本号变量
    private static final String VERSION = "1.0.0";

    // 文件差异类型枚举
    public enum FileDiffType {
        ADDED,      // 新增文件
        DELETED,    // 删除文件
        MODIFIED,   // 修改文件
        SAME        // 相同文件
    }

    // 文件差异信息类
    public static class FileDiff {
        private String filePath;
        private FileDiffType diffType;
        private String relativePath;

        public FileDiff(String relativePath, String filePath, FileDiffType diffType) {
            this.relativePath = relativePath;
            this.filePath = filePath;
            this.diffType = diffType;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public String getFilePath() {
            return filePath;
        }

        public FileDiffType getDiffType() {
            return diffType;
        }

        @Override
        public String toString() {
            switch (diffType) {
                case ADDED:
                    return "[新增] " + relativePath;
                case DELETED:
                    return "[删除] " + relativePath;
                case MODIFIED:
                    return "[修改] " + relativePath;
                case SAME:
                    return "[相同] " + relativePath;
                default:
                    return relativePath;
            }
        }
    }

    private String dir1Path;
    private String dir2Path;
    private List<FileDiff> differences;
    private boolean hideSameFiles; // 是否隐藏相同文件

    public DirectoryComparator(String dir1Path, String dir2Path, boolean hideSameFiles) {
        this.dir1Path = dir1Path;
        this.dir2Path = dir2Path;
        this.differences = new ArrayList<>();
        this.hideSameFiles = hideSameFiles;
    }

    /**
     * 比较两个目录
     * @return 差异列表
     */
    public List<FileDiff> compare() {
        differences.clear();
        compareDirectories(new File(dir1Path), new File(dir2Path), "");
        return differences;
    }

    /**
     * 递归比较目录
     */
    private void compareDirectories(File dir1, File dir2, String relativePath) {
        // 获取两个目录中的文件和子目录
        File[] files1 = dir1.listFiles();
        File[] files2 = dir2.listFiles();

        if (files1 == null && files2 == null) {
            return;
        }

        // 处理第一个目录中的文件
        if (files1 != null) {
            for (File file1 : files1) {
                String fileName = file1.getName();
                String currentRelativePath = relativePath.isEmpty() ? fileName : relativePath + "/" + fileName;

                File correspondingFile = findFile(files2, fileName);

                if (correspondingFile == null) {
                    // 文件在目录1中存在，但在目录2中不存在 - 删除
                    if (file1.isDirectory()) {
                        addDeletedDirectory(file1, currentRelativePath);
                    } else {
                        differences.add(new FileDiff(currentRelativePath, file1.getAbsolutePath(), FileDiffType.DELETED));
                    }
                } else {
                    // 文件在两个目录中都存在
                    if (file1.isDirectory() && correspondingFile.isDirectory()) {
                        // 都是目录，递归比较
                        compareDirectories(file1, correspondingFile, currentRelativePath);
                    } else if (file1.isFile() && correspondingFile.isFile()) {
                        // 都是文件，比较内容
                        compareFiles(file1, correspondingFile, currentRelativePath);
                    } else {
                        // 类型不同，视为删除和新增
                        if (file1.isDirectory()) {
                            addDeletedDirectory(file1, currentRelativePath);
                            addAddedDirectory(correspondingFile, currentRelativePath);
                        } else {
                            differences.add(new FileDiff(currentRelativePath, file1.getAbsolutePath(), FileDiffType.DELETED));
                            differences.add(new FileDiff(currentRelativePath, correspondingFile.getAbsolutePath(), FileDiffType.ADDED));
                        }
                    }
                }
            }
        }

        // 处理第二个目录中新增的文件
        if (files2 != null) {
            for (File file2 : files2) {
                String fileName = file2.getName();
                String currentRelativePath = relativePath.isEmpty() ? fileName : relativePath + "/" + fileName;

                File correspondingFile = findFile(files1, fileName);

                if (correspondingFile == null) {
                    // 文件在目录2中存在，但在目录1中不存在 - 新增
                    if (file2.isDirectory()) {
                        addAddedDirectory(file2, currentRelativePath);
                    } else {
                        differences.add(new FileDiff(currentRelativePath, file2.getAbsolutePath(), FileDiffType.ADDED));
                    }
                }
            }
        }
    }

    /**
     * 查找同名文件
     */
    private File findFile(File[] files, String fileName) {
        if (files == null) return null;

        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    /**
     * 添加删除的目录（递归）
     */
    private void addDeletedDirectory(File dir, String relativePath) {
        differences.add(new FileDiff(relativePath, dir.getAbsolutePath(), FileDiffType.DELETED));

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String currentRelativePath = relativePath + "/" + fileName;

                if (file.isDirectory()) {
                    addDeletedDirectory(file, currentRelativePath);
                } else {
                    differences.add(new FileDiff(currentRelativePath, file.getAbsolutePath(), FileDiffType.DELETED));
                }
            }
        }
    }

    /**
     * 添加新增的目录（递归）
     */
    private void addAddedDirectory(File dir, String relativePath) {
        differences.add(new FileDiff(relativePath, dir.getAbsolutePath(), FileDiffType.ADDED));

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String currentRelativePath = relativePath + "/" + fileName;

                if (file.isDirectory()) {
                    addAddedDirectory(file, currentRelativePath);
                } else {
                    differences.add(new FileDiff(currentRelativePath, file.getAbsolutePath(), FileDiffType.ADDED));
                }
            }
        }
    }

    /**
     * 比较两个文件
     */
    private void compareFiles(File file1, File file2, String relativePath) {
        try {
            // 比较文件大小
            if (file1.length() != file2.length()) {
                differences.add(new FileDiff(relativePath, file1.getAbsolutePath(), FileDiffType.MODIFIED));
                return;
            }

            // 比较文件内容的MD5值
            String md5_1 = calculateMD5(file1);
            String md5_2 = calculateMD5(file2);

            if (!md5_1.equals(md5_2)) {
                differences.add(new FileDiff(relativePath, file1.getAbsolutePath(), FileDiffType.MODIFIED));
            } else {
                differences.add(new FileDiff(relativePath, file1.getAbsolutePath(), FileDiffType.SAME));
            }
        } catch (Exception e) {
            System.err.println("比较文件时出错: " + relativePath + " - " + e.getMessage());
            differences.add(new FileDiff(relativePath, file1.getAbsolutePath(), FileDiffType.MODIFIED));
        }
    }

    /**
     * 计算文件的MD5值
     */
    private String calculateMD5(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] digest = md.digest(fileBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 打印差异结果
     */
    public void printDifferences() {
        System.out.println("目录比较结果 (版本: " + VERSION + "):");
        System.out.println("目录1: " + dir1Path);
        System.out.println("目录2: " + dir2Path);
        System.out.println("=" + "=".repeat(60));

        int addedCount = 0, deletedCount = 0, modifiedCount = 0, sameCount = 0;
        int displayedCount = 0;

        for (FileDiff diff : differences) {
            // 如果启用了隐藏相同文件功能，跳过相同文件的显示
            if (hideSameFiles && diff.getDiffType() == FileDiffType.SAME) {
                sameCount++;
                continue;
            }

            System.out.println(diff);
            displayedCount++;

            switch (diff.getDiffType()) {
                case ADDED: addedCount++; break;
                case DELETED: deletedCount++; break;
                case MODIFIED: modifiedCount++; break;
                case SAME: sameCount++; break;
            }
        }

        System.out.println("=" + "=".repeat(60));
        System.out.println("统计结果:");
        System.out.println("新增文件: " + addedCount);
        System.out.println("删除文件: " + deletedCount);
        System.out.println("修改文件: " + modifiedCount);
        System.out.println("相同文件: " + sameCount);
        System.out.println("总文件数: " + differences.size());

        if (hideSameFiles) {
            System.out.println("显示文件数: " + displayedCount);
        }
    }

    /**
     * 获取特定类型的差异
     */
    public List<FileDiff> getDifferencesByType(FileDiffType type) {
        List<FileDiff> result = new ArrayList<>();
        for (FileDiff diff : differences) {
            if (diff.getDiffType() == type) {
                result.add(diff);
            }
        }
        return result;
    }

    /**
     * 显示版本号
     */
    public static void showVersion() {
        System.out.println("DirectoryComparator 版本: " + VERSION);
    }

    /**
     * 显示帮助信息
     */
    public static void showHelp() {
        System.out.println("DirectoryComparator 使用帮助:");
        System.out.println("用法: java DirectoryComparator [选项] <目录1> <目录2>");
        System.out.println();
        System.out.println("选项:");
        System.out.println("  -v, --version    显示版本号");
        System.out.println("  -h, --help       显示此帮助信息");
        System.out.println("  -s, --hide-same  隐藏相同文件，只显示有变动的文件");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  java DirectoryComparator /path/to/dir1 /path/to/dir2");
        System.out.println("  java DirectoryComparator -s /path/to/dir1 /path/to/dir2");
        System.out.println("  java DirectoryComparator --version");
    }

    // 主方法
    public static void main(String[] args) {
        boolean hideSameFiles = false;
        String dir1 = null;
        String dir2 = null;

        // 解析命令行参数
        List<String> directories = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-v":
                case "--version":
                    showVersion();
                    return;

                case "-h":
                case "--help":
                    showHelp();
                    return;

                case "-s":
                case "--hide-same":
                    hideSameFiles = true;
                    break;

                default:
                    if (!arg.startsWith("-")) {
                        directories.add(arg);
                    } else {
                        System.err.println("未知参数: " + arg);
                        showHelp();
                        return;
                    }
                    break;
            }
        }

        // 检查是否只有版本参数
        if ((args.length == 1 && (args[0].equals("-v") || args[0].equals("--version")))) {
            showVersion();
            return;
        }

        // 检查目录参数
        if (directories.size() != 2) {
            System.err.println("错误: 必须提供两个目录参数");
            showHelp();
            return;
        }

        dir1 = directories.get(0);
        dir2 = directories.get(1);

        // 检查目录是否存在
        File directory1 = new File(dir1);
        File directory2 = new File(dir2);

        if (!directory1.exists() || !directory1.isDirectory()) {
            System.err.println("错误: 目录1不存在或不是目录: " + dir1);
            return;
        }

        if (!directory2.exists() || !directory2.isDirectory()) {
            System.err.println("错误: 目录2不存在或不是目录: " + dir2);
            return;
        }

        // 执行比较
        DirectoryComparator comparator = new DirectoryComparator(dir1, dir2, hideSameFiles);
        comparator.compare();
        comparator.printDifferences();
    }
}
