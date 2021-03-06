package net.n2oapp.framework.config.metadata.pack;

import net.n2oapp.framework.api.pack.MetadataPack;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.header.HeaderBinder;
import net.n2oapp.framework.config.metadata.compile.header.SimpleHeaderCompiler;
import net.n2oapp.framework.config.metadata.compile.header.SimpleHeaderIOv2;
import net.n2oapp.framework.config.metadata.compile.header.SimpleHeaderReader;
import net.n2oapp.framework.config.metadata.compile.menu.SimpleMenuIOv2;
import net.n2oapp.framework.config.metadata.compile.menu.SimpleMenuCompiler;
import net.n2oapp.framework.config.metadata.compile.menu.SimpleMenuReader;
import net.n2oapp.framework.config.reader.header.CustomHeaderReader;

/**
 * Набор для сборки header
 */
public class N2oHeaderPack implements MetadataPack<N2oApplicationBuilder> {
    @Override
    public void build(N2oApplicationBuilder b) {
        b.readers(new SimpleHeaderReader(), new CustomHeaderReader(), new SimpleMenuReader());
        b.ios(new SimpleHeaderIOv2(), new SimpleMenuIOv2());
        b.compilers(new SimpleHeaderCompiler(), new SimpleMenuCompiler());
        b.binders(new HeaderBinder());
    }
}
