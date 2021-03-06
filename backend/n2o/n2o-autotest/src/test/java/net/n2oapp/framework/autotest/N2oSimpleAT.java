package net.n2oapp.framework.autotest;

import net.n2oapp.framework.autotest.impl.component.page.N2oSimplePage;
import net.n2oapp.framework.autotest.impl.component.widget.N2oFormWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oHeaderPack;
import net.n2oapp.framework.config.metadata.pack.N2oPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oWidgetsPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class N2oSimpleAT extends AutoTestBase {

    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oHeaderPack(), new N2oPagesPack(), new N2oWidgetsPack());
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/simple/test.header.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/simple/index.page.xml"));
    }

    @Test
    public void test() {
        N2oSimplePage page = N2oSelenide.open("http://localhost:" + port, N2oSimplePage.class);
        page.shouldExists();
        page.single().shouldHaveSize(1);
        page.single().widget(N2oFormWidget.class).shouldExists();
    }
}
