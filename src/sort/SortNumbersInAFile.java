package sort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class SortNumbersInAFile {

	public static final long MAX_FILE_SIZE = 9000000;
	public static final int MAX_CHUNK_LENGTH = 9000000;

	/* Splits a large file into several temporary sorted small files */
	public int splitLargeFile(String fileName, String path) throws IOException {

		File file = new File(fileName);
		Scanner scan = new Scanner(file);

		ArrayList<Integer> numberList = new ArrayList<>();
		int fileIndex = 0;
		long sizeInBytes = 0;

		while (scan.hasNextLine()) {
			int temp = Integer.parseInt(scan.nextLine());
			sizeInBytes = Integer.BYTES + numberList.size() * Integer.BYTES;

			if (sizeInBytes < MAX_FILE_SIZE) {
				numberList.add(temp);
			} else {

				/* sort the entries and write into a temporary file */
				Collections.sort(numberList);

				String newFileName = path + "temp" + ++fileIndex + ".txt";
				StringBuilder sb = new StringBuilder();
				for (Integer value : numberList) {
					sb.append(value).append(System.lineSeparator());
				}
				fileWrite(newFileName, sb);

				numberList.clear();
				numberList.trimToSize();
				numberList.add(temp);
			}
		}

		/* Sort and write the last chunk of the large file into a temporary file */
		if (numberList.size() != 0) {

			Collections.sort(numberList);

			String newFileName = path + "temp" + ++fileIndex + ".txt";
			StringBuilder sb = new StringBuilder();
			for (Integer value : numberList) {
				sb.append(value).append(System.lineSeparator());
			}
			fileWrite(newFileName, sb);
		}

		scan.close();
		return fileIndex;
	}

	/* Sorts and merges all the temporary files entries into an output file */
	public void mergeSortedFiles(int noOfFiles, String outputFile, String path) throws IOException {

		ArrayList<ArrayList<Integer>> fileChunkList = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> chunkPositionList = new ArrayList<Integer>();
		ArrayList<Integer> filePositionList = new ArrayList<Integer>();

		int limit = MAX_CHUNK_LENGTH / noOfFiles;

		for (int i = 0; i < noOfFiles; i++) {
			String fileName = path + "temp" + (i + 1) + ".txt";
			chunkPositionList.add(0);
			fileChunkList.add(getChunk(fileName, chunkPositionList.get(i), limit));
			filePositionList.add(limit);
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

			while (true) {
				int fileIndex = 0;
				int minFileIndex = 0;
				Integer min = null;
				for (ArrayList<Integer> chunk : fileChunkList) {

					if (chunk != null) {

						/* Replace with new chunk from respective file, once all the existing entries are processed */
						if (chunk.size() <= chunkPositionList.get(fileIndex)) { 
							String fileName = path + "temp" + (fileIndex + 1) + ".txt";
							fileChunkList.set(fileIndex, getChunk(fileName, filePositionList.get(fileIndex), limit));
							filePositionList.set(fileIndex, filePositionList.get(fileIndex) + limit);
							chunkPositionList.set(fileIndex, 0);
							chunk = fileChunkList.get(fileIndex);
						}
						
						/* If EOF is not reached, find minimum */
						if (chunk != null) {
							int value = chunk.get(chunkPositionList.get(fileIndex));
							if (min == null || value < min) {
								min = value;
								minFileIndex = fileIndex;
							}
						}
					} 
					fileIndex++;
				}

				/* When all the files are completely processed, the minimum value is null */
				if (min == null) {
					break;
				}

				/* Write the minimum value into the BufferedWriter */
				chunkPositionList.set(minFileIndex, chunkPositionList.get(minFileIndex) + 1);
				bw.append(Integer.toString(min)).append(System.lineSeparator());
				
				System.out.println(min);
			}
			bw.flush();
		}

	}

	/* Returns a small chunk of file with certain number of lines, from a specified location */
	ArrayList<Integer> getChunk(String fileName, int pos, int limit) throws FileNotFoundException {

		ArrayList<Integer> returnList = new ArrayList<Integer>();

		int lineNum = 0;
		File file = new File(fileName);
		Scanner scan = new Scanner(file);

		while (scan.hasNextLine() && returnList.size() < limit) {
			if (lineNum >= pos) {
				returnList.add(Integer.parseInt(scan.nextLine()));
			} else {
				lineNum++;
				scan.nextLine();
			}
		}
		scan.close();

		if (returnList.isEmpty()) {
			return null;
		}
		return returnList;
	}

	/* Writes files into a files from StringBuilder */
	private void fileWrite(String fileName, StringBuilder sb) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
			final int len = sb.length();
			final int chunk = 1024;
			final char[] chars = new char[chunk];

			for (int start = 0; start < len; start += chunk) {
				final int end = Math.min(start + chunk, len);
				sb.getChars(start, end, chars, 0);
				bw.write(chars, 0, end - start);
			}
			bw.flush();
		}
	}

	public static void main(String[] args) throws IOException {

		SortNumbersInAFile largeFile = new SortNumbersInAFile();

		String path = "Run/";
		String outputFileName = path + "SortedFile.txt";
		String inputFileName = path + "test.txt";
		

		int noOfFiles = largeFile.splitLargeFile(inputFileName, path);
		largeFile.mergeSortedFiles(noOfFiles, outputFileName, path);

		/* Delete the temporary files */
		for (int i = 1; i <= noOfFiles; i++) {
			String fileName = path + "temp" + i + ".txt";
			Files.delete(FileSystems.getDefault().getPath(fileName));
		}
	}

}
