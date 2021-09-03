package com.project.Services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadService {

    @Autowired private Definitions definitions;
    public FileResponse uploadXlsx(MultipartFile file) throws Exception {
        FileResponse fileResponse = null;
        if(file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            String newFileName = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), "xlsx");
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
        }else{
            fileResponse = new FileResponse(true, "Invalid format.");
        }
        return fileResponse;
    }

    public BasicResponseModel uploadAndConvertXlsx2JSON(MultipartFile file) throws Exception {
        FileResponse uploadXlsx = uploadXlsx(file);
        BasicResponseModel responseModel;
        if(uploadXlsx.getFailed() == null){
            JsonArray jsonArray = new JsonArray();
            Workbook workbook = null;
            try {
                workbook = new XSSFWorkbook(OPCPackage.open(new File(uploadXlsx.getFileLocation())));
            } catch (InvalidFormatException | IOException e) {
                responseModel = new BasicResponseModel(definitions.UPLOAD_FAILED, definitions.UPLOAD_FAILED_MSG);
            }
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                ArrayList<String> columnNames = new ArrayList<String>();
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> sheetIterator = sheet.iterator();
                while (sheetIterator.hasNext()) {
                    Row currentRow = sheetIterator.next();
                    JsonObject jsonObject = new JsonObject();
                    if (currentRow.getRowNum() != 0) {
                        for (int j = 0; j < columnNames.size(); j++) {
                            if (currentRow.getCell(j) != null) {
                                if (currentRow.getCell(j).getCellType() == XSSFCell.CELL_TYPE_STRING) {
                                    jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getStringCellValue());
                                } else if (currentRow.getCell(j).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                                    jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getNumericCellValue());
                                } else if (currentRow.getCell(j).getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
                                    jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getBooleanCellValue());
                                } else if (currentRow.getCell(j).getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                                    jsonObject.addProperty(columnNames.get(j), "");
                                }
                            } else {
                                jsonObject.addProperty(columnNames.get(j), "");
                            }
                        }
                        jsonArray.add(jsonObject);
                    } else {
                        // store column names
                        for (int k = 0; k < currentRow.getPhysicalNumberOfCells(); k++) {
                            columnNames.add(currentRow.getCell(k).getStringCellValue());
                        }
                    }
                }
            }
            responseModel = new BasicResponseModel(uploadXlsx.getFileLocation(), jsonArray);
        }else{
            responseModel = new BasicResponseModel(definitions.UPLOAD_FAILED, definitions.UPLOAD_FAILED_MSG);
        }
        return responseModel;
    }
}