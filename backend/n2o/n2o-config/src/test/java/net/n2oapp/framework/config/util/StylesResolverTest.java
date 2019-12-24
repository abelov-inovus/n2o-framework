package net.n2oapp.framework.config.util;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

public class StylesResolverTest {

    @Test
    public void test() {
        Map<String, String> result = StylesResolver.resolveStyles("box-shadow: 0 0 1px #fff,0 0 5px rgba(0,0,0,0.3);    padding: 3px 5px");

        assertThat(result.size(), is(2));
        assertThat(result.get("boxShadow"), is("0 0 1px #fff,0 0 5px rgba(0,0,0,0.3)"));
        assertThat(result.get("padding"), is("3px 5px"));

        assertThat(StylesResolver.resolveStyles(" "), nullValue());
        assertThat(StylesResolver.resolveStyles(null), nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidInputTest() {
        StylesResolver.resolveStyles("box-shadow: illegal : argument;");
    }

}
