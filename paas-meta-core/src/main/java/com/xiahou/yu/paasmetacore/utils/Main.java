/*
package com.xiahou.yu.paasmetacore.utils;

import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.factory.fiducial.ConfigQrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

*/
/**
 * description:
 *
 * @author wanghaoxin
 * date     2022/9/4 11:40
 * @version 1.0
 *//*

public class Main {
    public static void main(String[] args) throws IOException {
        extracted();
    }

    private static void extracted() throws IOException {
        final long l = Instant.now().toEpochMilli();
        PDDocument document = PDDocument.load(new File("/Users/wanghaoxin/Downloads/发票扫描件2(1).pdf"));
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        List<BufferedImage> images = extractImages(document);
        for (BufferedImage image : images) {
            System.out.println(ObjectSizeCalculator.getObjectSize(image));
            singleImage(image);
        }
        document.close();
        System.out.println(Instant.now().toEpochMilli() - l);
    }

    private static void singleImage(BufferedImage input) {
//        BufferedImage input = UtilImageIO.loadImage("/Users/wanghaoxin/Downloads/page_1.jpg");
        GrayU8 gray = ConvertBufferedImage.convertFrom(input, (GrayU8) null);

        ConfigQrCode config = new ConfigQrCode();
//		config.considerTransposed = false; // by default, it will consider incorrectly encoded markers. Faster if false
        QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode(config, GrayU8.class);
        detector.process(gray);
        // Gets a list of all the qr codes it could successfully detect and decode
        List<QrCode> detections = detector.getDetections();
        for (QrCode qr : detections) {
            // The message encoded in the marker
            System.out.println("message: '" + qr.message + "'");

            // Visualize its location in the image
        }

        // List of objects it thinks might be a QR Code but failed for various reasons
        List<QrCode> failures = detector.getFailures();
        for (QrCode qr : failures) {
            // If the 'cause' is ERROR_CORRECTION or higher, then there's a decent chance it's a real marker
            if (qr.failureCause.ordinal() < QrCode.Failure.ERROR_CORRECTION.ordinal()) {
                continue;
            }

        }
        input = null;
    }

    private static List<BufferedImage> extractImages(PDDocument document) throws IOException {
        List<BufferedImage> images = new ArrayList<>();
        PDPageTree pages = document.getPages();
        for (PDPage page : pages) {
            PDResources resources = page.getResources();
            for (COSName name : resources.getXObjectNames()) {
                PDXObject object = resources.getXObject(name);
                if (object instanceof PDImageXObject) {
                    images.add(((PDImageXObject) object).getImage());
                }
            }
        }
        return images;
    }

}
*/
