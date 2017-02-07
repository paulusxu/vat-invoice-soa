package com.lenovo.invoice.common.utils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by mayan3 on 2016/8/11.
 */
public class PdfUtils {
    private static final Logger logger = LoggerFactory.getLogger(PdfUtils.class);

    private static final InputStream templatePdf=PdfUtils.class.getResourceAsStream("template.pdf");// pdf模板

    public static byte[] exportPdf(Map<String, String> data)  {
        PdfStamper ps=null;
        ByteArrayOutputStream bos=null;
        PdfReader reader=null;
        try {
            reader = new PdfReader(templatePdf);
            bos = new ByteArrayOutputStream();
            ps = new PdfStamper(reader, bos);
            AcroFields fields = ps.getAcroFields();
            fillData(fields, data);
            ps.setFormFlattening(true);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            try {
                bos.close();
                ps.close();
                reader.close();
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
        return bos.toByteArray();
    }

    public static void fillData(AcroFields fields, Map<String, String> data) throws IOException, DocumentException {
        for (String key : data.keySet()) {
            String value = data.get(key);
            fields.setField(key, value);
        }
    }
}
