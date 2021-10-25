package com.project.Services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.project.Objects.Entities.BasicResponseModel;
import com.project.Objects.Entities.FileResponse;
import com.project.Utils.Definitions;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadService {

    @Autowired
    private Definitions definitions;

    public FileResponse uploadXlsx(MultipartFile file) throws Exception {
        FileResponse fileResponse = null;
        if (file.getContentType().equals(definitions.EXCEL_CONTENT_TYPE)) {
            String newFileName = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), definitions.EXCEL_EXTENSION);
            InputStream in = file.getInputStream();
            File currDir = new File(".");
            String path = currDir.getAbsolutePath();
            String fileLocation = path.substring(0, path.length() - 1) + "files\\" + newFileName;
            FileOutputStream f = new FileOutputStream(fileLocation);
            int ch = 0;
            while ((ch = in.read()) != -1) {
                f.write(ch);
            }
            f.flush();
            f.close();
            fileResponse = new FileResponse(fileLocation);
        } else {
            fileResponse = new FileResponse(true, definitions.UPLOAD_FAILED_INVALID_FORMAT_MSG);
        }
        return fileResponse;
    }

    public BasicResponseModel uploadAndConvertXlsx2JSON(MultipartFile file) throws Exception {
        FileResponse uploadXlsx = uploadXlsx(file);
        BasicResponseModel responseModel = null;
        if (uploadXlsx.getFailed() == null) {
            JsonArray jsonArray = new JsonArray();
            Workbook workbook = null;
            boolean flag = false;
            try {
                Path xlsxPath = Paths.get(uploadXlsx.getFileLocation());
                InputStream xlsxPathInputStream = Files.newInputStream(xlsxPath);
                workbook = new XSSFWorkbook(OPCPackage.open(xlsxPathInputStream));
            } catch (InvalidFormatException | IOException e) {
                return new BasicResponseModel(definitions.UPLOAD_FAILED, definitions.UPLOAD_FAILED_MSG);
            }
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                ArrayList<String> columnNamesForCheck = new ArrayList<String>() {{
                    add("uid");
                    add("candidateName");
                    add("email");
                    add("phoneNumber");
                    add("candidateStatus");
                    add("registerationState");
                    add("comment");
                }};
                ArrayList<String> columnNames = new ArrayList<String>();
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> sheetIterator = sheet.iterator();
                while (sheetIterator.hasNext() && !flag) {
                    Row currentRow = sheetIterator.next();
                    JsonObject jsonObject = new JsonObject();
                    if (currentRow.getRowNum() != 0) { //fetch all the data
                        for (int j = 0; j < columnNames.size(); j++) {
                            if (currentRow.getCell(j) != null) {
                                currentRow.getCell(j).setCellType(Cell.CELL_TYPE_STRING); // cast all fields insto String.
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getStringCellValue().replace(",","&comma;"));
                            }
                        }
                        jsonArray.add(jsonObject);
                    } else { // fetch the columns name.
                        int index;
                        for (int k = 0; k < currentRow.getPhysicalNumberOfCells(); k++) {
                            columnNames.add(currentRow.getCell(k).getStringCellValue());
                            index = columnNamesForCheck.indexOf(currentRow.getCell(k).getStringCellValue());
                            if (index > -1) {
                                columnNamesForCheck.remove(index);
                            }
                        }
                        if (columnNamesForCheck.size() != 0) {
                            flag = true;
                        }
                    }
                }
            }
            if (flag) {
                responseModel = new BasicResponseModel(definitions.UPLOAD_FAILED_INVALID_XLSX_FILE, definitions.UPLOAD_FAILED_INVALID_XLSX_FILE_MSG);
            } else {
                responseModel = new BasicResponseModel(uploadXlsx.getFileLocation(), jsonArray);
            }
            File fileToDelete = new File(uploadXlsx.getFileLocation());
            fileToDelete.delete();

        } else {
            responseModel = new BasicResponseModel(definitions.UPLOAD_FAILED_INVALID_FORMAT, uploadXlsx.getErrorMessage());
        }
        return responseModel;
    }
}