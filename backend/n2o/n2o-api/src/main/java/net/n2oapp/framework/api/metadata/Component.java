package net.n2oapp.framework.api.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.aware.CssClassAware;
import net.n2oapp.framework.api.metadata.aware.IdAware;
import net.n2oapp.framework.api.metadata.aware.PropertiesAware;

import java.util.Map;

/**
 * Клиентская модель компонента
 */
@Getter
@Setter
public abstract class Component implements Compiled, IdAware, PropertiesAware {
    @JsonProperty
    private String id;
    @JsonProperty
    private String src;
    @JsonProperty
    private String className;
    private Map<String, Object> properties;
}