package XmlDataExtractor;

import generated.STLSheet;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;

public class XmlDataExtractor {

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "generated";

    public static STLSheet extractSTLSheet(String filePath) throws JAXBException, FileNotFoundException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException(STR."File not found: \{filePath}");
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        return (STLSheet) unmarshaller.unmarshal(file);
    }
}