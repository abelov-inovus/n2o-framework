package net.n2oapp.framework.config.metadata.validation;

import net.n2oapp.framework.api.metadata.validation.exception.N2oMetadataValidationException;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oRegionsPack;
import net.n2oapp.framework.config.metadata.pack.N2oWidgetsPack;
import net.n2oapp.framework.config.metadata.validation.standard.page.PageValidator;
import net.n2oapp.framework.config.metadata.validation.standard.page.StandardPageValidator;
import net.n2oapp.framework.config.test.SourceValidationTestBase;
import org.junit.Before;
import org.junit.Test;

public class PageValidatorTest extends SourceValidationTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oPagesPack(), new N2oRegionsPack(), new N2oWidgetsPack());
        builder.validators(new PageValidator(), new StandardPageValidator());
    }

    @Test(expected = N2oMetadataValidationException.class)
    public void testObjectNotExists() {
        validate("net/n2oapp/framework/config/metadata/validation/page/testObjectNotExists.page.xml");
    }

    @Test(expected = N2oMetadataValidationException.class)
    public void testDependsWidgetFind() {
        validate("net/n2oapp/framework/config/metadata/validation/page/testDependsWidgetFind.page.xml");
    }

    @Test(expected = N2oMetadataValidationException.class)
    public void testObjectNotExistsOnSimplePage() {
        validate("net/n2oapp/framework/config/metadata/validation/page/testObjectNotExistsOnSimplePage.page.xml");
    }
}
