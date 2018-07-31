package org.bitbucket.ytimes.client.egais;

import org.bitbucket.ytimes.client.egais.records.TTNPositionRecord;
import org.bitbucket.ytimes.client.egais.records.TTNRecord;
import org.bitbucket.ytimes.client.utils.Utils;
import org.springframework.stereotype.Component;
import ru.fsrar.wegais.clientref_v2.OrgInfoRusV2;
import ru.fsrar.wegais.replynoanswerttn.NoAnswerType;
import ru.fsrar.wegais.ttninformf2reg.WayBillInformF2RegType;
import ru.fsrar.wegais.ttnsingle_v3.PositionType;
import ru.fsrar.wegais.ttnsingle_v3.WayBillTypeV3;
import ru.fsrar.wegais.wb_doc_single_01.DocBody;
import ru.fsrar.wegais.wb_doc_single_01.Documents;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class XMLProcessor {

    public TTNRecord getTtnRecord(String link) throws EgaisException, JAXBException, MalformedURLException {
        DocBody docBody = getDocBody(link);
        if (docBody.getWayBillV3() == null) {
            throw new EgaisException("Неверный формат ответа ЕГАИС (WayBillV3)");
        }

        WayBillTypeV3 wayBill = docBody.getWayBillV3();
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

    public List<String> getNotAnswerTTNList(String url) throws EgaisException, JAXBException, MalformedURLException {
        DocBody docBody = getDocBody(url);
        if (docBody.getReplyNoAnswerTTN() == null) {
            throw new EgaisException("Неверный формат ответа ЕГАИС (ReplyNoAnswerTTN)");
        }

        List<String> result = new ArrayList<String>();
        for(NoAnswerType naType: docBody.getReplyNoAnswerTTN().getTtnlist().getNoAnswer()) {
            result.add(naType.getTtnNumber());
        }
        return result;
    }

    private DocBody getDocBody(String url) throws JAXBException, MalformedURLException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Documents.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Documents documents = (Documents) jaxbUnmarshaller.unmarshal(new URL(url));
        return documents.getDocument();
    }

    public Map<String, String> getTTNId(String url) throws EgaisException, JAXBException, MalformedURLException {
        DocBody docBody = getDocBody(url);
        if (docBody.getTTNInformF2Reg() == null) {
            throw new EgaisException("Неверный формат ответа ЕГАИС (TTNInformF2Reg)");
        }

        WayBillInformF2RegType ttnInformF2Reg = docBody.getTTNInformF2Reg();
        String identity = ttnInformF2Reg.getHeader().getIdentity();
        String number = ttnInformF2Reg.getHeader().getWBRegId();
        Map<String, String> res = new HashMap<String, String>();
        res.put(identity, number);
        return res;
    }

}
