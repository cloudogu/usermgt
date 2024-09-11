package de.triology.universeadm.user.imports;

import de.triology.universeadm.Constraint;
import de.triology.universeadm.mail.MailService;
import de.triology.universeadm.user.UserManager;
import de.triology.universeadm.user.Users;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.authz.AuthorizationException;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

import static de.triology.universeadm.user.imports.ImportHandler.getCode;
import static de.triology.universeadm.user.imports.ImportHandler.mapConstraintToColumn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ImportHandlerTest {

    private static final String VALID_FILENAME = "ImportUsers.csv";

    private final SummaryRepository summaryRepositoryMock = mock(SummaryRepository.class);
    private final MailService mailServiceMock = mock(MailService.class);

    @Test(expected = IllegalArgumentException.class)
    public void testMissingFileParts() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = Collections.emptyMap();

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));

        when(input.getFormDataMap()).thenReturn(inputParts);

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        importHandler.handle(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyFileParts() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));

        inputParts.put("file", Collections.emptyList());

        when(input.getFormDataMap()).thenReturn(inputParts);

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        importHandler.handle(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultipleFileParts() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Arrays.asList(mock(InputPart.class), mock(InputPart.class)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        importHandler.handle(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFileExtension() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.INVALID_HEADER_FILE_EXTENSION)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        importHandler.handle(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingFileName() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.INVALID_HEADER_MISSING_FILE_NAME)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        importHandler.handle(input);
    }

    @Test
    public void testInvalidCSVHeader() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.INVALID_CSV_HEADER)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(Stream.empty());
        when(parser.getErrors()).thenReturn(createMockErrorStream(1));

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        Result result = importHandler.handle(input);
        assertThat(result.getErrors()).hasSize(1);
    }

    private Stream<ImportEntryResult> createMockErrorStream(long lineNumber) {
        ImportError error = new ImportError.Builder(ImportError.Code.GENERIC_VALIDATION_ERROR)
            .withLineNumber(lineNumber)
            .withErrorMessage("test error")
            .build();
        List<ImportEntryResult> results = new ArrayList<>();
        results.add(ImportEntryResult.skipped(error));
        return results.stream();
    }

    @Test(expected = AuthorizationException.class)
    public void testMissingPermissions() throws Exception {
        UserManager userManager = createUserManagerMock(UserManagerCase.MISSING_PERMISSIONS);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID)));

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));
        when(parser.getErrors()).thenReturn(Stream.empty());

        when(input.getFormDataMap()).thenReturn(inputParts);

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        importHandler.handle(input);
    }

    @Test()
    public void testImportUsersCreate() throws Exception {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALID_CREATE);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));
        when(parser.getErrors()).thenReturn(Stream.empty());

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        Result result = importHandler.handle(input);

        verify(userManager, atLeast(1)).create(any());
        verify(userManager, never()).modify(any());

        assertSummary(2L, 0L, result);

        List<ImportError> errors = result.getErrors();
        assertThat(errors).isNullOrEmpty();
    }

    @Test()
    public void testImportUsersCreateModify() throws Exception {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALID_CREATE_MODIFY);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));
        when(parser.getErrors()).thenReturn(Stream.empty());

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        Result result = importHandler.handle(input);

        verify(userManager, atLeast(1)).create(any());
        verify(userManager, atLeast(1)).modify(any());

        assertSummary(1L, 1L, result);

        List<ImportError> errors = result.getErrors();
        assertThat(errors).isNullOrEmpty();
    }

    @Test()
    public void testImportUsersCreateValidationException() throws Exception {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALIDATION_EXCEPTION_CREATE);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));
        when(parser.getErrors()).thenReturn(Stream.empty());

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        Result result = importHandler.handle(input);

        verify(userManager, times(2)).create(any());
        verify(userManager, never()).modify(any());

        assertSummary(0L, 0L, result);

        List<ImportError> errors = result.getErrors();
        assertThat(errors).hasSize(2);

        assertThat(errors.get(0).getLineNumber()).isEqualTo(2);
        assertThat(errors.get(0).getErrorCode()).isEqualTo(ImportError.Code.GENERIC_VALIDATION_ERROR.value);

        assertThat(errors.get(1).getLineNumber()).isEqualTo(3);
        assertThat(errors.get(1).getErrorCode()).isEqualTo(ImportError.Code.GENERIC_VALIDATION_ERROR.value);
    }

    @Test()
    public void testImportUsersParsingError() throws Exception {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALID_CREATE);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID_ROW_PARSING_ERROR)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(1));
        when(parser.getErrors()).thenReturn(createMockErrorStream(2L));

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        Result result = importHandler.handle(input);

        verify(userManager, times(1)).create(any());
        verify(userManager, never()).modify(any());

        assertSummary(1L, 0L, result);

        List<ImportError> errors = result.getErrors();
    }

    @Test()
    public void testWriteResultError() throws Exception {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALID_CREATE);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.THROW_IOEXCEPTION_WRITE);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVParser parser = mock(CSVParser.class);
        when(parser.parse(any())).thenReturn(createMockStream(2));
        when(parser.getErrors()).thenReturn(Stream.empty());

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        Result result = importHandler.handle(input);

        verify(userManager, atLeast(1)).create(any());
        verify(userManager, never()).modify(any());

        assertSummary(2L, 0L, result);

        List<ImportError> errors = result.getErrors();
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getErrorCode()).isEqualTo(ImportError.Code.WRITE_RESULT_ERROR.value);
    }

    @Test()
    public void testGetResult() throws IOException {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALID_CREATE);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_READ);
        CSVParser parser = mock(CSVParser.class);

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        Result res = importHandler.getResult(UUID.randomUUID());

        assertThat(res).isNotNull();
    }

    @Test
    public void testGetSummariesReturnsEmptyList() throws IOException {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALID_CREATE);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_READ);
        CSVParser parser = mock(CSVParser.class);

        SummaryRepository sRepo = mock(SummaryRepository.class);
        when(sRepo.getSummaries()).thenReturn(Collections.emptyList());

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        Pair<List<Result.Summary>, Integer> res = importHandler.getSummaries(0, 10);

        assertThat(res).isNotNull();
        assertThat(res.getLeft()).isNullOrEmpty();
        assertThat(res.getRight()).isEqualTo(0);
    }

    @Test
    public void testGetSummariesReturnsFourElements() throws IOException {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALID_CREATE);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_READ);
        CSVParser parser = mock(CSVParser.class);

        SummaryRepository sRepo = mock(SummaryRepository.class);
        List<Result.Summary> sums = generateSummaries(4);
        when(sRepo.getSummaries()).thenReturn(sums);

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, sRepo, mailServiceMock);

        Pair<List<Result.Summary>, Integer> res = importHandler.getSummaries(0, 10);

        assertThat(res).isNotNull();
        assertThat(res.getLeft()).isNotNull();
        assertThat(res.getRight()).isEqualTo(4);
    }

    @Test
    public void testGetSummariesReturnsTwoElementsWithStart10() throws IOException {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALID_CREATE);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_READ);
        CSVParser parser = mock(CSVParser.class);

        SummaryRepository sRepo = mock(SummaryRepository.class);
        List<Result.Summary> sums = generateSummaries(12);
        when(sRepo.getSummaries()).thenReturn(sums);

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, sRepo, mailServiceMock);

        Pair<List<Result.Summary>, Integer> res = importHandler.getSummaries(10, 10);

        assertThat(res).isNotNull();
        assertThat(res.getLeft()).isNotNull();
        assertThat(res.getLeft()).hasSize(2);
        assertThat(res.getRight()).isEqualTo(12);
    }

    private static List<Result.Summary> generateSummaries(int count) {
        List<Result.Summary> sums = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String testFileName = String.format("file%s.csv", i + 1);
            sums.add(new Result.Summary(UUID.randomUUID(), testFileName, 123, 123, 123, 0));
        }
        return sums;
    }

    @Test()
    public void testDeleteResult() throws IOException {
        UserManager userManager = createUserManagerMock(UserManagerCase.VALID_CREATE);
        ResultRepository resultRepository = createResultRepositoryMock(ResultRepositoryCase.VALID_DELETE);
        CSVParser parser = mock(CSVParser.class);

        ImportHandler importHandler = new ImportHandler(userManager, parser, resultRepository, summaryRepositoryMock, mailServiceMock);

        assertThat(importHandler.deleteResult(UUID.randomUUID())).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInputStreamShouldThrowException() {
        ImportHandler sut = new ImportHandler(null, null, null, null, null);
        sut.getInputStream(new InputPart() {
            @Override
            public MultivaluedMap<String, String> getHeaders() {
                return null;
            }

            @Override
            public String getBodyAsString() throws IOException {
                throw new IOException("oh noez");
            }

            @Override
            public <T> T getBody(Class<T> aClass, Type type) throws IOException {
                throw new IOException("oh noez");
            }

            @Override
            public <T> T getBody(GenericType<T> genericType) throws IOException {
                throw new IOException("oh noez");
            }

            @Override
            public MediaType getMediaType() {
                return null;
            }

            @Override
            public boolean isContentTypeFromMessage() {
                return false;
            }

            @Override
            public void setMediaType(MediaType mediaType) {
            }
        });
    }

    @Test
    public void getCodeShouldMapExceptionContentToErrorCodes() {
        FieldConstraintViolationException exc1 = new FieldConstraintViolationException(Constraint.ID.UNIQUE_EMAIL);
        assertThat(getCode(exc1)).isEqualTo(ImportError.Code.UNIQUE_FIELD_ERROR);

        FieldConstraintViolationException exc2 = new FieldConstraintViolationException(Constraint.ID.UNIQUE_EMAIL);
        assertThat(getCode(exc2)).isEqualTo(ImportError.Code.UNIQUE_FIELD_ERROR);

        FieldConstraintViolationException exc3 = new FieldConstraintViolationException(Constraint.ID.VALID_EMAIL);
        assertThat(getCode(exc3)).isEqualTo(ImportError.Code.FIELD_FORMAT_ERROR);
    }


    @Test(expected = UnsupportedOperationException.class)
    public void getCodeShouldThrowExceptionOnUnsupportedExceptionContent() {
        FieldConstraintViolationException exc4 = new FieldConstraintViolationException(Constraint.ID.UNIQUE_GROUP_NAME);
        getCode(exc4);
    }

    @Test
    public void mapConstraintToColumnShouldMapConstraintsToListOfConstraints() {
        assertThat(mapConstraintToColumn(Constraint.ID.UNIQUE_EMAIL)).containsExactly("mail");
        assertThat(mapConstraintToColumn(Constraint.ID.VALID_EMAIL)).containsExactly("mail");
        assertThat(mapConstraintToColumn(Constraint.ID.UNIQUE_USERNAME)).containsExactly("username");
        assertThat(mapConstraintToColumn(Constraint.ID.UNIQUE_EMAIL, Constraint.ID.VALID_EMAIL, Constraint.ID.UNIQUE_USERNAME))
            .contains("username", "mail", "mail");
    }

    @Test
    public void mapConstraintToColumnShouldReturnEmptyList() {
        assertThat(mapConstraintToColumn()).isEmpty();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void mapConstraintToColumnShouldThrowUnsupportedExceptionOnUnknownOrNonUserContraintID() {
        mapConstraintToColumn(Constraint.ID.UNIQUE_GROUP_NAME);
    }

    private enum InputPartCase {
        VALID,
        VALID_ROW_PARSING_ERROR,
        INVALID_HEADER_FILE_EXTENSION,
        INVALID_HEADER_MISSING_FILE_NAME,
        INVALID_CSV_HEADER,
    }

    private InputPart createInputPartMock(InputPartCase c) throws Exception {

        InputPart inputPart = mock(InputPart.class);

        switch (c) {
            case INVALID_HEADER_FILE_EXTENSION:
            case INVALID_HEADER_MISSING_FILE_NAME:
                when(inputPart.getHeaders()).thenReturn(createInputPartHeader(c));
                break;
            case INVALID_CSV_HEADER:
                when(inputPart.getHeaders()).thenReturn(createInputPartHeader(InputPartCase.VALID));
                when(inputPart.getBody(any(), any())).thenReturn(CSVParserTest.readTestFileInputStream("InvalidHeader.csv"));
                break;
            case VALID_ROW_PARSING_ERROR:
                when(inputPart.getHeaders()).thenReturn(createInputPartHeader(InputPartCase.VALID));
                when(inputPart.getBody(any(), any())).thenReturn(CSVParserTest.readTestFileInputStream("InvalidLine_MissingRequiredField.csv"));
                break;
            default:
                when(inputPart.getHeaders()).thenReturn(createInputPartHeader(InputPartCase.VALID));
                when(inputPart.getBody(any(), any())).thenReturn(CSVParserTest.readTestFileInputStream("ImportUsers.csv"));
        }

        return inputPart;
    }

    private MultivaluedMap<String, String> createInputPartHeader(InputPartCase c) {
        //Content-Disposition: form-data; name="fieldName"; filename="filename.jpg"

        MultivaluedMap<String, String> header = new MultivaluedHashMap<>();

        switch (c) {
            case INVALID_HEADER_FILE_EXTENSION:
                header.putSingle("Content-Disposition", "form-data; name=\"file\"; filename=\"filename.jpg\"");
                break;
            case INVALID_HEADER_MISSING_FILE_NAME:
                header.putSingle("Content-Disposition", "form-data; name=\"file\"");
                break;
            default:
                header.putSingle("Content-Disposition", String.format("form-data; name=\"file\"; filename=\"%s\"", VALID_FILENAME));
        }

        return header;
    }

    private enum UserManagerCase {
        VALID_CREATE,
        VALID_CREATE_MODIFY,
        MISSING_PERMISSIONS,
        VALIDATION_EXCEPTION_CREATE
    }

    private UserManager createUserManagerMock(UserManagerCase c) {
        UserManager manager = mock(UserManager.class);

        switch (c) {
            case MISSING_PERMISSIONS:
                when(manager.get(any())).thenThrow(new AuthorizationException());
                doThrow(new AuthorizationException()).when(manager).create(any());
                break;
            case VALIDATION_EXCEPTION_CREATE:
                doThrow(new FieldConstraintViolationException(Constraint.ID.UNIQUE_EMAIL)).when(manager).create(any());
                break;
            case VALID_CREATE_MODIFY:
                when(manager.get(anyString())).thenAnswer(invocation -> {
                    String username = invocation.getArgument(0);
                    return username.equals("trillian") ? Users.createTrillian() : null;
                });
            default:
        }

        return manager;
    }

    private void assertSummary(Long expCreated, Long expUpdated, Result result) {
        Long created = (long) result.getCreated().size();
        assertThat(created).isNotNull();
        assertThat(expCreated).isEqualTo(created);

        Long updated = (long) result.getUpdated().size();
        assertThat(updated).isNotNull();
        assertThat(expUpdated).isEqualTo(updated);
    }

    private Stream<CSVUserDTO> createMockStream(int count) {
        List<CSVUserDTO> users = new ArrayList<>();
        CSVUserDTO dent = CSVUsers.createDent();
        dent.setLineNumber(2L);
        users.add(dent);
        if (count > 1) {
            CSVUserDTO trillian = CSVUsers.createTrillian();
            trillian.setLineNumber(3L);
            users.add(trillian);
        }
        return users.stream();
    }

    private enum ResultRepositoryCase {
        VALID_WRITE,
        VALID_READ,
        VALID_DELETE,
        THROW_IOEXCEPTION_WRITE
    }

    private ResultRepository createResultRepositoryMock(ResultRepositoryCase c) throws IOException {
        ResultRepository repo = mock(ResultRepository.class);

        switch (c) {
            case VALID_WRITE:
                doNothing().when(repo).write(any());
                break;
            case VALID_READ:
                when(repo.read(any())).thenReturn(new Result(UUID.randomUUID(), "test.csv"));
                break;
            case VALID_DELETE:
                when(repo.delete(any())).thenReturn(true);
                break;
            case THROW_IOEXCEPTION_WRITE:
                doThrow(new IOException("test IO Exception")).when(repo).write(any());
            default:
        }

        return repo;
    }
}
