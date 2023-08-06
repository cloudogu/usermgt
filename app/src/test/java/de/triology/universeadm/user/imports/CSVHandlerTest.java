package de.triology.universeadm.user.imports;


import de.triology.universeadm.ConstraintViolationException;
import de.triology.universeadm.user.UserManager;
import de.triology.universeadm.user.Users;
import org.apache.shiro.authz.AuthorizationException;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CSVHandlerTest {

    private static final String VALID_FILENAME = "ImportUsers.csv";

    @Test(expected = BadArgumentException.class)
    public void testMissingFileParts() throws BadArgumentException {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = Collections.emptyMap();

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        csvHandler.handle(input);
    }

    @Test(expected = BadArgumentException.class)
    public void testEmptyFileParts() throws BadArgumentException {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.emptyList());

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        csvHandler.handle(input);
    }

    @Test(expected = BadArgumentException.class)
    public void testMultipleFileParts() throws BadArgumentException {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Arrays.asList(mock(InputPart.class), mock(InputPart.class)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        csvHandler.handle(input);
    }

    @Test(expected = BadArgumentException.class)
    public void testInvalidFileExtension() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.INVALID_HEADER_FILE_EXTENSION)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        csvHandler.handle(input);
    }

    @Test(expected = BadArgumentException.class)
    public void testMissingFileName() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.INVALID_HEADER_MISSING_FILE_NAME)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        csvHandler.handle(input);
    }

    @Test(expected = BadArgumentException.class)
    public void testInvalidFileBodyPart() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.INVALID_BODY_PART)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        csvHandler.handle(input);
    }

    @Test(expected = BadArgumentException.class)
    public void testInvalidCSVHeader() throws Exception {
        UserManager userManager = mock(UserManager.class);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.INVALID_CSV_HEADER)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        csvHandler.handle(input);
    }

    @Test(expected = AuthorizationException.class)
    public void testMissingPermissions() throws Exception {
        UserManager userManager = createUserMangerMock(UserManagerCase.MISSING_PERMISSIONS);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        csvHandler.handle(input);
    }

    @Test()
    public void testImportUsersCreate() throws Exception {
        UserManager userManager = createUserMangerMock(UserManagerCase.VALID_CREATE);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        Result result = csvHandler.handle(input);

        verify(userManager, atLeast(1)).create(any());
        verify(userManager, never()).modify(any());

        assertSummary(2L, 0L, 0L, result);

        List<ImportError> errors = result.getErrors();
        assertTrue(errors.isEmpty());
    }

    @Test()
    public void testImportUsersCreateModify() throws Exception {
        UserManager userManager = createUserMangerMock(UserManagerCase.VALID_CREATE_MODIFY);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        Result result = csvHandler.handle(input);

        verify(userManager, atLeast(1)).create(any());
        verify(userManager, atLeast(1)).modify(any());

        assertSummary(1L, 1L, 0L, result);

        List<ImportError> errors = result.getErrors();
        assertTrue(errors.isEmpty());
    }

    @Test()
    public void testImportUsersCreateValidationException() throws Exception {
        UserManager userManager = createUserMangerMock(UserManagerCase.VALIDATION_EXCEPTION_CREATE);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        Result result = csvHandler.handle(input);

        verify(userManager, times(2)).create(any());
        verify(userManager, never()).modify(any());

        assertSummary(0L, 0L, 2L, result);

        List<ImportError> errors = result.getErrors();
        assertEquals(2, errors.size());

        assertEquals(2, errors.get(0).getLineNumber());
        assertEquals(ImportError.Code.VALIDATION_ERROR.value, errors.get(0).getErrorCode());

        assertEquals(3, errors.get(1).getLineNumber());
        assertEquals(ImportError.Code.VALIDATION_ERROR.value, errors.get(1).getErrorCode());
    }

    @Test()
    public void testImportUsersParsingError() throws Exception {
        UserManager userManager = createUserMangerMock(UserManagerCase.VALID_CREATE);
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> inputParts = new HashMap<>();

        inputParts.put("file", Collections.singletonList(createInputPartMock(InputPartCase.VALID_ROW_PARSING_ERROR)));

        when(input.getFormDataMap()).thenReturn(inputParts);

        CSVHandler csvHandler = new CSVHandler(userManager);

        Result result = csvHandler.handle(input);

        verify(userManager, times(1)).create(any());
        verify(userManager, never()).modify(any());

        assertSummary(1L, 0L, 1L, result);

        List<ImportError> errors = result.getErrors();
        assertEquals(1, errors.size());

        assertEquals(2, errors.get(0).getLineNumber());
        assertEquals(ImportError.Code.PARSING_ERROR.value, errors.get(0).getErrorCode());
    }

    private enum InputPartCase {
        VALID,
        VALID_ROW_PARSING_ERROR,
        INVALID_HEADER_FILE_EXTENSION,
        INVALID_HEADER_MISSING_FILE_NAME,
        INVALID_BODY_PART,
        INVALID_CSV_HEADER,
    }

    private InputPart createInputPartMock(InputPartCase c) throws Exception {

        InputPart inputPart = mock(InputPart.class);

        switch (c) {
            case INVALID_HEADER_FILE_EXTENSION:
            case INVALID_HEADER_MISSING_FILE_NAME:
                when(inputPart.getHeaders()).thenReturn(createInputPartHeader(c));
                break;
            case INVALID_BODY_PART:
                when(inputPart.getHeaders()).thenReturn(createInputPartHeader(InputPartCase.VALID));
                when(inputPart.getBody(any())).thenReturn(null);
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

    private MultivaluedMap<String,String> createInputPartHeader(InputPartCase c) {
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

    private UserManager createUserMangerMock(UserManagerCase c) {
        UserManager manager = mock(UserManager.class);

        switch (c) {
            case MISSING_PERMISSIONS:
                when(manager.get(any())).thenThrow(new AuthorizationException());
                doThrow(new AuthorizationException()).when(manager).create(any());
                break;
            case VALIDATION_EXCEPTION_CREATE:
                doThrow(new ConstraintViolationException()).when(manager).create(any());
                break;
            case VALID_CREATE_MODIFY:
                when(manager.get(anyString())).thenAnswer(invocation -> {
                    String username = invocation.getArgumentAt(0, String.class);
                    return username.equals("trillian") ? Users.createTrillian() : null;
                });
            default:
        }

        return manager;
    }

    private void assertSummary(Long expCreated, Long expUpdated, Long expSkipped, Result result) {
        Map<ResultType, Long> summary = result.getSummary();
        assertNotNull(summary);

        Long created = summary.get(ResultType.CREATED);
        assertNotNull(created);
        assertEquals(expCreated, created);

        Long updated = summary.get(ResultType.UPDATED);
        assertNotNull(updated);
        assertEquals(expUpdated, updated);

        Long skipped = summary.get(ResultType.SKIPPED);
        assertNotNull(skipped);
        assertEquals(expSkipped, skipped);
    }

}