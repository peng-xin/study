package pers.px;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.xmlbeans.XmlException;
import org.docx4j.TextUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Doc {
    private static Logger logger = LoggerFactory.getLogger(Doc.class);

    public static void main(String[] args) {
//        readDocx("C:\\Users\\stphen\\Desktop\\预算及费比管控-产品需求文档 v1.1.docx");
//        readDocx("H:\\datacenter\\docx4j-master\\docx4j-samples-docx4j\\sample-docs\\tables.docx");
//        read("C:\\Users\\stphen\\Desktop\\预算及费比管控-产品需求文档 v1.1.docx");
//        read("H:\\datacenter\\docx4j-master\\docx4j-samples-docx4j\\sample-docs\\tables.docx");
//        createDocxWithTitles("C:\\Users\\stphen\\Desktop\\createDocxWithTitles.docx");
        getWordTitles2007("C:\\Users\\stphen\\Desktop\\createDocxWithTitles.docx");
        getWordTitles2007("C:\\Users\\stphen\\Desktop\\预算及费比管控-产品需求文档 v1.1.docx");

        read("C:\\Users\\stphen\\Desktop\\createDocxWithTitles.docx");
//        readDocx("C:\\Users\\stphen\\Desktop\\createDocxWithTitles.docx");
        read("C:\\Users\\stphen\\Desktop\\预算及费比管控-产品需求文档 v1.1.docx");
//        read("H:\\datacenter\\docx4j-master\\docx4j-samples-docx4j\\sample-docs\\tables.docx");
    }

    private static void read(String docName) {

        XWPFDocument doc = null;
        try {
            doc = new XWPFDocument(new FileInputStream(docName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<IBodyElement> iter = doc.getBodyElementsIterator();
        while (iter.hasNext()) {
            IBodyElement elem = iter.next();
            if (elem instanceof XWPFParagraph) {
                logger.info(getTitleText((XWPFParagraph) elem));
            } else if (elem instanceof XWPFTable) {
                logger.info(((XWPFTable) elem).getText());
            }
        }

        if (docName.endsWith(".doc")) {
            WordExtractor ex = null;
            try {
                InputStream is = new FileInputStream(new File(docName));
                ex = new WordExtractor(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info(ex.getText());
        } else if (docName.endsWith(".docx")) {
            FileInputStream fs = null;
            XWPFDocument xdoc = null;
            try {
                fs = new FileInputStream(new File(docName));
                xdoc = new XWPFDocument(fs);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
            logger.info(extractor.getText());
            try {
                POITextExtractor poiTextExtractor = ExtractorFactory.<POITextExtractor>createExtractor(new File(docName));
                logger.info(poiTextExtractor.getText());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OpenXML4JException e) {
                e.printStackTrace();
            } catch (XmlException e) {
                e.printStackTrace();
            }
        }
    }

    private static int[] levelCurrentValues = new int[]{0, 0, 0};

    private static String getTitleTexts(XWPFParagraph xwpfParagraph) {

        String text = xwpfParagraph.getText();
        String levelText = xwpfParagraph.getNumLevelText();
        BigInteger levelDepth = xwpfParagraph.getNumIlvl();
        String levelFormat = xwpfParagraph.getNumFmt();

        if (levelText != null) {
            levelCurrentValues[levelDepth.intValue()] += 1;

            levelText = levelText.replace("%1", "" + levelCurrentValues[0]);
            levelText = levelText.replace("%2", "" + levelCurrentValues[1]);
            levelText = levelText.replace("%3", "" + levelCurrentValues[2]);

        }

        return levelText + " " + text;
    }

    private static String getTitleText(XWPFParagraph xwpfParagraph) {

        try {
            //判断该段落是否设置了大纲级别
            if (xwpfParagraph.getCTP().getPPr().getOutlineLvl() != null) {
                // System.out.println("getCTP()");
//              System.out.println(para.getParagraphText());
//              System.out.println(para.getCTP().getPPr().getOutlineLvl().getVal());

//                return String.valueOf(xwpfParagraph.getCTP().getPPr().getOutlineLvl().getVal());
                return xwpfParagraph.getText();
            }
        } catch (Exception e) {

        }

        try {
            //判断该段落的样式是否设置了大纲级别
            if (xwpfParagraph.getDocument().getStyles().getStyle(xwpfParagraph.getStyle()).getCTStyle().getPPr().getOutlineLvl() != null) {

                // System.out.println("getStyle");
//              System.out.println(para.getParagraphText());
//              System.out.println(doc.getStyles().getStyle(para.getStyle()).getCTStyle().getPPr().getOutlineLvl().getVal());

//                return String.valueOf(xwpfParagraph.getDocument().getStyles().getStyle(xwpfParagraph.getStyle()).getCTStyle().getPPr().getOutlineLvl().getVal());
                return xwpfParagraph.getText();
            }
        } catch (Exception e) {

        }

        try {
            //判断该段落的样式的基础样式是否设置了大纲级别
            if (xwpfParagraph.getDocument().getStyles().getStyle(xwpfParagraph.getDocument().getStyles().getStyle(xwpfParagraph.getStyle()).getCTStyle().getBasedOn().getVal())
                    .getCTStyle().getPPr().getOutlineLvl() != null) {
                // System.out.println("getBasedOn");
//              System.out.println(para.getParagraphText());
                String styleName = xwpfParagraph.getDocument().getStyles().getStyle(xwpfParagraph.getStyle()).getCTStyle().getBasedOn().getVal();
//              System.out.println(doc.getStyles().getStyle(styleName).getCTStyle().getPPr().getOutlineLvl().getVal());

//                return String.valueOf(xwpfParagraph.getDocument().getStyles().getStyle(styleName).getCTStyle().getPPr().getOutlineLvl().getVal());
                return xwpfParagraph.getText();
            }
        } catch (Exception e) {

        }

        try {
            if (xwpfParagraph.getStyleID() != null) {
                return xwpfParagraph.getStyleID();
            }
        } catch (Exception e) {

        }

        return "";

//        String text = xwpfParagraph.getText();
//        String levelText = xwpfParagraph.getNumLevelText();
//        BigInteger levelDepth = xwpfParagraph.getNumIlvl();
//        String levelFormat = xwpfParagraph.getNumFmt();
//
//        if (levelText != null) {
//            levelCurrentValues[levelDepth.intValue()] += 1;
//
//            levelText = levelText.replace("%1", "" + levelCurrentValues[0]);
//            levelText = levelText.replace("%2", "" + levelCurrentValues[1]);
//            levelText = levelText.replace("%3", "" + levelCurrentValues[2]);
//
//        }
//
//        return levelText + " " + text;
    }

    public static void getWordTitles2007(String path) {

        InputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //2007
//		OPCPackage p = POIXMLDocument.openPackage(path);
//		XWPFWordExtractor e = new XWPFWordExtractor(p);
//		POIXMLDocument doc = e.getDocument();
        List<String> list = new ArrayList<String>();
        XWPFDocument doc = null;
        try {
            doc = new XWPFDocument(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<XWPFParagraph> paras = doc.getParagraphs();
        for (XWPFParagraph graph : paras) {
            String text = graph.getParagraphText();
            String style = graph.getStyle();
            if ("1".equals(style)) {
//				System.out.println(text+"--["+style+"]");
            } else if ("2".equals(style)) {
//				System.out.println(text+"--["+style+"]");
            } else if ("3".equals(style)) {
//				System.out.println(text+"--["+style+"]");
            } else {
                continue;
            }
            list.add(text);
        }
        list.forEach(str -> logger.info(System.lineSeparator().concat(str)));
    }

    private static void readDocx(String docName) {
        if (StringUtils.isEmpty(docName)) {
            logger.error("docName must have a non null value");
            return;
        }
        File docFile = new File(docName);
        WordprocessingMLPackage wordprocessingMLPackage = null;
        try {
            wordprocessingMLPackage = WordprocessingMLPackage.load(docFile);
        } catch (InvalidFormatException e) {
            logger.error(e.getMessage());
        } catch (Docx4JException e) {
            logger.error(e.getMessage());
        }

        logger.info("The docName's contentType is {}", wordprocessingMLPackage.getContentType());

        List<Object> contentList = wordprocessingMLPackage.getMainDocumentPart().getContent();
        for (Object content : contentList) {
            readContent(content);
        }
    }

    private static void readContent(Object content) {
        if (content instanceof JAXBElement) {
            logger.info(content.getClass().getName());
            readContent(((JAXBElement) content).getValue());
        } else if (content instanceof ContentAccessor) {
            logger.info(content.getClass().getName());
            if (content instanceof P) {
                logger.info(getElementContent(content));
            } else if (content instanceof Tbl) {
                logger.info(StringUtils.join(getTblContentList((Tbl) content), System.lineSeparator()));
            }
        } else {
            logger.info(content.getClass().getName());
        }
    }

    private static List<String> getTblContentList(Tbl tbl) {
        List<String> resultList = new ArrayList<String>();
        List<Tr> trList = getTblAllTr(tbl);
        for (Tr tr : trList) {
            StringBuffer sb = new StringBuffer();
            List<Tc> tcList = getTrAllCell(tr);
            for (Tc tc : tcList) {
                sb.append(getElementContent(tc)).append("   ");
            }
            resultList.add(sb.toString());
        }
        return resultList;
    }

    private static List<Tr> getTblAllTr(Tbl tbl) {
        List<Object> objList = getAllElementFromObject(tbl, Tr.class);
        List<Tr> trList = new ArrayList<Tr>();
        if (objList == null) {
            return trList;
        }
        for (Object obj : objList) {
            if (obj instanceof Tr) {
                Tr tr = (Tr) obj;
                trList.add(tr);
            }
        }
        return trList;
    }

    private static List<Tc> getTrAllCell(Tr tr) {
        List<Object> objList = getAllElementFromObject(tr, Tc.class);
        List<Tc> tcList = new ArrayList<Tc>();
        if (objList == null) {
            return tcList;
        }
        for (Object obj : objList) {
            if (obj instanceof Tc) {
                Tc tc = (Tc) obj;
                tcList.add(tc);
            }
        }
        return tcList;
    }

    private static String getElementContent(Object obj) {
        StringWriter stringWriter = new StringWriter();
        try {
            TextUtils.extractText(obj, stringWriter);
        } catch (Exception e) {
            logger.error("{} getElementContent error,message is {}", obj, e.getMessage());
        }
        return stringWriter.toString();
    }

    private static void createDocxWithTitles(String docName) {
        WordprocessingMLPackage wordMLPackage = null;
        try {
            wordMLPackage = WordprocessingMLPackage.createPackage();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
//创建docx4j工厂
        ObjectFactory factory = Context.getWmlObjectFactory();
//获得word包中document.xml文件内容
        MainDocumentPart main = wordMLPackage.getMainDocumentPart();
//增加编号列表设置部分，注意，一定要增加到MainDocumentPart中，否则会出现引用不成功的情况
//        NumberingDefinitionsPart numberingPart = null;
//        try {
//            numberingPart = new NumberingDefinitionsPart();
//        } catch (InvalidFormatException e) {
//            e.printStackTrace();
//        }
////新建numbering标签
//        Numbering numbering = new Numbering();
////将numbering标签添加到编号列表设置文件中
//        numberingPart.setContents(numbering);
////将编号列表设置文件加入word文件夹下
//        try {
//            main.addTargetPart(numberingPart);
//        } catch (InvalidFormatException e) {
//            e.printStackTrace();
//        }
//// 首先定义AbstractNum部分
//        Numbering.AbstractNum an = new Numbering.AbstractNum();
//        an.setAbstractNumId(BigInteger.valueOf(1));
////设置编号类型为多级编号
//        Numbering.AbstractNum.MultiLevelType mlt = new Numbering.AbstractNum.MultiLevelType();
//        mlt.setVal("multilevel");
//        an.setMultiLevelType(mlt);
//        org.docx4j.wml.Numbering.AbstractNum.Name numberName = new org.docx4j.wml.Numbering.AbstractNum.Name();
//        numberName.setVal(NumberFormat.HEX.value());
//        an.setName(numberName);
//        Lvl numberlvl = new Lvl();
////设置为1级编号
//        numberlvl.setIlvl(BigInteger.valueOf(0));
//// 设置开始序号，如果不设置为1的话默认为0，可能出现某些格式的序号没有0值显示
//        Lvl.Start start = new Lvl.Start();
//        start.setVal(BigInteger.valueOf(1));
//        numberlvl.setStart(start);
//// 设置显示文本内容，如果不设置的话将不显示编号
//        Lvl.LvlText lvltext = new Lvl.LvlText();
//// 表示显示第一级编号，后面跟一个“.”
//        lvltext.setVal("%1.");
//        numberlvl.setLvlText(lvltext);
//// 标题增加加粗与字号
//        RPr rpr = new RPr();
//        rpr.setB(new BooleanDefaultTrue());
//        HpsMeasure sz = new HpsMeasure();
//        sz.setVal(BigInteger.valueOf(34));
//        rpr.setSz(sz);
//        rpr.setSzCs(sz);
//        numberlvl.setRPr(rpr);
//// 设置编号格式
//        NumFmt numfmt = new NumFmt();
//        numfmt.setVal(NumberFormat.HEX);
//        numberlvl.setNumFmt(numfmt);
////将设置好的编号层级加入到<w:abstractNum>标签中
//        an.getLvl().add(numberlvl);
//        numberlvl = new Lvl();
////设置为2级编号
//        numberlvl.setIlvl(BigInteger.valueOf(1));
//// 设置开始序号，如果不设置为1的话默认为0，可能出现某些格式的序号没有0值显示
//        start = new Lvl.Start();
//        start.setVal(BigInteger.valueOf(1));
//        numberlvl.setStart(start);
//// 设置显示文本内容，如果不设置的话将不显示编号
//        lvltext = new Lvl.LvlText();
//// 表示显示第二级编号，每一级后面跟一个“.”
//        lvltext.setVal("%1.%2.");
//        numberlvl.setLvlText(lvltext);
//// 标题增加加粗与字号
//        rpr = new RPr();
//        rpr.setB(new BooleanDefaultTrue());
//        sz = new HpsMeasure();
//        sz.setVal(BigInteger.valueOf(34));
//        rpr.setSz(sz);
//        rpr.setSzCs(sz);
//        numberlvl.setRPr(rpr);
//// 设置编号格式
//        numfmt = new NumFmt();
//        numfmt.setVal(NumberFormat.HEX);
//        numberlvl.setNumFmt(numfmt);
////将设置好的编号层级加入到<w:abstractNum>标签中
//        an.getLvl().add(numberlvl);
//        numberlvl = new Lvl();
////设置为3级编号
//        numberlvl.setIlvl(BigInteger.valueOf(1));
//// 设置开始序号，如果不设置为1的话默认为0，可能出现某些格式的序号没有0值显示
//        start = new Lvl.Start();
//        start.setVal(BigInteger.valueOf(1));
//        numberlvl.setStart(start);
//// 设置显示文本内容，如果不设置的话将不显示编号
//        lvltext = new Lvl.LvlText();
//// 表示显示第二级编号，每一级后面跟一个“.”
//        lvltext.setVal("%1.%2.%3.");
//        numberlvl.setLvlText(lvltext);
//// 标题增加加粗与字号
//        rpr = new RPr();
//        rpr.setB(new BooleanDefaultTrue());
//        sz = new HpsMeasure();
//        sz.setVal(BigInteger.valueOf(34));
//        rpr.setSz(sz);
//        rpr.setSzCs(sz);
//        numberlvl.setRPr(rpr);
//// 设置编号格式
//        numfmt = new NumFmt();
//        numfmt.setVal(NumberFormat.HEX);
//        numberlvl.setNumFmt(numfmt);
////将设置好的编号层级加入到<w:abstractNum>标签中
//        an.getLvl().add(numberlvl);
//// 将AbstractNum增加到Numbering中
//        numbering.getAbstractNum().add(an);
//// 设置一个Num的实例，通过AbstractNumId引用刚定义的AbstractNum
//        Numbering.Num.AbstractNumId anid = new Numbering.Num.AbstractNumId();
//        anid.setVal(BigInteger.valueOf(1));
//        Numbering.Num num = new Numbering.Num();
//        num.setAbstractNumId(anid);
//        num.setNumId(BigInteger.valueOf(1)); // 此处NumId不能为0，必须为正整数
//// 将Num增加到Numbering中
//        numbering.getNum().add(num);
////获得styles.xml文件内容
//        StyleDefinitionsPart sdp = main.getStyleDefinitionsPart();
////清空styles.xml中styles标签下的内容
//        try {
//            sdp.getContents().getStyle().clear();
//        } catch (Docx4JException e) {
//            e.printStackTrace();
//        }
////创建一个样式标签
//        Style style = factory.createStyle();
////设置样式标签的type属性
//        style.setType("paragraph");
////新建name标签
//        Style.Name name = new Style.Name();
////设置name标签的val属性
//        name.setVal("Heading 1");
////将设置好的name标签设置到样式标签中
//        style.setName(name);
////设置样式标签的id属性
//        style.setStyleId("Heading1");
////创建段落格式标签
//        PPr ppr = factory.createPPr();
////创建大纲级别标签
//        PPrBase.OutlineLvl lvl = new PPrBase.OutlineLvl();
////设置大纲级别标签的val属性为0
//        lvl.setVal(BigInteger.valueOf(0));
////将设置好的大纲级别标签设置到段落格式标签中
//        ppr.setOutlineLvl(lvl);
////绑定编号id
//        PPrBase.NumPr numpr = new PPrBase.NumPr();
//        PPrBase.NumPr.NumId numid = new PPrBase.NumPr.NumId();
//        numid.setVal(BigInteger.valueOf(1));
//        numpr.setNumId(numid);
//        PPrBase.NumPr.Ilvl ilvl = new PPrBase.NumPr.Ilvl();
//        ilvl.setVal(BigInteger.valueOf(0));
//        numpr.setIlvl(ilvl);
//        ppr.setNumPr(numpr);
////将设置好的段落格式标签设置到样式标签中
//        style.setPPr(ppr);
////将设置好的段落标签加入到styles.xml文件中的styles标签下
//        try {
//            sdp.getContents().getStyle().add(style);
//        } catch (Docx4JException e) {
//            e.printStackTrace();
//        }
////创建一个样式标签
//        style = factory.createStyle();
////设置样式标签的type属性
//        style.setType("paragraph");
////新建name标签
//        name = new Style.Name();
////设置name标签的val属性
//        name.setVal("Heading 2");
////将设置好的name标签设置到样式标签中
//        style.setName(name);
////设置样式标签的id属性
//        style.setStyleId("Heading2");
////创建段落格式标签
//        ppr = factory.createPPr();
////创建大纲级别标签
//        lvl = new PPrBase.OutlineLvl();
////设置大纲级别标签的val属性为1
//        lvl.setVal(BigInteger.valueOf(1));
////将设置好的大纲级别标签设置到段落格式标签中
//        ppr.setOutlineLvl(lvl);
////绑定编号id
//        numpr = new PPrBase.NumPr();
//        numid = new PPrBase.NumPr.NumId();
//        numid.setVal(BigInteger.valueOf(1));
//        numpr.setNumId(numid);
//        ilvl = new PPrBase.NumPr.Ilvl();
//        ilvl.setVal(BigInteger.valueOf(1));
//        numpr.setIlvl(ilvl);
//        ppr.setNumPr(numpr);
////将设置好的段落格式标签设置到样式标签中
//        style.setPPr(ppr);
////将设置好的段落标签加入到styles.xml文件中的styles标签下
//        try {
//            sdp.getContents().getStyle().add(style);
//        } catch (Docx4JException e) {
//            e.printStackTrace();
//        }
//获得document.xml文件下body标签内容
        Body body = null;
        try {
            body = main.getContents().getBody();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
//创建段落标签
        P p = factory.createP();
//创建段落格式标签
        PPr pPr = factory.createPPr();
//创建段落样式标签
        PPrBase.PStyle ps = new PPrBase.PStyle();
//设置段落样式标签的val属性值为前面创建的样式id
        ps.setVal("Heading1");
//将设置好的段落样式标签设置到段落格式标签中
        pPr.setPStyle(ps);
//将设置好的段落格式标签设置到段落标签中
        p.setPPr(pPr);
//创建r标签
        R run = factory.createR();
//创建t标签
        Text t = factory.createText();
//设置t标签内的内容
        t.setValue("测试");
//将设置好的t标签设置到r标签中
        run.getContent().add(t);
//将设置好的r标签设置到段落标签中
        p.getContent().add(run);
//将设置好的段落标签加入body标签中
        body.getContent().add(p);
//创建段落标签
        p = factory.createP();
//创建段落格式标签
        pPr = factory.createPPr();
//创建段落样式标签
        ps = new PPrBase.PStyle();
//设置段落样式标签的val属性值为前面创建的样式id
        ps.setVal("Heading2");
//将设置好的段落样式标签设置到段落格式标签中
        pPr.setPStyle(ps);
//将设置好的段落格式标签设置到段落标签中
        p.setPPr(pPr);
//创建r标签
        run = factory.createR();
//创建t标签
        t = factory.createText();
//设置t标签内的内容
        t.setValue("test");
//将设置好的t标签设置到r标签中
        run.getContent().add(t);
//将设置好的r标签设置到段落标签中
        p.getContent().add(run);
//将设置好的段落标签加入body标签中
        body.getContent().add(p);
        //创建段落标签
        p = factory.createP();
//创建段落格式标签
        pPr = factory.createPPr();
//创建段落样式标签
        ps = new PPrBase.PStyle();
//设置段落样式标签的val属性值为前面创建的样式id
        ps.setVal("Heading3");
//将设置好的段落样式标签设置到段落格式标签中
        pPr.setPStyle(ps);
//将设置好的段落格式标签设置到段落标签中
        p.setPPr(pPr);
//创建r标签
        run = factory.createR();
//创建t标签
        t = factory.createText();
//设置t标签内的内容
        t.setValue("test3");
//将设置好的t标签设置到r标签中
        run.getContent().add(t);
//将设置好的r标签设置到段落标签中
        p.getContent().add(run);
//将设置好的段落标签加入body标签中
        body.getContent().add(p);
//设置word文档要存放的文件
        File file = new File(docName);
//将设置好的word包保存到指定文件中
        try {
            wordMLPackage.save(file);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
    }

    private static void createDocxWithTitle(String docName) {
        //新建word包
        WordprocessingMLPackage wordMLPackage = null;
        try {
            wordMLPackage = WordprocessingMLPackage.createPackage();
        } catch (InvalidFormatException e) {
            logger.error(e.getMessage());
        }
        //创建docx4j工厂
        ObjectFactory factory = Context.getWmlObjectFactory();
        //获得word包中document.xml文件内容
        MainDocumentPart main = wordMLPackage.getMainDocumentPart();
        //获得styles.xml文件内容
        StyleDefinitionsPart sdp = main.getStyleDefinitionsPart();
        //创建一个样式标签
        Style style = factory.createStyle();
        //设置样式标签的type属性
        style.setType("paragraph");
        //新建name标签
        Style.Name name = new Style.Name();
        //设置name标签的val属性
        name.setVal("Heading 1");
        //将设置好的name标签设置到样式标签中
        style.setName(name);
        //设置样式标签的id属性
        style.setStyleId("Heading1");
        //创建段落格式标签
        PPr ppr = factory.createPPr();
        //创建大纲级别标签
        PPrBase.OutlineLvl lvl = new PPrBase.OutlineLvl();
        //设置大纲级别标签的val属性为0
        lvl.setVal(BigInteger.valueOf(0));
        //将设置好的大纲级别标签设置到段落格式标签中
        ppr.setOutlineLvl(lvl);
        //将设置好的段落格式标签设置到样式标签中
        style.setPPr(ppr);
        //将设置好的段落标签加入到styles.xml文件中
        try {
            sdp.getContents().getStyle().add(style);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        //获得document.xml文件下body标签内容
        Body body = null;
        try {
            body = main.getContents().getBody();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        //创建段落标签
        P p = factory.createP();
        //创建段落格式标签
        PPr pPr = factory.createPPr();
        //创建段落样式标签
        PPrBase.PStyle ps = new PPrBase.PStyle();
        //设置段落样式标签的val属性值为前面创建的样式id
        ps.setVal("Heading1");
        //将设置好的段落样式标签设置到段落格式标签中
        pPr.setPStyle(ps);
        //将设置好的段落格式标签设置到段落标签中
        p.setPPr(pPr);
        //创建r标签
        R run = factory.createR();
        //创建t标签
        Text t = factory.createText();
        //设置t标签内的内容
        t.setValue("测试");
        //将设置好的t标签设置到r标签中
        run.getContent().add(t);
        //将设置好的r标签设置到段落标签中
        p.getContent().add(run);
        //将设置好的段落标签加入body标签中
        body.getContent().add(p);
        //设置word文档要存放的文件
        File file = new File(docName);
        //将设置好的word包保存到指定文件中
        try {
            wordMLPackage.save(file);
        } catch (Docx4JException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 创建一个简单的docx
     */
    private static void createDocx(String docName) {
        // Create the package
        WordprocessingMLPackage wordMLPackage;
        try {
            wordMLPackage = WordprocessingMLPackage.createPackage();
            // 另存为新的文件
            wordMLPackage.save(new File(docName));
        } catch (InvalidFormatException e) {
            logger.error("createDocx error:InvalidFormatException", e);
        } catch (Docx4JException e) {
            logger.error("createDocx error: Docx4JException", e);
        }
    }

    /**
     * 增加一个段落，增加完成记得保存，否则不生效
     */
    public static void addParagraph(String docName) {
        WordprocessingMLPackage wordprocessingMLPackage;
        try {
            wordprocessingMLPackage = WordprocessingMLPackage
                    .load(new File(docName));
            wordprocessingMLPackage.getMainDocumentPart().addParagraphOfText("Hello Word!");
            wordprocessingMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", "Hello Word!");
            wordprocessingMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle", " a subtitle!");
            wordprocessingMLPackage.save(new File(docName));
        } catch (Docx4JException e) {
            logger.error("addParagraph to docx error: Docx4JException", e);
        }
    }

    /**
     * 增加一个段落，增加完成记得保存，否则不生效
     */
    public static void addParagraph2(String docName, String simpleText) {

        try {
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage
                    .load(new File(docName));
            org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
            org.docx4j.wml.P para = factory.createP();
            if (simpleText != null) {
                org.docx4j.wml.Text t = factory.createText();
                t.setValue(simpleText);
                org.docx4j.wml.R run = factory.createR();
                run.getContent().add(t);
                para.getContent().add(run);
            }
            wordprocessingMLPackage.getMainDocumentPart().getContent().add(para);
            wordprocessingMLPackage.save(new File(docName));
        } catch (Exception e) {
            logger.error("addParagraph to docx error: Docx4JException", e);
        }
    }

    private static void readParagraph(String docName) {
        try {
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage
                    .load(new File(docName));

            String contentType = wordprocessingMLPackage.getContentType();
            logger.info("contentType -> {}", contentType);

            MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
            List<Object> content = mainDocumentPart.getContent();
            for (Object ob : content) {
                if (ob instanceof P) {
                    logger.info("ob -> {},this ob is title {}", ob.getClass(), ((P) ob).getPPr() == null ? false : ((P) ob).getPPr().getPStyle() != null);
                } else if (ob instanceof Tbl) {
                    logger.info("ob -> {},this ob is title {}", ob.getClass(), false);
                } else {
                    logger.info("ob -> {},this ob is title {}", ob.getClass(), false);
                }
            }
            JAXBContext jaxbContext = mainDocumentPart.getJAXBContext();
//            Docx4J.toPDF(docName.replace("docx","pdf"));
//            logger.info("jaxbContext -> {} {}", jaxbContext.getClass(), jaxbContext);
        } catch (Docx4JException e) {
            logger.error("createDocx error: Docx4JException", e);
        }
    }

    private static void trans2pdf(String docName) {
        try {
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage
                    .load(new File(docName));

            String contentType = wordprocessingMLPackage.getContentType();
            logger.info("contentType -> {}", contentType);

            MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
            List<Object> content = mainDocumentPart.getContent();
            for (Object ob : content) {
                logger.info("ob -> {} {}", ob.getClass(), ob);
            }
            JAXBContext jaxbContext = mainDocumentPart.getJAXBContext();
//            Docx4J.toPDF(docName.replace("docx","pdf"));
//            logger.info("jaxbContext -> {} {}", jaxbContext.getClass(), jaxbContext);
        } catch (Docx4JException e) {
            logger.error("createDocx error: Docx4JException", e);
        }
    }

    /**
     * 获取文档可操作对象
     *
     * @param docxPath 文档路径
     * @return
     */
    static WordprocessingMLPackage getWordprocessingMLPackage(String docxPath) {
        WordprocessingMLPackage wordMLPackage = null;
        if (StringUtils.isEmpty(docxPath)) {
            try {
                wordMLPackage = WordprocessingMLPackage.createPackage();
            } catch (InvalidFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        File file = new File(docxPath);
        if (file.isFile()) {
            try {
                wordMLPackage = WordprocessingMLPackage.load(file);
            } catch (Docx4JException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return wordMLPackage;
    }

    /**
     * 读取word文件，这里没有区分 word中的样式格式
     */
    public static void readParagraph2(String path) {
        try {
            WordprocessingMLPackage wordprocessingMLPackage = getWordprocessingMLPackage(path);

            String contentType = wordprocessingMLPackage.getContentType();
            logger.info("contentType:" + contentType);
            MainDocumentPart part = wordprocessingMLPackage.getMainDocumentPart();
            List<Object> list = part.getContent();
            System.out.println("content -> body -> " + part.getContents().getBody().toString());
            for (Object o : list) {
                logger.info("info:" + o);

            }
        } catch (Exception e) {

        }
    }

    /**
     * 读取word文件，这里没有区分 word中的样式格式
     */
    public static void readParagraph3(String path) {
        File doc = new File(path);
        WordprocessingMLPackage wordMLPackage = null;
        try {
            wordMLPackage = WordprocessingMLPackage
                    .load(doc);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        MainDocumentPart mainDocumentPart = wordMLPackage
                .getMainDocumentPart();
        String textNodesXPath = "//w:t";
        List<Object> textNodes = null;
        try {
            textNodes = mainDocumentPart
                    .getJAXBNodesViaXPath(textNodesXPath, true);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XPathBinderAssociationIsPartialException e) {
            e.printStackTrace();
        }
        for (Object obj : textNodes) {
            Text text = (Text) ((JAXBElement) obj).getValue();
            String textValue = text.getValue();
            System.out.println(textValue);
        }
    }

    /**
     * @Description:得到所有表格
     */
    public static List<Tbl> getAllTbl(WordprocessingMLPackage wordMLPackage) {
        MainDocumentPart mainDocPart = wordMLPackage.getMainDocumentPart();
        List<Object> objList = getAllElementFromObject(mainDocPart, Tbl.class);
        if (objList == null) {
            return null;
        }
        List<Tbl> tblList = new ArrayList<Tbl>();
        for (Object obj : objList) {
            if (obj instanceof Tbl) {
                Tbl tbl = (Tbl) obj;
                tblList.add(tbl);
            }
        }
        return tblList;
    }

    public static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<Object>();
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement<?>) obj).getValue();
        }
        if (obj.getClass().equals(toSearch)) {
            result.add(obj);
        } else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }
        }
        return result;
    }

}
