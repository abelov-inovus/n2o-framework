package net.n2oapp.framework.api.metadata.global.view.page;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.SourceComponent;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oButton;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oToolbar;

/**
 * Диалог подтверждения действия
 */
@Getter
@Setter
public class N2oDialog implements SourceComponent {

    public N2oDialog(String id) {
        this.id = id;
    }

    /**
     * Идентификатор диалога, обязателен для заполнения
     */
    private String id;

    /**
     * Маршрут диалога
     */
    private String route;

    /**
     * Заголовок диалога
     */
    private String title;

    /**
     * Описание диалога
     */
    private String description;

    /**
     * Размер окна диалога (sm, lg)
     */
    private String size;

    /**
     * Кнопки диалога
     */
    private N2oToolbar toolbar;

    private String namespaceUri;
}
