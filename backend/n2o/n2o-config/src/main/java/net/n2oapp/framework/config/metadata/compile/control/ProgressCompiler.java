package net.n2oapp.framework.config.metadata.compile.control;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.control.plain.N2oProgress;
import net.n2oapp.framework.api.metadata.meta.control.Progress;
import org.springframework.stereotype.Component;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Компиляция компонента отображения прогресса
 */
@Component
public class ProgressCompiler extends FieldCompiler<Progress, N2oProgress> {
    @Override
    protected String getSrcProperty() {
        return "n2o.api.control.progress.src";
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oProgress.class;
    }

    @Override
    public Progress compile(N2oProgress source, CompileContext<?, ?> context, CompileProcessor p) {
        Progress progress = new Progress();
        progress.setMax(source.getMax());
        progress.setBarText(source.getBarText());
        progress.setAnimated(p.cast(source.getAnimated(), p.resolve(property("n2o.api.control.progress.animated"), Boolean.class)));
        progress.setStriped(p.cast(source.getStriped(), p.resolve(property("n2o.api.control.progress.striped"), Boolean.class)));
        progress.setColor(source.getColor());
        progress.setBarClass(source.getBarClass());
        compileField(progress, source, context, p);
        return progress;
    }
}