package de.triology.universeadm.user.imports;

import de.triology.universeadm.user.Users;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class ResultRepositoryTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    @Test
    public void writeResult() throws IOException {
        String folderPath = getFolderPath();
        ResultRepository repo = new ResultRepository(folderPath);

        Result result = createResult();
        repo.write(result);

        String filePath = Paths.get(folderPath).resolve(result.getImportID().toString() + ".json").toAbsolutePath().toString();

        new FileReader(filePath).read();
    }

    @Test
    public void readResult() throws FileNotFoundException {
        String folderPath = getFolderPath();
        ResultRepository repo = new ResultRepository(folderPath);

        Result result = createResult();

        try {
            repo.write(result);
        } catch (IOException e) {
            fail("unexpected exception while writing file");
        }

        Result readResult = repo.read(result.getImportID());

        assertNotNull(readResult);
        assertEquals(result, readResult);
    }

    @Test
    public void getAllResult() throws IOException {
        String folderPath = getFolderPath();
        ResultRepository repo = new ResultRepository(folderPath);

        Result result = createResult();
        Result result2 = createResult();

        try {
            repo.write(result);
            repo.write(result2);

            this.folder.newFile("ebf9d27f-f83c-4832-9c9e-06cb42976a0a.csv");
            this.folder.newFile("invalid.json");
        } catch (IOException e) {
            fail("unexpected exception while writing file");
        }

        List<Result> results = repo.getAllResults();

        assertEquals(2, results.size());
    }

    private Result createResult() {
        return new Result(
                UUID.randomUUID(),
                "testFile.csv",
                Collections.singletonList(Users.createDent()),
                Collections.singletonList(Users.createDent2()),
                Collections.singletonList(new ImportError.Builder(ImportError.Code.UNIQUE_FIELD_ERROR)
                        .withLineNumber(3)
                        .withErrorMessage("testError")
                        .build()));
    }

    private String getFolderPath() {
        return folder.getRoot().getAbsolutePath();
    }
}