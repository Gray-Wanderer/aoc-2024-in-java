package andrei.mishunin.aoc2024.day09;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.ArrayList;
import java.util.List;

public class DiskFragmenter {
    public static long hardFragmentation(String file) {
        List<String> input = InputReader.readAllLines(file);
        char[] diskCoded = input.get(0).toCharArray();
        int l = 0;
        int r = 0;
        int rFileId = -1;
        for (int i = 0; i < diskCoded.length; i += 2) {
            r = i;
            rFileId++;
        }

        long hash = 0;
        int lFileId = 0;
        int blockNum = 0;
        while (lFileId < rFileId) {
            int fileBlocks = diskCoded[l] - '0';
            for (int i = 0; i < fileBlocks; i++) {
                hash += (long) blockNum * lFileId;
                blockNum++;
            }

            l++;
            lFileId++;

            int freeBlocks = diskCoded[l] - '0';
            while (freeBlocks > 0 && lFileId < rFileId) {
                int rightFileBlocks = diskCoded[r] - '0';
                if (rightFileBlocks == 0) {
                    rFileId--;
                    r -= 2;
                } else {
                    hash += (long) blockNum * rFileId;

                    freeBlocks--;
                    rightFileBlocks--;
                    blockNum++;

                    diskCoded[r] = (char) (rightFileBlocks + '0');
                }
            }
            l++;
        }

        int fileBlocks = diskCoded[l] - '0';
        for (int i = 0; i < fileBlocks; i++) {
            hash += (long) blockNum * lFileId;
            blockNum++;
        }

        return hash;
    }

    public static long softFragmentation(String file) {
        List<String> input = InputReader.readAllLines(file);
        char[] diskCoded = input.get(0).toCharArray();

        List<DiskBlock> files = new ArrayList<>();
        List<DiskBlock> freeSpaces = new ArrayList<>();

        int blockNum = 0;
        int fileId = 0;
        for (int i = 0; i < diskCoded.length; i++) {
            DiskBlock fileObj = new DiskBlock(blockNum, diskCoded[i] - '0', fileId++);
            files.add(fileObj);
            blockNum += fileObj.blocksCount;

            i++;
            if (i == diskCoded.length) {
                break;
            }

            DiskBlock freeSpaceObj = new DiskBlock(blockNum, diskCoded[i] - '0');
            if (freeSpaceObj.blocksCount > 0) {
                freeSpaces.add(freeSpaceObj);
                blockNum += freeSpaceObj.blocksCount;
            }
        }

        for (int i = files.size() - 1; i >= 0; i--) {
            DiskBlock fileObj = files.get(i);

            for (DiskBlock freeSpace : freeSpaces) {
                if (freeSpace.position >= fileObj.position) {
                    break;
                }
                if (freeSpace.blocksCount < fileObj.blocksCount) {
                    continue;
                }

                fileObj.position = freeSpace.position;
                freeSpace.position += fileObj.blocksCount;
                freeSpace.blocksCount -= fileObj.blocksCount;
                break;
            }
        }

        long hash = 0;
        for (DiskBlock fileObj : files) {
            hash += (long) fileObj.fileId * fileObj.blocksCount * (fileObj.position * 2L + fileObj.blocksCount - 1) / 2;
        }

        return hash;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(hardFragmentation("day09/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(hardFragmentation("day09/input.txt"));
        System.out.println("== TEST 2 ==");
        System.out.println(softFragmentation("day09/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(softFragmentation("day09/input.txt"));
    }

    private static class DiskBlock {
        private int position;
        private int blocksCount;
        private final int fileId;

        private DiskBlock(int position, int blocksCount) {
            this(position, blocksCount, -1);
        }

        private DiskBlock(int position, int blocksCount, int fileId) {
            this.position = position;
            this.blocksCount = blocksCount;
            this.fileId = fileId;
        }
    }
}
