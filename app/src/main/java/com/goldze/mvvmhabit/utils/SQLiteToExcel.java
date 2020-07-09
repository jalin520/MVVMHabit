package com.goldze.mvvmhabit.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class SQLiteToExcel {

    private static Handler handler = new Handler(Looper.getMainLooper());

    private final static String UTF8_ENCODING = "UTF-8";
    private WritableFont arial14font = null;
    private WritableCellFormat arial14format = null;
    private WritableFont arial10font = null;
    private WritableCellFormat arial10format = null;
    private WritableFont arial12font = null;
    private WritableCellFormat arial12format = null;
    private String protectKey;
    private String encryptKey;
    private String fileName;
    private String filePath;
    private List<String> tables;
    private String sql;
    private String sheetName;

    private SQLiteDatabase database;

    public static class Builder {
        private String dataBaseName;
        private String filePath;
        private String fileName;
        private String protectKey;
        private String encryptKey;
        private List<String> tables;
        private String sql;
        private String sheetName;

        public Builder(Context context) {
            this.filePath = context.getExternalFilesDir(null).getPath();
        }

        public SQLiteToExcel build() {
            if (TextUtils.isEmpty(dataBaseName)) {
                throw new IllegalArgumentException("Database name must not be null.");
            }
            if (TextUtils.isEmpty(fileName)) {
                throw new IllegalArgumentException("Output file name must not be null.");
            }
            return new SQLiteToExcel(tables, protectKey, encryptKey, fileName, dataBaseName, filePath, sql, sheetName);
        }

        public Builder setDataBase(String dataBaseName) {
            this.dataBaseName = dataBaseName;
            this.fileName = new File(dataBaseName).getName() + ".xls";
            return this;
        }

        /**
         * @param fileName
         * @return Builder
         * @deprecated Use {@link #setOutputFileName(String fileName)} instead.
         */
        @Deprecated
        public Builder setFileName(String fileName) {
            return setOutputFileName(fileName);
        }

        public Builder setOutputFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setProtectKey(String protectPassword) {
            this.protectKey = protectPassword;
            return this;
        }

        public Builder setEncryptKey(String encryptKey) {
            this.encryptKey = encryptKey;
            return this;
        }

        public Builder setTables(String... tables) {
            this.tables = Arrays.asList(tables);
            return this;
        }


        /**
         * @param path
         * @return Builder
         * @deprecated Use {@link #setOutputPath(String path)} instead.
         */
        @Deprecated
        public Builder setPath(String path) {
            return setOutputPath(path);
        }

        public Builder setOutputPath(String path) {
            this.filePath = path;
            return this;
        }

        public Builder setSQL(@NonNull String sheetName, @NonNull String sql) {
            this.sql = sql;
            this.sheetName = sheetName;
            return this;
        }

        public Builder setSQL(@NonNull String sql) {
            return setSQL("Sheet1", sql);
        }

        public String start() {
            final SQLiteToExcel sqliteToExcel = build();
            return sqliteToExcel.start();
        }

        public void start(ExportListener listener) {
            final SQLiteToExcel sqliteToExcel = build();
            sqliteToExcel.start(listener);
        }
    }

    /**
     * import Tables task
     *
     * @return output file path
     */
    public String start() {
        try {
            if (tables == null || tables.size() == 0) {
                tables = getTablesName(database);
            }
            return exportTables(getTablesName(database), fileName);
        } catch (Exception e) {
            if (database != null && database.isOpen()) {
                database.close();
            }
            return null;
        }
    }

    /**
     * importTables task with a listener
     *
     * @param listener callback
     */
    public void start(final ExportListener listener) {
        if (listener != null) {
            listener.onStart();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (tables == null || tables.size() == 0) {
                        tables = getTablesName(database);
                    }
                    final String filePath = exportTables(getTablesName(database), fileName);
                    if (listener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCompleted(filePath);
                            }
                        });
                    }
                } catch (final Exception e) {
                    if (database != null && database.isOpen()) {
                        database.close();
                    }
                    if (listener != null)
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(e);
                            }
                        });
                }
            }
        }).start();
    }

    private SQLiteToExcel(List<String> tables, String protectKey, String encryptKey, String fileName,
                          String dataBaseName, String filePath, String sql, String sheetName) {
        this.protectKey = protectKey;
        this.encryptKey = encryptKey;
        this.fileName = fileName;
        this.filePath = filePath;
        this.sql = sql;
        this.sheetName = sheetName;

        try {
            database = SQLiteDatabase.openOrCreateDatabase(dataBaseName, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            //对齐格式
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            //设置边框
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * core code, export tables to a excel file
     *
     * @param tables   database tables
     * @param fileName target file name
     * @return target file path
     * @throws Exception
     */
    private String exportTables(List<String> tables, final String fileName) throws Exception {
        if (!fileName.toLowerCase().endsWith(".xls")) {
            throw new IllegalArgumentException("File name is null or unsupported file format!");
        }
        format();

        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        WritableWorkbook writebook = null;
        InputStream in = null;
        try {
            WorkbookSettings setEncode = new WorkbookSettings();
            setEncode.setEncoding(UTF8_ENCODING);

            in = new FileInputStream(file);
            Workbook workbook = Workbook.getWorkbook(in);
            writebook = Workbook.createWorkbook(file, workbook);
            if (TextUtils.isEmpty(sql)) {
                for (int i = 0; i < tables.size(); i++) {
                    //设置表格的名字
                    WritableSheet sheet = writebook.createSheet(tables.get(i), i);
                    String sqlAll = "select * from " + tables.get(i);
                    fillSheet(sqlAll, sheet);
                }
            } else {
                WritableSheet sheet = writebook.createSheet(sheetName, 0);
                fillSheet(sql, sheet);
            }
            writebook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writebook != null) {
                try {
                    writebook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file.getAbsolutePath();
    }

    /**
     * Query the database ,then fill in to the sheet
     *
     * @param sql   query sql
     * @param sheet target sheet
     */
    private void fillSheet(String sql, WritableSheet sheet) throws WriteException {
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        // 列个数
        final int columnsCount = cursor.getColumnCount();
        //创建标题栏
        sheet.addCell((WritableCell) new Label(0, 0, filePath, arial14format));
        for (int i = 0; i < columnsCount; i++) {
            sheet.addCell(new Label(i, 0, "" + cursor.getColumnNames()[i], arial10format));
        }
        //设置行高
        sheet.setRowView(0, 340);
        int rn = 1;// 行号
        while (!cursor.isAfterLast()) {
            for (int cn = 0; cn < columnsCount; cn++) {
                String value = cursor.getString(cn);
                if (!TextUtils.isEmpty(value) && value.length() >= 32767) {
                    value = value.substring(0, 32766);
                }
                sheet.addCell(new Label(cn, rn + 1, value, arial12format));
            }
            //设置行高
            sheet.setRowView(rn + 1, 350);
            rn++;
            cursor.moveToNext();
        }
        cursor.close();
    }


    /**
     * get database all tables
     *
     * @return tables
     */
    private List<String> getTablesName(SQLiteDatabase database) {
        List<String> tables = new ArrayList<>();
        Cursor cursor = database.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0));
        }
        cursor.close();
        return tables;
    }

    /**
     * /**
     * Callbacks for export events.
     */
    public interface ExportListener {
        void onStart();

        void onCompleted(String filePath);

        void onError(Exception e);
    }
}
