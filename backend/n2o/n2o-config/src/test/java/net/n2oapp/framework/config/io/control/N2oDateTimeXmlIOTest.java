package net.n2oapp.framework.config.io.control;

import net.n2oapp.context.CacheTemplateByMapMock;
import net.n2oapp.context.StaticSpringContext;
import net.n2oapp.framework.api.metadata.control.plain.N2oDatePicker;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oFieldSet;
import net.n2oapp.framework.config.reader.control.N2oStandardControlReaderTestBase;
import net.n2oapp.framework.config.selective.ION2oMetadataTester;
import net.n2oapp.framework.config.selective.persister.SelectiveStandardPersister;
import net.n2oapp.framework.config.selective.reader.SelectiveStandardReader;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.mock;

/**
 * Тестирование чтения и записи контрола DateTime из/в xml-файла
 */
public class N2oDateTimeXmlIOTest extends N2oStandardControlReaderTestBase {

    @Before
    public void setUp() throws Exception {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        StaticSpringContext staticSpringContext = new StaticSpringContext();
        staticSpringContext.setApplicationContext(applicationContext);
        staticSpringContext.setCacheTemplate(new CacheTemplateByMapMock());
    }

    @Test
    public void testDateTimeXmlIO(){
        ION2oMetadataTester tester = new ION2oMetadataTester()
                .addReader(new SelectiveStandardReader().addFieldSet4Reader().addEventsReader())
                .addPersister(new SelectiveStandardPersister().addFieldsetPersister());

        assert tester.check("net/n2oapp/framework/config/io/control/testDateTimeReader.fieldset.xml",
                (N2oFieldSet fieldSet) -> {
                    assertCountField(fieldSet, 1);
                    N2oDatePicker datePicker = (N2oDatePicker) fieldSet.getItems()[0];
                    assertStandardAttribute(datePicker);
                    assert datePicker.getDefaultTime().equals("00:00");
                    assert datePicker.getDateFormat().equals("DD.MM.YYYY");
                    assert datePicker.getDefaultValue().equals("test");
                    assert datePicker.getMax().equals("today()");
                    assert datePicker.getMin().equals("01.03.2018");
                    assert datePicker.getUtc().equals(true);
                });
    }
}
