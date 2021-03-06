package net.n2oapp.framework.autotest.api.component.control;

/**
 * Ввод пароля для автотестирования
 */
public interface PasswordControl extends Control {

    String val();

    void val(String value);

    void shouldHavePlaceholder(String value);

    void clickEyeButton();

    void passwordShouldBeVisible();

    void passwordShouldNotBeVisible();
}
