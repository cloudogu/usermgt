package de.triology.universeadm.user.imports;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SummaryRepository is a repository handling the summaries of the results from the csv import.
 */
public class SummaryRepository {

    private final ResultRepository resultRepository;

    /**
     * Constructor for the repository
     * @param resultRepository - underlying {@link ResultRepository} for the results
     */
    @Inject
    public SummaryRepository(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    /**
     * Get summaries for all imports made.
     * @return List of {@link Result.Summary}
     * @throws IOException - in case {@link ResultRepository} cannot be read
     */
    public List<Result.Summary> getSummaries() throws IOException {
        return this.resultRepository.getAllResults().stream()
                .map(Result::getSummary)
                .collect(Collectors.toList());
    }

}
