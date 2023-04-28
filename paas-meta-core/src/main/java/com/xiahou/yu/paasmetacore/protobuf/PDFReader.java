package com.xiahou.yu.paasmetacore.protobuf;


public class PDFReader {
    //    public static void main(String[] args) throws IOException, ReaderException {
//        PDDocument document = PDDocument.load(new File("/Users/wanghaoxin/Downloads/多张发票.pdf"));
//        PDFTextStripper pdfStripper = new PDFTextStripper();
//        String text = pdfStripper.getText(document);
//
//        List<BufferedImage> images = extractImages(document);
//        for (BufferedImage image : images) {
//            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
//            MultiFormatReader reader = new MultiFormatReader();
//            Result result = reader.decode(binaryBitmap);
//            System.out.println(result.getText());
//        }
//
//        document.close();
//    }
//
//    private static List<BufferedImage> extractImages(PDDocument document) throws IOException {
//        List<BufferedImage> images = new ArrayList<>();
//        PDPageTree pages = document.getPages();
//        for (PDPage page : pages) {
//            PDResources resources = page.getResources();
//            for (COSName name : resources.getXObjectNames()) {
//                PDXObject object = resources.getXObject(name);
//                if (object instanceof PDImageXObject) {
//                    images.add(((PDImageXObject) object).getImage());
//                }
//            }
//        }
//        return images;
//    }

/*
    public static void main(String[] args) {
        // 加载图像
        GrayF32 image = UtilImageIO.loadImage("qrcodes.png", GrayF32.class);

        // 创建SIFT特征点检测器和描述器
        ConfigDetectInterestPoint configSIFT = new ConfigDetectInterestPoint();
        configSIFT.maxFeatures = 5000;
        DetectInterestPoint<GrayF32> detectorSIFT = FactoryDetectInterestPoint.sift(configSIFT, null, null, GrayF32.class);
        DescribeRegionPoint<GrayF32, SurfFeature> describeSIFT = FactoryDescribeRegionPoint.surfStable(null, GrayF32.class);

        // 对图像进行特征点检测和描述
        detectorSIFT.detect(image);
        List<Point2D_F64> points = new ArrayList<>();
        List<SurfFeature> features = new ArrayList<>();
        for (int i = 0; i < detectorSIFT.getNumberOfFeatures(); i++) {
            Point2D_F64 p = new Point2D_F64();
            detectorSIFT.getLocation(i, p);
            points.add(p);
            SurfFeature f = describeSIFT.createDescription();
            describeSIFT.process(image, (float) p.x, (float) p.y, 0, f);
            features.add(f);
        }
*/



}

