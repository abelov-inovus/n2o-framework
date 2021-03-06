package net.n2oapp.framework.config.io.object;

import net.n2oapp.framework.api.exception.SeverityType;
import net.n2oapp.framework.api.metadata.global.dao.invocation.model.N2oInvocation;
import net.n2oapp.framework.api.metadata.global.dao.object.AbstractParameter;
import net.n2oapp.framework.api.metadata.global.dao.object.MapperType;
import net.n2oapp.framework.api.metadata.global.dao.object.N2oObject;
import net.n2oapp.framework.api.metadata.global.dao.object.field.ObjectListField;
import net.n2oapp.framework.api.metadata.global.dao.object.field.ObjectReferenceField;
import net.n2oapp.framework.api.metadata.global.dao.object.field.ObjectScalarField;
import net.n2oapp.framework.api.metadata.global.dao.object.field.ObjectSetField;
import net.n2oapp.framework.api.metadata.global.dao.validation.N2oConstraint;
import net.n2oapp.framework.api.metadata.global.dao.validation.N2oMandatory;
import net.n2oapp.framework.api.metadata.global.dao.validation.N2oValidation;
import net.n2oapp.framework.api.metadata.global.dao.validation.N2oValidationCondition;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.api.metadata.io.NamespaceIO;
import net.n2oapp.framework.config.io.dataprovider.DataProviderIOv1;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.stereotype.Component;

/**
 * Чтение\запись объекта версии 3.0
 */
@Component
public class ObjectElementIOv3 implements NamespaceIO<N2oObject> {
    private Namespace defaultNamespace = DataProviderIOv1.NAMESPACE;

    @Override
    public void io(Element e, N2oObject t, IOProcessor p) {
        p.attribute(e, "name", t::getName, t::setName);
        p.attribute(e, "table-name", t::getTableName, t::setTableName);
        p.attribute(e, "entity-class", t::getEntityClass, t::setEntityClass);
        p.attribute(e, "service-class", t::getServiceClass, t::setServiceClass);
        p.attribute(e, "service-name", t::getServiceName, t::setServiceName);
        p.attribute(e, "app-name", t::getAppName, t::setAppName);
        p.attribute(e, "module-name", t::getModuleName, t::setModuleName);
        p.children(e, "operations", "operation", t::getOperations, t::setOperations, N2oObject.Operation::new, this::operation);
        p.anyChildren(e, "fields", t::getObjectFields, t::setObjectFields, p.oneOf(AbstractParameter.class)
                .add("field", ObjectScalarField.class, this::field)
                .add("reference", ObjectReferenceField.class, this::reference)
                .add("list", ObjectListField.class, this::reference)
                .add("set", ObjectSetField.class, this::reference));
        p.anyChildren(e, "validations", t::getN2oValidations, t::setN2oValidations, p.oneOf(N2oValidation.class)
                .add("constraint", N2oConstraint.class, this::constraint)
                .add("condition", N2oValidationCondition.class, this::condition)
                .add("mandatory", N2oMandatory.class, this::mandatory));
    }

    private void abstractParameter(Element e, AbstractParameter t, IOProcessor p) {
        p.attribute(e, "id", t::getId, t::setId);
        p.attribute(e, "name", t::getName, t::setName);
        p.attribute(e, "mapping", t::getMapping, t::setMapping);
        p.attributeBoolean(e, "required", t::getRequired, t::setRequired);
    }

    private void field(Element e, ObjectScalarField t, IOProcessor p) {
        abstractParameter(e, t, p);
        p.attribute(e, "domain", t::getDomain, t::setDomain);
        p.attribute(e, "default-value", t::getDefaultValue, t::setDefaultValue);
        p.attribute(e, "normalize", t::getNormalize, t::setNormalize);
        p.attributeEnum(e, "mapper", t::getMapperType, t::setMapperType, MapperType.class);
    }

    private void reference(Element e, ObjectReferenceField t, IOProcessor p) {
        abstractParameter(e, t, p);
        p.attribute(e, "object-id", t::getReferenceObjectId, t::setReferenceObjectId);
        p.attribute(e, "entity-class", t::getEntityClass, t::setEntityClass);
        p.children(e, null, "field", t::getFields, t::setFields, ObjectScalarField.class, this::field);
    }

