    package ro.unibuc.hello.service;

    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.*;
    import ro.unibuc.hello.data.Reader;
    import ro.unibuc.hello.data.Rent;
    import ro.unibuc.hello.repository.ReaderRepository;
    import ro.unibuc.hello.repository.RentRepository;

    import java.time.LocalDateTime;
    import java.util.*;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    class ReaderServiceTest {

        @Mock
        private ReaderRepository readerRepository;

        @Mock
        private RentRepository rentRepository;

        @InjectMocks
        private ReaderService readerService;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        void test_getAllReaders() {
            List<Reader> readers = List.of(new Reader(), new Reader());
            when(readerRepository.findAll()).thenReturn(readers);

            List<Reader> result = readerService.getAllReaders();

            assertEquals(2, result.size());
            verify(readerRepository).findAll();
        }

        @Test
        void test_getReaderByEmail_found() {
            Reader reader = new Reader();
            when(readerRepository.findByEmail("test@email.com")).thenReturn(Optional.of(reader));

            Reader result = readerService.getReaderByEmail("test@email.com");

            assertNotNull(result);
            verify(readerRepository).findByEmail("test@email.com");
        }

        @Test
        void test_getReaderByEmail_notFound() {
            when(readerRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

            Reader result = readerService.getReaderByEmail("missing@email.com");

            assertNull(result);
        }

        @Test
        void test_getReadersWithBorrowedBooks() {
            Reader r1 = new Reader(); r1.setId("1");
            Reader r2 = new Reader(); r2.setId("2");
            List<Reader> allReaders = List.of(r1, r2);

            when(readerRepository.findAll()).thenReturn(allReaders);
            when(rentRepository.findByIdReader("1")).thenReturn(List.of(new Rent()));
            when(rentRepository.findByIdReader("2")).thenReturn(List.of());

            List<Reader> result = readerService.getReadersWithBorrowedBooks();

            assertEquals(1, result.size());
            assertEquals("1", result.get(0).getId());
        }

        @Test
        void test_getLateReaders() {
            Reader r1 = new Reader();
            r1.setId("1");

            Reader r2 = new Reader();
            r2.setId("2");

            Rent oldRent = mock(Rent.class);
            Rent recentRent = mock(Rent.class);

            // Mock renturile să returneze date controlate
            when(oldRent.getBorrowDate()).thenReturn(LocalDateTime.now().minusWeeks(2));
            when(recentRent.getBorrowDate()).thenReturn(LocalDateTime.now().minusDays(2));

            when(readerRepository.findAll()).thenReturn(List.of(r1, r2));
            when(rentRepository.findByIdReader("1")).thenReturn(List.of(oldRent));
            when(rentRepository.findByIdReader("2")).thenReturn(List.of(recentRent));

            List<Reader> result = readerService.getLateReaders();

            assertEquals(1, result.size());
            assertEquals("1", result.get(0).getId());
        }

    
        @Test
        void test_banLateReaders() {
            Reader lateReader = new Reader(); lateReader.setId("1"); lateReader.setRole("USER");
    
            LocalDateTime fixedDate = LocalDateTime.of(2024, 1, 1, 12, 0);
            Rent rent = new Rent();
            rent.setBorrowDate(fixedDate.minusWeeks(2)); // este considerat întârziat
    
            when(readerRepository.findAll()).thenReturn(List.of(lateReader));
            when(rentRepository.findByIdReader("1")).thenReturn(List.of(rent));
    
            List<Reader> result = readerService.banLateReaders();
    
            assertEquals(1, result.size());
            assertEquals("BANNED", result.get(0).getRole());
            verify(readerRepository).saveAll(result);
        }
    
        @Test
        void test_getBannedReaders() {
            Reader r1 = new Reader(); r1.setRole("BANNED");
            Reader r2 = new Reader(); r2.setRole("USER");

            when(readerRepository.findAll()).thenReturn(List.of(r1, r2));

            List<Reader> result = readerService.getBannedReaders();

            assertEquals(1, result.size());
            assertEquals("BANNED", result.get(0).getRole());
        }

        @Test
        void test_unbanReader_found() {
            Reader reader = new Reader(); reader.setRole("BANNED");

            when(readerRepository.findById("1")).thenReturn(Optional.of(reader));

            Boolean result = readerService.unbanReader("1");

            assertTrue(result);
            assertEquals("USER", reader.getRole());
            verify(readerRepository).save(reader);
        }

        @Test
        void test_unbanReader_notFound() {
            when(readerRepository.findById("missing")).thenReturn(Optional.empty());

            Boolean result = readerService.unbanReader("missing");

            assertNull(result);
        }
    }
