package sort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SortNumbersInAFileTest {

	static SortNumbersInAFile file;

	@BeforeAll
	static void setup() {
		file = new SortNumbersInAFile();
	}

	@Test
	void fileNotFoundTest() {
		FileNotFoundException exception = null;
		try {
			file.splitLargeFile("abcd.txt", "Testcases/");
		} catch (FileNotFoundException e) {
			exception = e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(exception);
	}

	@Test
	void fileFoundTest() {
		Exception exception = null;
		int noOfFiles = 0;
		String path = "Testdata/Split/";
		try {
			noOfFiles = file.splitLargeFile(path + "test.txt", path);
		} catch (Exception e) {
			exception = e;
		}

		assertNull(exception);

		deleteTempFiles(noOfFiles, path);
	}

	/*
	 * @Test void OutOfMemoryTest() { Error er = null; try {
	 * file.splitLargeFile("Testdata/test.txt", "Testdata/"); } catch (Exception e)
	 * {
	 * 
	 * } catch (OutOfMemoryError e) { er = e; }
	 * 
	 * assertNotNull(er); }
	 */

	@Test
	void successfulSpiltTest() {
		Exception exception = null;
		int noOfFiles = 0;
		String path = "Testdata/Split/";
		try {
			noOfFiles = file.splitLargeFile(path + "test.txt", path);
		} catch (Exception e) {
			exception = e;
		}

		String[] fileList = new File(path).list();
		int expectedNo = 0;
		for (String file : fileList) {
			if (file.matches("temp[1-9][0-9]*.txt")) {
				expectedNo++;
			}
		}

		assertEquals(expectedNo, noOfFiles);
		assertNull(exception);

		deleteTempFiles(noOfFiles, path);
	}

	@Test
	void failSpiltTest() {
		Exception exception = null;
		try {
			file.splitLargeFile("Testdata/Split/testFail.txt", "Testdata/Split/");
		} catch (NumberFormatException e) {
			exception = e;
		} catch (Exception e) {

		}

		assertNotNull(exception);
	}

	@Test
	void successMergeFilesTest() {

		try {
			file.mergeSortedFiles(2, "Testdata/Merge/Sorted.txt", "Testdata/Merge/");
		} catch (IOException e) {

		}

		String[] fileList = new File("Testdata/Merge/").list();
		boolean found = false;
		for (String file : fileList) {
			if ("Sorted.txt".equals(file)) {
				if (new File("Testdata/Merge/" + file).length() != new File("Testdata/Merge/test.txt").length()) {
					fail("file not successfully created");
				}
				assertTrue(true);
				found = true;

			}
		}

		if (!found)
			fail("File not created");
	}

	@Test
	void failMergeFilesTest() {
		IOException exception = null;
		try {
			file.mergeSortedFiles(2, "Testdata/Merge/Sorted.txt", "");
		} catch (NoSuchFileException e) {
			exception = e;
		} catch (FileNotFoundException e) {
			exception = e;
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertNotNull(exception);
	}

	@Test
	void getChunkSuccessTest() {
		FileNotFoundException exception = null;
		try {
			assertEquals(100, file.getChunk("Testdata/Merge/test.txt", 0, 100).size());
		} catch (FileNotFoundException e) {
			exception = e;
		}

		assertNull(exception);

	}

	@Test
	void getChunkFailTest() {
		FileNotFoundException exception = null;
		try {
			file.getChunk("Testdata/Merge/junk.txt", 0, 100).size();
		} catch (FileNotFoundException e) {
			exception = e;
		}

		assertNotNull(exception);

	}

	void deleteTempFiles(int noOfFiles, String path) {
		for (int i = 1; i <= noOfFiles; i++) {
			String fileName = path + "temp" + i + ".txt";
			try {
				Files.delete(FileSystems.getDefault().getPath(fileName));
			} catch (IOException e) {
				System.out.println("Error deleting temp files");
			}
		}
	}

}
