package net.n2oapp.framework.config.metadata.compile.widget.chart;

import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.widget.chart.N2oStandardChart;
import net.n2oapp.framework.api.metadata.meta.widget.chart.*;
import org.springframework.stereotype.Component;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Компиляция стандартного компонента диаграммы
 */
@Component
public abstract class StandardChartCompiler<D extends StandardChartWidgetComponent, S extends N2oStandardChart> extends AbstractChartCompiler<D, S> {

    protected D compileStandardChart(D chart, S source, CompileContext<?, ?> context, CompileProcessor p) {
        ChartAxis xAxis = new ChartAxis();
        xAxis.setFieldId(source.getXAxisFieldId());
        xAxis.setPosition((p.cast(source.getXAxisPosition(), N2oStandardChart.XAxisPositionEnum.bottom)).toString());
        xAxis.setHasLabel(p.cast(source.getXHasLabel(), p.resolve(property("n2o.api.default.widget.chart.axis.has-label"), Boolean.class)));
        chart.setXAxis(xAxis);
        ChartAxis yAxis = new ChartAxis();
        yAxis.setFieldId(source.getYAxisFieldId());
        yAxis.setPosition((p.cast(source.getYAxisPosition(), N2oStandardChart.YAxisPositionEnum.left)).toString());
        yAxis.setHasLabel(p.cast(source.getYHasLabel(), p.resolve(property("n2o.api.default.widget.chart.axis.has-label"), Boolean.class)));
        chart.setYAxis(yAxis);
        ChartGrid grid = new ChartGrid();
        grid.setStrokeDashArray(source.getGridStrokeDashArray());
        grid.setHorizontal(p.cast(source.getGridHorizontal(), true));
        grid.setVertical(p.cast(source.getGridVertical(), true));
        chart.setGrid(grid);
        ChartTooltip tooltip = new ChartTooltip();
        tooltip.setSeparator(source.getTooltipSeparator());
        chart.setTooltip(tooltip);
        ChartLegend legend = new ChartLegend();
        legend.setIconType(p.cast(source.getLegendIconType(), ChartLegendIconType.line));
        chart.setLegend(legend);
        return chart;
    }
}
