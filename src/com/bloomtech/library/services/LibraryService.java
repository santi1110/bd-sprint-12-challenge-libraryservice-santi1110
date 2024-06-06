package com.bloomtech.library.services;

import com.bloomtech.library.exceptions.CheckableNotFoundException;
import com.bloomtech.library.exceptions.LibraryNotFoundException;
import com.bloomtech.library.exceptions.ResourceExistsException;
import com.bloomtech.library.models.*;
import com.bloomtech.library.models.checkableTypes.Checkable;
import com.bloomtech.library.models.checkableTypes.Media;
import com.bloomtech.library.repositories.LibraryRepository;
import com.bloomtech.library.models.CheckableAmount;
import com.bloomtech.library.views.LibraryAvailableCheckouts;
import com.bloomtech.library.views.OverdueCheckout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    //TODO: Implement behavior described by the unit tests in tst.com.bloomtech.library.services.LibraryService

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private CheckableService checkableService;

    public List<Library> getLibraries() {
        return libraryRepository.findAll();
    }

    public Library getLibraryByName(String name) {
        return libraryRepository.findByName(name).orElseThrow(() -> new LibraryNotFoundException("Library not found"));
    }

    public void save(Library library) {
        List<Library> libraries = libraryRepository.findAll();
        if (libraries.stream().filter(p->p.getName().equals(library.getName())).findFirst().isPresent()) {
            throw new ResourceExistsException("Library with name: " + library.getName() + " already exists!");
        }
        libraryRepository.save(library);
    }

    public CheckableAmount getCheckableAmount(String name, String isbn) {
        Library library = libraryRepository.findByName(name)
                .orElseThrow(() -> new LibraryNotFoundException("Library not found"));

        Checkable checkable = checkableService.getByIsbn(isbn);

        return library.getCheckables().stream()
                .filter(ca -> ca.getCheckable().getIsbn().equals(isbn))
                .findFirst()
                .orElse(new CheckableAmount(checkable, 0));
    }




    public List<LibraryAvailableCheckouts> getLibrariesWithAvailableCheckout(String isbn) {
        // Retrieve the Checkable object by ISBN

        // Get all libraries
        List<Library> libraries = libraryRepository.findAll();

        // List to store libraries with available checkouts
        List<LibraryAvailableCheckouts> availableCheckouts = new ArrayList<>();

        // Iterate through each library to find available checkouts for the given ISBN
        for (Library library : libraries) {
            int available = library.getCheckables().stream()
                    .filter(ca -> ca.getCheckable().getIsbn().equals(isbn) && ca.getAmount() > 0)
                    .mapToInt(CheckableAmount::getAmount)
                    .sum();

            // If available checkouts found, add to the list
            if (available > 0) {
                availableCheckouts.add(new LibraryAvailableCheckouts(available, library.getName()));
            }
        }

        return availableCheckouts;
    }





    public List<OverdueCheckout> getOverdueCheckouts(String libraryName) {
        Library library = libraryRepository.findByName(libraryName).orElseThrow(() -> new LibraryNotFoundException("Library not found"));
        List<OverdueCheckout> overdueCheckouts = new ArrayList<>();

        for (LibraryCard card : library.getLibraryCards()) {
            for (Checkout checkout : card.getCheckouts()) {
                if (checkout.getDueDate().isBefore(LocalDateTime.now())) {
                    overdueCheckouts.add(new OverdueCheckout(card.getPatron(), checkout));
                }
            }
        }

        return overdueCheckouts;
    }}

