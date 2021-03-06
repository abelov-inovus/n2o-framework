package net.n2oapp.framework.autotest.control;

import net.n2oapp.framework.autotest.api.component.control.AutoComplete;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Автотест для компонента ввода текста с автозаполнением
 */
public class AutoCompleteAT extends AutoTestBase {

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
        builder.packs(new N2oPagesPack(), new N2oHeaderPack(), new N2oWidgetsPack(), new N2oFieldSetsPack(), new N2oControlsPack());
    }

    @Test
    public void testAutoComplete() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/control/auto_complete/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));

        SimplePage page = open(SimplePage.class);
        page.shouldExists();

        AutoComplete autoComplete = page.single().widget(FormWidget.class).fields().field("AutoComplete1")
                .control(AutoComplete.class);
        autoComplete.shouldExists();

        autoComplete.shouldBeEmpty();
        autoComplete.val("c");
        autoComplete.shouldHaveValue("c");
        autoComplete.shouldHaveDropdownOptions("abc", "ccc");
        autoComplete.chooseDropdownOption("ccc");
        autoComplete.shouldHaveValue("ccc");
        autoComplete.val("ab");
        autoComplete.shouldHaveDropdownOptions("abc");
        autoComplete.chooseDropdownOption("abc");
        autoComplete.shouldHaveValue("abc");
        autoComplete.val("d");
        autoComplete.shouldNotHaveDropdownOptions();
    }
}
