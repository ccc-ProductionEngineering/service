package ro.unibuc.hello.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.service.ReaderService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReaderControllerTest {

    @Mock
    private ReaderService readerService;

    @InjectMocks
    private ReaderController readerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(readerController).build();
    }

    @Test
    void testGetReadersWithBorrowedBooks() throws Exception {
        List<Reader> readers = List.of(new Reader(), new Reader());
        when(readerService.getReadersWithBorrowedBooks()).thenReturn(readers);

        mockMvc.perform(get("/readers/borrowers"))
                .andExpect(status().isOk());
        verify(readerService).getReadersWithBorrowedBooks();
    }

    @Test
    void testGetLateReaders() throws Exception {
        List<Reader> lateReaders = List.of(new Reader());
        when(readerService.getLateReaders()).thenReturn(lateReaders);

        mockMvc.perform(get("/readers/late"))
                .andExpect(status().isOk());
        verify(readerService).getLateReaders();
    }

    @Test
    void testBanLateReaders() throws Exception {
        List<Reader> banned = List.of(new Reader(), new Reader());
        when(readerService.banLateReaders()).thenReturn(banned);

        mockMvc.perform(post("/readers/banlate"))
                .andExpect(status().isOk());
        verify(readerService).banLateReaders();
    }

    @Test
    void testGetBannedReaders() throws Exception {
        List<Reader> banned = List.of(new Reader());
        when(readerService.getBannedReaders()).thenReturn(banned);

        mockMvc.perform(get("/readers/banned"))
                .andExpect(status().isOk());
        verify(readerService).getBannedReaders();
    }

    @Test
    void testUnbanReader_success() throws Exception {
        when(readerService.unbanReader("123")).thenReturn(true);

        mockMvc.perform(post("/readers/unban/123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilizatorul a fost debanat cu succes!"));
        verify(readerService).unbanReader("123");
    }

    @Test
    void testUnbanReader_notFound() throws Exception {
        when(readerService.unbanReader("999")).thenReturn(false);

        mockMvc.perform(post("/readers/unban/999"))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilizatorul nu a fost gasit!"));
        verify(readerService).unbanReader("999");
    }

    @Test
    void testGetAllReaders() throws Exception {
        List<Reader> readers = List.of(new Reader(), new Reader(), new Reader());
        when(readerService.getAllReaders()).thenReturn(readers);

        mockMvc.perform(get("/readers/all"))
                .andExpect(status().isOk());
        verify(readerService).getAllReaders();
    }
}
