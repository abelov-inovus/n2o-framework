package net.n2oapp.framework.config.metadata.compile.widget.table;

import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.AbstractColumn;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.N2oSimpleColumn;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oCell;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oTextCell;
import net.n2oapp.framework.api.metadata.local.CompiledQuery;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.api.metadata.meta.control.ValidationType;
import net.n2oapp.framework.api.metadata.meta.widget.table.ColumnHeader;
import net.n2oapp.framework.api.metadata.meta.widget.toolbar.Condition;
import net.n2oapp.framework.api.script.ScriptProcessor;
import net.n2oapp.framework.config.metadata.compile.ComponentScope;
import net.n2oapp.framework.config.metadata.compile.widget.CellsScope;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Компиляция простого заголовка таблицы
 */
@Component
public class SimpleColumnHeaderCompiler<T extends N2oSimpleColumn> extends AbstractHeaderCompiler<T> {
    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oSimpleColumn.class;
    }

    @Override
    public ColumnHeader compile(T source, CompileContext<?, ?> context, CompileProcessor p) {
        ColumnHeader header = new ColumnHeader();
        source.setId(p.cast(source.getId(), source.getTextFieldId()));
        source.setSortingFieldId(p.cast(source.getSortingFieldId(), source.getTextFieldId()));

        N2oCell cell = source.getCell();
        if (cell == null) {
            cell = new N2oTextCell();
        }
        cell = p.compile(cell, context, new ComponentScope(source));
        CellsScope cellsScope = p.getScope(CellsScope.class);
        if (cellsScope != null && cellsScope.getCells() != null)
            cellsScope.getCells().add(cell);

        header.setId(source.getId());
        header.setIcon(source.getLabelIcon());
        header.setWidth(source.getWidth());
        header.setResizable(source.getResizable());
        header.setFixed(source.getFixed());

        String tableId = null;
        WidgetScope widgetScope = p.getScope(WidgetScope.class);
        if (widgetScope != null)
            tableId = widgetScope.getWidgetId();

        if (StringUtils.isLink(source.getVisible())) {
            Condition condition = new Condition();
            condition.setExpression(source.getVisible().substring(1, source.getVisible().length() - 1));
            condition.setModelLink(new ModelLink(ReduxModel.FILTER, tableId).getBindLink());
            if (!header.getConditions().containsKey(ValidationType.visible)) {
                header.getConditions().put(ValidationType.visible, new ArrayList<>());
            }
            header.getConditions().get(ValidationType.visible).add(condition);
        } else {
            header.setVisible(p.resolveJS(source.getVisible(), Boolean.class));
        }
        if (source.getColumnVisibilities() != null) {
            for (AbstractColumn.ColumnVisibility visibility : source.getColumnVisibilities()) {
                String refWidgetId = p.cast(visibility.getRefWidgetId(), tableId);
                ReduxModel refModel = p.cast(visibility.getRefModel(), ReduxModel.FILTER);
                Condition condition = new Condition();
                condition.setExpression(ScriptProcessor.resolveFunction(visibility.getValue()));
                condition.setModelLink(new ModelLink(refModel, refWidgetId).getBindLink());
                if (!header.getConditions().containsKey(ValidationType.visible)) {
                    header.getConditions().put(ValidationType.visible, new ArrayList<>());
                }
                header.getConditions().get(ValidationType.visible).add(condition);
            }
        }

        CompiledQuery query = p.getScope(CompiledQuery.class);
        if (query != null && query.getFieldsMap().containsKey(source.getTextFieldId())) {
            header.setLabel(p.cast(source.getLabelName(), query.getFieldsMap().get(source.getTextFieldId()).getName()));
        } else {
            header.setLabel(source.getLabelName());
        }
        if (query != null && query.getFieldsMap().containsKey(header.getId())) {
            header.setSortable(!query.getFieldsMap().get(header.getId()).getNoSorting());
        }

        return header;
    }
}
