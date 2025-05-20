package com.srirama.tms.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExcelWriterService {
	
	private static final Path STORAGE_DIR = Paths.get("data-logs");

	private final List<List<String>> buffer;

	@Value("${export.maxBufferSize:4096}")
	private int maxBufferSize;
	
	public ExcelWriterService() {
		this.buffer = new ArrayList<>();
	}

	public synchronized void addRow(List<String> rowData) {
		buffer.add(rowData);
		if (buffer.size() >= maxBufferSize) {
			flushToExcel();
		}
	}

	public synchronized void flushToExcel() {
		if (buffer.isEmpty())
			return;

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Data");

		for (int i = 0; i < buffer.size(); i++) {
			Row row = sheet.createRow(i);
			List<String> dataRow = buffer.get(i);
			for (int j = 0; j < dataRow.size(); j++) {
				row.createCell(j).setCellValue(dataRow.get(j));
			}
		}

		String fileName = "logger_data_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
				+ ".xlsx";

		try (FileOutputStream fileOut = new FileOutputStream(STORAGE_DIR.resolve(fileName).toFile())) {
			workbook.write(fileOut);
			log.info("Data written to Excel: {}", fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			buffer.clear();
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
