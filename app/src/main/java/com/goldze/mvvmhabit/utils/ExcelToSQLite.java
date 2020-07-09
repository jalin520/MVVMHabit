package com.goldze.mvvmhabit.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;

public class ExcelToSQLite {
    private static Handler handler = new Handler(Looper.getMainLooper());

    private Context mContext;
    private String dataBaseName;
    private SQLiteDatabase database;
    private String filePath;
    private String assetFileName;
    private String decryptKey;
    private String dateFormat;

    private SimpleDateFormat sdf;

    public static class Builder {
        private Context context;
        private String dataBaseName;
        private String filePath;
        private String assetFileName;
        private String decryptKey;
        private String dateFormat;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public ExcelToSQLite build() {
            if (TextUtils.isEmpty(dataBaseName)) {
                throw new IllegalArgumentException("Database name must not be null.");
            }
            return new ExcelToSQLite(context, dataBaseName, filePath, assetFileName, decryptKey, dateFormat);
        }

        public Builder setDataBase(String dataBaseName) {
            this.dataBaseName = dataBaseName;
            return this;
        }

        public Builder setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public Builder setFilePath(String path) {
            this.filePath = path;
            this.assetFileName = null;
            if (TextUtils.isEmpty(this.dataBaseName)) {
                this.dataBaseName = context.getDatabasePath(new File(path).getName() + ".db").getAbsolutePath();
            }
            return this;
        }

        public Builder setDecryptKey(String decryptKey) {
            this.decryptKey = decryptKey;
            return this;
        }

        public Builder setAssetFileName(String name) {
            this.assetFileName = name;
            this.filePath = null;
            if (TextUtils.isEmpty(this.dataBaseName)) {
                this.dataBaseName = context.getDatabasePath(new File(name).getName() + ".db").getPath();
            }
            return this;
        }

        public void start() {
            final ExcelToSQLite excelToSqlite = build();
            excelToSqlite.start();
        }

        public void start(ImportListener listener) {
            final ExcelToSQLite excelToSqlite = build();
            excelToSqlite.start(listener);
        }

    }

    private ExcelToSQLite(Context context, String dataBaseName, String filePath, String assetFileName, String decryptKey, String dateFormat) {
        this.mContext = context;
        this.filePath = filePath;
        this.assetFileName = assetFileName;
        this.decryptKey = decryptKey;
        this.dataBaseName = dataBaseName;
        this.dateFormat = dateFormat;
        if (!TextUtils.isEmpty(dateFormat)) {
            sdf = new SimpleDateFormat(dateFormat);
        }

        try {
            database = SQLiteDatabase.openOrCreateDatabase(dataBaseName, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * start task
     */
    public boolean start() {
        if (TextUtils.isEmpty(filePath) && TextUtils.isEmpty(assetFileName)) {
            throw new IllegalArgumentException("Asset file or external file name must not be null.");
        }
        try {
            if (TextUtils.isEmpty(filePath)) {
                return importTables(mContext.getAssets().open(assetFileName), assetFileName);
            } else {
                return importTables(new FileInputStream(filePath), filePath);
            }
        } catch (Exception e) {
            if (database != null && database.isOpen()) {
                database.close();
            }
            return false;
        }
    }

    /**
     * start task with a listener
     *
     * @param listener
     */
    public void start(final ImportListener listener) {
        if (TextUtils.isEmpty(filePath) && TextUtils.isEmpty(assetFileName)) {
            throw new IllegalArgumentException("Asset file or external file name must not be null.");
        }
        if (listener != null) {
            listener.onStart();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(filePath)) {
                        importTables(mContext.getAssets().open(assetFileName), assetFileName);
                    } else {
                        importTables(new FileInputStream(filePath), filePath);
                    }
                    if (listener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCompleted(dataBaseName);
                            }
                        });
                    }
                } catch (final Exception e) {
                    if (database != null && database.isOpen()) {
                        database.close();
                    }
                    if (listener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(e);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    /**
     * core code
     *
     * @param stream   asset stream or file stream
     * @param fileName origin file name
     * @throws Exception
     */
    private boolean importTables(InputStream stream, String fileName) throws Exception {
        Workbook workbook;
        if (fileName.toLowerCase().endsWith(".xls")) {
            workbook = Workbook.getWorkbook(stream);
        } else {
            throw new UnsupportedOperationException("Unsupported file format!");
        }
        stream.close();
        int sheetNumber = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNumber; i++) {
            createTable(workbook.getSheet(i));
        }
        database.close();
        return true;
    }

    /**
     * create table by sheet
     *
     * @param sheet
     */
    private void createTable(Sheet sheet) {
        StringBuilder createTableSql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        createTableSql.append(sheet.getName());
        createTableSql.append("(");
        int colCount = sheet.getColumns();//列的大小
        Cell[] rowHeader = sheet.getRow(0);
        List<String> columns = new ArrayList<>();
        for (int i = 0; i < colCount; i++) {
            String colVal = rowHeader[i].getContents();
            createTableSql.append(colVal);
            if (i == colCount - 1) {
                createTableSql.append(" TEXT");
            } else {
                createTableSql.append(" TEXT,");
            }
            columns.add(colVal);
        }
        createTableSql.append(")");
        int rowCount = sheet.getRows();// 行的大小
        database.execSQL(createTableSql.toString());
        for (int i = 0; i < rowCount; i++) {
            Cell[] row = sheet.getRow(i);
            ContentValues values = new ContentValues();
            for (int n = 0; n < rowCount; n++) {
                Cell val = row[i];
                if (val == null) {
                    continue;
                }
                values.put(columns.get(n), val.getContents());
            }
            if (values.size() == 0)
                continue;
            long result = database.insert(sheet.getName(), null, values);
            if (result < 0) {
                throw new RuntimeException("Insert value failed!");
            }
        }
    }

    private static String getRealStringValueOfDouble(Double d) {
        String doubleStr = d.toString();
        boolean b = doubleStr.contains("E");
        int indexOfPoint = doubleStr.indexOf('.');
        if (b) {
            int indexOfE = doubleStr.indexOf('E');
            BigInteger xs = new BigInteger(doubleStr.substring(indexOfPoint
                    + BigInteger.ONE.intValue(), indexOfE));
            int pow = Integer.valueOf(doubleStr.substring(indexOfE
                    + BigInteger.ONE.intValue()));
            int xsLen = xs.toByteArray().length;
            int scale = xsLen - pow > 0 ? xsLen - pow : 0;
            doubleStr = String.format("%." + scale + "f", d);
        } else {
            Pattern p = Pattern.compile(".0$");
            java.util.regex.Matcher m = p.matcher(doubleStr);
            if (m.find()) {
                doubleStr = doubleStr.replace(".0", "");
            }
        }
        return doubleStr;
    }

    /**
     * Callbacks for import events.
     */
    public interface ImportListener {
        void onStart();

        void onCompleted(String dataBaseName);

        void onError(Exception e);
    }

}