    private void operation(Element e, N2oObject.Operation t, IOProcessor p) {
        p.attribute(e, "id", t::getId, t::setId);
        p.attribute(e, "name", t::getName, t::setName);
        p.attribute(e, "submit-label", t::getFormSubmitLabel, t::setFormSubmitLabel);
        p.attribute(e, "description", t::getDescription, t::setDescription);
        p.attribute(e, "success-text", t::getSuccessText, t::setSuccessText);
        p.attribute(e, "fail-text", t::getFailText, t::setFailText);
        p.attribute(e, "confirm-text", t::getConfirmationText, t::setConfirmationText);
        p.attributeBoolean(e, "confirm", t::getConfirm, t::setConfirm);
        p.anyChild(e, "invocation", t::getInvocation, t::setInvocation, p.anyOf(N2oInvocation.class), defaultNamespace);
        p.children(e, "in-parameters", "param", t::getInParameters, t::setInParameters, N2oObject.Parameter.class, this::param);
        p.children(e, "out-parameters", "param", t::getOutParameters, t::setOutParameters, N2oObject.Parameter.class, this::param);
        p.child(e, null, "validations", t::getValidations, t::setValidations, N2oObject.Operation.Validations.class, this::operationInlineValidations);
    }

    private void operationInlineValidations(Element e, N2oObject.Operation.Validations t, IOProcessor p) {
        p.attributeArray(e, "black-list", ",", t::getBlackList, t::setBlackList);
        p.attributeArray(e, "white-list", ",", t::getWhiteList, t::setWhiteList);
        p.anyChildren(e, null, t::getInlineValidations, t::setInlineValidations, p.oneOf(N2oValidation.class)
                .add("constraint", N2oConstraint.class, this::constraint)
                .add("condition", N2oValidationCondition.class, this::condition)
                .add("mandatory", N2oMandatory.class, this::mandatory));
    }

    private void param(Element e, N2oObject.Parameter t, IOProcessor p) {
        p.attribute(e, "id", t::getId, t::setId);
        p.attribute(e, "default-value", t::getDefaultValue, t::setDefaultValue);
        p.attribute(e, "domain", t::getDomain, t::setDomain);
        p.attribute(e, "normalize", t::getNormalize, t::setNormalize);
        p.attributeEnum(e, "mapper", t::getMapper, t::setMapper, MapperType.class);
        p.attribute(e, "mapping", t::getMapping, t::setMapping);
        p.attributeBoolean(e, "required", t::getRequired, t::setRequired);
        p.attribute(e, "mapping-condition", t::getMappingCondition, t::setMappingCondition);
        p.attribute(e, "entity-class", t::getEntityClass, t::setEntityClass);
        p.children(e, null, "child-param", t::getChildParams, t::setChildParams, N2oObject.Parameter.class, this::param);
    }

    private void validation(Element e, N2oValidation t, IOProcessor p) {
        p.attribute(e, "id", t::getId, t::setId);
        p.attributeEnum(e, "severity", t::getSeverity, t::setSeverity, SeverityType.class);
        p.attributeEnum(e, "client-moment", t::getClientMoment, t::setClientMoment, N2oValidation.ClientMoment.class);
        p.attributeEnum(e, "server-moment", t::getServerMoment, t::setServerMoment, N2oValidation.ServerMoment.class);
        p.attribute(e, "field-id", t::getFieldId, t::setFieldId);
        p.attribute(e, "message", t::getMessage, t::setMessage);
        p.attribute(e, "enabled", t::getEnabled, t::setEnabled);
        p.attribute(e, "side", t::getSide, t::setSide);
    }

    private void constraint(Element e, N2oConstraint t, IOProcessor p) {
        validation(e, t, p);
        p.attribute(e, "result", t::getResult, t::setResult);
        p.children(e, "in-parameters", "param", t::getInParameters, t::setInParameters, N2oObject.Parameter.class, this::param);
        p.children(e, "out-parameters", "param", t::getOutParameters, t::setOutParameters, N2oObject.Parameter.class, this::param);
        p.anyChild(e, "invocation", t::getN2oInvocation, t::setN2oInvocation, p.anyOf(N2oInvocation.class), defaultNamespace);
    }

    private void condition(Element e, N2oValidationCondition t, IOProcessor p) {
        validation(e, t, p);
        p.text(e, t::getExpression, t::setExpression);
        p.attribute(e, "on", t::getExpressionOn, t::setExpressionOn);
        p.attribute(e, "src", t::getSrc, t::setSrc);
    }

    private void mandatory(Element e, N2oMandatory t, IOProcessor p) {
        validation(e, t, p);
        p.text(e, t::getExpression, t::setExpression);
        p.attribute(e, "on", t::getExpressionOn, t::setExpressionOn);
        p.attribute(e, "src", t::getSrc, t::setSrc);
    }

    @Override
    public Class<N2oObject> getElementClass() {
        return N2oObject.class;
    }

    @Override
    public String getElementName() {
        return "object";
    }

    @Override
    public String getNamespaceUri() {
        return "http://n2oapp.net/framework/config/schema/object-3.0";
    }

    public void setDefaultNamespace(Namespace defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }
}
