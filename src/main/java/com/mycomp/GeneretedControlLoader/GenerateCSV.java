package com.mycomp.GeneretedControlLoader;

import com.mycomp.ExtUtils.FileUtils;
import com.mycomp.ExtUtils.StrUtils;
import org.jamel.dbf.DbfReader;
import org.jamel.dbf.structure.DbfField;
import org.jamel.dbf.structure.DbfHeader;

import java.io.File;
import java.io.IOException;

/**
 * Created by kondakov on 07.07.2016.
 */
public class GenerateCSV {


    public static void main(String[] args) {
        genCsv("БТИ. Диапазон квартир", "d:\\Temp\\BTI_old\\DIAPKV.DBF", "d:\\Temp\\BTI3\\DIAPKV.DBF", "D:\\out.txt");
        genCsv("БТИ. Инвентаризационно-технические характеристики строения", "d:\\Temp\\BTI_old\\FSKS.DBF", "d:\\Temp\\BTI3\\FSKS.DBF", "D:\\out.txt");
        genCsv("БТИ. Классификатор улиц", "d:\\Temp\\BTI_old\\FKUN.DBF", "d:\\Temp\\BTI3\\FKUN.DBF", "D:\\out.txt");
        genCsv("БТИ. Адресный реестр", "d:\\Temp\\BTI_old\\FADS.DBF", "d:\\Temp\\BTI3\\FADS.DBF", "D:\\out.txt");
        genCsv("БТИ. Соответствие ФИАС КЛАДР БТИ", "d:\\Temp\\BTI_old\\UKH.DBF", "d:\\Temp\\BTI3\\UKH.DBF", "D:\\out.txt");
        genCsv("Адресный реестр", "d:\\Temp\\BTI_old\\SAR_DIT.DBF", "d:\\Temp\\BTI3\\A_REESTR.dbf", "D:\\out.txt");





//        genCsv("БТИ. Нежилые квартиры(помещения)", "d:\\Temp\\BTI_old\\FKVA_NG.DBF", "d:\\Temp\\BTI3\\FKVA_NG.DBF", "D:\\out.txt");
//        genCsv("БТИ. Жилые квартиры(помещения)", "d:\\Temp\\BTI_old\\FKVA_G.DBF", "d:\\Temp\\BTI3\\FKVA_G.DBF", "D:\\out.txt");
//        genCsv("БТИ. Нежилые комнаты", "d:\\Temp\\BTI_old\\FKMN_NG.DBF", "d:\\Temp\\BTI3\\FKMN_NG1.DBF", "D:\\out.txt");
//        genCsv("БТИ. Жилые комнаты", "d:\\Temp\\BTI_old\\FKMN_G.DBF", "d:\\Temp\\BTI3\\FKMN_G1.DBF", "D:\\out.txt");
    }


    private static void genCsv(String ctgName, String pathOld, String pathNew, String pathOut) {

        File dbfOld = new File(pathOld);
        File dbfNew = new File(pathNew);


        DbfHeader headerOld = new DbfReader(dbfOld).getHeader();
        DbfHeader headerNew = new DbfReader(dbfNew).getHeader();
        String out = "\n" + ctgName + "\n";
        out = out + StrUtils.spaceStr("name"," ",10,StrUtils.agLeft) + "|" + StrUtils.spaceStr("typeOld"," ",15,StrUtils.agLeft) + "|" + StrUtils.spaceStr("typeNew"," ",15,StrUtils.agLeft) + "|" + StrUtils.spaceStr("change"," ",5,StrUtils.agCenter) + "\n";
        out = out + StrUtils.spaceStr("=","=",45,StrUtils.agLeft) + "\n";
        for (int i = 0; i < headerNew.getFieldsCount(); i++) {
            DbfField fieldNew = headerNew.getField(i);
            String nameFieldNew = fieldNew.getName();
            int leghtNew = fieldNew.getFieldLength();
            String typeNew = fieldNew.getDataType().name() + "(" + leghtNew + ")";
            boolean s = false;
            for (int j = 0; j < headerOld.getFieldsCount(); j++) {
                DbfField fieldOld = headerOld.getField(j);
                String nameFieldOld = fieldOld.getName();

                if (nameFieldNew.equals(nameFieldOld)) {
                    int leghtOld = fieldOld.getFieldLength();

                    String typeOld = fieldOld.getDataType().name() + "(" + leghtOld + ")";

                    String izm = leghtOld != leghtNew ? "1" : "0";
                    out = out + StrUtils.spaceStr(nameFieldNew," ",10,StrUtils.agLeft) + "|" + StrUtils.spaceStr(typeOld," ",15,StrUtils.agLeft) + "|" + StrUtils.spaceStr(typeNew," ",15,StrUtils.agLeft) + "|" + StrUtils.spaceStr(izm," ",2,StrUtils.agCenter) + "\n";
                    s = true;
                    break;
                }
            }
            if (!s)
                out = out + StrUtils.spaceStr(nameFieldNew," ",10,StrUtils.agLeft) + "|" + StrUtils.spaceStr(" "," ",15,StrUtils.agLeft) + "|" + StrUtils.spaceStr(typeNew," ",15,StrUtils.agLeft) + "|" + StrUtils.spaceStr("1"," ",2,StrUtils.agCenter) + "\n";

        }
        System.out.println(out);
        try {
            FileUtils.writeFile(pathOut, out, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
