package com.bankaccounts.repository;

import com.bankaccounts.model.Yanki;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("YANKI")
class YankiRepositoryTest {

    @Mock
    private YankiRepository yankiRepository;

    Yanki yanki = new Yanki();

    Flux<Yanki> list;

    @BeforeAll
    void init() {
        System.out.println("START OF TESTS");
    }

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
        yanki.setCustomerId("6428cfa73c4a07682cac5fa0");
        yanki.setTypeYanki(0);
        yanki.setAmount(8000.00);
        yanki.setImeitelephone("123456789101213");
        list = Flux.just(yanki);
    }

    @Nested
    @DisplayName("CRUD")
    public class crud {

        @Test
        @DisplayName("LIST")
        void findAll(){
            when(yankiRepository.findAll()).thenReturn(list);
            System.out.println("LIST = " + yanki);
            assertNotEquals(null, list);
        }

        @Test
        @DisplayName("SAVE")
        void save(){
            yankiRepository.save(yanki);
            System.out.println("SAVE = " + yanki);
            assertNotEquals(null, list);
        }

        @Test
        @DisplayName("UPDATE")
        void update(){
            System.out.println("ORIGINAL LIST = " + yanki);
            yanki.setAmount(20000.00);
            yankiRepository.save(yanki);
            System.out.println("LIST UPDATE = " + yanki);
            assertNotNull(yanki.getAmount());
        }

        @Test
        @DisplayName("DELETE")
        void delete(){
            update();
            yankiRepository.deleteById(yanki.getId());
            Mono<Yanki> yanki2 = yankiRepository.findById(yanki.getId());
            System.out.println("DELETE BY ID = " + yanki.getId());
            System.out.println("DELETE = " + yanki);
            assertEquals(null, yanki2);
        }


    }

    @AfterAll
    void end() {
        System.out.println("END OF TESTS");
    }


}