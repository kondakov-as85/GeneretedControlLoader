package com.mycomp.GeneretedControlLoader;

import com.mycomp.ExtUtils.FilePathParams;
import com.mycomp.ExtUtils.FileUtils;
import com.mycomp.ExtUtils.StrUtils;
import org.jamel.dbf.DbfReader;
import org.jamel.dbf.processor.DbfProcessor;
import org.jamel.dbf.processor.DbfRowProcessor;
import org.jamel.dbf.structure.DbfField;
import org.jamel.dbf.structure.DbfHeader;

import java.io.File;
import java.io.IOException;

/**
 * Created by kondakov on 04.02.2015.
 */
public class MainClass {

    public static String prefix = "";
    public static final String errNotFound = "dbf file not found!";
    public static final String lineSeparate = "\n=====================================";

    public static void main( String[] args ){
//        String dbfPath = "d:\\!!!WORK\\ASUR_NSI\\BTIKladr\\010215_BTI\\DIT_010215\\FSKS.DBF";
        String dbfPath = args[0];
        if (args.length>1)
            //if (args[1]!=null)
                prefix = args[1]+"_";
        String err = "";
        if (dbfPath==null){
            err = errNotFound;
        }
        if (dbfPath.equals("")){
            err = errNotFound;
        }
        if(err.equals("")) {
            System.out.println(lineSeparate);
            genCtrl(dbfPath);
            System.out.println(lineSeparate);
            printFieldsLenght(dbfPath);
        }else{
            System.out.println(err);
        }
    }

    private static void printFieldsLenght(String dbfPath) {
        try {
            File dbf = new File(dbfPath);
            DbfReader reader = new DbfReader(dbf);
            DbfHeader header = reader.getHeader();
            FilePathParams filePath = FileUtils.getFilePathParams(dbfPath);
            String outPath = "./" + filePath.nameFile + "." + filePath.extFile + ".leng";
            String head = StrUtils.spaceStr("#"," ",3,StrUtils.agCenter) +
                        "|" + StrUtils.spaceStr("FieldName"," ",15,StrUtils.agCenter) +
                        "|" + StrUtils.spaceStr("Lenght"," ",6,StrUtils.agCenter) +
                        "|" + StrUtils.spaceStr("Type"," ",10,StrUtils.agCenter) + "\n" +
                        StrUtils.spaceStr("","-",37,StrUtils.agLeft)+"\n";
            String out = "";
            for (int i = 0; i < header.getFieldsCount(); i++) {
                DbfField field = header.getField(i);
                String nameField = StrUtils.spaceStr(" " + field.getName(), " ", 15, StrUtils.agLeft);
                String lenghtfield = StrUtils.spaceStr(" " + field.getFieldLength(), " ", 6, StrUtils.agLeft);
                String numberField = StrUtils.spaceStr((i + 1)+" "," ",3,StrUtils.agCenter);
                String typeField = StrUtils.spaceStr(" " + field.getDataType().name()," ",10,StrUtils.agLeft);
                out = out + numberField + "|" + nameField + "|" + lenghtfield + "|" + typeField +"\n";
            }
            out = head + out;
            System.out.println(out);
            FileUtils.writeFile(outPath, out, false);
            System.out.println("Out LengFile: "+outPath);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private static void genCtrl(String dbfPath){
        try {
            File dbf = new File(dbfPath);
            DbfReader reader = new DbfReader(dbf);
            DbfHeader header = reader.getHeader();
            FilePathParams filePath = FileUtils.getFilePathParams(dbfPath);
            String fileName = filePath.nameFile+"."+filePath.extFile;
            String outPath = "./"+filePath.nameFile + "." + filePath.extFile + ".ctrl";
            int startPosition = 2;
            String head = "load data\n" +
                    "   CHARACTERSET  'RU8PC866'\n" +
                    "   infile '..."+fileName+"' \"fix @lenght@\"\n" +
                    "   badfile '..."+fileName+".bad'\n" +
                    "   discardfile '..."+fileName+".dis'\n" +
                    "   append\n" +
                    "   into table \"[scheme]\".\"[table]\"\n" +
                    "   append\n" +
                    "   fields\n" +
                    "   TRAILING NULLCOLS\n" +
                    "   (\n";
            String out = "";
            int totalLenght = 0;
            for(int i=0; i<header.getFieldsCount(); i++){
                DbfField field = header.getField(i);
                String nameField = prefix+field.getName();
                int lenghtfield = field.getFieldLength();
                int tmpInd = lenghtfield - 1;
                int endPosition = ((tmpInd + startPosition) == startPosition?0:tmpInd+startPosition);
                String tmpStr = "       "+nameField+" POSITION("+startPosition+":"+endPosition+") ";
                if(endPosition==0)
                    tmpStr = "       "+nameField+" POSITION("+startPosition+") ";
                tmpStr = tmpStr + getType(field)+",\n";
                out = out + tmpStr;
                startPosition = startPosition + lenghtfield;
                totalLenght = totalLenght + lenghtfield;
            }
            out = head.replace("@lenght@",(totalLenght+1)+"") + out+"  )\n";
            System.out.println(out);
            FileUtils.writeFile(outPath,out,false);
            System.out.println("Out CtrlFile: "+outPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getType(DbfField field) {
        String result = "";
        String name = prefix+field.getName();
        String type = field.getDataType().name();
        if(type.equals("NUMERIC")) {
            result = "\"to_number(:"+name+")\"";
        } else if (type.equals("DATE")) {
            result = "\"to_date(:"+name+", 'YYYYMMDD')\"";
        } else if (type.equals("CHAR")) {
            result = "\"trim(:"+name+")\"";
        }
        return result;
    }


}
