package de.triology.universeadm.user.imports;

import de.triology.universeadm.user.Users;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SummaryRepositoryTest {

    @Test
    public void getSummaries() throws IOException {
        ResultRepository resultRepoMock = mock(ResultRepository.class);
        Result result = createResult();

        when(resultRepoMock.getAllResults()).thenReturn(Collections.singletonList(result));

        SummaryRepository summaryRepository = new SummaryRepository(resultRepoMock);

        List<Result.Summary> summaryList = summaryRepository.getSummaries();

        assertEquals(Collections.singletonList(result.getSummary()), summaryList);
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

}