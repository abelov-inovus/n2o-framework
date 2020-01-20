package net.n2oapp.demo.model;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;

import java.util.List;

import static com.codeborne.selenide.Selenide.page;

/**
 * Тесты ProtoPage
 */
public class ProtoPage implements ProtoPageSelectors {

    /**
     * Проверка правильности селекторов
     */
    public ProtoPage checkAllElementsExists() {
        getMainTableHead().shouldBe(Condition.exist);
        getMainTableRows().shouldBe(CollectionCondition.sizeGreaterThan(0));
        getMainTableFilter().shouldBe(Condition.exist);

        getTableHeaderSurname().shouldBe(Condition.exist);
        getFilterGenderMale().shouldBe(Condition.exist);
        getFilterGenderFemale().shouldBe(Condition.exist);
        getFilterGenderUnknown().shouldBe(Condition.exist);
        getFilterSearchButton().shouldBe(Condition.exist);
        return page(ProtoPage.class);
    }

    /**
     * Проверка работы фильтра по полу
     */
    public ProtoPage assertGender() {
        getFilterGenderFemale().click();
        getFilterSearchButton().click();
        assert isAllMatch(getCol(getMainTableRows(), 4), "Женский");

        getFilterGenderFemale().click();
        getFilterGenderMale().click();
        getFilterSearchButton().click();
        assert isAllMatch(getCol(getMainTableRows(), 4), "Мужской");

        getFilterGenderMale().click();
        getFilterGenderUnknown().click();
        getFilterSearchButton().click();
        assert getCol(getMainTableRows(), 4).isEmpty();

        return page(ProtoPage.class);
    }

    /**
     * Проверка работы сортировки по фамилии
     */
    public ProtoPage assertSorting() {
        getTableHeaderSurname().click();

        assert isSorted(getCol(getMainTableRows(), 0), true);

        getTableHeaderSurname().click();
        assert isSorted(getCol(getMainTableRows(), 0), false);

        getTableHeaderSurname().click();
        List<String> list = getCol(getMainTableRows(), 0);
        assert !isSorted(list, true);
        assert !isSorted(list, false);

        return page(ProtoPage.class);
    }

    /**
     * Проверка создания клиента
     */
    public ProtoPage assertClientCreation() {
        getAddClientButton().click();

        assert WebDriverRunner.url().endsWith("/#/create2");
        getActiveBreadcrumbItem().shouldBe(Condition.text("Карточка клиента"));

        ProtoClient protoClient = page(ProtoClient.class);
        protoClient.assertPatronymic("Тест");
        protoClient.getInputByLabel("Фамилия").setValue("Иванов");
        protoClient.getInputByLabel("Имя").setValue("Алексей");
        protoClient.getInputByLabel("Отчество").setValue("Петрович");
        protoClient.getRadioByLabel("Мужской").click();
        protoClient.getInputByLabel("Дата рождения").setValue("17.01.2020");
        protoClient.getCheckboxByLabel("VIP").click();
        protoClient.getSaveButton().click();

        getActiveBreadcrumbItem().shouldBe(Condition.text("Список контактов"));

        assert getMainTableActivePageNumber() == 1;
        assert getMainTableActiveRowNumber() == 0;

        List<String> row = getRow(getMainTableRows(), 0);

        assert "Иванов".equals(row.get(0));
        assert "Алексей".equals(row.get(1));
        assert "Петрович".equals(row.get(2));
        assert "17.01.2020".equals(row.get(3));
        assert "Мужской".equals(row.get(4));
        assert "true".equals(row.get(5));

        return page(ProtoPage.class);
    }

    /**
     * Проверка создания клиента через модально окно
     */
    public ProtoPage assertClientCreationFromModal() {
        getCreateButton().click();

        ProtoClient protoClient = page(ProtoClient.class);
        protoClient.assertPatronymic("Тест");
        protoClient.getInputByLabel("Фамилия").setValue("Иванов");
        protoClient.getInputByLabel("Имя").setValue("Алексей");
        protoClient.getInputByLabel("Отчество").setValue("Петрович");
        protoClient.getRadioByLabel("Мужской").click();
        protoClient.getInputByLabel("Дата рождения").setValue("17.01.2020");
        protoClient.getCheckboxByLabel("VIP").click();
        protoClient.getSaveButton().click();

        protoClient = page(ProtoClient.class);
        protoClient.getCloseButton().click();

        assert "1".equals(getMainTablePaginationButtons()
                .stream().filter(li -> li.getAttribute("class").contains("active")).findFirst().get().getText());

        assert getMainTableRows().get(0).getAttribute("class").contains("table-active");

        assert "Иванов".equals(getCol(getMainTableRows(), 0).get(0));
        assert "Алексей".equals(getCol(getMainTableRows(), 1).get(0));
        assert "Петрович".equals(getCol(getMainTableRows(), 2).get(0));
        assert "17.01.2020".equals(getCol(getMainTableRows(), 3).get(0));
        assert "Мужской".equals(getCol(getMainTableRows(), 4).get(0));
        assert "true".equals(getCol(getMainTableRows(), 5).get(0));

        return page(ProtoPage.class);
    }

}
