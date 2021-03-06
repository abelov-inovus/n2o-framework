package net.n2oapp.framework.config.metadata.compile.page;

import net.n2oapp.framework.api.metadata.meta.page.StandardPage;
import net.n2oapp.framework.api.metadata.meta.region.*;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Тестирование компиляции страницы с левыми и правыми регионами
 */
public class LeftRightPageCompileTest extends SourceCompileTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllPagesPack(), new N2oAllDataPack())
                .sources(new CompileInfo("net/n2oapp/framework/config/metadata/compile/object/utObjectField.object.xml"));
    }

    @Test
    public void leftRightPage() {
        StandardPage page = (StandardPage) compile("net/n2oapp/framework/config/metadata/compile/page/testLeftRightPage.page.xml")
                .get(new PageContext("testLeftRightPage"));

        assertThat(page.getWidth().getLeft(), is("70%"));
        assertThat(page.getWidth().getRight(), nullValue());

        assertThat(page.getRegions().size(), is(2));
        List<Region> left = page.getRegions().get("left");
        List<Region> right = page.getRegions().get("right");

        assertThat(left.size(), is(1));
        assertThat(left.get(0), instanceOf(LineRegion.class));
        assertThat(right.size(), is(3));
        assertThat(right.get(0), instanceOf(PanelRegion.class));
        assertThat(right.get(1), instanceOf(TabsRegion.class));
        assertThat(right.get(2), instanceOf(CustomRegion.class));
    }
}
