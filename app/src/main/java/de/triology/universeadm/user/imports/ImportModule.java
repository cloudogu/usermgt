package de.triology.universeadm.user.imports;

import de.triology.universeadm.BaseDirectory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import org.kohsuke.MetaInfServices;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

@MetaInfServices(Module.class)
public class ImportModule extends AbstractModule {
    @Override
    protected void configure() {
        FileSystem fileSystem = FileSystems.getDefault();
        String appBasePath = fileSystem.getPath(BaseDirectory.get().toString() + fileSystem.getSeparator() + "..").normalize().toString();
        bind(CSVParser.class).to(CSVParserImpl.class);
        bind(ResultRepository.class).toInstance(new ResultRepository(appBasePath + "/importHistory"));
        bind(SummaryRepository.class);
        bind(ImportHandler.class);
    }
}
