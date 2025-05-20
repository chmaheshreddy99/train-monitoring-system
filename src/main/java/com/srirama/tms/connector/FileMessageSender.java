package com.srirama.tms.connector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

/**
 * A utility class to write messages to a specified file. The file will be
 * cleared and overwritten with each new message.
 */
@Component
public class FileMessageSender {

	private static final Path STORAGE_DIR = Paths.get("pcb-data");

	private final String fileName;

	/**
	 * Constructs the FileWriterUtil with a specified file path.
	 *
	 * @param filePath the full path of the file to write to
	 */
	public FileMessageSender() {
		this.fileName = "descriptor.txt";
	}

	/**
	 * Writes the given message to the file, overwriting any existing content. If
	 * the file or its parent directories do not exist, they will be created.
	 *
	 * @param message the message to write to the file
	 * @throws IOException if an error occurs during writing
	 */
	public void writeMessage(String message) throws IOException {
		File file = STORAGE_DIR.resolve(fileName).toFile();

		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			if (!parent.mkdirs()) {
				throw new IOException("Failed to create directories: " + parent.getAbsolutePath());
			}
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
			writer.write(message);
		}
	}
}
