/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamo.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author edgar
 */
public class PrestamoUtils {

    public static float amortizarPrestamo(double monto, float tasa) {
        return 0f;
    }

    public static boolean containsOnlyNumbers(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }

        //Replace '-'
        str = str.replaceAll("-", "");

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isCedulaSizeValid(String cedula) {
        if (cedula.length() == 11) {
            return true;
        }
        return false;
    }

    public static boolean isTelefonoSizeValid(String telefono) {
        if (telefono.length() == 10) {
            return true;
        }
        return false;
    }

    private static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("-dd-MM-yyyy");
        return sdf.format(c.getTime());
    }

    public static void exportDataBase(String drive) throws IOException {
        String path = ""+drive+"backup" + getCurrentDate() + ".sql";
        String dumpCommand = "mysqldump -uroot -pwiled prestamos -r " + path;
        File tst = new File(path);
        FileWriter fw = null;

        fw = new FileWriter(tst);
        fw.close();

        Runtime rt = Runtime.getRuntime();

        Process proc = rt.exec(dumpCommand);
        InputStream in = proc.getInputStream();
        InputStreamReader read = new InputStreamReader(in, "latin1");
        BufferedReader br = new BufferedReader(read);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tst, true));
        String line = null;
        StringBuilder buffer = new StringBuilder();
        while ((line = br.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        String toWrite = buffer.toString();
        bw.write(toWrite);
        bw.close();
        br.close();
    }
}
