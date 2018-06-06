package org.bitbucket.ytimes.client.egais;

import com.mycila.xmltool.XMLDoc;
import com.mycila.xmltool.XMLTag;
import org.bitbucket.ytimes.client.egais.records.TTNPositionRecord;
import org.bitbucket.ytimes.client.egais.records.TTNRecord;
import org.bitbucket.ytimes.client.kkm.Utils;
import org.bitbucket.ytimes.client.kkm.services.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fsrar.wegais.clientref_v2.OrgInfoRusV2;
import ru.fsrar.wegais.ttninformf2reg.WayBillInformF2RegType;
import ru.fsrar.wegais.ttnsingle_v3.PositionType;
import ru.fsrar.wegais.ttnsingle_v3.WayBillTypeV3;
import ru.fsrar.wegais.wb_doc_single_01.Documents;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.MalformedURLException;
import java.net.URL;
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
        String url = configService.getValue("egaisUTMAddress", "http://localhost:8080/");
        address = url + address;
        return changeURL(address);
    }

    private String changeURL(String address) {
        if (address.equals("http://localhost:8080/opt/out")) {
            return "http://static.nyc.local:81/egais/out.xml";
        }
        if (address.equals("http://localhost:8080/opt/out/WayBill_v3/2")) {
            return "http://static.nyc.local:81/egais/WayBill_v3_2.xml";
        }
        if (address.equals("http://localhost:8080/opt/out/TTNHISTORYF2REG/3")) {
            return "http://static.nyc.local:81/egais/TTNHISTORYF2REG_3.xml";
        }
        if (address.equals("http://localhost:8080/opt/out/FORM2REGINFO/4")) {
            return "http://static.nyc.local:81/egais/FORM2REGINFO_4.xml";
        }
        if (address.equals("http://localhost:8080/opt/out/FORM2REGINFO/5")) {
            return "http://static.nyc.local:81/egais/FORM2REGINFO_5.xml";
        }
        if (address.equals("http://localhost:8080/opt/out/WayBill_v3/6")) {
            return "http://static.nyc.local:81/egais/WayBill_v3_6.xml";
        }
        if (address.equals("http://localhost:8080/opt/out/TTNHISTORYF2REG/7")) {
            return "http://static.nyc.local:81/egais/TTNHISTORYF2REG_7.xml";
        }
        return address;
    }

}
