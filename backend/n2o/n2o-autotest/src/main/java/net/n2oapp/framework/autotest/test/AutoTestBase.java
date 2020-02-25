package net.n2oapp.framework.autotest.test;

import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.test.N2oTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;

import static com.codeborne.selenide.Configuration.headless;

/**
 * Базовый класс для автотестов
 */
@SpringBootTest(classes = AutoTestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AutoTestBase extends N2oTestBase {
    @LocalServerPort
    protected int port;

    private ApplicationContext context;

    public static void configureSelenide() {
        System.setProperty("chromeoptions.args", "--no-sandbox,--verbose,--whitelisted-ips=''");
        headless = true;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context.getBean(N2oController.class).setBuilder(builder);
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}
