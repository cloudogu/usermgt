package de.triology.universeadm.user.imports;

import com.opencsv.exceptions.CsvException;

public interface ExecptionListener {

    void notify(CsvException e);
}
