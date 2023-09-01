package de.triology.universeadm.user.imports;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Module.class)
public class ImportModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CSVParser.class).to(CSVParserImpl.class);
        bind(ResultRepository.class).toInstance(new ResultRepository("/var/lib/usermgt/importHistory"));
        bind(CSVHandler.class);
    }
}
