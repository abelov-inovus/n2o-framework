package net.n2oapp.framework.autotest.page;

import net.n2oapp.framework.autotest.api.component.page.LeftRightPage;
import net.n2oapp.framework.autotest.api.component.region.TabsRegion;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oHeaderPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TabsRegionAT extends AutoTestBase {
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
        builder.packs(new N2oAllPagesPack(), new N2oHeaderPack());
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/region/tabs/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));
    }

    @Test
    public void testTabsRegion() {
        LeftRightPage page = open(LeftRightPage.class);
        page.shouldExists();
        TabsRegion tabs = page.left().region(0, TabsRegion.class);
        tabs.shouldHaveSize(3);
        tabs.tab(0).shouldBeActive();
        tabs.tab(1).shouldNotBeActive();
        tabs.tab(2).shouldNotBeActive();
        tabs.tab(0).shouldHaveText("tab1");
        tabs.tab(1).shouldHaveText("customName");
        tabs.tab(2).shouldHaveText("tab3");

        tabs.tab(1).click();
        tabs.tab(0).shouldNotBeActive();
        tabs.tab(1).shouldBeActive();
        tabs.tab(2).shouldNotBeActive();

        tabs.tab(2).click();

        tabs.tab(0).shouldNotBeActive();
        tabs.tab(1).shouldNotBeActive();
        tabs.tab(2).shouldBeActive();

        TabsRegion singleTab = page.right().region(0, TabsRegion.class);
        singleTab.shouldHaveSize(1);
        singleTab.tab(0).shouldBeActive();
        singleTab.tab(0).shouldHaveText("tab4");
    }

}
