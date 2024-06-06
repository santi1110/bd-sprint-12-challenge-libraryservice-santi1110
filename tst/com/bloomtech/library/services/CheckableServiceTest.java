package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.CheckableNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.Library;
import com.bloomtech.library.models.checkableTypes.*;
import com.bloomtech.library.repositories.CheckableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@SpringBootTest
public class CheckableServiceTest {

    @Autowired
    private CheckableService checkableService;

    @MockBean
    private CheckableRepository checkableRepository;

    //TODO: Inject dependencies and mocks

    private List<Checkable> checkables;

    @BeforeEach
    void init() {
        //Initialize test data
        checkables = new ArrayList<>();

        checkables.addAll(
                Arrays.asList(
                        new Media("1-0", "The White Whale", "Melvin H", MediaType.BOOK),
                        new Media("1-1", "The Sorcerer's Quest", "Ana T", MediaType.BOOK),
                        new Media("1-2", "When You're Gone", "Complaining at the Disco", MediaType.MUSIC),
                        new Media("1-3", "Nature Around the World", "DocuSpecialists", MediaType.VIDEO),
                        new ScienceKit("2-0", "Anatomy Model"),
                        new ScienceKit("2-1", "Robotics Kit"),
                        new Ticket("3-0", "Science Museum Tickets"),
                        new Ticket("3-1", "National Park Day Pass")
                )
        );
    }
    @Test
    void getAll() {
        Mockito.when(checkableRepository.findAll()).thenReturn(checkables);
        List<Checkable> allCheckables = checkableService.getAll();
        assertEquals(8, allCheckables.size());
    }

    @Test
    void getAll_emptyList() {
        Mockito.when(checkableRepository.findAll()).thenReturn(new ArrayList<>());
        List<Checkable> allCheckables = checkableService.getAll();
        assertTrue(allCheckables.isEmpty());
    }

    @Test
    void getByIsbn_existingIsbn() {
        Mockito.when(checkableRepository.findByIsbn("1-0")).thenReturn(Optional.of(checkables.get(0)));
        Checkable checkable = checkableService.getByIsbn("1-0");
        assertEquals("The White Whale", checkable.getTitle());
    }

    @Test
    void getByIsbn_nonExistingIsbn() {
        Mockito.when(checkableRepository.findByIsbn("non-existing")).thenReturn(Optional.empty());
        assertThrows(CheckableNotFoundException.class, () -> {
            checkableService.getByIsbn("non-existing");
        });
    }

    @Test
    void getByType_existingType() {
        Mockito.when(checkableRepository.findByType(Media.class)).thenReturn(Optional.of(checkables.get(0)));
        Checkable checkable = checkableService.getByType(Media.class);
        assertEquals("The White Whale", checkable.getTitle());
    }

    @Test
    void getByType_nonExistingType() {
        Mockito.when(checkableRepository.findByType(NonExistentType.class)).thenReturn(Optional.empty());
        assertThrows(CheckableNotFoundException.class, () -> {
            checkableService.getByType(NonExistentType.class);
        });
    }

    @Test
    void save_newCheckable() {
        Mockito.when(checkableRepository.findAll()).thenReturn(checkables);
        Checkable newCheckable = new Media("1-4", "New Title", "Author", MediaType.BOOK);
        checkableService.save(newCheckable);
        Mockito.verify(checkableRepository).save(newCheckable);
    }

    @Test
    void save_existingIsbn_throwsResourceExistsException() {
        Mockito.when(checkableRepository.findAll()).thenReturn(checkables);
        Checkable existingCheckable = new Media("1-0", "Duplicate Title", "Author", MediaType.BOOK);
        assertThrows(ResourceExistsException.class, () -> {
            checkableService.save(existingCheckable);
        });
        Mockito.verify(checkableRepository, Mockito.never()).save(existingCheckable);
    }

    @Test
    void save_checkableWithNullValues() {
        Mockito.when(checkableRepository.findAll()).thenReturn(new ArrayList<>());
        Checkable nullCheckable = new Media(null, null, null, null);
        checkableService.save(nullCheckable);
        Mockito.verify(checkableRepository).save(nullCheckable);
    }

    @Test
    void save_checkableWhenRepositoryIsEmpty() {
        Mockito.when(checkableRepository.findAll()).thenReturn(new ArrayList<>());
        Checkable newCheckable = new Media("1-5", "Unique Title", "Unique Author", MediaType.BOOK);
        checkableService.save(newCheckable);
        Mockito.verify(checkableRepository).save(newCheckable);
    }

    //TODO: Write Unit Tests for all CheckableService methods and possible Exceptions
}