package net.n2oapp.framework.config.io;

import net.n2oapp.framework.api.metadata.aware.NamespaceUriAware;
import net.n2oapp.framework.api.metadata.io.NamespaceIO;
import net.n2oapp.framework.api.metadata.io.NamespaceIOFactory;
import net.n2oapp.framework.api.metadata.io.ProxyNamespaceIO;
import net.n2oapp.framework.api.metadata.persister.NamespacePersister;
import net.n2oapp.framework.api.metadata.persister.NamespacePersisterFactory;
import net.n2oapp.framework.api.metadata.reader.NamespaceReader;
import net.n2oapp.framework.api.metadata.reader.NamespaceReaderFactory;
import org.jdom.Namespace;

import java.util.*;

/**
 * Реализация фабрики чтения / записи элементов по неймспейсу
 *
 * @param <T> Тип модели
 * @param <R> Ти ридера
 * @param <P> Тип персистера
 */
public class NamespaceIOFactoryByMap<T extends NamespaceUriAware, R extends NamespaceReader<? extends T>, P extends NamespacePersister<? super T>>
        implements NamespaceIOFactory<T, R, P> {
    private Class<T> baseElementClass;
    private Map<String, Map<String, R>> names = new HashMap<>();
    private Map<String, Map<Class, P>> classes = new HashMap<>();
    private NamespaceReaderFactory readerFactory;
    private NamespacePersisterFactory persisterFactory;
    private Set<String> ignoredElements = new HashSet<>();

    public NamespaceIOFactoryByMap(Class<T> baseElementClass,
                                   NamespaceReaderFactory readerFactory,
                                   NamespacePersisterFactory persisterFactory) {
        this.baseElementClass = baseElementClass;
        this.readerFactory = readerFactory;
        this.persisterFactory = persisterFactory;
    }

    @Override
    public P produce(Class<T> clazz, Namespace... namespaces) {
        for (Namespace namespace : namespaces) {
            if (classes.containsKey(namespace.getURI()) && classes.get(namespace.getURI()).containsKey(clazz))
                return classes.get(namespace.getURI()).get(clazz);
        }
        return (P) persisterFactory.produce(clazz, namespaces);
    }

    @Override
    public R produce(String elementName, Namespace... namespaces) {
        if (ignoredElements.contains(elementName)) return null;
        for (Namespace namespace : namespaces) {
            if (names.containsKey(namespace.getURI()) && names.get(namespace.getURI()).containsKey(elementName))
                return names.get(namespace.getURI()).get(elementName);
        }
        return (R) readerFactory.produce(elementName, namespaces);
    }

    @Override
    public NamespaceIOFactory<T, R, P> add(NamespaceIO<? extends T> nio) {
        ProxyNamespaceIO<? extends T> proxy = new ProxyNamespaceIO<>(nio);
        names.computeIfAbsent(nio.getNamespaceUri(), k -> new HashMap<>()).put(nio.getElementName(), (R) proxy);
        classes.computeIfAbsent(nio.getNamespaceUri(), k -> new HashMap<>()).put(nio.getElementClass(), (P) proxy);
        return this;
    }

    @Override
    public NamespaceIOFactory<T, R, P> ignore(String... elementNames) {
        if (elementNames != null) {
            Collections.addAll(ignoredElements, elementNames);
        }
        return this;
    }

    @Override
    public void add(NamespacePersister<T> persister) {
        classes.computeIfAbsent(persister.getNamespaceUri(), k -> new HashMap<>()).put(persister.getElementClass(), (P) persister);
    }

    @Override
    public void add(NamespaceReader<T> reader) {
        names.computeIfAbsent(reader.getNamespaceUri(), k -> new HashMap<>()).put(reader.getElementName(), (R) reader);
    }

    @Override
    public Class<T> getBaseElementClass() {
        return baseElementClass;
    }
}
