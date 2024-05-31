package de.triology.universeadm.user.imports;

import com.google.common.collect.Lists;
import de.triology.universeadm.user.User;

public final class CSVUsers {

    private CSVUsers(){}

    public static CSVUserDTO createDent(){

        CSVUserDTO csvUserDTO = new CSVUserDTO();

        csvUserDTO.setUsername("dent");
        csvUserDTO.setDisplayname("Arthur Dent");
        csvUserDTO.setGivenname("Arthur");
        csvUserDTO.setSurname("Dent");
        csvUserDTO.setMail("arthur.dent@hitchhiker24.com");
        csvUserDTO.setPwdReset(true);
        csvUserDTO.setExternal(false);

        return csvUserDTO;
    }

    public static CSVUserDTO createTrillian(){

        CSVUserDTO csvUserDTO = new CSVUserDTO();

        csvUserDTO.setUsername("trillian");
        csvUserDTO.setDisplayname("Tricia McMillan");
        csvUserDTO.setGivenname("Tricia");
        csvUserDTO.setSurname("McMillan");
        csvUserDTO.setMail("tricia.mcmillan-1337@hitchhiker.com");
        csvUserDTO.setPwdReset(false);
        csvUserDTO.setExternal(true);

        return csvUserDTO;
    }
}
