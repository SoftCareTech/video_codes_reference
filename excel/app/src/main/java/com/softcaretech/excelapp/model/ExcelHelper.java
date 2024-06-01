package com.softcaretech.excelapp.model;

import static com.softcaretech.excelapp.MainActivity.TAG;

import android.util.Log;

import com.softcaretech.excelapp.MainActivity;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Helper class to handle Excel operations for Product objects.
 */
public class ExcelHelper {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final String filePath;
    private OPCPackage pkg;

    /**
     * Constructor to open or create the workbook and sheet.
     *
     * @param filePath Path of the Excel file to create or update.
     * @throws IOException If an I/O error occurs.
     */
    public ExcelHelper(String filePath) throws IOException {
        this.filePath = filePath+".xlsx";
        Log.d(TAG, filePath);

    }
    public boolean open()   {
        File file = new File(filePath);
        if ( file.exists()   ) {
            // Open existing workbook
            try {
                pkg = OPCPackage.open(file);
               workbook = new XSSFWorkbook(pkg );
                sheet = workbook.getSheet("Products");
                if (sheet == null) {
                    sheet = workbook.createSheet("Products");
                    createHeaderRow(sheet);
                }
                return true;
            }  catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Create new workbook and sheet
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Products");
            createHeaderRow(sheet);
            return true;
        }
return   false;
    }
    public boolean openNew()   {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Products");
            createHeaderRow(sheet);
            return true;

    }
    /**
     * Adds a single product to the Excel sheet.
     *
     * @param product The product to add to the sheet.
     * @return true if the product was added successfully, false otherwise.
     */
    public boolean addProduct(Product product) {
        try {
            int rowNum = sheet.getLastRowNum();
            if (rowNum == 0 && sheet.getRow(0) == null) {
                // No data in sheet yet
            } else {
                rowNum++; // Start from the next empty row
            }

            Row row = sheet.createRow(rowNum);
            createProductRow(product, row);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Adds multiple products to the Excel sheet.
     *
     * @param products The list of products to add to the sheet.
     * @return true if all products were added successfully, false otherwise.
     */
    public boolean addProducts(List<Product> products) {
        boolean success = true;
        for (Product product : products) {
            if (!addProduct(product)) {
                success = false;
            }
        }
        return success;
    }

    /**
     * Saves the workbook to the file.
     *
     * @return true if the workbook was saved successfully, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
    public boolean save()   {
            // Write the workbook to a file
            Log.d(TAG,filePath);
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.setWorkbookType(XSSFWorkbookType.XLSX);
                workbook.write(fileOut);
                workbook.close();
               if(pkg!=null) {
                    pkg.close();
                }
                return true;
            }catch (IOException e) {
                e.printStackTrace();
                return false;
            }
    }

    /**
     * Retrieves the list of products from the Excel sheet.
     *
     * @return List of products.
     */
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        try {
            int rowNum = sheet.getLastRowNum();
            for (int i = 1; i <= rowNum; i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row != null) {
                    Product product = createProductFromRow(row);
                    products.add(product);
                }
            }
        }catch (Exception e){
         //   e.printStackTrace();
        }
        return products;
    }

    /**
     * Creates the header row for the sheet.
     *
     * @param sheet The sheet to add the header row to.
     */
    private static void createHeaderRow(XSSFSheet sheet) {
        Row headerRow = sheet.createRow(0);

        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Name");

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Count");

        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("Price");

        Cell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("Transaction Type");

        Cell headerCell5 = headerRow.createCell(4);
        headerCell5.setCellValue("Description");
    }

    /**
     * Creates a row in the sheet with the product data.
     *
     * @param product The product to write to the row.
     * @param row     The row to populate with product data.
     */
    private static void createProductRow(Product product, Row row) {
        Cell cell1 = row.createCell(0);
        cell1.setCellValue(product.getName());

        Cell cell2 = row.createCell(1);
        cell2.setCellValue(product.getCount());

        Cell cell3 = row.createCell(2);
        cell3.setCellValue(product.getPrice());

        Cell cell4 = row.createCell(3);
        cell4.setCellValue(product.getTransactionType().getDisplayName());

        Cell cell5 = row.createCell(4);
        cell5.setCellValue(product.getDescription());
    }

    /**
     * Creates a Product object from a row in the sheet.
     *
     * @param row The row to read data from.
     * @return A Product object.
     */
    private static Product createProductFromRow(Row row) {
        String name = row.getCell(0).getStringCellValue();
        int count = (int) row.getCell(1).getNumericCellValue();
        double price = row.getCell(2).getNumericCellValue();
        String transactionTypeStr = row.getCell(3).getStringCellValue();
        TransactionType transactionType = TransactionType.valueOf(transactionTypeStr.toUpperCase());
        String description = row.getCell(4).getStringCellValue();

        return new Product(name, count, price, transactionType, description);
    }
}


