package org.bitbucket.ytimes.client.egais;

import com.mycila.xmltool.XMLDoc;
import com.mycila.xmltool.XMLTag;
import javafx.beans.property.ReadOnlySetProperty;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bitbucket.ytimes.client.egais.records.Response;
import org.bitbucket.ytimes.client.egais.records.TTNPositionRecord;
import org.bitbucket.ytimes.client.egais.records.TTNRecord;
import org.bitbucket.ytimes.client.egais.records.TicketResult;
import org.bitbucket.ytimes.client.utils.StringUtils;
import org.bitbucket.ytimes.client.utils.Utils;
import org.bitbucket.ytimes.client.kkm.services.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fsrar.wegais.actttnsingle_v3.AcceptType;
import ru.fsrar.wegais.actttnsingle_v3.WayBillActTypeV3;
import ru.fsrar.wegais.clientref_v2.OrgInfoRusV2;
import ru.fsrar.wegais.queryparameters.Parameter;
import ru.fsrar.wegais.queryparameters.QueryParameters;
import ru.fsrar.wegais.ticket.TicketType;
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

    @Autowired
    private XMLProcessor xmlProcessor;

    public List<TTNRecord> getAvailableTTNList(String requestId) throws EgaisException, MalformedURLException, JAXBException {
        logger.info("request ttn list");
        try {
            List<String> incomeDocList = StringUtils.isEmpty(requestId) ? getIncomeDocList() : getIncomeDocList(requestId);
            return getTTNListByUrlList(incomeDocList);
        }
        catch (Exception e) {
            throw new EgaisException(e);
        }
    }

    private List<TTNRecord> getTTNListByUrlList(List<String> incomeDocList) throws EgaisException, JAXBException, MalformedURLException {
        Map<String, TTNRecord> ttnList = new LinkedHashMap<String, TTNRecord>();
        for(String link: incomeDocList) {
            if (link.contains("WayBill")) {
                TTNRecord ttn = xmlProcessor.getTtnRecord(link);
                ttn.wayBillLink = link;
                ttnList.put(ttn.id, ttn);
            }
        }

        for(String link: incomeDocList) {
            if (link.contains("FORM2REGINFO")) {
                Map<String, String> res = xmlProcessor.getTTNId(link);
                String identity = res.keySet().iterator().next();
                ttnList.get(identity).number = res.get(identity);
                ttnList.get(identity).form2RegInfoLink = link;
            }
        }
        return new ArrayList<TTNRecord>(ttnList.values());
    }

    public String sendNotAnswerTTNRequest() throws EgaisException, Exception {
        Parameter parameter = new Parameter();
        parameter.setName("КОД");
        parameter.setValue(configService.getValue("egaisFSRARID"));

        QueryParameters.Parameters parameters = new QueryParameters.Parameters();
        parameters.getParameter().add(parameter);

        QueryParameters queryNATTN = new QueryParameters();
        queryNATTN.getParameters().add(parameters);

        DocBody docBody = new DocBody();
        docBody.setQueryNATTN(queryNATTN);
        return sendXML(docBody, "opt/in/QueryNATTN");
    }

    public Response<List<String>> loadNotAnswerTTNResponse(String requestId) throws EgaisException, MalformedURLException, JAXBException {
        logger.info("loadNotAnswerTTNResponse: " + requestId);
        Response<List<String>> res = new Response<List<String>>();

        List<String> incomeDocList = getIncomeDocList(requestId);
        if (incomeDocList.isEmpty()) {
            res.completed = false;
            return res;
        }

        for(String docName: incomeDocList) {
            if (docName.contains("ReplyNATTN")) {
                res.data = xmlProcessor.getNotAnswerTTNList(docName);
            }
        }
        return res;
    }

    public String requestTtnById(String ttnId) throws EgaisException, Exception {
        Parameter parameter = new Parameter();
        parameter.setName("WBREGID");
        parameter.setValue(ttnId);

        QueryParameters.Parameters parameters = new QueryParameters.Parameters();
        parameters.getParameter().add(parameter);

        QueryParameters query = new QueryParameters();
        query.getParameters().add(parameters);

        DocBody docBody = new DocBody();
        docBody.setQueryResendDoc(query);
        return sendXML(docBody, "opt/in/QueryResendDoc");
    }

    public Response<List<TTNRecord>> ttnByIdResponse(String requestId) throws EgaisException, MalformedURLException, JAXBException {
        logger.info("loadNotAnswerTTNResponse: " + requestId);
        Response<List<TTNRecord>> res = new Response<List<TTNRecord>>();

        List<String> incomeDocList = getIncomeDocList(requestId);
        if (incomeDocList.isEmpty()) {
            res.completed = false;
            return res;
        }

        res.data = getTTNListByUrlList(incomeDocList);
        return res;
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

    private List<String> getIncomeDocList(String requestId) throws MalformedURLException {
        List<String> res = new ArrayList<String>();
        URL docList = new URL(getUrl("opt/out"));
        XMLTag xml = XMLDoc.from(docList, true);
        for(XMLTag tag: xml.getChilds()) {
            if (!tag.hasAttribute("replyId")) {
                continue;
            }
            String replyId = tag.getAttribute("replyId");
            if (requestId.equals(replyId)) {
                String addr = changeURL(tag.getInnerText());
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
//            return "http://static.nyc.local:81/egaistest/out.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/WayBill_v3/2")) {
//            return "http://static.nyc.local:81/egaistest/WayBill_v3_2.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/TTNHISTORYF2REG/3")) {
//            return "http://static.nyc.local:81/egaistest/TTNHISTORYF2REG_3.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/FORM2REGINFO/4")) {
//            return "http://static.nyc.local:81/egaistest/FORM2REGINFO_4.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/FORM2REGINFO/5")) {
//            return "http://static.nyc.local:81/egaistest/FORM2REGINFO_5.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/WayBill_v3/6")) {
//            return "http://static.nyc.local:81/egaistest/WayBill_v3_6.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/TTNHISTORYF2REG/7")) {
//            return "http://static.nyc.local:81/egaistest/TTNHISTORYF2REG_7.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/in/WayBillAct_v3")) {
//            return "http://static.nyc.local:81/egaistest/WayBillAct_v3_response.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/in/QueryNATTN")) {
//            return "http://static.nyc.local:81/egaistest/QueryNATTN_response.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/Ticket/215")) {
//            return "http://static.nyc.local:81/egaistest/Ticket215.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/Ticket/216")) {
//            return "http://static.nyc.local:81/egaistest/Ticket216.xml";
//        }
//        if (address.equals("http://localhost:8080/opt/out/ReplyNATTN/213")) {
//            return "http://static.nyc.local:81/egaistest/ReplyNATTN.xml";
//        }

        return address;
    }

    public String rejectTTN(TTNRecord record) throws EgaisException, Exception {
        logger.info("REJECT");
        return sendWayBillAct(record, AcceptType.REJECTED, record.actNumber, null);
    }


    public String acceptTTN(TTNRecord record) throws EgaisException, Exception {
        logger.info("ACCEPT");
        return sendWayBillAct(record, AcceptType.ACCEPTED, record.actNumber, null);
    }

    public String acceptPartialTTN(TTNRecord record) throws EgaisException, Exception {
        List<ru.fsrar.wegais.actttnsingle_v3.PositionType> types = new ArrayList<ru.fsrar.wegais.actttnsingle_v3.PositionType>();

        for(TTNPositionRecord posRecord: record.itemList) {
            ru.fsrar.wegais.actttnsingle_v3.PositionType position = new ru.fsrar.wegais.actttnsingle_v3.PositionType();
            position.setInformF2RegId(posRecord.F2RegId);
            position.setIdentity(posRecord.identity);
            position.setRealQuantity(new BigDecimal(posRecord.actualQuantity));
            types.add(position);
        }
        logger.info("ACCEPTPARTIAL");
        return sendWayBillAct(record, AcceptType.ACCEPTED, record.actNumber, types);
    }

    private String sendWayBillAct(TTNRecord record, ru.fsrar.wegais.actttnsingle_v3.AcceptType isAccept, String actNumber,
                                  List<ru.fsrar.wegais.actttnsingle_v3.PositionType> positionTypes) throws EgaisException, Exception {
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

        return sendXML(docBody, "opt/in/WayBillAct_v3");
    }

    private String sendXML(DocBody docBody, String url) throws JAXBException, IOException, EgaisException {
        SenderInfo owner = new SenderInfo();
        owner.setFSRARID(configService.getValue("egaisFSRARID"));

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
        return sendXMLToEgais(xml, url);
    }

    private String sendXMLToEgais(String xml, String url) throws IOException, EgaisException {
        logger.info("Send xml to: " + url);
        logger.info(xml);

        url = getUrl(url);
        XMLTag xmlResp = null;
        if (url.contains("egaistest")) {
            xmlResp = XMLDoc.from(new URL(url), true);
        }
        else {
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
            xmlResp = XMLDoc.from(content, true);
        }

        for(XMLTag tag: xmlResp.getChilds()) {
            if ("url".equals(tag.getCurrentTagName())) {
                return tag.getInnerText();
            }
        }

        throw new EgaisException("Неизвестный формат ответа от ЕГАИС");
    }

    public Response<TicketResult> getTTNActResult(String requestGuid) throws EgaisException {
        logger.info("request ttn act result");
        try {
            Response<TicketResult> resp = new Response<TicketResult>();
            List<String> incomeDocList = getIncomeDocList(requestGuid);
            if (incomeDocList.isEmpty()) {
                resp.completed = false;
                return resp;
            }

            for(String url: incomeDocList) {
                if (url.contains("Ticket")) {
                    TicketType ticket = getTicket(url);
                    if ("WayBillAct_v3".equals(ticket.getDocType())) {
                        TicketResult res = new TicketResult();
                        res.docType = ticket.getDocType();
                        res.resultStatus = ticket.getResult().getConclusion().value();
                        res.resultComment = ticket.getResult().getComments();
                        resp.data = res;
                    }
                }
            }

            return resp;
        } catch (Exception e) {
            throw new EgaisException(e);
        }
    }

    public Response<TicketResult> getTTNResult(String requestGuid) throws EgaisException {
        logger.info("request ttn result");
        try {
            Response<TicketResult> resp = new Response<TicketResult>();
            List<String> incomeDocList = getIncomeDocList(requestGuid);
            if (incomeDocList.isEmpty()) {
                resp.completed = false;
                return resp;
            }

            for(String url: incomeDocList) {
                if (url.contains("Ticket")) {
                    TicketType ticket = getTicket(url);
                    if ("WAYBILL".equals(ticket.getDocType())) {
                        TicketResult res = new TicketResult();
                        res.docType = ticket.getDocType();
                        res.resultStatus = ticket.getOperationResult().getOperationResult().value();
                        res.resultComment = ticket.getOperationResult().getOperationComment();
                        resp.data = res;
                    }
                }
            }

            return resp;
        } catch (Exception e) {
            throw new EgaisException(e);
        }
    }

    private TicketType getTicket(String url) throws JAXBException, MalformedURLException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Documents.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Documents documents = (Documents) jaxbUnmarshaller.unmarshal(new URL(url));
        return documents.getDocument().getTicket();
    }


}
