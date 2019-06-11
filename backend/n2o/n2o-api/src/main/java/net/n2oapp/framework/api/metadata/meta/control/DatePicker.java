package net.n2oapp.framework.api.metadata.meta.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Клиентская модель календаря
 */
@Getter
@Setter
public class DatePicker extends Control {
    @JsonProperty
    private String dateFormat;
    @JsonProperty
    private String timeFormat;
    @JsonProperty
    private String max;
    @JsonProperty
    private String min;
    @JsonProperty
    private String popupPlacement;
    @JsonProperty
    private String outputFormat;
    @JsonProperty
    private Boolean utc;
}
