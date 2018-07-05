package org.bitbucket.ytimes.client.egais;

import com.mycila.xmltool.XMLDoc;
import com.mycila.xmltool.XMLTag;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bitbucket.ytimes.client.egais.records.TTNPositionRecord;
import org.bitbucket.ytimes.client.egais.records.TTNRecord;
import org.bitbucket.ytimes.client.main.Utils;
import org.bitbucket.ytimes.client.kkm.services.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fsrar.wegais.actttnsingle_v3.AcceptType;
import ru.fsrar.wegais.actttnsingle_v3.WayBillActTypeV3;
import ru.fsrar.wegais.clientref_v2.OrgInfoRusV2;
import ru.fsrar.wegais.ttninformf2reg.WayBillInformF2RegType;
import ru.fsrar.wegais.ttnsingle_v3.PositionType;
import ru.fsrar.wegais.ttnsingle_v3.WayBillTypeV3;
import ru.fsrar.wegais.wb_doc_single_01.DocBody;
import ru.fsrar.wegais.wb_doc_single_01.Documents;
import ru.fsrar.wegais.wb_doc_single_01.SenderInfo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EGAISProcessor {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ConfigService configService;

    public List<TTNRecord> getAvailableTTNList() throws EgaisException {
        logger.info("request ttn list");
        try {
            List<String> incomeDocList = getIncomeDocList();

            Map<String, TTNRecord> ttnList = new LinkedHashMap<String, TTNRecord>();
            for(String link: incomeDocList) {
                if (link.contains("WayBill")) {
                    TTNRecord ttn = getTtnRecord(link);
                    ttn.wayBillLink = link;
                    ttnList.put(ttn.id, ttn);
                }
            }

            for(String link: incomeDocList) {
                if (link.contains("FORM2REGINFO")) {
                    WayBillInformF2RegType wayBillInform = getWayBillInform(link);
                    String identity = wayBillInform.getHeader().getIdentity();
                    ttnList.get(identity).number = wayBillInform.getHeader().getWBRegId();
                    ttnList.get(identity).form2RegInfoLink = link;
                }
            }
            return new ArrayList<TTNRecord>(ttnList.values());
        }
        catch (Exception e) {
            throw new EgaisException(e);
        }
    }

    private TTNRecord getTtnRecord(String link) throws JAXBException, MalformedURLException {
        WayBillTypeV3 wayBill = getWayBill(link);
        TTNRecord ttn = new TTNRecord();
        ttn.id = wayBill.getIdentity();
        ttn.date = Utils.toDateString(wayBill.getHeader().getDate().toGregorianCalendar().getTime());

        OrgInfoRusV2 shipper = wayBill.getHeader().getShipper();
        if (shipper != null && shipper.getUL() != null) {
            ttn.supplierFullName = shipper.getUL().getFullName();
            ttn.supplierShortName = shipper.getUL().getShortName();
            ttn.supplierINN = shipper.getUL().getINN();
            ttn.supplierKPP = shipper.getUL().getKPP();
        }
        ttn.itemList = new ArrayList();

        for(PositionType positionType: wayBill.getContent().getPosition()) {
            TTNPositionRecord record = new TTNPositionRecord();
            record.alcCode = positionType.getProduct().getAlcCode();
            record.alcVolume = positionType.getProduct().getAlcVolume().doubleValue();
            record.shortName = positionType.getProduct().getShortName();
            record.fullName = positionType.getProduct().getFullName();
            record.price = positionType.getPrice().doubleValue();
            record.quantity = positionType.getQuantity().intValue();
            record.identity = positionType.getIdentity();
            record.FARegId = positionType.getFARegId();
            record.F2RegId = positionType.getInformF2().getF2RegId();
            ttn.itemList.add(record);
        }
        return ttn;
    }

    private WayBillTypeV3 getWayBill(String url) throws JAXBException, MalformedURLException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Documents.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Documents documents = (Documents) jaxbUnmarshaller.unmarshal(new URL(url));
        return documents.getDocument().getWayBillV3();
    }

    private WayBillInformF2RegType getWayBillInform(String url) throws JAXBException, MalformedURLException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Documents.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Documents documents = (Documents) jaxbUnmarshaller.unmarshal(new URL(url));
        return documents.getDocument().getTTNInformF2Reg();
    }

    private List<String> getIncomeDocList() throws MalformedURLException {
        List<String> res = new ArrayList<String>();
        URL docList = new URL(getUrl("opt/out"));
        XMLTag xml = XMLDoc.from(docList, true);
        for(XMLTag tag: xml.getChilds()) {
            String addr = tag.getInnerText();
            if (addr.contains("WayBill") || addr.contains("FORM2REGINFO")) {
                addr = changeURL(addr);
                res.add(addr);
            }
        }
        return res;
    }

    private String getUrl(String address) {
        String url = configService.getValue("egaisUTMAddress");
        address = url + address;
        return changeURL(address);
    }

    private String changeURL(String address) {
//        if (address.equals("http://localhost:8080/opt/out")) {
//            return "http://static.nyc.local:81/egais/out.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/WayBill_v3/2")) {
//            return "http://static.nyc.local:81/egais/WayBill_v3_2.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/TTNHISTORYF2REG/3")) {
//            return "http://static.nyc.local:81/egais/TTNHISTORYF2REG_3.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/FORM2REGINFO/4")) {
//            return "http://static.nyc.local:81/egais/FORM2REGINFO_4.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/FORM2REGINFO/5")) {
//            return "http://static.nyc.local:81/egais/FORM2REGINFO_5.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/WayBill_v3/6")) {
//            return "http://static.nyc.local:81/egais/WayBill_v3_6.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/TTNHISTORYF2REG/7")) {
//            return "http://static.nyc.local:81/egais/TTNHISTORYF2REG_7.xml";
//        }
        return address;
    }

    public void rejectTTN(TTNRecord record) throws EgaisException {
        sendWayBillAct(record, AcceptType.REJECTED, "1-1", null);
        logger.info("REJECT");
    }


    public void acceptTTN(TTNRecord record) throws EgaisException {
        sendWayBillAct(record, AcceptType.ACCEPTED, "1-1", null);
        logger.info("ACCEPT");
    }

    public void acceptPartialTTN(TTNRecord record) throws EgaisException {
        List<ru.fsrar.wegais.actttnsingle_v3.PositionType> types = new ArrayList<ru.fsrar.wegais.actttnsingle_v3.PositionType>();

        for(TTNPositionRecord posRecord: record.itemList) {
            ru.fsrar.wegais.actttnsingle_v3.PositionType position = new ru.fsrar.wegais.actttnsingle_v3.PositionType();
            position.setInformF2RegId(posRecord.F2RegId);
            position.setIdentity(posRecord.identity);
            position.setRealQuantity(new BigDecimal(posRecord.actualQuantity));
            types.add(position);
        }
        sendWayBillAct(record, AcceptType.ACCEPTED, "1-1", types);
        logger.info("ACCEPTPARTIAL");
    }

    private void sendWayBillAct(TTNRecord record, ru.fsrar.wegais.actttnsingle_v3.AcceptType isAccept, String actNumber, List<ru.fsrar.wegais.actttnsingle_v3.PositionType> positionTypes) throws EgaisException {
        try {
            XMLGregorianCalendar curDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            WayBillActTypeV3.Header header = new WayBillActTypeV3.Header();
            header.setIsAccept(isAccept);
            header.setACTNUMBER(actNumber);
            header.setActDate(curDate);
            header.setWBRegId(record.number);
            header.setNote(record.actNote);

            WayBillActTypeV3.Content content = new WayBillActTypeV3.Content();
            if (positionTypes != null) {
                content.getPosition().addAll(positionTypes);
            }

            WayBillActTypeV3 act = new WayBillActTypeV3();
            act.setHeader(header);
            act.setContent(content);

            DocBody docBody = new DocBody();
            docBody.setWayBillActV3(act);

            SenderInfo owner = new SenderInfo();
            owner.setFSRARID(configService.getValue("egaisFSRARID"));

            sendXML(docBody, owner);
        }
        catch (EgaisException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new EgaisException(e);
        }
    }

    private void sendXML(DocBody docBody, SenderInfo owner) throws JAXBException, IOException, EgaisException {
        Documents documents = new Documents();
        documents.setDocument(docBody);
        documents.setOwner(owner);

        JAXBContext jaxbContext = JAXBContext.newInstance(Documents.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(documents, sw);

        String xml = sw.toString();
        sendXMLToEgais(xml, "opt/in/WayBillAct_v3");
    }

    private void sendXMLToEgais(String xml, String url) throws IOException, EgaisException {
        logger.info("Send xml to: " + url);
        logger.info(xml);

        url = getUrl(url);

        HttpEntity entity = MultipartEntityBuilder
                .create()
                .addTextBody("xml_file", xml)
                .build();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(httpPost);

        int status = response.getStatusLine().getStatusCode();
        String content = EntityUtils.toString(response.getEntity());

        logger.info("Return code: " + status);
        logger.info("Response: " + content);
        if (status != 200) {
            throw new EgaisException("Отправка в ЕГАИС завершилась с ошибкой: " + status);
        }
    }

}
