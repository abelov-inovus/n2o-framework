package net.n2oapp.framework.engine.validation.engine;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.data.InvocationProcessor;
import net.n2oapp.framework.api.data.validation.Validation;
import net.n2oapp.framework.api.data.validation.ValidationFailureCallback;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author operehod
 * @since 02.04.2015
 */
public class TestFieldEqualsValidation extends Validation {


    private String field;
    private Object expectedValue;

    public TestFieldEqualsValidation(String field, Object expectedValue) {
        this.field = field;
        this.expectedValue = expectedValue;
        setFields(new HashSet<>(Collections.singletonList(field)));
    }

    @Override
    public String getId() {
        return "equals." + field + expectedValue;
    }

    @Override
    public void validate(DataSet dataSet, InvocationProcessor serviceProvider, ValidationFailureCallback callback) {
        if (!expectedValue.equals(dataSet.get(field)))
            callback.onFail(String.format("%s is wrong", field));
    }

    @Override
    public String getType() {
        return null;
    }
}
